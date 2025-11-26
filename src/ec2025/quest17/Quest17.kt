package ec2025.quest17

import coords.findAll
import go
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 1560) { part1(provideInput(year, quest, 1)) }
    go("part2", 66807) { part2(provideInput(year, quest, 2)) }
    go("part3") { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val grid = data.lines()
    val (vr, vc) = grid.findAll('@').single()
    return destroyed(grid, vc, vr, 10)
}

private fun destroyed(grid: List<String>, vc: Int, vr: Int, step: Int): Int = grid.indices.sumOf { r ->
    grid[r].indices.sumOf { c ->
        if ((c - vc) * (c - vc) + (r - vr) * (r - vr) in 1..step * step) grid[r][c].digitToInt() else 0
    }
}

fun part2(data: String): Any {
    val grid = data.lines()
    val (vr, vc) = grid.findAll('@').single()
    var prev = 0
    var step = 0
    val steps = buildMap {
        while (step == 0 || prev != 0) {
            step++
            val d = destroyed(grid, vc, vr, step)
            put(step, d - prev)
            prev = d
        }
    }
    return steps.maxBy { it.value }.let { it.key * it.value }
}

fun part3(data: String): Any {
    return data
}
