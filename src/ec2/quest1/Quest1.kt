package ec2.quest1

import go
import provideInput
import yearAndQuestFromPackage

private fun String.toss(start: Int, token: String): Int {
    var position = start * 2 - 2
    val turns = token.iterator()
    lines().forEach { line ->
        if (line[position] == '*') {
            when (turns.next()) {
                'L' -> position--
                'R' -> position++
            }
            if (position < line.indices.first) position = line.indices.first + 1
            if (position > line.indices.last) position = line.indices.last - 1
        }
    }
    return (position + 2 - start).coerceAtLeast(0)
}

private fun topScoreWithDistinctStarts(best: List<List<Pair<Int, Int>>>): Int {
    var foundBest = Int.MIN_VALUE
    val firstTest = best
    val seen = mutableSetOf(firstTest.map { it.size }) // just sizes
    val toCheck = mutableListOf(firstTest)
    while (toCheck.isNotEmpty()) {
        val test = toCheck.removeFirst()
        val selected = test.map { it.first() }
        val counts = selected.groupingBy { (start, _) -> start }.eachCount()
        val isValid = counts.all { it.value == 1 }
        if (isValid) {
            val found = selected.sumOf { (_, score) -> score }
            foundBest = foundBest.coerceAtLeast(found)
        } else {
            counts.filter { (_, score) -> score > 1 }.keys.forEach { slot ->
                test.forEachIndexed { i, l ->
                    if (l.first().first == slot) {
                        val candidate = test.mapIndexed { i2, l2 -> if (i2 == i) l2.drop(1) else l2 }
                        val hash = candidate.map { it.size }
                        if (hash.none { it == 0 } && hash !in seen) {
                            seen += hash
                            toCheck += candidate
                        }
                    }
                }
            }
        }
    }
    return foundBest
}

fun part1(data: String): Any {
    val (setup, tokens) = data.split("\n\n")
    return tokens.lines().withIndex().sumOf { (start, token) -> setup.toss(start + 1, token) }
}

fun part2(data: String): Any {
    val (setup, tokens) = data.split("\n\n")
    val slots = 1..(setup.lines().first().length + 1) / 2
    return tokens.lines().sumOf { token ->
        slots.maxOf { start -> setup.toss(start, token) }
    }
}

fun part3(data: String): Any {
    val (setup, tokens) = data.split("\n\n")
    val slots = 1..(setup.lines().first().length + 1) / 2
    val wins = tokens.lines().map { token ->
        slots.associateWith { start -> setup.toss(start, token) }
            .toList().sortedByDescending { (_, score) -> score }
    }
    val best = wins.map { it.take(wins.size) }
        .let(::topScoreWithDistinctStarts)
//        .also { println("best: $it") }
    val worst = wins.map { it.takeLast(wins.size).reversed().map { (start, score) -> start to -score } }
        .let(::topScoreWithDistinctStarts)
//        .also { println("worst: $it") }
    return "${-worst} $best"
}

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 55) { part1(provideInput(year, quest, 1)) }
    go("part2", 1207) { part2(provideInput(year, quest, 2)) }
    go("part3", "44 121") { part3(provideInput(year, quest, 3)) }
}
