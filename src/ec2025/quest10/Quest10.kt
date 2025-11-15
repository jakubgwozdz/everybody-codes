package ec2025.quest10

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

private fun Pos.dragonMoves(grid: List<String>): Set<Pos> =
    moves.mapNotNull { (dr, dc) -> (row + dr to col + dc).takeIf { it in grid } }.toSet()

data class State(val dragon: Pos, val sheep: List<Int?>) {
    override fun toString() =
        "dragon=${dragon.toStr()}, sheep=${sheep.mapIndexedNotNull { c, r -> r?.let { it to c } }.toStr()}"
}

private fun Pos.toStr() = "${'A' + col}${row + 1}"
private fun Collection<Pos>.toStr() = joinToString(",", prefix = "[", postfix = "]") { it.toStr() }

fun part3(data: String): Any {
    val (grid, chars) = parse(data)
    var result = 0L
    var toCheck = mapOf(
        State(chars['D']!!.single(), grid.first().indices.map { c -> chars['S']!!.firstOrNull { it.col == c }?.row })
                to 1L
    )
//    val hides = chars['#']!!
    var round = 0
    var statesChecked = 0L

    while (toCheck.isNotEmpty()) toCheck = buildMap {
        round++
        toCheck.forEach { (curr, count) ->
            statesChecked++
            if (curr.sheep.all { it == null }) {
                result += count
            } else {
                val possibleSheepMoves: List<List<Int?>> = curr.sheep.singleSheepMoves(grid, curr.dragon)
                val possibleDragonMoves = curr.dragon.dragonMoves(grid)
                possibleSheepMoves.forEach { sheep ->
                    possibleDragonMoves.forEach { dragon ->
                        val aliveSheep = sheep.mapIndexed { c, r ->
                            when {
                                r != null && r to c == dragon && grid[r][c] != '#' -> null
                                else -> r
                            }
                        }
                        increment(State(dragon, aliveSheep), count)
                    }
                }
            }
        }
        debug { "round $round, $statesChecked checked, $size to check, result $result" }
    }
    return result
}

private fun List<Int?>.singleSheepMoves(
    grid: List<String>,
    dragon: Pos
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
    .filter { sheep -> sheep.all { it == null || it in grid.indices } }
