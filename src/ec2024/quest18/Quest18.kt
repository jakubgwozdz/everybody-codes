package ec2024.quest18

import coords.e
import coords.findAll
import coords.n
import coords.pair.Pos
import coords.pair.col
import coords.s
import coords.w
import go
import provideInput
import yearAndQuestFromPackage

val example3 = """
    ##########
    #.#......#
    #.P.####P#
    #.#...P#.#
    ##########
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 101) { part1(provideInput(year, quest, 1)) }
    go("part2", 1671) { part2(provideInput(year, quest, 2)) }
    go("example3", 12) { part3(example3) }
    go("part3", 236883) { part3(provideInput(year, quest, 3)) }
}

private fun solve(
    start: Collection<Pos>,
    trees: Set<Pos>,
    available: Set<Pos>
): Int {
    val queue = start.map { it to 0 }.toMutableList()
    val visited = mutableSetOf<Pos>().apply { addAll(start) }
    var toGo = trees.size
    while (toGo > 0) {
        val (current, steps) = queue.removeFirst()
        listOf(current.n(), current.e(), current.s(), current.w())
            .filter { it in available && it !in visited }
            .forEach { next ->
                if (next in trees) toGo--
                visited += next
                queue += next to steps + 1
            }
    }

    return queue.last().second
}

fun part1(data: String): Any {
    val trees = data.lines().findAll('P')
    val available = data.lines().findAll('.') + trees

    val start = available.filter { it.col == 0 || it.col == data.lines().first().lastIndex }

    return solve(start, trees, available)
}


fun part2(data: String): Any = part1(data)

fun part3(data: String): Any {
    val grid = data.lines()
    val trees = grid.findAll('P')
    val available = grid.findAll('.') + trees

    val distances = mutableMapOf<Pos, Int>()

    trees.forEach { start ->
        val queue = mutableListOf(start to 0)
        val visited = mutableSetOf(start)
        while (queue.isNotEmpty()) {
            val (current, steps) = queue.removeFirst()
            listOf(current.n(), current.e(), current.s(), current.w())
                .filter { it in available && it !in visited }
                .forEach {
                    distances.merge(it, steps + 1, Int::plus)
                    visited += it
                    queue += it to steps + 1
                }
        }
    }

    return distances.toList().filter { it.first !in trees }.minOf { it.second }
}
