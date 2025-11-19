package ec2025.quest12

import coords.e
import coords.n
import coords.pair.Pos
import coords.pair.col
import coords.pair.row
import coords.s
import coords.w
import go
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 208) { part1(provideInput(year, quest, 1)) }
    go("part2", 5691) { part2(provideInput(year, quest, 2)) }
    go("part3") { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val grid = data.lines()
    return grid.process(emptySet(), Pos(0, 0)).size
}

fun part2(data: String): Any {
    val grid = data.lines()
    return grid.process(emptySet(), Pos(0, 0))
        .let { grid.process(it, Pos(grid.lastIndex, grid.last().lastIndex)) }.size
}

fun part3(data: String): Any {
    val grid = data.lines()
    val destroyed = mutableSetOf<Pos>()

    destroyed += part3round(grid, destroyed)
    destroyed += part3round(grid, destroyed)
    destroyed += part3round(grid, destroyed)
    return destroyed.size
}

private fun part3round(grid: List<String>, destroyed: Set<Pos>): Set<Pos> {
    val cache = mutableMapOf<Pos, Set<Pos>>()
    ('1'..'9').forEach { c ->
        grid.indices.forEach { row ->
            grid[row].indices.forEach { col ->
                if (grid[row][col] == c)
                    cache[Pos(row, col)] = grid.process(emptySet(), Pos(row, col), cache) { it !in destroyed }
            }
        }
    }
    return cache.values.maxBy { it.size }
}

private fun List<String>.process(
    done: Set<Pos>,
    first: Pos,
    cache: Map<Pos, Set<Pos>> = emptyMap(),
    filter: (Pos) -> Boolean = { true },
): Set<Pos> {
    val toGo = mutableListOf(first)
    val destroyed = done.toMutableSet()
    while (toGo.isNotEmpty()) {
        val curr = toGo.removeFirst()
        if (curr in destroyed) continue
        destroyed += curr
        listOf(curr.n(), curr.e(), curr.s(), curr.w()).forEach {
            if (
                it.row in indices && it.col in this[it.row].indices
                && this[it.row][it.col] <= this[curr.row][curr.col] && filter(it)
            ) {
                if (it in cache) destroyed += cache[it]!!
                else toGo.add(it)
            }
        }
    }
    return destroyed
}
