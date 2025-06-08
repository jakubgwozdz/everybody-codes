package ec2024.quest16

import collections.Cached
import collections.transposed
import go
import provideInput
import yearAndQuestFromPackage

val example3 = """
    1,2,3
    
    ^_^ -.- ^,-
    >.- ^_^ >.<
    -_- -.- ^.^
        -.^ >.<
        >.>
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", "<:^ *:- <,^ ^_^") { part1(provideInput(year, quest, 1)) }
    go("part2", 123105614598) { part2(provideInput(year, quest, 2)) }
    go("example3", "627 128") { part3(example3) }
    go("part3", "564 56") { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any = parse(data).wheels.joinToString(" ") { (strip, step) ->
    strip[100 * step % strip.size]
}

data class Wheel(val strip: List<String>, val step: Int)
data class Input(val wheels: List<Wheel>)

private fun parse(data: String): Input {
    val steps = data.lineSequence().first().split(",").map { it.toInt() }
    val strips = data.lineSequence().drop(2)
        .map { line -> line.windowed(3, 4) }
        .toList()
        .transposed { "   " }
        .map { it.filter { it.isNotBlank() } }
    val wheels = strips.zip(steps).map { (strip, step) -> Wheel(strip, step) }
    return Input(wheels)
}


fun part2(data: String): Any {
    val initial = parse(data)
    val selections = Cached { spins: Int -> initial.select(spins) }
    val cycleDetector = mutableSetOf<List<Int>>()
    generateSequence(0) { it + 1 }
        .map { selections(it) }
        .takeWhile { it !in cycleDetector }
        .forEach { cycleDetector += it }
    val cycle = cycleDetector.size
    val cycles = 202420242024 / cycle
    val remaining = 202420242024 % cycle
    val sequence = generateSequence(1) { it + 1 }.map { i ->
        val selected = selections(i % cycle)
        initial.score(selected)
    }
    return sequence.take(cycle).sum() * cycles + sequence.take(remaining.toInt()).sum()
}

private fun Input.score(selected: List<Int>): Int {
    val eyes = wheels
        .mapIndexed { index, wheel -> wheel.strip[selected[index]] }
        .flatMap { listOf(it[0], it[2]) }
    val score = eyes.groupingBy { it }.eachCount().values.sumOf { if (it > 2) it - 2 else 0 }
    return score
}

private fun Input.select(spins: Int, delta: Int = 0): List<Int> = wheels.map { (strip, step) ->
    (delta + step * spins).mod(strip.size)
}

fun part3(data: String): Any {
    val initial = parse(data)
    val score = Cached { selected: List<Int> -> initial.score(selected) }

    val current = mutableMapOf(0 to (0 to 0))
    val next = mutableMapOf<Int, Pair<Int, Int>>()

    (1..256).forEach { spins ->
        (-spins..spins).forEach { delta ->
            val selected = initial.select(spins, delta)
            val score = score(selected)
            var (max, min) = 0 to Int.MAX_VALUE
            current[delta]?.let { (max1, min1) ->
                max = max.coerceAtLeast(max1)
                min = min.coerceAtMost(min1)
            }
            current[delta - 1]?.let { (max1, min1) ->
                max = max.coerceAtLeast(max1)
                min = min.coerceAtMost(min1)
            }
            current[delta + 1]?.let { (max1, min1) ->
                max = max.coerceAtLeast(max1)
                min = min.coerceAtMost(min1)
            }
            next[delta] = score + max to score + min
        }
        current.putAll(next)
    }

    return "${current.values.maxOf { it.first }} ${current.values.minOf { it.second }}"
}
