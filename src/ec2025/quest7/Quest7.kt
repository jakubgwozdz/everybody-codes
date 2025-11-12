package ec2025.quest7

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

typealias Case = Pair<Pair<Char, Int>, Int> // ((ch, len), count)

val Case.ch get() = first.first
val Case.len get() = first.second
val Case.count get() = second

fun part3(data: String): Any {
    val (names, rules) = parseInputData(data)
    var cases: List<Case> = names.filter { matchesRules(it, rules) }
        .distinctByPrefix()
        .map { it.last() to it.length }.groupingBy { it }.eachCount()
        .toList()
    (1..5).forEach { i -> cases = cases.expand(i, rules) }
    check(cases.all { it.len == 6 }) // just to make sure
    return (6..10).sumOf { i ->
        cases = cases.expand(i, rules)
        cases.sumOf { it.count }
    }
}

private fun List<String>.distinctByPrefix(): Set<String> = buildSet {
    this@distinctByPrefix.forEach { next ->
        val prev = firstOrNull { e: String -> next.startsWith(e) || e.startsWith(next) }
        when {
            prev == null -> add(next)
            prev.length > next.length -> {
                remove(prev)
                add(next)
            }
        }
    }
}

private fun List<Case>.expand(i: Int, rules: Map<Char, List<Char>>) = flatMap { case ->
    when {
        case.len > i -> listOf(case)
        case.len == i -> rules[case.ch].orEmpty().map { it to case.len + 1 to case.count }
        else -> error("Invalid len ${case.len}")
    }
}.compress()

private fun <T> List<Pair<T, Int>>.compress() = buildMap {
    this@compress.forEach { (pair, count) -> put(pair, getOrDefault(pair, 0) + count) }
}.toList()
