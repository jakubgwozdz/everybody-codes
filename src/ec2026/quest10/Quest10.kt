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

data class NullGhost(
    val id: Int,
    var pos: Pos,
    var dir: Direction,
    var collisions: Long = 0,
    var isAlive: Boolean = true
)

fun part1(data: String): Any {
    val (walls, nullGhosts) = parse(data)
    repeat(10) {
        movePhase(nullGhosts, walls)
        ghostCollisionsCheck(nullGhosts)
    }
    return nullGhosts.sumOf { it.collisions }
}

data class Snapshot(
    val round: Long,
    val ghosts: Map<Key, Long>
) {
    data class Key(val id: Int, val pos: Pos, val dir: Direction)

    operator fun get(key: Key): Long = ghosts.getValue(key)
}

private fun makeSnapshot(round: Long, nullGhosts: List<NullGhost>): Snapshot {
    val ghosts = nullGhosts.filter { it.isAlive }.associate { Snapshot.Key(it.id, it.pos, it.dir) to it.collisions }
    return Snapshot(round, ghosts)
}

fun part2(data: String, limit: Long = 10000): Any {
    val (walls, nullGhosts) = parse(data)
    var round = 0L
    val snapshots = mutableMapOf<Set<Snapshot.Key>, Snapshot>()
    while (nullGhosts.any { it.isAlive }) {
        round += detectAndSkipLoops(round, nullGhosts, snapshots, limit)
        round++
        movePhase(nullGhosts, walls)
        nullGhosts.forEach { if (it.collisions >= limit) it.isAlive = false }
        ghostCollisionsCheck(nullGhosts)
        nullGhosts.forEach { if (it.collisions >= limit) it.isAlive = false }
    }
    return round
}

// returns skipped rounds
private fun detectAndSkipLoops(
    round: Long,
    nullGhosts: List<NullGhost>,
    snapshots: MutableMap<Set<Snapshot.Key>, Snapshot>,
    limit: Long
): Long {
    val snapshot = makeSnapshot(round, nullGhosts)
    val keys = snapshot.ghosts.keys
    val previousSnapshot = snapshots[keys]
    if (previousSnapshot != null) {
        snapshots.clear()

        val deltas = keys.associate { it.id to snapshot[it] - previousSnapshot[it] }
        val remainingCycles = keys.minOf { (limit - snapshot[it] - 1) / deltas.getValue(it.id) }

        nullGhosts.filter { it.isAlive }.forEach { it.collisions += remainingCycles * deltas.getValue(it.id) }
        return remainingCycles * (snapshot.round - previousSnapshot.round)
    } else {
        snapshots[keys] = snapshot
        return 0
    }
}

fun part3(data: String) = part2(data, 1000000000000)

private fun movePhase(nullGhosts: List<NullGhost>, walls: Set<Pos>) {
    nullGhosts.filter { it.isAlive }.forEach {
        val next = it.pos.move(it.dir)
        if (next in walls) {
            it.collisions++
            it.dir = it.dir.turnRight()
        } else it.pos = next
    }
}

private fun ghostCollisionsCheck(nullGhosts: List<NullGhost>) {
    nullGhosts.filter { it.isAlive }.groupBy { it.pos }.values.filter { it.size > 1 }.forEach { list ->
        list.forEach {
            it.collisions++
            it.dir = it.dir.turnLeft().turnLeft()
        }
    }
}

private fun parse(data: String): Pair<Set<Pos>, List<NullGhost>> {
    val lines = data.split("\n")
    val walls = lines.findAll('#')
    val nullGhosts = lines.findAll { it in "<>^v" }.mapIndexed { index, pos ->
        val dir = when (lines[pos]) {
            '<' -> Direction.W
            '>' -> Direction.E
            '^' -> Direction.N
            'v' -> Direction.S
            else -> error("WTF")
        }
        NullGhost(index + 1, pos, dir)
    }
    return Pair(walls, nullGhosts)
}

