package ec2025.quest15

import coords.Direction
import coords.move
import coords.pair.Pos
import go
import provideInput
import search.astar
import yearAndQuestFromPackage

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    val part1e1 = """
        L6,L3,L6,R3,L6,L3,L3,R6,L6,R6,L6,L6,R3,L3,L3,R3,R3,L6,L6,L3
    """.trimIndent()
    go("part1e1", 16) { part1(part1e1) }
    go("part1", 109) { part1(provideInput(year, quest, 1)) }
    go("part2", 3689) { part2(provideInput(year, quest, 2)) }
    go("part3") { part3(provideInput(year, quest, 3)) }
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

fun part2(data: String) = part1(data)

fun part3(data: String) = part1(data)
