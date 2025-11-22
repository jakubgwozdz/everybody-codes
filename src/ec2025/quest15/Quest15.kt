package ec2025.quest15

import coords.Direction
import coords.manhattanDistance
import coords.move
import coords.pair.Pos
import coords.pair.col
import coords.pair.row
import go
import logged
import provideInput
import search.astar
import yearAndQuestFromPackage

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    val part1e1 = """
        L6,L3,L6,R3,L6,L3,L3,R6,L6,R6,L6,L6,R3,L3,L3,R3,R3,L6,L6,L3
    """.trimIndent()
    go("part1e1", 16) { part1(part1e1) }
    go("part1", 109) { part1(provideInput(year, quest, 1)) }
    go("part2", 3689) { part2(provideInput(year, quest, 2)) }
    go("part3", 511612640) { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val start = Pos(0, 0)
    var end = start
    val hWalls = mutableMapOf<Int, MutableList<IntRange>>()
    val vWalls = mutableMapOf<Int, MutableList<IntRange>>()
    var d = Direction.N
    data.split(',').forEach { m ->
        val from = end
        d = when (m.first()) {
            'L' -> d.turnLeft()
            'R' -> d.turnRight()
            else -> error("wrong direction $m")
        }
        val dist = m.drop(1).toInt()
        end = from.move(d, dist)
        when (d) {
            Direction.N -> vWalls.getOrPut(from.col) { mutableListOf() }.add(end.row + 1..from.row)
            Direction.E -> hWalls.getOrPut(from.row) { mutableListOf() }.add(from.col..end.col - 1)
            Direction.S -> vWalls.getOrPut(from.col) { mutableListOf() }.add(from.row..end.row - 1)
            Direction.W -> hWalls.getOrPut(from.row) { mutableListOf() }.add(end.col + 1..from.col)
        }
    }

    // compression
    val rows = (hWalls.keys + end.row + 0).flatMap { setOf(it, it - 1, it + 1) }.sorted().distinct()
    val cols = (vWalls.keys + end.col + 0).flatMap { setOf(it, it - 1, it + 1) }.sorted().distinct()
    val lookupRows = rows.withIndex().associate { (i, r) -> r to i }
    val lookupCols = cols.withIndex().associate { (i, c) -> c to i }
    fun compress(p: Pos) = Pos(lookupRows[p.row]?:error("$p unknown row"), lookupCols[p.col]?:error("$p unknown col"))
    fun decompress(p: Pos) = Pos(rows[p.row], cols[p.col])

    fun neighbors(current: Pos): List<Pair<Pos, Int>> {
        val compressed = compress(current)
        return Direction.entries.map(compressed::move)
            .map { decompress(it) }
            .filterNot { n -> vWalls[n.col]?.any { n.row in it } == true }
            .filterNot { n -> hWalls[n.row]?.any { n.col in it } == true }
            .map { it to current.manhattanDistance(it) }
    }

    return astar(start, end, end::manhattanDistance, ::neighbors)
        .windowed(2).sumOf { (a, b) -> a.manhattanDistance(b) }
}

fun part2(data: String) = part1(data)

fun part3(data: String) = part1(data)
