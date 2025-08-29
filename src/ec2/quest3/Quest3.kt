package ec2.quest3

import go
import provideInput
import yearAndQuestFromPackage

data class Die(
    val id: Int,
    val faces: List<Int>,
    val seed: Long,
) {
    private var pulse: Long = seed
    private var selected: Int = 0

    fun roll(rollNumber: Int): Int {
        val spin = rollNumber * pulse
        selected = ((selected + spin) % faces.size).toInt()
        pulse += spin
        pulse %= seed
        pulse += rollNumber + 1 + seed
        return faces[selected]
    }
}

private fun parseDice(data: String): List<Die> = data.lines().takeWhile { !it.isBlank() }.map { line ->
    val id = line.substringBefore(':').toInt()
    val faces = line.substringAfter('[').substringBefore(']').split(",").map(String::toInt)
    val seed = line.substringAfter("seed=").toLong()
    Die(id, faces, seed)
}

fun part1(data: String): Any {
    val dice = parseDice(data)
    var rolls = 0
    var totalPoints = 0
    while (totalPoints < 10000) {
        rolls++
        dice.forEach { totalPoints += it.roll(rolls) }
    }
    return rolls
}

fun part2(data: String): Any {
    val dice = parseDice(data).associateBy { it.id }
    val track = data.lines().dropWhile { !it.isBlank() }.dropWhile { it.isBlank() }.first()
        .map { it.digitToInt() }
    val positions = dice.keys.associateWith { 0 }.toMutableMap()
    val result = mutableListOf<Int>()
    var rolls = 0
    while (positions.isNotEmpty()) {
        rolls++
        val finished = mutableSetOf<Int>()
        positions.keys.forEach { id ->
            val die = dice[id]!!
            val roll = die.roll(rolls)
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

fun part3(data: String): Any = TODO()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 617) { part1(provideInput(year, quest, 1)) }
    go("part2", "4,5,8,3,6,9,2,7,1") { part2(provideInput(year, quest, 2)) }
    go("part3", 21503122) { part3(provideInput(year, quest, 3)) }
}
