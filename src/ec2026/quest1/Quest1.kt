package ec2026.quest1

import go
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 1317) { part1(provideInput(year, quest, 1)) }
    go("part2", 314) { part2(provideInput(year, quest, 2)) }
    go("part3", 673269) { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any = data.split(",").map(String::toInt).sumOf { i ->
    generateSequence(1) { it + 1 }.map { it * it - i }.first { it >= 0 }
}

// 22 incorrect
fun part2(data: String) = data.split(",").map(String::toInt).sumOf { i ->
    generateSequence(3) { it + 1 }.map { (it - 1) * 4 - i }.first { it >= 0 }
}

fun part3(data: String) = data.split(",").map(String::toInt).sumOf { i ->
    generateSequence(3) { it + 2 }.map { it * it - 1 - i }.first { it >= 0 }
}
