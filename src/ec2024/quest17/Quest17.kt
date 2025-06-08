package ec2024.quest17

import coords.findAll
import coords.pair.Pos
import coords.pair.col
import coords.pair.row
import go
import provideInput
import yearAndQuestFromPackage
import kotlin.math.absoluteValue

val example3 = """
    .......................................
    ..*.......*...*.....*...*......**.**...
    ....*.................*.......*..*..*..
    ..*.........*.......*...*.....*.....*..
    ......................*........*...*...
    ..*.*.....*...*.....*...*........*.....
    .......................................
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 137) { part1(provideInput(year, quest, 1)) }
    go("part2", 1230) { part2(provideInput(year, quest, 2)) }
    go("example3", 15624) { part3(example3) }
    go("part3", 4905701010) { part3(provideInput(year, quest, 3)) }
}

fun dist(a: Pos, b: Pos) = (a.row - b.row).absoluteValue + (a.col - b.col).absoluteValue

fun part1(data: String): Any {
    val stars = data.lines().findAll('*')

    val toAdd = stars.toMutableSet()
    val added = mutableSetOf(toAdd.first().also { toAdd.remove(it) })
    var result = 0

    while (toAdd.isNotEmpty()) {
        val next = toAdd.minBy { star -> added.minOf { dist(star, it) } }
        val adj = added.minBy { dist(next, it) }
        added.add(next)
        toAdd.remove(next)
        result += dist(next, adj)
    }
    result += added.size

    return result
}

fun part2(data: String): Any = part1(data)

fun part3(data: String): Any {
    val stars = data.lines().findAll('*')
    val precalc =
        stars.associateWith { s1 -> stars.filter { s1 != it }.map { it to dist(s1, it) }.sortedBy { it.second } }

    val groups = mutableSetOf<Int>()
    val toAdd = stars.toMutableSet()

    val added = mutableSetOf(toAdd.first().also { toAdd.remove(it) })
    var result = 0

    while (toAdd.isNotEmpty()) {
        val (next, dist) = added.map { precalc[it]!!.first { (n, _) -> n in toAdd } }.minBy { it.second }
        toAdd.remove(next)
        if (dist < 6) result += dist
        else {
            groups.add(result + added.size)
            added.clear()
            result = 0
        }
        added.add(next)
    }
    groups.add(result + added.size)
    return groups.sortedDescending().take(3).fold(1L) { acc, i -> acc * i }
}
