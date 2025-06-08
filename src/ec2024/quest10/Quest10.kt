package ec2024.quest10

import coords.pair.Pos
import coords.pair.col
import coords.pair.row
import go
import provideInput
import yearAndQuestFromPackage

val example1 = """
    **PCBS**
    **RLNW**
    BV....PT
    CR....HZ
    FL....JW
    SG....MN
    **FTZV**
    **GMJH**
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("example1", "PTBVRCZHFLJWGMNS") { part1(example1) }
    go("part1", "TWGLSMHXBRVQCKNP") { part1(provideInput(year, quest, 1)) }
    go("part2", 198780) { part2(provideInput(year, quest, 2)) }
    go("part3", 212905) { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any = parse(data, 9).map { (grid, pos) -> grid.word(pos) }.single()
fun part2(data: String): Any = parse(data, 9).map { (grid, pos) -> grid.word(pos) }.sumOf { it.power() }
fun part3(data: String): Any = parse(data, 6).let { blocks ->
    generateSequence { blocks.map { (grid, pos) -> grid.word(pos) } }
        .zipWithNext().first { (a, b) -> a == b }.first
        .sumOf { it.power() }
}

typealias Grid = List<CharArray>

private fun parse(data: String, step: Int): List<Pair<Grid, Pos>> = data.lines().map { it.toCharArray() }.let { grid ->
    grid.indices.windowed(8, step).map { it.first() }.flatMap { row ->
        grid[row].indices.windowed(8, step).map { it.first() }.map { col ->
            grid to Pos(row, col)
        }
    }
}

private fun Grid.word(pos: Pos): String {
    inner(pos.row).forEach { row ->
        inner(pos.col).forEach { col ->
            if (this[row][col] == '.') {
                val outerRow = outer(pos.row).map { it to this[it][col] }
                val outerCol = outer(pos.col).map { it to this[row][it] }
                val intersect = outerRow.map { it.second } intersect outerCol.map { it.second }
                if (intersect.size == 1 && intersect.single() != '?') this[row][col] = intersect.single()
                else {
                    val innerRow = inner(pos.row).map { this[it][col] }.filter { it != '.' }
                    val innerCol = inner(pos.col).map { this[row][it] }.filter { it != '.' }
                    if (innerRow.size == 3 && innerCol.size == 3) {
                        val unusedFromRow = outerRow.singleOrNull { it.second !in innerRow }
                        val unusedFromCol = outerCol.singleOrNull { it.second !in innerCol }
                        if (unusedFromRow?.second == '?' && unusedFromCol != null && unusedFromCol.second != '?') {
                            this[row][col] = unusedFromCol.second
                            this[unusedFromRow.first][col] = unusedFromCol.second
                        }
                        if (unusedFromRow != null && unusedFromRow.second != '?' && unusedFromCol?.second == '?') {
                            this[row][col] = unusedFromRow.second
                            this[row][unusedFromCol.first] = unusedFromRow.second
                        }
                    }
                }
            }
        }
    }
    return buildString {
        inner(pos.row).forEach { row -> inner(pos.col).forEach { col -> append(this@word[row][col]) } }
    }
}

private fun inner(i: Int) = (2 + i..5 + i)
private fun outer(i: Int) = listOf(i, i + 1, i + 6, i + 7)

private fun String.power(): Int = if ('.' in this) 0 else mapIndexed { i, c -> (c - 'A' + 1) * (i + 1) }.sum()

