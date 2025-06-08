package ec2024.quest15


import coords.e
import coords.findAll
import coords.get
import coords.n
import coords.pair.Pos
import coords.pair.row
import coords.s
import coords.w
import go
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 188) { part1(provideInput(year, quest, 1)) }
    go("part2", 520) { part2(provideInput(year, quest, 2)) }
    go("part3") { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val lines = data.lines()
    val herbs = lines.findAll('H')
    val available = lines.findAll('.') + herbs
    val blocked = lines.findAll('#')
    val start = available.single { it.row == 0 }

    val queue = mutableListOf(start to 0)
    val visited = mutableSetOf(start)
    while (queue.isNotEmpty()) {
        val (pos, dist) = queue.removeFirst()
        sequenceOf(pos.n(), pos.s(), pos.w(), pos.e())
            .filter { it in available && it !in visited }
            .forEach {
                if (it in herbs) return ((dist + 1) * 2)
                visited += it
                queue += it to dist + 1
            }
    }
    error("No herbs found")
}

fun part2(data: String): Any {
    val lines = data.lines()
    val allHerbs = lines.findAll { it.isLetter() }
    val herbsByType = allHerbs.groupBy { lines[it]!! }
    val herbsByPos = allHerbs.associateWith { lines[it]!! }
    val available = lines.findAll('.') + herbsByType.values.flatten()
    val startPos = available.single { it.row == 0 }
    val start = startPos to emptySet<Char>()
    val end = startPos to herbsByType.keys

    val queue = mutableListOf(start to 0)
    val visited = mutableMapOf(startPos to mutableListOf(emptySet<Char>()))
    while (queue.isNotEmpty()) {
        val (state, dist) = queue.removeFirst()
        val (pos, herbs) = state
        sequenceOf(pos.n(), pos.s(), pos.w(), pos.e())
            .filter { it in available }
            .map { it to if (it in allHerbs) herbs + herbsByPos[it]!! else herbs }
            .filter { (pos, herbs) -> visited[pos].orEmpty().none { it.containsAll(herbs) } }
            .forEach {
                if (it == end) return dist + 1
                visited.getOrPut(it.first) { mutableListOf() }.add(it.second)
                queue += it to dist + 1
            }
    }
    error("No herbs found")
}

fun part3(data: String): Any {
    println("Part 3 is super stupid and it takes about 5 hours to run")
    return part2(data)
}
