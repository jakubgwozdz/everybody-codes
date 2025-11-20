package ec2025.quest13

import go
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
//    val ex = """
//        10-15
//        12-13
//        20-21
//        19-23
//        30-37
//    """.trimIndent()
//    listOf(1, 10, 11, 12, 13, 14, 15, 20, 21, 30, 31, 32, 33, 34, 35, 36, 37, 23, 22, 21, 20, 19, 13, 12, 1)
//        .forEachIndexed { i, n -> go("ex($i)", n) { part1(ex, i.toLong()) } }
//
    go("part1", 797) { part1(provideInput(year, quest, 1)) }
    go("part2", 6558) { part2(provideInput(year, quest, 2)) }
    go("part3", 91492) { part3(provideInput(year, quest, 3)) }
}

// not really ranges, but pairs (first,size)
private fun parse(data: String): List<Pair<Long, Long>> = data.lines().map { it.split('-').map(String::toLong) }
    .map {
        val first = it[0]
        val last = it.getOrNull(1) ?: first
        first to last - first + 1
    }

fun part1(data: String, count: Long = 2025): Long {
    val input = parse(data)
    val dialSize = input.sumOf { (first, size) -> size } + 1
    val pos = count % dialSize
    if (pos == 0L) return 1

    var i = 1L // next clockwise pos
    var j = dialSize - 1 // next counter-clockwise pos
    var clockwise = true
    input.forEach { (first, size) ->
        if (clockwise) {
            if (pos < i + size) return first + pos - i else i += size
        } else {
            if (pos > j - size) return first + j - pos else j -= size
        }
        clockwise = !clockwise
    }
    error("Not gonna happen")
}

fun part2(data: String) = part1(data, 20252025)
fun part3(data: String) = part1(data, 202520252025)
