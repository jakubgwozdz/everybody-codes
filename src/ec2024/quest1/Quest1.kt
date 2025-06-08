package ec2024.quest1

import provideInput

fun main() {
    part1(provideInput(2024, 1, 1)).also { println("part1: $it") }
    part2(provideInput(2024, 1, 2)).also { println("part2: $it") }
    part3(provideInput(2024, 1, 3)).also { println("part3: $it") }
}

private fun cost1(c: Char) = when (c) {
    'A' -> 0
    'B' -> 1
    'C' -> 3
    'D' -> 5
    else -> error("Invalid character `$c`")
}

fun cost2(a: Char, b: Char) = when {
    a == 'x' && b == 'x' -> 0
    a == 'x' -> cost1(b)
    b == 'x' -> cost1(a)
    else -> cost1(a) + cost1(b) + 2
}

fun cost3(a: Char, b: Char, c: Char) = when {
    a == 'x' && b == 'x' && c == 'x' -> 0
    a == 'x' -> cost2(b, c)
    b == 'x' -> cost2(a, c)
    c == 'x' -> cost2(a, b)
    else -> cost1(a) + cost1(b) + cost1(c) + 6
}

fun part1(data: String) = data.sumOf(::cost1)

fun part2(data: String) = data.chunked(2).sumOf { cost2(it[0], it[1]) }

fun part3(data: String) = data.chunked(3).sumOf { cost3(it[0], it[1], it[2]) }
