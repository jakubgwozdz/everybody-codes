package ec2025.quest8

import go
import provideInput
import yearAndQuestFromPackage
import kotlin.math.max
import kotlin.math.min

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 64) { part1(provideInput(year, quest, 1)) }
    go("part2", 2925213) { part2(provideInput(year, quest, 2)) }
    go("part3", 2800) { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String) = data.parseToPairs()
    .count { (a, b) -> a + 16 == b }

fun part2(data: String) = data.parseToPairs().let { pairs ->
    pairs.indices.sumOf { i -> pairs.subList(0, i).count { it.intersects(pairs[i]) } }
}

fun part3(data: String) = data.parseToPairs().let { pairs ->
    (2..256).maxOf { b ->
        (1..<b).maxOf { a ->
            val other = a to b
            pairs.count { it.intersects(other) || it == other }
        }
    }
}

fun String.parseToPairs(): List<Pair<Int, Int>> =
    split(",").map { it.toInt() }.windowed(2).map { (a, b) -> min(a, b) to max(a, b) }

fun Pair<Int, Int>.intersects(other: Pair<Int, Int>): Boolean {
    val (a, b) = this
    val (c, d) = other
    return (a < c && c < b && b < d) || (c < a && a < d && d < b)
}
