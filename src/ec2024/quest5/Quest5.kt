package ec2024.quest5

import go
import provideInput

val example1 = """
    2 3 4 5
    3 4 5 2
    4 5 2 3
    5 2 3 4
""".trimIndent()

val example2 = """
    2 3 4 5
    6 7 8 9
""".trimIndent()

fun main() {
    go("example1", 2323) { part1(example1) }
    go("part1", 2223) { part1(provideInput(2024, 5, 1)) }
    go("example2", 50877075) { part2(example2) }
    go("part2", 21579793807341) { part2(provideInput(2024, 5, 2)) }
    go("example3", 6584) { part3(example2) }
    go("part3") { part3(provideInput(2024, 5, 3)) }
}

fun part1(data: String): Any {
    val columns = parse(data)
    repeat(10, columns::round)
    return columns.result()
}

private fun List<List<Int>>.result() = joinToString("") { it[0].toString() }.toLong()

private fun List<MutableList<Int>>.round(i: Int) {
    val clapper = this[i % 4].removeFirst()
    val l = this[(i + 1) % 4].size
    val pos = ((clapper - 1) % (2 * l)).let { if (it >= l) 2 * l - it else it }
    this[(i + 1) % 4].add(pos, clapper)
}

private fun parse(data: String): List<MutableList<Int>> = data.lines().map { it.split(" ").map { it.toInt() } }
    .let { list -> list[0].indices.map { c -> list.map { it[c] }.toMutableList() } }

fun part2(data: String): Any {
    val columns = parse(data)
    val counts = mutableMapOf<Long, Int>()
    var counter = 0
    while (true) {
        columns.round(counter)
        counter++
        val result = columns.result()
        val c = counts.compute(result) { _, v -> (v ?: 0) + 1 }
        if (c == 2024) return result * counter
    }
}

fun part3(data: String): Any {
    val columns = parse(data)
    val shouted = mutableSetOf<Long>()
    val found = mutableSetOf<List<List<Int>>>()
    var counter = 0
    while (true) {
        columns.round(counter++)
        val result = columns.result()
        if (result !in shouted) shouted.add(result)
        if (counter % 8 == 7) {
            if (found.contains(columns)) return shouted.max()
            found += columns
        }
    }
}
