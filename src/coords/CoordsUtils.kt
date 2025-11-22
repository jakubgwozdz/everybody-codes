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

fun Pos.n(): Pos = Pos(row - 1, col)
fun Pos.ne(): Pos = Pos(row - 1, col + 1)
fun Pos.e(): Pos = Pos(row, col + 1)
fun Pos.se(): Pos = Pos(row + 1, col + 1)
fun Pos.s(): Pos = Pos(row + 1, col)
fun Pos.sw(): Pos = Pos(row + 1, col - 1)
fun Pos.w(): Pos = Pos(row, col - 1)
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

fun Pos.move(dir: Direction): Pos {
    return when (dir) {
        Direction.N -> n()
        Direction.E -> e()
        Direction.S -> s()
        Direction.W -> w()
    }
}

fun Pos.manhattanDistance(other: Pos) = abs(row - other.row) + abs(col - other.col)

