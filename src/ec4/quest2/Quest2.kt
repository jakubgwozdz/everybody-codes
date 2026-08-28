package ec4.quest2

import coords.e
import coords.n
import coords.pair.Pos
import coords.s
import coords.w
import go
import provideInput
import yearAndQuestFromPackage

val ex1 = """
    START=[5,0]
    A=[0,0]
    B=[10,0]
    C=[5,10]
    MOVES=ABCCBABCA
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1ex", 8) { part1(ex1) }
    go("part1") { part1(provideInput(year, quest, 1)) }
    go("part2ex", 25) { part2(ex1) }
    go("part2") { part2(provideInput(year, quest, 2)) }
    go("part3ex", 42) { part3(ex1) }
    go("part3", 17620) { part3(provideInput(year, quest, 3)) }
}

data class Input(
    val start: Pos,
    val a: Pos,
    val b: Pos,
    val c: Pos,
    val moves: Iterable<Char>,
)

fun String.toPos() = substringAfter('[').substringBefore(']').split(",").let { (a, b) ->
    Pos(a.toInt(), b.toInt())
}

fun parse(data: String): Input = data.lines().associate {
    val (k, v) = it.split('=')
    k to v
}.let {
    Input(
        it["START"]!!.toPos(),
        it["A"]!!.toPos(),
        it["B"]!!.toPos(),
        it["C"]!!.toPos(),
        it["MOVES"].orEmpty().toList()
    )
}

fun Pos.halfway(o: Pos) = Pos((first + o.first) / 2, (second + o.second) / 2)

fun part1(data: String): Any = illuminate(parse(data)).size

private fun illuminate(input: Input): MutableSet<Pos> {
    val illuminated = mutableSetOf(input.start)
    var swarm = input.start
    input.moves.forEach {
        val beacon = when (it) {
            'A' -> input.a
            'B' -> input.b
            'C' -> input.c
            else -> error("Unexpected character: $it")
        }
        swarm = swarm.halfway(beacon)
        illuminated += swarm
    }
    return illuminated
}

fun part2(data: String): Any = addFireflies(illuminate(parse(data))).size

private fun addFireflies(beetles: MutableSet<Pos>): Set<Pos> {
    val firelies = mutableSetOf<Pos>()
    beetles.forEach { p ->
        firelies += p.n()
        firelies += p.s()
        firelies += p.e()
        firelies += p.w()
    }
    firelies -= beetles
    return firelies
}

fun part3(data: String): Any {
    val input = parse(data)
    val illuminated = mutableSetOf(input.start)
    val toGo = mutableListOf(input.start)
    val beacons = mutableListOf(input.a, input.b, input.c)
    while (toGo.isNotEmpty()) {
        val next = toGo.removeLast()
        beacons.map { next.halfway(it) }
            .filter { it !in illuminated }
            .forEach {
                toGo += it
                illuminated += it
            }
    }

    return addFireflies(illuminated).size
}
