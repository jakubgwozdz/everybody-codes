package ec2026.quest7

import collections.increment
import go
import provideInput
import yearAndQuestFromPackage

private val exampleA = """
    id=1, energy=5, connections=2,5,7
    id=2, energy=9, connections=6,7
    id=3, energy=3, connections=4
    id=4, energy=8, connections=3
    id=5, energy=1, connections=6,7
    id=6, energy=7, connections=1,7
    id=7, energy=6, connections=5
""".trimIndent()

private val exampleB = """
    id=1, energy=5, connections=2,3,5,7
    id=2, energy=9, connections=1,3,4,5,6,7
    id=3, energy=3, connections=1,2,4,6,7
    id=4, energy=8, connections=1,2,7
    id=5, energy=1, connections=2,3,4,6
    id=6, energy=7, connections=1,2,3,4
    id=7, energy=6, connections=1,2,4,6
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1 ex", 102) { part1(exampleA) }
    go("part1", 254) { part1(provideInput(year, quest, 1)) }
    go("part2 ex", 1316) { part2(exampleA) }
    go("part2 ex2", 1214633) { part2(exampleB) }
    go("part2", 170660670) { part2(provideInput(year, quest, 2)) }
    go("part3 ex", 187) { part3(exampleA, 100) }
    go("part3", 36015318) { part3(provideInput(year, quest, 3)) }
}

data class Cloud(var energy: Long, val connections: List<String>, var isAlive: Boolean = true)

fun part1(data: String): Long {
    val clouds = parse(data)
    val firstCloud = clouds.maxBy { (_, v) -> v.connections.size }.key
    return calculate(clouds, firstCloud, 5L, null)
}

fun part2(data: String): Long {
    val clouds = parse(data)
    val firstCloud = clouds.maxBy { (_, v) -> v.connections.size }.key
    return calculate(clouds, firstCloud, 10L, null)
}

fun part3(data: String, limit: Long = 1000000): Any {
    val clouds = parse(data)
    return clouds.keys.maxOf { calculate(clouds, it, 10L, limit) }
}

private fun calculate(
    clouds: Map<String, Cloud>,
    firstCloud: String,
    firstCharge: Long,
    limit: Long?
): Long = clouds
    .mapValues { it.value.copy() } // deep copy to work on mutable values
    .apply { propagate(firstCloud, firstCharge, limit) }
    .values.sumOf { it.energy }

private fun Map<String, Cloud>.propagate(
    firstCloud: String,
    firstCharge: Long,
    limit: Long?,
) {
    var receivers = mapOf((firstCloud to firstCharge) to 1L)
    while (receivers.isNotEmpty()) { receivers = oneStep(receivers, limit) }
}

private fun Map<String, Cloud>.oneStep(
    receivers: Map<Pair<String, Long>, Long>,
    limit: Long?
): Map<Pair<String, Long>, Long> {
    val newReceivers = mutableMapOf<Pair<String, Long>, Long>()
    receivers.forEach { (idWithCharge, count) ->
        val (id, charge) = idWithCharge
        val cloud = this[id]!!
        if (cloud.isAlive) {
            cloud.energy += charge * count
            if (charge > 1) cloud.connections.forEach { n ->
                if (this[n]!!.isAlive) newReceivers.increment((n to charge - 1), count)
            }
        }
    }
    if (limit != null) this.forEach { (i, cloud) ->
        if (cloud.energy >= limit) {
            cloud.isAlive = false
            cloud.energy = 0
            cloud.connections.forEach { n ->
                if (this[n]!!.isAlive) newReceivers.increment((n to 10), 1)
            }
        }
    }
    return newReceivers
}

private fun parse(data: String): Map<String, Cloud> = data.lines()
    .associate { line ->
        val (a, b, c) = line.split(" ")
        val id = a.substringAfter('=').substringBefore(',')
        val energy = b.substringAfter('=').substringBefore(',').toLong()
        val connections = c.substringAfter('=').split(",")
        id to Cloud(energy, connections)
    }
