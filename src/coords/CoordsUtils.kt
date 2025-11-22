package coords

import coords.pair.Pos
import coords.pair.col
import coords.pair.row
import kotlin.math.abs

fun List<String>.findAll(ch: Char): Set<Pos> = flatMapIndexed { r, line ->
    line.mapIndexedNotNull { c, ch1 -> if (ch1 == ch) Pos(r, c) else null }
}.toSet()

fun List<String>.findAll(op: (Char) -> Boolean): Set<Pos> = flatMapIndexed { r, line ->
    line.mapIndexedNotNull { c, ch -> if (op(ch)) Pos(r, c) else null }
}.toSet()

operator fun List<String>.get(pos: Pos): Char? = getOrNull(pos.row)?.getOrNull(pos.col)

fun Pos.n(dist: Int = 1): Pos = Pos(row - dist, col)
fun Pos.ne(): Pos = Pos(row - 1, col + 1)
fun Pos.e(dist: Int = 1): Pos = Pos(row, col + dist)
fun Pos.se(): Pos = Pos(row + 1, col + 1)
fun Pos.s(dist: Int = 1): Pos = Pos(row + dist, col)
fun Pos.sw(): Pos = Pos(row + 1, col - 1)
fun Pos.w(dist: Int = 1): Pos = Pos(row, col - dist)
fun Pos.nw(): Pos = Pos(row - 1, col - 1)

enum class Direction {
    N, E, S, W;

    fun opposite(): Direction = when (this) {
        N -> S
        E -> W
        S -> N
        W -> E
    }

    fun turnRight(): Direction = when (this) {
        N -> E
        E -> S
        S -> W
        W -> N
    }

    fun turnLeft(): Direction = when (this) {
        N -> W
        E -> N
        S -> E
        W -> S
    }
}

fun Pos.move(dir: Direction, dist: Int = 1): Pos {
    return when (dir) {
        Direction.N -> n(dist)
        Direction.E -> e(dist)
        Direction.S -> s(dist)
        Direction.W -> w(dist)
    }
}

fun Pos.manhattanDistance(other: Pos) = abs(row - other.row) + abs(col - other.col)

