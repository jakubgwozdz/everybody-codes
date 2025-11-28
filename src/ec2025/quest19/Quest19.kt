package ec2025.quest19

import go
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 57) { part1(provideInput(year, quest, 1)) }
    go("part2", 699) { part2(provideInput(year, quest, 2)) }
    go("part3", 4850006) { part3(provideInput(year, quest, 3)) }
}

// 56 first correct
fun part1(data: String): Any {
    val walls = data.lines().map { it.split(",").map(String::toInt) }
        .groupBy { (d, _, _) -> d }.mapValues { it.value.minBy { (_, h, _) -> h } }.map { it.value }
    return walls.maxOf { (d, h, _) -> (d + 1 - h) / 2 + h }
}

fun part2(data: String) = part1(data)

fun part3(data: String) = part1(data)
