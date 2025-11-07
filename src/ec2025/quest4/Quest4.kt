package ec2025.quest4

import go
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 12816) { part1(provideInput(year, quest, 1)) }
    go("part2", 2346491228071) { part2(provideInput(year, quest, 2)) }
    go("part3", 524354302005) { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val gears = data.lines()
    val first = gears.first().toLong()
    val last = gears.last().toLong()
    return first * 2025 / last
}

fun part2(data: String): Any {
    val gears = data.lines()
    val first = gears.first().toLong()
    val last = gears.last().toLong()
    return (last * 10000000000000 + first - 1) / first
}

fun part3(data: String): Any {
    val gears = data.lines()
    val first = gears.first().toLong()
    val last = gears.last().toLong()
    val ratio = gears.filter { "|" in it }.map { it.split("|") }
        .map { (a, b) -> a.toLong() to b.toLong() }
        .onEach { (a, b) -> require(b % a == 0L) { "TODO: not integer ratio for $a|$b" } }
        .map { (a, b) -> b / a }.fold(1L, Long::times)
    return 100 * first * ratio / last
}
