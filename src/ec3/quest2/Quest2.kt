package ec3.quest2

import coords.e
import coords.findAll
import coords.n
import coords.pair.Pos
import coords.pair.col
import coords.pair.row
import coords.s
import coords.w
import go
import provideInput
import yearAndQuestFromPackage

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    val part1ex = """
        .......
        .......
        .......
        .#.@...
        .......
        .......
        .......
    """.trimIndent()
    go("part1ex", 12) { part1(part1ex) }
    go("part1", 201) { part1(provideInput(year, quest, 1)) }
    val part2ex = """
        .......
        .......
        .......
        .#.@...
        .......
        .......
        .......
    """.trimIndent()
    go("part2ex", 47) { part2(part2ex) }
    go("part2") { part2(provideInput(year, quest, 2)) }
    go("part3ex", 87) { part3(part2ex) }
    go("part3", 2840) { part3(provideInput(year, quest, 3)) }
}


fun part1(input: String): Int {
    val map = input.reader().readLines()
    val bones = map.findAll('#')
    val start = map.findAll('@').single()
    return solve(start, bones, emptySet(), 1)
}

fun part2(input: String): Int {
    val map = input.reader().readLines()
    val bones = map.findAll('#')
    val start = map.findAll('@').single()
    return solve(start, bones.flatMap { listOf(it.n(), it.e(), it.s(), it.w()) }.toSet(), bones, 1)
}

fun part3(input: String): Int {
    val map = input.reader().readLines()
    val bones = map.findAll('#')
    val start = map.findAll('@').single()
    return solve(start, bones.flatMap { listOf(it.n(), it.e(), it.s(), it.w()) }.toSet(), bones, 3)
}

private fun solve(
    start: Pos,
    required: Set<Pos>,
    initiallyOccupied: Set<Pos>,
    count: Int, // 1 for p1 & p2, 3 for p3
): Int {
    val moves = sequence {
        while (true) {
            repeat(count) { yield(Pos::n) }
            repeat(count) { yield(Pos::e) }
            repeat(count) { yield(Pos::s) }
            repeat(count) { yield(Pos::w) }
        }
    }.iterator()
    val visited = initiallyOccupied.toMutableSet().apply { add(start) }
    val visitedRows = visited.map { it.row }.toMutableSet()
    val visitedCols = visited.map { it.col }.toMutableSet()
    fun markEnclosed(p1: Pos) {
        if (p1 !in visited) {
            val enclosed = findEnclosed(p1, visited, visitedRows, visitedCols)
            visited += enclosed
            visitedRows += enclosed.map { it.row }
            visitedCols += enclosed.map { it.col }
        }
    }

    required.forEach(::markEnclosed)

    var source = start
    var steps = 0
    while (required.any { it !in visited }) {
        source = generateSequence { moves.next().invoke(source, 1) }.take(4 * count).first { it !in visited }
        visited += source
        visitedRows += source.row
        visitedCols += source.col
        steps++
        listOf(source.n(), source.e(), source.s(), source.w()).forEach(::markEnclosed)
    }
    return steps
}

fun findEnclosed(p: Pos, occupied: Set<Pos>, occupiedRows: Set<Int>, occupiedCols: Set<Int>): Set<Pos> {
    val toGo = mutableListOf(p)
    val enclosed = mutableSetOf(p)
    while (toGo.isNotEmpty()) {
        val next = toGo.removeFirst()
        listOf(next.n(), next.e(), next.s(), next.w()).forEach { p1 ->
            if (p1.row !in occupiedRows) return emptySet()
            if (p1.col !in occupiedCols) return emptySet()
            if (p1 !in occupied && p1 !in enclosed) {
                toGo += p1
                enclosed += p1
            }
        }
    }

    return enclosed
}

