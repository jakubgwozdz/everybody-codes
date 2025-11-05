package ec2025.quest1

import go
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", "Rylardravor") { part1(provideInput(year, quest, 1)) }
    go("part2", "Fyrdravor") { part2(provideInput(year, quest, 2)) }
    go("part3", "Quirquor") { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val (names, _, moves) = data.lines().map { it.split(",") }
    val pos = moves.map { it.toMove() }
        .fold(0) { acc, c -> (acc + c).coerceIn(names.indices) }

    return names[pos]
}

fun part2(data: String): Any {
    val (names, _, moves) = data.lines().map { it.split(",") }
    val pos = moves.map { it.toMove() }
        .fold(0) { acc, c -> (acc + c) }.mod(names.size)

    return names[pos]
}

fun part3(data: String): Any {
    val (names, _, moves) = data.lines().map { it.split(",").toMutableList() }
    moves.map { it.toMove().mod(names.size) }.forEach { i->
        val t = names[0]
        names[0] = names[i]
        names[i] = t
    }
    return names[0]
}

private fun String.toMove(): Int {
    val c = this.drop(1).toInt()
    return if (startsWith('L')) -c else c
}

