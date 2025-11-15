package ec2025.quest10

import collections.increment
import debug
import go
import provideInput
import yearAndQuestFromPackage
import java.time.Duration

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
    go("part3") { part3(provideInput(year, quest, 3)) }
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
        dragon = dragon.moveDragon(grid).filter { it !in visited }.toSet()
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
        dragon = dragon.moveDragon(grid)
        sheep = sheep.eat(dragon, hides).also { result += sheep.size - it.size }
        sheep = sheep.moveSheep(grid)
        sheep = sheep.eat(dragon, hides).also { result += sheep.size - it.size }
    }
    return result
}

private fun Set<Pos>.eat(dragon: Set<Pos>, hides: Set<Pos>): Set<Pos> = filter { it !in dragon || it in hides }.toSet()

private operator fun List<String>.contains(pos: Pos): Boolean =
    pos.first in indices && pos.second in this[pos.first].indices

private fun Set<Pos>.moveSheep(grid: List<String>): Set<Pos> =
    mapNotNull { (r, c) -> (r + 1 to c).takeIf { it in grid } }.toSet()

private fun Set<Pos>.moveDragon(grid: List<String>): Set<Pos> =
    flatMap { it.moveDragon(grid) }.toSet()

private fun Pos.moveDragon(grid: List<String>): Set<Pos> =
    moves.mapNotNull { (dr, dc) -> (first + dr to second + dc).takeIf { it in grid } }.toSet()

data class State(val dragon: Pos, val sheep: Set<Pos>) {
    override fun toString() = "dragon=${dragon.toStr()}, sheep=${sheep.toStr()}"
}

private fun Pos.toStr() = "${'A' + second}${first + 1}"
private fun Collection<Pos>.toStr() = joinToString(",", prefix = "[", postfix = "]") { it.toStr() }

fun part3(data: String): Any {
    val (grid, chars) = parse(data)
    var result = 0L
    val toCheck = mutableMapOf<State, Long>()
    toCheck.increment(State(chars['D']!!.single(), chars['S']!!))
    val hides = chars['#']!!

    while (toCheck.isNotEmpty()) {
//        val (curr, count) = toCheck.entries.first()
        val (curr, count) = toCheck.entries.minBy { (_, c) -> c }
        toCheck.remove(curr)
        if (curr.sheep.isEmpty()) {
            result += count
            continue
        }
        val possibleSheepMoves = curr.sheep.mapNotNull { lamb ->
            val (lr, lc) = lamb
            val nextLamb = lr + 1 to lc
            if (nextLamb in hides || nextLamb != curr.dragon) curr.sheep - lamb + nextLamb else null
        }
            .ifEmpty { listOf(curr.sheep) }
            .filter { sheep -> sheep.all { it in grid } }
        val possibleDragonMoves = curr.dragon.moveDragon(grid)
        possibleSheepMoves.forEach { sheep ->
            possibleDragonMoves.forEach { dragon ->
                toCheck.increment(State(dragon, sheep.filter { it != dragon || it in hides }.toSet()), count)
            }
        }
        debug(Duration.ofSeconds(1)) { toCheck.size }
//        toCheck.forEach { (state, count) -> debug("${count}x $state") }
    }
    return result
}
