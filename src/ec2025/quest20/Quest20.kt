package ec2025.quest20

import collections.PriorityQueue
import coords.findAll
import coords.get
import coords.pair.Pos
import coords.pair.col
import coords.pair.row
import go
import logged
import provideInput
import search.dijkstra
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    val part1ex1 = """
        T#TTT###T##
        .##TT#TT##.
        ..T###T#T..
        ...##TT#...
        ....T##....
        .....#.....
    """.trimIndent()
    go("part1ex1", 7) { part1(part1ex1) }
    go("part1", 128) { part1(provideInput(year, quest, 1)) }
    go("part2", 571) { part2(provideInput(year, quest, 2)) }
    val part3ex1 = """
        T####T#TTT##T##T#T#
        .T#####TTTT##TTT##.
        ..TTTT#T###TTTT#T..
        ...T#TTT#ETTTT##...
        ....#TT##T#T##T....
        .....#TT####T#.....
        ......T#TT#T#......
        .......T#TTT.......
        ........TT#........
        .........S.........
    """.trimIndent()
    go("part3ex1", 23) { part3(part3ex1) }
    go("part3") { part3(provideInput(year, quest, 3)) }
}

fun Pos.neighbours() = when (row % 2) {
    0 -> listOf(Pos(row - 1, col), Pos(row + 1, col - 1), Pos(row + 1, col))
    else -> listOf(Pos(row - 1, col), Pos(row - 1, col + 1), Pos(row + 1, col))
}

private fun skew(data: String): List<String> = data.lines().mapIndexed { i, line -> line.drop(i).dropLast(i) }
    .flatMap { l ->
        mutableListOf(StringBuilder(), StringBuilder()).also {
            l.forEachIndexed { j, c -> it[j % 2].append(c) }
        }
    }.map { it.toString() }


fun part1(data: String): Any {
    val skewed = skew(data)
    return skewed.indices.sumOf { r ->
        when (r % 2) {
            0 -> skewed[r].indices.sumOf { c ->
                if (skewed[r][c] == 'T') Pos(r, c).neighbours().count { skewed[it] == 'T' } else 0
            }
            else -> 0
        }
    }
}

fun part2(data: String): Any {
    val skewed = skew(data)
    val start = skewed.findAll('S').single()
    val end = skewed.findAll('E').single()
    val places = skewed.findAll('T') + end

    return dijkstra(
        start,
        endPredicate = { p: Pos -> p == end },
        neighbours = { p: Pos -> p.neighbours().filter(places::contains).map { it to 1 } }
    )
}

fun part3(data: String): Any {
    val skewed = skew(data)
    val start = skewed.findAll('S').single()
    val end = skewed.findAll('E').single()
    val places = skewed.findAll('T') + end

    val size = skewed.size.logged("size")
    require(skewed.first().length == size)

    return dijkstra(
        start,
        endPredicate = { p: Pos -> p == end },
        neighbours = { p: Pos -> p.neighbours().filter(places::contains).map { it to 1 } }
    )
}
