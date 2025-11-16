package ec2025.quest7

import collections.increment
import go
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", "Urardith") { part1(provideInput(year, quest, 1)) }
    go("part2", 2980) { part2(provideInput(year, quest, 2)) }
    go("part3", 9007938) { part3(provideInput(year, quest, 3)) }
}

private fun parseInputData(data: String): Pair<List<String>, Map<Char, List<Char>>> {
    val names = data.substringBefore("\n").trim().split(",")
    val rules = data.substringAfter("\n\n").lines()
        .associate { line -> line.split(" > ").let { (k, v) -> k[0] to v.split(",").map { it[0] } } }
    return names to rules
}

private fun matchesRules(name: String, rules: Map<Char, List<Char>>) =
    name.windowed(2).all { rules[it[0]].orEmpty().contains(it[1]) }

fun part1(data: String): Any {
    val (names, rules) = parseInputData(data)
    return names.first { name -> matchesRules(name, rules) }
}

fun part2(data: String): Any {
    val (names, rules) = parseInputData(data)
    return names.withIndex()
        .filter { (_, name) -> matchesRules(name, rules) }
        .sumOf { (i, _) -> i + 1 }
}

fun part3(data: String): Any {
    val (names, rules) = parseInputData(data)
    val distinctNames = names.filter { matchesRules(it, rules) }.distinctByPrefix()

    val cases = mutableMapOf<Int, MutableMap<Char, Int>>()

    distinctNames.forEach { name -> cases.increment(name.length, name.last()) }
    (0..10).forEach { len ->
        val thisCases = cases[len]
        val nextCases = cases.getOrPut(len + 1) { mutableMapOf() }
        thisCases?.forEach { (ch, count) -> rules[ch]?.forEach { nextCases.increment(it, count) } }
//        println("len ${len + 1}: ${nextCases.toSortedMap()}")
    }
    return (7..11).sumOf { len -> cases[len]?.values?.sum() ?: 0 }
}

private fun List<String>.distinctByPrefix() = buildList {
    this@distinctByPrefix.sorted().forEach { next ->
        if (isEmpty()) add(next)
        if (!next.startsWith(this.last())) add(next)
    }
}
