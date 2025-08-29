package ec2.quest3

import go
import provideInput
import yearAndQuestFromPackage

data class Die(
    val id: Int,
    val faces: List<Int>,
    val seed: Int,
    var pulse: Int = seed,
    var face: Int = 0,
)

private fun parseDice(data: String): List<Die> = data.lines().takeWhile { !it.isBlank() }.map { line ->
    val id = line.substringBefore(':').toInt()
    val faces = line.substringAfter('[').substringBefore(']').split(",").map(String::toInt)
    val seed = line.substringAfter("seed=").toInt()
    Die(id, faces, seed)
}

fun part1(data: String): Any {
    val dice = parseDice(data)
    var rolls = 0
    var totalPoints = 0
    val pulses = IntArray(dice.size) { dice[it].seed }
    val faces = IntArray(dice.size) // { dice[it].faces.first() }
    while (totalPoints < 10000) {
        rolls++
        dice.forEachIndexed { index, die ->
            val spin = rolls * pulses[index]
            faces[index] = (faces[index] + spin) % die.faces.size
            pulses[index] += spin
            pulses[index] %= die.seed
            pulses[index] += rolls + 1 + die.seed
            totalPoints += die.faces[faces[index]]
        }
    }
    return rolls
}

fun part2(data: String): Any {
    val dice = parseDice(data)
    val track = data.lines().dropWhile { !it.isBlank() }.dropWhile { it.isBlank() }.first()
    val players = IntArray(dice.size)
    TODO()
}

fun part3(data: String): Any = TODO()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 617) { part1(provideInput(year, quest, 1)) }
    go("part2", 21305) { part2(provideInput(year, quest, 2)) }
    go("part3", 21503122) { part3(provideInput(year, quest, 3)) }
}
