package ec2024.quest11

import go
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 31) { part1(provideInput(year, quest, 1)) }
    go("part2", 249722) { part2(provideInput(year, quest, 2)) }
    go("part3", 814904241744) { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any = population(parse(data), "A", 4)

private fun population(rules: Map<String, List<String>>, start: String, steps: Int): Long =
    generateSequence(mapOf(start to 1L)) {
        val result = mutableMapOf<String, Long>()
        it.forEach { (id, count) -> rules[id]!!.forEach { result[it] = result.getOrDefault(it, 0) + count } }
        result.toMap()
    }.drop(steps).first().values.sum()

fun part2(data: String): Any = population(parse(data), "Z", 10)


fun part3(data: String): Any {
    val rules = parse(data)
    return rules.mapValues { (k, v) -> population(rules, k, 20) }.values.sorted().let {
        it.last() - it.first()
    }
}

private fun parse(data: String): Map<String, List<String>> =
    data.lines().map { it.split(":") }.associate { (k, v) -> k to v.split(",") }

