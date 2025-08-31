package ec2.quest3

import coords.pair.Pos
import coords.pair.col
import coords.pair.row
import go
import provideInput
import yearAndQuestFromPackage

typealias Face = Int

data class Die(
    val id: Int,
    val faces: List<Face>,
    val seed: Long,
) {
    private var pulse: Long = seed
    private var selected: Int = 0
    private var rollNumber: Int = 1

    fun roll(): Face {
        val spin = rollNumber * pulse
        selected = ((selected + spin) % faces.size).toInt()
        pulse += spin
        pulse %= seed
        pulse += rollNumber + 1 + seed
        rollNumber++
        return faces[selected]
    }
}

private fun parseDice(data: String): List<Die> = data.lines().takeWhile { !it.isBlank() }.map { line ->
    val id = line.substringBefore(':').toInt()
    val faces = line.substringAfter('[').substringBefore(']').split(",").map(String::toInt)
    val seed = line.substringAfter("seed=").toLong()
    Die(id, faces, seed)
}

typealias Grid = List<List<Face>>

private fun parseGrid(data: String): Grid =
    data.lines().dropWhile { !it.isBlank() }.filterNot { it.isBlank() }.map { line ->
        line.map { it.digitToInt() }
    }

fun part1(data: String): Any {
    val dice = parseDice(data)
    var rolls = 0
    var totalPoints = 0
    while (totalPoints < 10000) {
        rolls++
        dice.forEach { totalPoints += it.roll() }
    }
    return rolls
}

fun part2(data: String): Any {
    val dice = parseDice(data).associateBy { it.id }
    val track = parseGrid(data).single()
    val positions = dice.keys.associateWith { 0 }.toMutableMap()
    val result = mutableListOf<Int>()
    while (positions.isNotEmpty()) {
        val finished = mutableSetOf<Int>()
        positions.keys.forEach { id ->
            val die = dice[id]!!
            val roll = die.roll()
            val pos = positions[id]!!
            if (roll == track[pos]) {
                positions[id] = pos + 1
                if (pos == track.lastIndex) finished += id
            }
        }
        finished.forEach {
            result += it
            positions -= it
        }
    }

    return result.joinToString(",")
}

operator fun Grid.get(pos: Pos) = this.getOrNull(pos.row)?.getOrNull(pos.col)
operator fun Grid.contains(pos: Pos) = pos.row in this.indices && pos.col in this[pos.row].indices

fun Grid.positions(): Sequence<Pair<Pos, Face>> = sequence {
    forEachIndexed { row, line ->
        line.forEachIndexed { col, value ->
            yield(Pos(row, col) to value)
        }
    }
}

fun part3(data: String): Any {
    val dice = parseDice(data)
    val grid = parseGrid(data)
    val connections = buildMap {
        grid.positions().forEach { (pos, _) ->
            val (row, col) = pos
            this[pos] = listOf(
                pos,
                Pos(row - 1, col),
                Pos(row + 1, col),
                Pos(row, col - 1),
                Pos(row, col + 1),
            )
                .filter { it in grid }
                .map { it to grid[it]!! }
        }
    }

    fun possibleMoves(previous: Set<Pos>, face: Face) = buildSet {
        previous.flatMap { prevPos -> connections[prevPos].orEmpty() }
            .forEach { (nextPos, nextFace) -> if (face == nextFace) add(nextPos) }
    }

    val startsForDigits: Map<Face, Set<Pos>> = grid.positions().groupBy { (_, face) -> face }
        .mapValues { (_, v) -> v.map { (pos, _) -> pos }.toSet() }

    val takenCoins = mutableSetOf<Pos>()

    dice.forEach { die ->
        var toCheck = startsForDigits[die.roll()].orEmpty()
        while (toCheck.isNotEmpty()) {
            toCheck.forEach { takenCoins += it }
            toCheck = possibleMoves(toCheck, die.roll())
        }
    }

    return takenCoins.size
}

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 617) { part1(provideInput(year, quest, 1)) }
    go("part2", "4,5,8,3,6,9,2,7,1") { part2(provideInput(year, quest, 2)) }
    go("part3", 157648) { part3(provideInput(year, quest, 3)) }
}
