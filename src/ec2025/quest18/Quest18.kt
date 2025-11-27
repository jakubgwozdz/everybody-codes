package ec2025.quest18

import go
import logged
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 538121) { part1(provideInput(year, quest, 1)) }
    val part2ex1 = """
        Plant 1 with thickness 1:
        - free branch with thickness 1

        Plant 2 with thickness 1:
        - free branch with thickness 1

        Plant 3 with thickness 1:
        - free branch with thickness 1

        Plant 4 with thickness 10:
        - branch to Plant 1 with thickness -25
        - branch to Plant 2 with thickness 17
        - branch to Plant 3 with thickness 12

        Plant 5 with thickness 14:
        - branch to Plant 1 with thickness 14
        - branch to Plant 2 with thickness -26
        - branch to Plant 3 with thickness 15

        Plant 6 with thickness 150:
        - branch to Plant 4 with thickness 5
        - branch to Plant 5 with thickness 6


        1 0 1
        0 0 1
        0 1 1
    """.trimIndent()
    go("part2ex1", 324) { part2(part2ex1) }
    go("part2", 10647934892) { part2(provideInput(year, quest, 2)) }
    val part3ex1 = """
        Plant 1 with thickness 1:
        - free branch with thickness 1

        Plant 2 with thickness 1:
        - free branch with thickness 1

        Plant 3 with thickness 1:
        - free branch with thickness 1

        Plant 4 with thickness 1:
        - free branch with thickness 1

        Plant 5 with thickness 8:
        - branch to Plant 1 with thickness -8
        - branch to Plant 2 with thickness 11
        - branch to Plant 3 with thickness 13
        - branch to Plant 4 with thickness -7

        Plant 6 with thickness 7:
        - branch to Plant 1 with thickness 14
        - branch to Plant 2 with thickness -9
        - branch to Plant 3 with thickness 12
        - branch to Plant 4 with thickness 9

        Plant 7 with thickness 23:
        - branch to Plant 5 with thickness 17
        - branch to Plant 6 with thickness 18


        0 1 0 0
        0 1 0 1
        0 1 1 1
        1 1 0 1
    """.trimIndent()
    go("part3ex1", 946) { part3(part3ex1) }
    go("part3", 388376) { part3(provideInput(year, quest, 3)) }
}

sealed interface Plant
data object Free : Plant
data class Branching(val thickness: Int, val from: List<Pair<Int, Int>>) : Plant

fun parsePlants(data: String) = buildMap {
    val lines = data.lines().iterator()
    val headerRegex = Regex("""Plant (\d+) with thickness (-?\d+):""")
    val freeRegex = Regex("""- free branch with thickness 1""")
    val branchRegex = Regex("""- branch to Plant (\d+) with thickness (-?\d+)""")
    fun String.numbers(regex: Regex) =
        regex.matchEntire(this)?.groupValues?.drop(1)?.map(String::toInt) ?: error("invalid line: $this")
    while (lines.hasNext()) {
        val (id, thickness) = lines.next().numbers(headerRegex)
        var next = lines.next()
        if (next.matches(freeRegex)) {
            require(thickness == 1)
            this[id] = Free
            if (lines.hasNext()) lines.next()
        } else {
            val branches = mutableListOf<Pair<Int, Int>>()
            while (next.isNotBlank()) {
                branches += next.numbers(branchRegex).let { (a, b) -> a to b }
                next = if (lines.hasNext()) lines.next() else ""
            }
            this[id] = Branching(thickness, branches)
        }
    }
}

fun calcEmitted(plants: Map<Int, Plant>, id: Int, enabled: (Int) -> Long = { 1 }): Long {
    val emittedCache = mutableMapOf<Int, Long>()
    fun emitted(id: Int): Long = emittedCache.getOrPut(id) {
        when (val plant = plants[id]!!) {
            is Free -> enabled(id)
            is Branching -> plant.from.sumOf { (bId, bTh) -> emitted(bId) * bTh }.takeIf { it >= plant.thickness } ?: 0
        }
    }
    return emitted(id)
}

fun part1(data: String): Any {
    val plants = parsePlants(data)
    return calcEmitted(plants, findLast(plants))
}

fun part2(data: String): Any {
    val plants = parsePlants(data.substringBefore("\n\n\n"))
    val last = findLast(plants)
    return data.substringAfter("\n\n\n").lines().sumOf {
        val enabled = parseSetup(it)
        calcEmitted(plants, last) { id -> enabled[id - 1] }
    }
}

fun part3(data: String): Any {
    val plants = parsePlants(data.substringBefore("\n\n\n"))
    val last = findLast(plants)
    val rounds = data.substringAfter("\n\n\n").lines().sorted().map {
        val enabled = parseSetup(it)
        enabled to calcEmitted(plants, last) { id -> enabled[id - 1] }
    }.filter { it.second > 0 }
    var bestSetup = rounds.maxBy { it.second }.first
    var best = calcEmitted(plants, last) { id -> bestSetup[id - 1] }
    var i = 0
    var j = 0
    val size = bestSetup.size
    while (j <= size) {
        val n = List(size) { if (it == i) 1 - bestSetup[i] else bestSetup[it] }
        val nn = calcEmitted(plants, last) { id -> n[id - 1] }
        if (nn > best) {
            bestSetup = n
            best = nn.logged("new best after flipping ${i + 1}")
            j = 0
        } else {
            j++
        }
        i = (i + 1) % size
    }
    return rounds.sumOf { best - it.second }
}

private fun parseSetup(setup: String) = setup.split(" ").map(String::toLong)

private fun findLast(plants: Map<Int, Plant>): Int =
    (plants.keys - plants.values.filterIsInstance<Branching>().flatMap { it.from }.map { it.first }
        .toSet()).single { plants[it] is Branching }
