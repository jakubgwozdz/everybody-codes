package ec2026.quest10

import coords.Direction
import coords.findAll
import coords.get
import coords.move
import coords.pair.Pos
import go
import provideInput
import yearAndQuestFromPackage

val exampleA = """
    ###########
    #####.#####
    ###.....###
    ##.......##
    ##.>>.<<.##
    #v...>...v#
    ##...v...##
    ##.......##
    ###.....###
    #####.#####
    ###########
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1 ex", 29) { part1(exampleA) }
    go("part1") { part1(provideInput(year, quest, 1)) }
    go("part2 ex", 53) { part2(exampleA, 10) }
    go("part2 ex", 50000) { part2(exampleA) }
    go("part2") { part2(provideInput(year, quest, 2)) }
//    go("part3 ex", exampleA) { part3(exampleA) }
    go("part3") { part3(provideInput(year, quest, 3)) }
}

data class NullGhost(var pos: Pos, var dir: Direction, var collisions: Long = 0, var isAlive: Boolean = true)

fun part1(data: String): Any {
    val (walls, nullGhosts) = parse(data)
    var collisions = 0L
    repeat(10) {
        collisions += movePhase(nullGhosts, walls)
        collisions += ghostCollisionsCheck(nullGhosts)
    }
    return collisions
}

fun part2(data: String, limit: Long = 10000): Any {
    val (walls, nullGhosts) = parse(data)
    var round = 0L
    val snapshots = mutableMapOf<Pos, Long>()
    while (nullGhosts.any { it.isAlive }) {
        round++
        movePhase(nullGhosts, walls)
        nullGhosts.forEach { if (it.collisions >= limit) it.isAlive = false }
        ghostCollisionsCheck(nullGhosts)
        nullGhosts.forEach { if (it.collisions >= limit) it.isAlive = false }
    }
    return round
}

fun part3(data: String) = part2(data, 1000000000000)

private fun movePhase(
    nullGhosts: List<NullGhost>,
    walls: Set<Pos>
): Long {
    var wallCollisions = 0L
    nullGhosts.filter { it.isAlive }.forEach {
        val next = it.pos.move(it.dir)
        if (next in walls) {
            wallCollisions++
            it.collisions++
            it.dir = it.dir.turnRight()
        } else it.pos = next
    }
    return wallCollisions
}

private fun ghostCollisionsCheck(nullGhosts: List<NullGhost>): Long {
    var ghostCollisions = 0L
    nullGhosts.filter { it.isAlive }.groupBy { it.pos }.values.filter { it.size > 1 }.forEach { list ->
        list.forEach {
            ghostCollisions++
            it.collisions++
            it.dir = it.dir.turnLeft().turnLeft()
        }
    }
    return ghostCollisions
}

private fun parse(data: String): Pair<Set<Pos>, List<NullGhost>> {
    val lines = data.split("\n")
    val walls = lines.findAll('#')
    val nullGhosts = lines.findAll { it in "<>^v" }.map {
        val dir = when (lines[it]) {
            '<' -> Direction.W
            '>' -> Direction.E
            '^' -> Direction.N
            'v' -> Direction.S
            else -> error("WTF")
        }
        NullGhost(it, dir)
    }
    return Pair(walls, nullGhosts)
}

