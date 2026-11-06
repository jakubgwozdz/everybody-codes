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
    go("part3 ex", 187) { part3(exampleB, 100) }
    go("part3") { part3(provideInput(year, quest, 3)) }
}

data class Cloud(var energy: Long, val connections: List<Int>, var isAlive: Boolean = true)

fun part1(data: String): Long {
    val firstCharge = 5
    val clouds = parse(data)
    val firstCloud = clouds.maxBy { (_, v) -> v.connections.size }.key
    return calculate(clouds, firstCloud, firstCharge, null)
}

fun part2(data: String): Long {
    val firstCharge = 10
    val clouds = parse(data)
    val firstCloud = clouds.maxBy { (_, v) -> v.connections.size }.key
    return calculate(clouds, firstCloud, firstCharge, null)
}

fun part3(data: String, limit: Long = 1000000): Any {
    val firstCharge = 10
    val clouds = parse(data)
    val firstCloud = clouds.maxBy { (_, v) -> v.connections.size }.key
    return calculate(clouds, firstCloud, firstCharge, limit)
}

private fun calculate(
    clouds: Map<Int, Cloud>,
    firstCloud: Int,
    firstCharge: Int,
    limit: Long?
): Long = clouds.mapValues { it.value.copy() }
    .apply { propagate(firstCloud, firstCharge, limit) }
    .values.filter { it.isAlive }.sumOf { it.energy }

private fun Map<Int, Cloud>.propagate(
    firstCloud: Int,
    firstCharge: Int,
    limit: Long?,
) {
    var receivers = mapOf(firstCloud to 1L)
    (firstCharge downTo 1).forEach { charge ->
        receivers = buildMap {
            receivers.forEach { (id, count) ->
                val cloud = this@propagate[id]!!
                if (cloud.isAlive) {
                    cloud.energy += charge * count
                    cloud.connections.forEach { n ->
                        if (this@propagate[id]!!.isAlive) increment(n, count)
                    }
                }
            }
        }
        if (limit != null) forEach { (i, cloud) ->
            if (cloud.isAlive && cloud.energy >= limit) {
                cloud.isAlive = false
            }
        }
    }
}

private fun parse(data: String): Map<Int, Cloud> = data.lines()
    .associate { line ->
        val (a, b, c) = line.split(" ")
        val id = a.substringAfter('=').substringBefore(',').toInt()
        val energy = b.substringAfter('=').substringBefore(',').toLong()
        val connections = c.substringAfter('=').split(",").map { it.toInt() }
        id to Cloud(energy, connections)
    }
