package ec2025.quest10

import collections.Cached
import collections.increment
import coords.pair.col
import coords.pair.row
import debug
import go
import provideInput
import yearAndQuestFromPackage

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 143) { part1(provideInput(year, quest, 1)) }
    go("part2", 1725) { part2(provideInput(year, quest, 2)) }
    val part3ex1 = """
        SSS
        ..#
        #.#
        #D.
    """.trimIndent()
    go("part3ex1", 15) { part3(part3ex1) }
    val part3ex2 = """
        SSS
        ..#
        ..#
        .##
        .D#
    """.trimIndent()
    go("part3ex2", 8) { part3(part3ex2) }
    val part3ex3 = """
        .SS.S
        #...#
        ...#.
        ##..#
        .####
        ##D.#
    """.trimIndent()
    go("part3ex3", 4406) { part3(part3ex3) }
    val part3ex4 = """
        SSS.S
        .....
        #.#.#
        .#.#.
        #.D.#
    """.trimIndent()
    go("part3ex4", 13033988838) { part3(part3ex4) }
    go("part3", 42444382367832) { part3(provideInput(year, quest, 3)) }
}

typealias Pos = Pair<Int, Int>

private fun parse(data: String): Pair<List<String>, Map<Char, Set<Pos>>> {
    val grid = data.lines()
    val chars = grid.flatMapIndexed { r, line ->
        line.mapIndexed { c, ch -> r to c to ch }
    }.groupBy { (_, ch) -> ch }.mapValues { (_, v) -> v.map { (p) -> p }.toSet() }
    return grid to chars
}

private val moves = listOf(-2 to 1, -1 to 2, 1 to 2, 2 to 1, 2 to -1, 1 to -2, -1 to -2, -2 to -1)

fun part1(data: String, rounds: Int = 4): Any {
    val (grid, chars) = parse(data)
    var dragon = chars['D']!!
    val sheep = chars['S']!!

    val visited = dragon.toMutableSet()
    repeat(rounds) {
        dragon = dragon.dragonMoves(grid).filter { it !in visited }.toSet()
        visited += dragon
    }
    return visited.count { it in sheep }
}

fun part2(data: String, rounds: Int = 20): Any {
    val (grid, chars) = parse(data)
    var dragon = chars['D']!!
    var sheep = chars['S']!!
    val hides = chars['#']!!
    var result = 0
    repeat(rounds) {
        dragon = dragon.dragonMoves(grid)
        sheep = sheep.eat(dragon, hides).also { result += sheep.size - it.size }
        sheep = sheep.sheepMoves(grid)
        sheep = sheep.eat(dragon, hides).also { result += sheep.size - it.size }
    }
    return result
}

private fun Set<Pos>.eat(dragon: Set<Pos>, hides: Set<Pos>): Set<Pos> = filter { it !in dragon || it in hides }.toSet()

private operator fun List<String>.contains(pos: Pos): Boolean =
    pos.row in indices && pos.col in this[pos.row].indices

private fun Set<Pos>.sheepMoves(grid: List<String>): Set<Pos> =
    mapNotNull { (r, c) -> (r + 1 to c).takeIf { it in grid } }.toSet()

private fun Set<Pos>.dragonMoves(grid: List<String>): Set<Pos> =
    flatMap { it.dragonMoves(grid) }.toSet()

private val dragonMovesCache = Cached { (dragon, grid): Pair<Pos, List<String>> ->
    moves.mapNotNull { (dr, dc) -> (dragon.row + dr to dragon.col + dc).takeIf { it in grid } }
}

private fun Pos.dragonMoves(grid: List<String>): List<Pos> =
    dragonMovesCache(this to grid)
//    moves.mapNotNull { (dr, dc) -> (row + dr to col + dc).takeIf { it in grid } }

fun part3(data: String): Any {
    val (grid, chars) = parse(data)
    var result = 0L
    val dragon = chars['D']!!.single()
    val sheep = grid.first().indices.map { c -> chars['S']!!.firstOrNull { it.col == c }?.row }
    var toCheck = mapOf(dragon to mapOf(sheep to 1L))
    var round = 0
    var statesChecked = 0L
    val validRows =
        grid.first().indices.map { c -> sheep[c]?.let { it..grid.indexOfLast { it[c] != '#' } } ?: IntRange.EMPTY }

    while (toCheck.isNotEmpty()) {
        round++
        val (toCheckNext, finished) = round(toCheck, grid, validRows)
        result += finished
        statesChecked += toCheck.entries.sumOf { (_, inner) -> inner.size }
        debug { "round $round, $statesChecked checked, ${toCheckNext.entries.sumOf { (_, inner) -> inner.size }} to check, result $result" }
        toCheck = toCheckNext
    }
    return result
}

private fun round(
    toCheck: Map<Pos, Map<List<Int?>, Long>>,
    grid: List<String>,
    validRows: List<IntRange>,
): Pair<Map<Pos, Map<List<Int?>, Long>>, Long> {
    var finished = 0L
    val toCheckNext = mutableMapOf<Pos, MutableMap<List<Int?>, Long>>()
    toCheck.forEach { (currDragon, inner) ->
        inner.forEach { (currSheep, count) ->
            val possibleSheepMoves: List<List<Int?>> = currSheep.singleSheepMoves(grid, currDragon, validRows)
            currDragon.dragonMoves(grid).forEach { dragon ->
                possibleSheepMoves.forEach { sheep ->
                    val aliveSheep = sheep.mapIndexed { c, r ->
                        when {
                            r != null && r to c == dragon && grid[r][c] != '#' -> null
                            else -> r
                        }
                    }
                    if (aliveSheep.any { it != null }) toCheckNext.increment(dragon, aliveSheep, count)
                    else finished += count
                }
            }
        }
    }
    return toCheckNext to finished
}

private fun List<Int?>.singleSheepMoves(
    grid: List<String>,
    dragon: Pos,
    validRows: List<IntRange>
): List<List<Int?>> = buildList {
    this@singleSheepMoves.forEachIndexed { c, r ->
        when {
            r == null -> {} // skip, no sheep
            r + 1 to c == dragon && grid[r + 1][c] != '#' -> {} // skip, illegal
            else -> add(this@singleSheepMoves.mapIndexed { c1, r1 -> if (c1 == c) r + 1 else r1 })
        }
    }
}
    .ifEmpty { listOf(this) }
    .filter { sheep -> sheep.indices.all { c -> sheep[c] == null || sheep[c] in validRows[c] } }
