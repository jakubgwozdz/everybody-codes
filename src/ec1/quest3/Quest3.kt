package ec1.quest3

import go
import provideInput
import yearAndQuestFromPackage

val exampleA = """
    x=1 y=2
    x=2 y=3
    x=3 y=4
    x=4 y=4
""".trimIndent()

fun part1(data: String): Any =
    generateSequence(parse(data)) { it.map { (x, y) -> if (y == 1) y to x else x + 1 to y - 1 } }
        .drop(100).first()
        .sumOf { (x, y) -> x + 100 * y }

private fun parse(data: String): List<Pair<Int, Int>> = data.lines().map { line ->
    line.split(' ')
        .associate { it.substringBefore('=') to it.substringAfter('=') }
        .let { Pair(it["x"]!!.toInt(), it["y"]!!.toInt()) }
}

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 1310) { part1(exampleA) }
    go("part1") { part1(provideInput(year, quest, 1)) }
//    go("part2", "QUACK!BXXFYHTSRMFSHW") { part2(provideInput(year, quest, 2)) }
//    go("part3", "DJCGL") { part3(exampleA) }
//    go("part3") { part3(provideInput(year, quest, 3)) }
}
