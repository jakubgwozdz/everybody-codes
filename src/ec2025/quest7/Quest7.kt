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

fun part3(data: String): Any {
    val (names, rules) = parseInputData(data)
    val cases = mutableMapOf<Int, MutableMap<Char, Int>>()
    names.filter { matchesRules(it, rules) }
        .distinctByPrefix()
        .forEach { name -> cases.increment(name.length, name.last()) }

    (1..10).forEach { len->
        cases[len].orEmpty().forEach { (ch, count) -> rules[ch].orEmpty().forEach { cases.increment(len + 1, it, count)} }
    }

    return (7..11).sumOf { len->cases[len].orEmpty().values.sum() }
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

fun <T> MutableMap<T, Int>.increment(k: T, i: Int = 1) =
    put(k, getOrDefault(k, 0) + i)

fun <S, T> MutableMap<S, MutableMap<T, Int>>.increment(k1: S, k2: T, i: Int = 1) =
    getOrPut(k1) { mutableMapOf() }.increment(k2, i)
