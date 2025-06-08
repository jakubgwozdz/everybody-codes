package ec2024.quest12

import coords.findAll
import coords.pair.Pos
import coords.pair.col
import coords.pair.row
import go
import provideInput
import yearAndQuestFromPackage

val example1 = """
    .............
    .C...........
    .B......T....
    .A......T.T..
    =============
""".trimIndent()

val example3 = """
    6 5
    6 7
    10 5
""".trimIndent()

val example3a = """
    5 5
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("example1", 13) { part1(example1) }
    go("part1", 220) { part1(provideInput(year, quest, 1)) }
    go("part2", 21723) { part2(provideInput(year, quest, 2)) }
    go("example3", 11) { part3(example3) }
    go("example3a", 2) { part3(example3a) }
    go("part3", 719559) { part3(provideInput(year, quest, 3)) }
}

//112 bad
fun part1(data: String): Any {
    val lines = data.lines()
    val targets = lines.findAll('T')
    val ruin = lines.findAll('H')
    val a = lines.findAll('A').single()
    val b = lines.findAll('B').single()
    val c = lines.findAll('C').single()

    return targets.sumOf { target -> calcForTarget(target, a, b, c) } +
            ruin.sumOf { target -> calcForTarget(target, a, b, c) } * 2
}

private fun calcForTarget(target: Pos, a: Pos, b: Pos, c: Pos): Int {
    val (row, col) = target
    val distA = col - (row - a.row) - a.col
    val distB = col - (row - b.row) - b.col
    val distC = col - (row - c.row) - c.col
    return if (distA % 3 == 0) distA / 3
    else if (distB % 3 == 0) distB / 3 * 2
    else distC
}

fun part2(data: String): Any = part1(data)

enum class Phase { UP, FLAT, DOWN }
data class Hit(val tower: Int, val time: Int, val power: Int, val phase: Phase, val score: Int = power * tower)

fun part3(data: String): Any {
    val a = Pos(0, 0)
    val b = Pos(-1, 0)
    val c = Pos(-2, 0)

    val meteors = data.lines().map { it.split(" ").map { it.toInt() } }
        .map { (v1, v2) -> Pos(v2, v1) } // normal positions
        .sortedBy { it.col } // sort by time
        .map { p -> if (p.col % 2 == 0) p else Pos(p.row - 1, p.col - 1) } // wait for even time
        .map { (row, col) -> Pos(row - col / 2, col / 2) } // hit at the center

    return meteors.sumOf { pos ->
        val t = pos.col // / 2
        val hits = mutableMapOf<Pos, Hit>()
        (t / 3 - 2..t).forEach { power ->
            val phase = when {
                t <= power -> Phase.UP
                t <= power * 2 -> Phase.FLAT
                else -> Phase.DOWN
            }
//            val r = if (t <= power) t else if (t <= power * 2) power else power - (t - power * 2)
            val r = when(phase) {
                Phase.UP -> t
                Phase.FLAT -> power
                Phase.DOWN -> power - (t - power * 2)
            }
            val c = t
            val hitA = Hit(1, t, power, phase)
            val hitB = Hit(2, t, power, phase)
            val hitC = Hit(3, t, power, phase)
            val posA = Pos(r, c)
            val posB = Pos(r + 1, c)
            val posC = Pos(r + 2, c)
            hits.merge(posA, hitA) { old, new -> if (new.score < old.score) new else old }
            hits.merge(posB, hitB) { old, new -> if (new.score < old.score) new else old }
            hits.merge(posC, hitC) { old, new -> if (new.score < old.score) new else old }
        }
        hits[pos]!!
            .also { println("$pos, diagonal $it") }
            .score
    }
}
