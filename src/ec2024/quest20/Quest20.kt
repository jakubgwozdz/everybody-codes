package ec2024.quest20

import coords.Direction
import coords.findAll
import coords.move
import coords.pair.Pos
import coords.pair.col
import coords.pair.row
import debug
import go
import provideInput
import yearAndQuestFromPackage
import java.time.Duration

val example1 = """
    #....S....#
    #.........#
    #---------#
    #.........#
    #..+.+.+..#
    #.+-.+.++.#
    #.........#
""".trimIndent()

val example2a = """
    ####S####
    #-.+++.-#
    #.+.+.+.#
    #-.+.+.-#
    #A+.-.+C#
    #.+-.-+.#
    #.+.B.+.#
    #########
""".trimIndent()

val example2b = """
    ###############S###############
    #-----------------------------#
    #-------------+++-------------#
    #-------------+++-------------#
    #-------------+++-------------#
    #-----------------------------#
    #-----------------------------#
    #-----------------------------#
    #--A-----------------------C--#
    #-----------------------------#
    #-----------------------------#
    #-----------------------------#
    #-----------------------------#
    #-----------------------------#
    #-----------------------------#
    #--------------B--------------#
    #-----------------------------#
    #-----------------------------#
    ###############################
""".trimIndent()

val example3 = """
    #......S......#
    #-...+...-...+#
    #.............#
    #..+...-...+..#
    #.............#
    #-...-...+...-#
    #.............#
    #..#...+...+..#
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("example1", 1045) { part1(example1) }
    go("part1", 1029) { part1(provideInput(year, quest, 1)) }
    go("example2a", 24) { part2(example2a) }
//    go("example2b", 208) { part2(example2b) }
//    go("part2") { part2(provideInput(year, quest, 2)) }
    go("example3", 768790) { part3(example3) }
    go("part3", 768795) { part3(provideInput(year, quest, 3)) }
}

data class State(val pos: Pos, val dir: Direction, val altitude: Long, val time: Int, val checkpoints: Int = 0)

fun part1(data: String): Any {
    val lines = data.lines()
    val dots = lines.findAll('.')
    val start = State(lines.findAll('S').single(), Direction.S, 1000L, 0)
    val ups = lines.findAll('+')
    val downs = lines.findAll('-')

    var max = start
    val queue = mutableListOf(start)
    val visited = mutableMapOf<Pair<Pos, Direction>, Long>()

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (max.altitude < current.altitude) {
            max = current
        }
        Direction.entries.filterNot { it == current.dir.opposite() }.forEach { newDir ->
            val newPos = current.pos.move(newDir)
            val newState = newPos to newDir
            val newAltitude = when (newPos) {
                in dots -> current.altitude - 1
                in downs -> current.altitude - 2
                in ups -> current.altitude + 1
                else -> null
            }
            if (current.time < 100 && newAltitude != null && (newState !in visited || visited[newState]!! < newAltitude)) {
                visited[newState] = newAltitude
                queue.add(State(newPos, newDir, newAltitude, current.time + 1))
            }
        }
    }

    return max.altitude
}

data class Result(val altitude: Long, val time: Int, val checkpoints: Int)

data class Results(val results: List<Result>) {
    fun offer(result: Result): Results? {
        return if (results.any { it.isBetterOrEqualThan(result) }) {
            null
        } else {
            Results(results.filterNot { result.isBetterOrEqualThan(it) } + result)
        }
    }
}

fun Result.isBetterOrEqualThan(other: Result): Boolean =
//    checkpoints >= other.checkpoints &&
//            altitude >= other.altitude &&
    time <= other.time


fun part2(data: String): Any {
    val lines = data.lines()
    val dots = lines.findAll('.')
    val posS = lines.findAll('S').single()
    val posA = lines.findAll('A').single()
    val posB = lines.findAll('B').single()
    val posC = lines.findAll('C').single()
    val ups = lines.findAll('+')
    val downs = lines.findAll('-')
    val start = State(posS, Direction.S, 10000L, 0, 0)
    debug { "$posS -> $posA -> $posB -> $posC -> $posS" }

    val queue = mutableListOf(start)

    val visited = mutableMapOf<State, Long>(start.copy(altitude = 0, time = 0) to start.altitude)

    var shortest = Int.MAX_VALUE
    while (queue.isNotEmpty()) {
        debug(Duration.ofSeconds(5)) { "queue: ${queue.size}, visited: ${visited.size}" }
//        debug { "queue: ${queue.size}, visited: ${visited.size}" }
        val current = queue.removeFirst()
        if (current.checkpoints == 4 && current.time < shortest) {
            debug { "found: $current" }
            return current.time
//            shortest = current.time
        }
        Direction.entries.filterNot { it == current.dir.opposite() }.forEach { newDir ->
            val newPos = current.pos.move(newDir)
            val newAltitude = when (newPos) {
                in dots -> current.altitude - 1
                in downs -> current.altitude - 2
                in ups -> current.altitude + 1
                posA -> (current.altitude - 1).takeIf { current.checkpoints == 0 }
                posB -> (current.altitude - 1).takeIf { current.checkpoints == 1 }
                posC -> (current.altitude - 1).takeIf { current.checkpoints == 2 }
                posS -> (current.altitude - 1).takeIf { current.checkpoints == 3 && current.altitude > 10000 }
                else -> null
            }?.takeIf { it < 10100L }
            if (newAltitude != null) {
                val newCheckpoints = when (newPos) {
                    posA -> 1
                    posB -> 2
                    posC -> 3
                    posS -> 4
                    else -> current.checkpoints
                }
                val newTime = current.time + 1
                val newState = State(newPos, newDir, newAltitude, newTime, newCheckpoints)
                val newKey = newState.copy(altitude = 0, time = 0)
                if (newKey !in visited || visited[newKey]!! < newAltitude) {
                    visited[newKey] = newAltitude
                    queue.add(newState)
                }
            }
        }
    }

    return shortest
}

fun part3(data: String): Any {
    val lines = data.lines()
    val dots = lines.findAll('.')
    val posS = lines.findAll('S').single()
    val ups = lines.findAll('+')
    val downs = lines.findAll('-')
    val altitude = 384400L
    val cycle = data.lines().size
    debug { "cycle: $cycle" }
    val lowAlt = 20 * cycle + altitude % cycle
    val start = State(posS, Direction.S, lowAlt, 0, 0)
    debug { "start: $start" }

    val queue = mutableListOf(start)
    val visited = mutableMapOf((start.pos to start.dir) to start.altitude)
    val bestInRow = mutableMapOf(start.pos.row to start.altitude)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        Direction.entries.filterNot { it == current.dir.opposite() }.forEach { newDir ->
            val newPos = current.pos.move(newDir)
            val newState = newPos to newDir
            val newAltitude = when (Pos(newPos.row % cycle, newPos.col)) {
                in dots -> current.altitude - 1
                in downs -> current.altitude - 2
                in ups -> current.altitude + 1
                posS -> current.altitude - 1
                else -> null
            }
            if (newAltitude != null && newAltitude > 0 &&
                (newState !in visited || visited[newState]!! < newAltitude) &&
                (newPos.row !in bestInRow || bestInRow[newPos.row]!! <= newAltitude + 3)
            ) {
                visited[newState] = newAltitude
                if (newPos.row !in bestInRow || bestInRow[newPos.row]!! < newAltitude) {
                    bestInRow[newPos.row] = newAltitude
                }
                queue.add(State(newPos, newDir, newAltitude, current.time + 1))
            }
        }
    }

    val last = bestInRow.keys.max().debug { "last: $it" }
    val drop = bestInRow[last - cycle]!! - bestInRow[last]!!
    debug { "drop: $drop" }
    val cycles = (altitude - lowAlt) / drop

    return last + cycles * cycle + 1
}
