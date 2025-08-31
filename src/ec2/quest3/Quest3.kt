package ec2.quest3

import coords.e
import coords.n
import coords.pair.Pos
import coords.pair.col
import coords.pair.row
import coords.s
import coords.w
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
    var rollNumber: Int = 1
        private set

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

fun parseDice(data: String): List<Die> = data.lines().takeWhile { !it.isBlank() }.map { line ->
    val id = line.substringBefore(':').toInt()
    val faces = line.substringAfter('[').substringBefore(']').split(",").map(String::toInt)
    val seed = line.substringAfter("seed=").toLong()
    Die(id, faces, seed)
}

@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
data class Grid(val grid: List<List<Face>>) : List<List<Face>> by grid {
    operator fun get(pos: Pos) = grid.getOrNull(pos.row)?.getOrNull(pos.col)
    operator fun contains(pos: Pos) = pos.row in indices && pos.col in this[pos.row].indices

    private val connections: Map<Pos, Set<Pos>> by lazy {
        indices.flatMap { row -> this[row].indices.map { col -> Pos(row, col) } }
            .associateWith { pos -> listOf(pos, pos.n(), pos.e(), pos.s(), pos.w()).filter { it in this }.toSet() }
    }

    private val byFaces: Map<Face, Set<Pos>> by lazy {
        indices.flatMap { row -> this[row].indices.map { col -> Pos(row, col) } }
            .groupBy { this[it]!! }.mapValues { (_, v) -> v.toSet() }
    }

    fun placesWithFace(face: Face) = byFaces[face].orEmpty()

    fun possibleMoves(previous: Set<Pos>, face: Face): Set<Pos> =
        previous.flatMap { prevPos -> connections[prevPos].orEmpty() }.filter { this[it] == face }.toSet()

}

fun parseGrid(data: String): Grid = data.lines().dropWhile { !it.isBlank() }.filterNot { it.isBlank() }
    .map { line -> line.map { it.digitToInt() } }
    .let { Grid(it) }

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

fun part3(data: String): Any {
    val dice = parseDice(data)
    val grid = parseGrid(data)
    val takenCoins = grid.map { BooleanArray(it.size) }

    dice.forEach { die ->
        var toCheck = grid.placesWithFace(die.roll())
        while (toCheck.isNotEmpty()) {
            toCheck.forEach { (row, col) -> takenCoins[row][col] = true }
            toCheck = grid.possibleMoves(toCheck, die.roll())
        }
    }
    return takenCoins.sumOf { row -> row.count { it } }
}

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 617) { part1(provideInput(year, quest, 1)) }
    go("part2", "4,5,8,3,6,9,2,7,1") { part2(provideInput(year, quest, 2)) }
    go("part3", 157648) { part3(provideInput(year, quest, 3)) }
}
