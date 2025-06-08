package ec2024.quest3

import coords.pair.Pos
import coords.e
import coords.findAll
import coords.n
import coords.ne
import coords.nw
import coords.s
import coords.se
import coords.sw
import coords.w
import provideInput

val example1 = """
    ..........
    ..###.##..
    ...####...
    ..######..
    ..######..
    ...####...
    ..........
""".trimIndent()

fun main() {
    part1(example1).also { println("example1: $it") }
    part1(provideInput(2024, 3, 1)).also { println("part1: $it") }
    part1(provideInput(2024, 3, 2)).also { println("part2: $it") }
    part3(example1).also { println("example3: $it") }
    part3(provideInput(2024, 3, 3)).also { println("part3: $it") }
}

fun part1(data: String): Any {
    val points = data.lines().findAll('#')
    return generateSequence(points) { it.inners().takeIf { it.isNotEmpty() } }.sumOf { it.size }
}

fun part3(data: String): Any {
    val points = data.lines().findAll('#')
    return generateSequence(points) { it.inners3().takeIf { it.isNotEmpty() } }.sumOf { it.size }
}

private fun Set<Pos>.inners() = filter {
    it.n() in this && it.s() in this && it.e() in this && it.w() in this
}.toSet()

private fun Set<Pos>.inners3() = filter {
    it.n() in this && it.s() in this && it.e() in this && it.w() in this
            && it.ne() in this && it.nw() in this && it.se() in this && it.sw() in this
}.toSet()
