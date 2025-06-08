package ec2024.quest14

import go
import provideInput
import yearAndQuestFromPackage

val example3 = """
    U20,L1,B1,L2,B1,R2,L1,F1,U1
    U10,F1,B1,R1,L1,B1,L1,F1,R2,U1
    U30,L2,F1,R1,B1,R1,F2,U1,F1
    U25,R1,L2,B1,U1,R2,F1,L2
    U16,L1,B1,L1,B3,L1,B1,F1
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 155) { part1(provideInput(year, quest, 1)) }
    go("part2", 4830) { part2(provideInput(year, quest, 2)) }
    go("example3", 46) { part3(example3) }
    go("part3") { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any = data.splitToSequence(",")
    .map {
        when (it.first()) {
            'U' -> it.drop(1).toInt()
            'D' -> -it.drop(1).toInt()
            else -> 0
        }
    }
    .fold(0 to 0) { (v, max), d ->
        v + d to max.coerceAtLeast(v + d)
    }.second

fun part2(data: String): Any {
    val (segments, _) = grow(data)
    return segments.size // remove origin
}

fun part3(data: String): Any {
    val (segments, leaves) = grow(data)
    val possible = segments.filter { (x, y, z) -> x == 0 && z == 0 }
    return possible.asSequence()
        .map { from -> dist(segments, from, leaves) }
        .min()
}

typealias Pos3 = Triple<Int, Int, Int>

private fun grow(data: String): Pair<Set<Pos3>, Set<Pos3>> {
    val segments = mutableSetOf<Pos3>()
    val leaves = mutableSetOf<Pos3>()
    data.lines().forEach { line ->
        line.splitToSequence(",")
            .flatMap { str ->
                val dir = str.first()
                val dist = str.drop(1).toInt()
                sequence { repeat(dist) { yield(dir) } }
            }
            .runningFold(Pos3(0, 0, 0)) { v, dir ->
                var (x, y, z) = v
                when (dir) {
                    'L' -> x--
                    'R' -> x++
                    'F' -> z++
                    'B' -> z--
                    'U' -> y++
                    'D' -> y--
                    else -> error("Unknown direction $dir")
                }
                Pos3(x, y, z)
            }
            .onEach { segments.add(it) }
            .last().let { leaves.add(it) }
    }
    return Pair(segments - Pos3(0, 0, 0), leaves)
}

private fun dist(segments: Set<Pos3>, from: Pos3, leaves: Set<Pos3>): Int {
    val toGo = leaves.toMutableSet()
    val queue = mutableListOf(from to 0)
    var sum = 0
    val visited = mutableSetOf(from)
    while (queue.isNotEmpty() && toGo.isNotEmpty()) {
        val (curr, cost) = queue.removeFirst()
        if (curr in toGo) {
            toGo.remove(curr)
            sum += cost
        }
        val (x, y, z) = curr
        sequenceOf(
            Pos3(x + 1, y, z),
            Pos3(x - 1, y, z),
            Pos3(x, y + 1, z),
            Pos3(x, y - 1, z),
            Pos3(x, y, z + 1),
            Pos3(x, y, z - 1),
        )
            .filter { it in segments && it !in visited }
            .forEach {
                visited += it
                queue.add(it to cost + 1)
            }
    }
    return sum
}
