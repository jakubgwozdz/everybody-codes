package ec2025.quest10

import collections.Cached
import coords.pair.col
import coords.pair.row
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
typealias Sheep = List<Int?>

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
    val board = Board(grid)
    var dragon = chars['D']!!
    val sheep = chars['S']!!

    val visited = dragon.toMutableSet()
    repeat(rounds) {
        dragon = dragon.dragonMoves(board).filter { it !in visited }.toSet()
        visited += dragon
    }
    return visited.count { it in sheep }
}

fun part2(data: String, rounds: Int = 20): Any {
    val (grid, chars) = parse(data)
    val board = Board(grid)
    var dragon = chars['D']!!
    var sheep = chars['S']!!
    val hides = chars['#']!!
    var result = 0
    repeat(rounds) {
        dragon = dragon.dragonMoves(board)
        sheep = sheep.eat(dragon, hides).also { result += sheep.size - it.size }
        sheep = sheep.sheepMoves(board)
        sheep = sheep.eat(dragon, hides).also { result += sheep.size - it.size }
    }
    return result
}

private fun Set<Pos>.eat(dragon: Set<Pos>, hides: Set<Pos>): Set<Pos> = filter { it !in dragon || it in hides }.toSet()

private operator fun List<String>.contains(pos: Pos): Boolean =
    pos.row in indices && pos.col in this[pos.row].indices

private fun Set<Pos>.sheepMoves(board: Board): Set<Pos> =
    mapNotNull { (r, c) -> (r + 1 to c).takeIf { it in board.grid } }.toSet()

private fun Set<Pos>.dragonMoves(board: Board): Set<Pos> =
    flatMap { board.dragonMovesCached(it) }.toSet()

class Board(val grid: List<String>) {
    val dragonCols = grid.first().indices
    val dragonColsCount = dragonCols.count()
    val dragonRows = grid.indices
    val dragonRowsCount = dragonRows.count()

    val sheepCols = dragonCols//.filter { c -> grid.any { it[c] == 'S' } }
    val sheepRows = sheepCols.map { c ->
        val first = grid.indexOfFirst { it[c] == 'S' }
        val last = grid.indexOfLast { it[c] != '#' }
        if (first == -1) IntRange.EMPTY else first..last
    }
    val sheepRowsCounts = sheepRows.map { it.count() }

    fun dragonMoves(dragon: Pos): List<Pair<Int, Int>> =
        moves.mapNotNull { (dr, dc) -> (dragon.row + dr to dragon.col + dc).takeIf { it in grid } }

    val dragonMovesCached = Cached(::dragonMoves)
    val solveP3StateCached = Cached(::solveP3State)

    val bases = sheepRowsCounts.map { it + 1 } + dragonRowsCount + dragonColsCount
    val possibleStates = bases.fold(1, Int::times)
    val indexer = Indexer(
        bases,
        { (dragon, sheep) ->
            IntArray(sheepRowsCounts.size + 2) { c ->
                when (c) {
                    in sheep.indices -> sheep[c]?.let { it + 1 } ?: 0
                    sheep.size -> dragon.row
                    sheep.size + 1 -> dragon.col
                    else -> error("invalid index $c")
                }
            }
        },
        { ints ->
            val sheep: Sheep = ints.take(sheepRowsCounts.size).map { if (it == 0) null else it - 1 }
            val dragonRow = ints[sheepRowsCounts.size]
            val dragonCol = ints[sheepRowsCounts.size + 1]
            Pos(dragonRow, dragonCol) to sheep
        }
    )

    fun stateToIndex(dragon: Pos, sheep: Sheep): Int = indexer.indexOf(dragon to sheep)
    fun indexToState(index: Int): Pair<Pos, Sheep> = indexer.valueOf(index)
}

fun part3(data: String): Any {
    val (grid, chars) = parse(data)
    val board = Board(grid)
    var result = 0L
    val dragon = chars['D']!!.single()
    val sheep = board.sheepCols.map { c -> chars['S']!!.firstOrNull { it.col == c }?.row }
    var toCheck = LongArray(board.possibleStates).also { it[board.stateToIndex(dragon, sheep)] = 1 }

    while (toCheck.any { it > 0 }) {
        val (toCheckNext, finished) = round(toCheck, board)
        result += finished
        toCheck = toCheckNext
    }
    return result
}

private fun round(
    toCheck: LongArray,
    board: Board,
): Pair<LongArray, Long> {
    var finished = 0L
    val toCheckNext = LongArray(board.possibleStates)
    toCheck.forEachIndexed { index, count ->
        if (count > 0) {
            val (innerToCheck, won) = board.solveP3State(board.indexToState(index))
            innerToCheck.forEach { (d, s) -> toCheckNext[board.stateToIndex(d, s)] += count }
            finished += won * count
        }
    }
    return toCheckNext to finished
}

fun Board.solveP3State(state: Pair<Pos, Sheep>): Pair<List<Pair<Pos, Sheep>>, Long> {
    val (currDragon, currSheep) = state
    val innerToCheck = mutableListOf<Pair<Pos, Sheep>>()
    var won = 0L
    val possibleSheepMoves: List<Sheep> = singleSheepMoves(currDragon, currSheep)
    val possibleDragonMoves = dragonMovesCached(currDragon)
    possibleDragonMoves.forEach { nextDragon ->
        possibleSheepMoves.forEach { nextSheep ->
            val aliveSheep = nextSheep.mapIndexed { c, r ->
                when {
                    r != null && r to c == nextDragon && grid[r][c] != '#' -> null
                    else -> r
                }
            }
            if (aliveSheep.any { it != null }) innerToCheck += nextDragon to aliveSheep
            else won++
        }
    }
    return innerToCheck to won
}

private fun Board.singleSheepMoves(
    dragon: Pos,
    sheep: Sheep,
): List<Sheep> = buildList {
    sheep.forEachIndexed { c, r ->
        when {
            r == null -> {} // skip, no sheep
            r + 1 to c == dragon && grid[r + 1][c] != '#' -> {} // skip, illegal
            else -> add(sheep.mapIndexed { c1, r1 -> if (c1 == c) r + 1 else r1 })
        }
    }
}
    .ifEmpty { listOf(sheep) }
    .filter { sheep -> sheep.indices.all { c -> sheep[c] == null || sheep[c] in sheepRows[c] } }

class Indexer<T>(val bases: List<Int>, val ser: (T) -> IntArray, val deser: (IntArray) -> T) {
    val multipliers = bases
        .asReversed()
        .runningFold(1) { acc, b -> acc * b }.reversed().drop(1)

    fun indexOf(t: T): Int = ser(t).let { ints -> ints.indices.sumOf { ints[it] * multipliers[it] } }
    fun valueOf(index: Int): T {
        var rest = index
        val result = IntArray(bases.size) { c ->
            (rest / multipliers[c]).also { rest %= multipliers[c] }
        }
        return deser(result)
    }
}
