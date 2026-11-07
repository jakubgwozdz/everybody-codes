package ec2026.quest10

import collections.increment
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
    go("part3", 14449275358775) { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val (walls, snapshot) = parse(data)
    return generateSequence(snapshot) {
        it.movePhase(walls)
            .ghostCollisionsCheck()
            .nextRound()
    }.dropWhile { it.round < 10 }.first().ghosts.values.sum()
}

fun part2(data: String, limit: Long = 10000): Any {
    val (walls, snapshot) = parse(data)
    val snapshots = mutableMapOf<Set<Snapshot.Key>, Snapshot>()
    return generateSequence(snapshot) {
        it.movePhase(walls).removeOverLimit(limit)
            .ghostCollisionsCheck().removeOverLimit(limit)
            .detectAndSkipLoops(snapshots, limit)
            .nextRound()
    }.dropWhile { it.ghosts.isNotEmpty() }.first().round
}

fun part3(data: String) = part2(data, 1000000000000)

data class Snapshot(
    val round: Long,
    val ghosts: Map<Key, Long>
) {
    data class Key(val id: Int, val pos: Pos, val dir: Direction)

    operator fun get(key: Key): Long = ghosts.getValue(key)
}

fun Snapshot.detectAndSkipLoops(
    snapshots: MutableMap<Set<Snapshot.Key>, Snapshot>,
    limit: Long
): Snapshot {
    val keys = ghosts.keys
    val previousSnapshot = snapshots[keys]
    if (previousSnapshot != null) {
        val deltas = keys.associate { it.id to this[it] - previousSnapshot[it] }
        val remainingCycles = keys.minOf { (limit - this[it] - 1) / deltas.getValue(it.id).coerceAtLeast(1) }
        return copy(
            round = round + remainingCycles * (round - previousSnapshot.round),
            ghosts = ghosts.mapValues { (key, collisions) -> collisions + remainingCycles * deltas.getValue(key.id) }
        ).also {
            snapshots.clear()
        }
    } else {
        snapshots[keys] = this
        return this
    }
}

fun Snapshot.removeOverLimit(limit: Long) = copy(ghosts = ghosts.filterValues { it < limit })

fun Snapshot.nextRound() = copy(round = round + 1)

fun Snapshot.movePhase(walls: Set<Pos>) = copy(ghosts = ghosts.asIterable().associate { (key, collisions) ->
    val next = key.pos.move(key.dir)
    if (next in walls) {
        key.copy(dir = key.dir.turnRight()) to collisions + 1
    } else {
        key.copy(pos = next) to collisions
    }
})

fun Snapshot.ghostCollisionsCheck(): Snapshot {
    val collisions = mutableMapOf<Int, Long>()
    ghosts.keys.groupBy { it.pos }.values.filter { it.size > 1 }.flatten().forEach { collisions.increment(it.id) }
    val newCounters = ghosts.entries.associate { (key, value) ->
        val colls = collisions[key.id] ?: 0
        val newKey = if (colls % 2 == 0L) key else key.copy(dir = key.dir.turnRight().turnRight())
        newKey to value + colls
    }
    return copy(ghosts = newCounters)
}

private fun parse(data: String): Pair<Set<Pos>, Snapshot> {
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
        Snapshot.Key(index + 1, pos, dir)
    }
    val snapshot = Snapshot(0, nullGhosts.associateWith { 0 })
    return Pair(walls, snapshot)
}

