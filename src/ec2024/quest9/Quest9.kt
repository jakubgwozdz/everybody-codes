package ec2024.quest9

import go
import provideInput
import yearAndQuestFromPackage

val example1 = """
    2
    4
    7
    16
""".trimIndent()

val example2 = """
    33
    41
    55
    99
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("example1", 10) { part1(example1) }
    go("part1", 13001) { part1(provideInput(year, quest, 1)) }
    go("example2", 10) { part2(example2) }
    go("part2", 5154) { part2(provideInput(year, quest, 2)) }
    go("part3", 145052) { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val balls = parse(data)
    val stamps = listOf<Int>(1, 3, 5, 10)
    val dp = buildCache(stamps, balls.max())
    return balls.sumOf { dp[it] }
}

fun part2(data: String): Any {
    val balls = parse(data)
    val stamps = listOf<Int>(1, 3, 5, 10, 15, 16, 20, 24, 25, 30)
    val dp = buildCache(stamps, balls.max())
    return balls.sumOf { dp[it] }
}

fun part3(data: String): Any {
    val balls = parse(data)
    val stamps = listOf(1, 3, 5, 10, 15, 16, 20, 24, 25, 30, 37, 38, 49, 50, 74, 75, 100, 101)
    val dp = buildCache(stamps, balls.max() / 2 + 100)
    return balls.sumOf {
        val half = it / 2
        val other = it - half
        (0..50).minOf { dp[half + it] + dp[other - it] }
    }
}

private fun buildCache(stamps: List<Int>, max: Int) = IntArray(max + 1).also { dp ->
    (1..max).forEach { i -> dp[i] = stamps.filter { it <= i }.minOf { dp[i - it] } + 1 }
}

private fun parse(data: String): List<Int> = data.lines().map { it.toInt() }
