package ec2025.quest13

import go
import logged
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 797) { part1(provideInput(year, quest, 1)) }
    go("part2", 6558) { part2(provideInput(year, quest, 2)) }
    go("part3", 91492) { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val ranges = data.lines().map { it.toInt() }.map { it..it }
    return solve(ranges, 2025)
}

fun part2(data: String): Any {
    val ranges = data.lines().map { it.split('-').let { (a, b) -> a.toInt()..b.toInt() } }
    return solve(ranges, 20252025)
}

fun part3(data: String): Any {
    val ranges = data.lines().map { it.split('-').let { (a, b) -> a.toInt()..b.toInt() } }
    return solve(ranges, 202520252025)
}

private val IntRange.length: Int get() = (last - first + 1) / step
private operator fun IntRange.get(i: Int): Int = first + i * step

private fun solve(ranges: List<IntRange>, count: Long): Int {
    val size = ranges.sumOf { it.length } + 1
    val pos = (count % size).toInt()
    if (pos == 0) return 1

    var i = 1
    var j = size - 1
    var clockwise = true
    ranges.forEach { r ->
        if (clockwise) {
            i += r.length
            if (pos < i) return r[pos - i - r.length]
        } else {
            j -= r.length
            if (pos > j) return r[j + r.length - pos]
        }
        clockwise = !clockwise
    }
    error("WAT")
}

