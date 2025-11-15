package ec2025.quest10

import go
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 143) { part1(provideInput(year, quest, 1)) }
    go("part2", 1725) { part2(provideInput(year, quest, 2)) }
    go("part3") { part3(provideInput(year, quest, 3)) }
}

typealias Pos = Pair<Int, Int>

private fun parse(data: String): Pair<List<String>, Map<Char, Set<Pos>>> {
    val grid = data.lines()
    val chars = grid.flatMapIndexed { r, line ->
        line.mapIndexed { c, ch -> r to c to ch }
    }.groupBy { (_, ch) -> ch }.mapValues { (_, v) -> v.map { (p) -> p }.toSet() }
    return grid to chars
}

private val moves = listOf(-2 to 1, -1 to 2, 1 to 2, 2 to 1, 2 to -1, 1 to -2, -1 to -2, -2 to -1)

fun part1(data: String, rounds: Int = 4): Any {
    val (grid, chars) = parse(data)
    val start = chars['D']!!
    val sheep = chars['S']!!

    val toSee = start.map { it to 0 }.toMutableList()
    val visited = start.toMutableSet()
    while (toSee.isNotEmpty()) {
        val (prev, count) = toSee.removeFirst()
        val (r, c) = prev
        moves.forEach { (dr, dc) ->
            val nr = r + dr
            val nc = c + dc
            val next = nr to nc
            if (nr in grid.indices && nc in grid[nr].indices && next !in visited) {
                visited += next
                if (count < rounds - 1) toSee += next to count + 1
            }
        }
    }
    return visited.count { it in sheep }
}

fun part2(data: String, rounds: Int = 20): Any {
    val (grid, chars) = parse(data)
    var dragon = chars['D']!!
    var sheep = chars['S']!!
    val hides = chars['#']!!
    var result = 0
    repeat(rounds) {
        dragon = dragon.moveDragon(grid)
        sheep = sheep.eat(dragon, hides).also { result += sheep.size - it.size }
        sheep = sheep.moveSheep(grid)
        sheep = sheep.eat(dragon, hides).also { result += sheep.size - it.size }
    }
    return result
}

private fun Set<Pos>.eat(dragon: Set<Pos>, hides: Set<Pos>): Set<Pos> = filter { it !in dragon || it in hides }.toSet()

private fun Set<Pos>.moveSheep(grid: List<String>): Set<Pos> =
    map { (r, c) -> r + 1 to c }
        .filter { (r, c) -> r in grid.indices && c in grid[r].indices }.toSet()

private fun Set<Pos>.moveDragon(grid: List<String>): Set<Pos> =
    flatMap { (r, c) -> moves.map { (dr, dc) -> r + dr to c + dc } }
        .filter { (r, c) -> r in grid.indices && c in grid[r].indices }.toSet()

fun part3(data: String): Any {
    return data
}
