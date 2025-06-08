package ec2024.quest13

import collections.PriorityQueue
import coords.e
import coords.findAll
import coords.get
import coords.n
import coords.pair.Pos
import coords.s
import coords.w
import go
import provideInput
import yearAndQuestFromPackage
import kotlin.math.absoluteValue
import kotlin.math.min

val example1 = """
    #######
    #6769##
    S50505E
    #97434#
    #######
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("example1", 28) { part1(example1) }
    go("part1", 141) { part1(provideInput(year, quest, 1)) }
    go("part2", 606) { part2(provideInput(year, quest, 2)) }
    go("part3", 577) { part3(provideInput(year, quest, 3)) }
}

data class Road(val pos3d: Pair<Pos, Int>, val cost: Int = 0)

fun part1(data: String): Any {
    val grid = data.lines()
    val starts = grid.findAll('S')
    val ends = grid.findAll('E')
    val platforms: Map<Pos, Int> =
        grid.findAll { it.isDigit() }.associateWith { grid[it]!!.digitToInt() } + ends.map { it to 0 }

    val queue = PriorityQueue<Road>(compareBy { it.cost })
    starts.forEach { queue.add(Road(it to 0)) }
    val visited = mutableMapOf<Pos, Int>()
    while (queue.isNotEmpty()) {
        val curr = queue.removeFirst()
        if (curr.pos3d.first in ends) return curr.cost
        val (pos, h) = curr.pos3d
        visited[pos] = min(visited[pos] ?: Int.MAX_VALUE, curr.cost)
        listOf(pos.n(), pos.s(), pos.e(), pos.w())
            .filter { it in platforms.keys }
            .forEach { next ->
                val nextH = platforms[next]!!
                val delta = (nextH - h).absoluteValue.let { if (it > 5) 10 - it else it }
                val cost = curr.cost + delta + 1
                if (cost < (visited[next] ?: Int.MAX_VALUE))
                    queue.add(Road(next to nextH, cost))
            }
    }
    error("No path found")
}

fun part2(data: String): Any = part1(data)
fun part3(data: String): Any = part1(data)
