package ec2026.quest5

import coords.e
import coords.get
import coords.n
import coords.ne
import coords.nw
import coords.pair.Pos
import coords.s
import coords.se
import coords.sw
import coords.w
import go
import provideInput
import yearAndQuestFromPackage

val ex1 = """
    010100100100001101000
    111101001000001110000
    101111011000101010011
    001000101010000100000
    000110100110011001100
    000001110111111000000
    101100010101011110000
    001101100000001010011
    001001001111010001001
    100011011110011010101
    001100101110010010000
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1ex", 137) { part1(ex1) }
    go("part1", 1244) { part1(provideInput(year, quest, 1)) }
    go("part2ex", 69) { part2(ex1) }
    go("part2", 1038) { part2(provideInput(year, quest, 2)) }
    go("part3ex", 14) { part3(ex1) }
    go("part3", 9439) { part3(provideInput(year, quest, 3)) }
}

fun Pos.adjacent() = sequenceOf(n(), ne(), e(), se(), s(), sw(), w(), nw())
fun Pos.treeNeighbour() = sequenceOf(
    n(2).e(), s(2).e(), w(2).n(), e(2).n(),
    n(2).w(), s(2).w(), w(2).s(), e(2).s(),
    n(2), s(2), w(2), e(2),
//    n(), s(), w(), e(),
//    ne(), se(), nw(), sw()
)

fun is1(map: List<String>, p: Pos) = map.getOrNull(p.first)?.getOrNull(p.second) == '1'

fun part1(data: String) = findLand(data.lines()).size

fun part2(data: String) = findBeach(findLand(data.lines())).size

fun part3(data: String): Any {
    val map = data.lines()
//    draw(map)
    val land = findLand(map)
//    draw(map, land)
    val beach = findBeach(land)
//    draw(map, land, beach)
    val trees = findForest(land - beach).toMutableSet()
//    draw(map, land, beach, trees)
    repeat(2) {
        trees += growForest(trees, beach)
//        draw(map, land, beach)
    }
    return trees.size
}

internal fun growForest(
    trees: Set<Pos>,
    beach: Set<Pos>,
) = buildSet { trees.forEach { addAll(it.adjacent()) } }
    .filter { l -> l.adjacent().none { it in beach } && l.adjacent().count { it in trees } >= 4 }

internal fun findForest(grass: Set<Pos>) = grass.filter { l -> l.treeNeighbour().all { it in grass } }.toSet()

internal fun findLand(map: List<String>, center: Pos = findCenter(map)): Set<Pos> {
    val queue = mutableListOf(center)
    val land = mutableSetOf<Pos>()
    while (queue.isNotEmpty()) {
        val next = queue.removeFirst()
        next.adjacent().filter { it !in land }
            .filter { it.adjacent().count { p -> p in land || is1(map, p) } >= 5 }
            .forEach { queue += it; land += it }
    }
    return land.toSet()
}

internal fun findCenter(map: List<String>): Pair<Int, Int> = (map.size / 2 + 1) to (map.first().length / 2 + 1)

internal fun findBeach(land: Set<Pos>): Set<Pos> {
    val queue = land.filter { it.adjacent().any { n -> n !in land } }.toMutableList()
    val beach = queue.toMutableSet()
    while (queue.isNotEmpty()) {
        val next = queue.removeFirst()
        next.adjacent().filter { it in land && it !in beach }
            .filter { it.adjacent().count { p -> p in beach } >= 5 }
            .forEach { queue += it; beach += it }
    }
    return beach.toSet()
}


private fun draw(
    map: List<String>,
    land: Set<Pos> = emptySet(),
    beach: Set<Pos> = emptySet(),
    forest: Set<Pos> = emptySet(),
) = map.forEachIndexed { r, line ->
    line.indices.forEach { c ->
        val pos = r to c
        val ch = when {
            pos in forest -> '@'
            pos in beach -> '='
            pos in land -> '#'
            map[pos] == '1' -> '^'
            else -> '~'
        }
        print(ch)
    }
    println()
}
    .also { println() }
