package ec3.quest1

import go
import provideInput
import yearAndQuestFromPackage

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 49328) { part1(provideInput(year, quest, 1)) }
    go("part2", 44498) { part2(provideInput(year, quest, 2)) }
    go("part3", 10341866) { part3(provideInput(year, quest, 3)) }
}

private fun parse(input: String) = input.reader().readLines().map { it.split(":") }
    .associate { (id, color) ->
        id.toInt() to color
            .split(" ")
            .map { it.fold(0) { acc, c -> if (c.isUpperCase()) acc * 2 + 1 else acc * 2 } }
    }


fun part1(input: String) = parse(input)
    .filterValues { color -> color[1] > color[0] && color[1] > color[2] }
    .keys.sum()

fun part2(input: String): Int {
    val scales = parse(input)
    return scales.keys.sortedWith(
        compareByDescending<Int> { scales[it]!![3] }
            .thenBy { scales[it]!![0] + scales[it]!![1] + scales[it]!![2] }
    )
        .first()
}

fun part3(input: String) = parse(input)
    .mapValues { (_, color) ->
        val shine = when {
            color[3] >= 33 -> "shiny"
            color[3] <= 30 -> "matte"
            else -> null
        }
        val color = when {
            color[0] > color[1] && color[0] > color[2] -> "red"
            color[1] > color[0] && color[1] > color[2] -> "green"
            color[2] > color[0] && color[2] > color[1] -> "blue"
            else -> null
        }
        if (shine != null && color != null) "$color-$shine" else "---"
    }
    .filterValues { it != "---" }
    .toList()
    .groupBy { (_, dominant) -> dominant }
    .maxBy { (dominant, list) -> list.size }
    .value.sumOf { (id, _) -> id }
