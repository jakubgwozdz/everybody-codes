package ec2025.quest15

import collections.PriorityQueue
import coords.Direction
import coords.manhattanDistance
import coords.move
import coords.pair.Pos
import go
import provideInput
import yearAndQuestFromPackage

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    val part1e1 = """
        L6,L3,L6,R3,L6,L3,L3,R6,L6,R6,L6,L6,R3,L3,L3,R3,R3,L6,L6,L3
    """.trimIndent()
    go("part1e1", 16) { part1(part1e1) }
    go("part1", 109) { part1(provideInput(year, quest, 1)) }
    go("part2") { part2(provideInput(year, quest, 2)) }
//    go("part3") { part3(provideInput(year, quest, 3)) }
}

// A* search
fun <T> reconstructPath(cameFrom: Map<T, T>, end: T): List<T> {
    val totalPath = mutableListOf(end)
    var current = end
    while (current in cameFrom.keys) {
        current = cameFrom[current]!!
        totalPath.add(0, current)
    }
    return totalPath
}

fun astar(start: Pos, end: Pos, walls: Set<Pos>, heuristics: (Pos) -> Int = end::manhattanDistance): List<Pos> {
    val comeFrom = mutableMapOf<Pos, Pos>()
    val gScore = mutableMapOf(start to 0).withDefault { Int.MAX_VALUE / 2 }
    val fScore = mutableMapOf(start to heuristics(start)).withDefault { Int.MAX_VALUE / 2 }
    val toCheck = PriorityQueue({ a, b -> fScore.getValue(a) - fScore.getValue(b) }, start)
    while (toCheck.isNotEmpty()) {
        val current = toCheck.removeFirst()
        if (current == end) return reconstructPath(comeFrom, current)
        Direction.entries.map { current.move(it) to 1 }
            .filterNot { (neighbor) -> neighbor in walls }
            .forEach { (neighbor, dist) ->
                val tentativeGScore = gScore.getValue(current) + dist
                if (tentativeGScore < gScore.getValue(neighbor)) {
                    gScore[neighbor] = tentativeGScore
                    fScore[neighbor] = tentativeGScore + heuristics(neighbor)
                    comeFrom[neighbor] = current
                    toCheck.add(neighbor)
                }
            }
    }
    error("No path from $start to $end")
}

fun part1(data: String): Any {
    val start = Pos(0, 0)
    var end = start
    val walls = buildSet {
        var d = Direction.N
        data.split(',').forEach { m ->
            d = when (m.first()) {
                'L' -> d.turnLeft()
                'R' -> d.turnRight()
                else -> error("wrong direction $m")
            }
            repeat(m.drop(1).toInt()) {
                add(end)
                end = end.move(d)
            }
        }
    }

    return astar(start, end, walls).size - 1
}

fun part2(data: String): Any {
    return data
}

fun part3(data: String): Any {
    return data
}
