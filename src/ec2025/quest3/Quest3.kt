package ec2025.quest3

import go
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 2648) { part1(provideInput(year, quest, 1)) }
    go("part2", 278) { part2(provideInput(year, quest, 2)) }
    go("part3", 2277) { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val crates = data.split(",").map { it.toInt() }
    return crates.distinct().sum()
}

fun part2(data: String): Any {
    val crates = data.split(",").map { it.toInt() }
    return crates.distinct().sorted().take(20).sum()
}

fun part3(data: String): Any {
    val crates = data.split(",").map { it.toInt() }
    return crates.groupingBy { it }.eachCount().values.max()
}
