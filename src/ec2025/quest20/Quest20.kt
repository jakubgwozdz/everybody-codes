package ec2025.quest20

import coords.findAll
import coords.pair.Pos
import coords.pair.col
import coords.pair.row
import go
import provideInput
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
    go("part2") { part2(provideInput(year, quest, 2)) }
//    go("part3") { part3(provideInput(year, quest, 3)) }
}

fun Pos.neighbours() = when (row % 2) {
    0 -> listOf(Pos(row - 1, col), Pos(row + 1, col - 1), Pos(row + 1, col))
    else -> listOf(Pos(row - 1, col), Pos(row - 1, col + 1), Pos(row + 1, col))
}

fun part1(data: String): Any {
    val skewed = data.lines().mapIndexed { i, line -> line.drop(i).dropLast(i) }
        .flatMap { l ->
            mutableListOf(StringBuilder(), StringBuilder()).also {
                l.forEachIndexed { j, c -> it[j % 2].append(c) }
            }
        }.map { it.toString() }
    return skewed.indices.sumOf { r ->
        when (r % 2) {
            0 -> skewed[r].indices.count { c -> skewed[r][c] == 'T' && skewed.getOrNull(r + 1)?.getOrNull(c) == 'T' } +
                    skewed[r].indices.count { c ->
                        skewed[r][c] == 'T' && skewed.getOrNull(r + 1)?.getOrNull(c - 1) == 'T'
                    }
            else -> skewed[r].indices.count { c -> skewed[r][c] == 'T' && skewed.getOrNull(r + 1)?.getOrNull(c) == 'T' }
        }
    }
}

fun part2(data: String): Any {
    return data
}

fun part3(data: String): Any {
    return data
}
