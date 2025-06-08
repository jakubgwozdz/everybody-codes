package ec2024.quest19

import coords.e
import coords.get
import coords.n
import coords.ne
import coords.nw
import coords.pair.Pos
import coords.pair.col
import coords.pair.row
import coords.s
import coords.se
import coords.sw
import coords.w
import go
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 2911616634265598) { part1(provideInput(year, quest, 1)) }
    go("part2", 9399283282399685) { part2(provideInput(year, quest, 2)) }
    go("part3", 2696818816561532) { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any = solve(data, 1)
fun part2(data: String): Any = solve(data, 100)
fun part3(data: String): Any = solve(data, 1048576000)

fun solve(data: String, rounds: Int): String {
    val input = parse(data)
    val width = input.grid.first().length
    val height = input.grid.size
    val mapping = buildMapping(width, height, input.key)
    val allPositions: List<Pos> = (0..<width).flatMap { col -> (0..<height).map { row -> Pos(row, col) } }
    val identity = allPositions.associate { it to it }

    val bits = rounds.toString(2).reversed().map { it == '1' }
    val powers = mutableListOf(mapping)
    (1..<bits.size).forEach { i ->
        val prev = powers.last()
        val next = prev.mapValues { (_, v) -> prev[v]!! }
        powers.add(next)
    }

    val totalMapping = bits
        .mapIndexedNotNull { i, bit -> if (bit) powers[i] else null }
        .fold(identity) { acc, map -> acc.mapValues { (_, v) -> map[v]!! } }

    val grid = input.grid.mapIndexed { row, line ->
        Array(line.length) { col -> input.grid[totalMapping[Pos(row, col)]!!]!! }
    }

    grid.forEach { println(it.joinToString("")) }

    return grid.map { it.joinToString("") }.single { '>' in it && '<' in it }
        .substringAfter('>').substringBefore('<')
}

private fun buildMapping(width: Int, height: Int, key: String): Map<Pos, Pos> {
    val grid = List(height) { row -> Array(width) { col -> Pos(row, col) } }
    fun nextDir(row: Int, col: Int) = key[((row - 1) * (width - 2) + (col - 1)) % key.length]
    (1..<grid.lastIndex).forEach { row ->
        (1..<grid[row].lastIndex).forEach { col ->
            val center = Pos(row, col)
            val positions = listOf(
                center.n(),
                center.ne(),
                center.e(),
                center.se(),
                center.s(),
                center.sw(),
                center.w(),
                center.nw(),
            )
            val dir = nextDir(row, col)
            val rotated = when (dir) {
                'R' -> positions.drop(1) + positions.first()
                'L' -> listOf(positions.last()) + positions.dropLast(1)
                else -> error("Invalid direction: $dir")
            }
            val values = positions.map { grid[it.row][it.col] }
            rotated.zip(values).forEach { (pos, value) ->
                grid[pos.row][pos.col] = value
            }
        }
    }
    val allPositions: List<Pos> = (0..<width).flatMap { col -> (0..<height).map { row -> Pos(row, col) } }
    return allPositions.associateWith { grid[it.row][it.col] }
}

data class Input(val key: String, val grid: List<String>)

private fun parse(data: String): Input {
    val key = data.lines().first()
    val grid = data.lines().drop(2)
    return Input(key, grid)
}
