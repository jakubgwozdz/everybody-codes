package ec1.quest3

import go
import provideInput
import yearAndQuestFromPackage

val exampleA = """
    x=12 y=2
    x=8 y=4
    x=7 y=1
    x=1 y=5
    x=1 y=3
""".trimIndent()

val exampleB = """
    x=3 y=1
    x=3 y=9
    x=1 y=5
    x=4 y=10
    x=5 y=3
""".trimIndent()

private fun sequenceOfPositions(data: String): Sequence<List<Pair<Int, Int>>> =
    generateSequence(parse(data)) { it.map { (x, y) -> if (y == 1) y to x else x + 1 to y - 1 } }

fun part1(data: String): Any = sequenceOfPositions(data)
    .drop(100).first()
    .sumOf { (x, y) -> x + 100 * y }

fun part2(data: String): Any {
    val initial = parse(data)
    return generateSequence(initial) { it.map { (x, y) -> if (y == 1) y to x else x + 1 to y - 1 } }
        .indexOfFirst { it.all { (x, y) -> y == 1 } }
}

fun part3(data: String) = part2(data)

private fun parse(data: String): List<Pair<Int, Int>> = data.lines().map { line ->
    line.split(' ')
        .associate { it.substringBefore('=') to it.substringAfter('=') }
        .let { Pair(it["x"]!!.toInt(), it["y"]!!.toInt()) }
}

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 3343) { part1(provideInput(year, quest, 1)) }
    go("part2exA", 14) { part2(exampleA) }
    go("part2exB", 13659) { part2(exampleB) }
    go("part2", 1098490) { part2(provideInput(year, quest, 2)) }
    go("part3") { part3(provideInput(year, quest, 3)) }
}
