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

fun part1(data: String) = data.lines().map { it.split(",").map(String::toInt) } // parse
    .groupingBy { (d, _, _) -> d }
    .reduce { _, gap1, gap2 -> if (gap1[1] < gap2[1]) gap1 else gap2 }  // select lowest in multi-gap
    .values
//    .groupBy { (d, _, _) -> d }.values.map { it.minBy { (_, h, _) -> h } } // select lowest in multi-gap
    .maxOf { (d, h, _) -> (d + 1 - h) / 2 + h } // iterate through each and get minimal

fun part2(data: String) = part1(data)

fun part3(data: String) = part1(data)
