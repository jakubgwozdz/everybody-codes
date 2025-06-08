package ec2024.quest6

import go
import provideInput

fun main() {
    go("part1", "RRVPJLNLXLZT@") { part1(provideInput(2024, 6, 1)) }
    go("part2", "RRBHBDLFWM@") { part2(provideInput(2024, 6, 2)) }
    go("part3", "RXFMNQQDZLMV@") { part3(provideInput(2024, 6, 3)) }
}

private fun parse(data: String): Map<String, List<String>> = data.lines().associate { line ->
    line.substringBefore(":") to line.substringAfter(":").trim().split(",")
}

fun grow(branch: List<String>, rules: Map<String, List<String>>): List<List<String>> {
    val rule = rules[branch.last()]
    return rule
        ?.filter { it !in branch }
        ?.flatMap { r -> grow(branch + r, rules) } ?: listOf(branch)
}

fun part1(data: String) = grow(listOf("RR"), parse(data))
    .filter { it.last() == "@" }
    .groupBy { it.size }.values.single { it.size == 1 }.single()
    .joinToString("")

fun part2(data: String) = grow(listOf("RR"), parse(data))
    .filter { it.last() == "@" }
    .groupBy { it.size }.values.single { it.size == 1 }.single()
    .joinToString("") { it.first().toString() }


fun part3(data: String): Any {
    return part2(data)
}
