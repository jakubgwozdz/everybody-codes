package ec2024.quest4

import provideInput
import kotlin.math.abs

val example3 = """
    2
    4
    5
    6
    8
""".trimIndent()

fun main() {
    part1(provideInput(2024, 4, 1)).also { println("part1: $it") }
    part1(provideInput(2024, 4, 2)).also { println("part2: $it") }
    part3(example3).also { println("example3: $it") }
    part3(provideInput(2024, 4, 3)).also { println("part3: $it") }
}

fun part1(data: String): Any {
    val l = data.lines().map { it.toInt() }
    val min = l.min()
    return l.sumOf { it - min }
}

fun part3(data: String): Any {
    val l = data.lines().map { it.toLong() }.sorted()
    val median = l[l.size / 2]
    return l.sumOf { abs(it - median) }
}
