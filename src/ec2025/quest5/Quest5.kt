package ec2025.quest5

import go
import provideInput
import yearAndQuestFromPackage

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 6462634285) { part1(provideInput(year, quest, 1)) }
    go("part2", 8994899313648) { part2(provideInput(year, quest, 2)) }
    go("part3", 30329994) { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any = data.toSword().fishbone.quality()

fun part2(data: String) = data.lines()
    .map { it.toSword().fishbone.quality() }
    .let { it.max() - it.min() }

fun part3(data: String) = data.lines()
    .map { it.toSword() }
    .sortedWith(swordComparator.reversed())
    .withIndex()
    .sumOf { (i, sword) -> (i + 1) * sword.id }

private fun String.toSword() = split(":").let { (id, fishbone) -> Sword(id.toInt(), fishbone.toFishbone()) }

private fun String.toFishbone(): Fishbone {
    val numbers = split(",").map { it.toInt() }
    val fishbone = mutableListOf<Segment>()
    numbers.forEach { num ->
        fishbone.forEachIndexed { index, (left, mid, right) ->
            if (left == null && mid > num) {
                fishbone[index] = Segment(num, mid, right)
                return@forEach
            }
            if (right == null && mid < num) {
                fishbone[index] = Segment(left, mid, num)
                return@forEach
            }
        }
        fishbone.add(Segment(null, num, null))
    }
    return fishbone.toList()
}

data class Segment(val left: Int?, val mid: Int, val right: Int?)

fun Segment.score() = listOfNotNull(left, mid, right).joinToString("").toInt()

typealias Fishbone = List<Segment>

fun Fishbone.quality() = joinToString("") { it.mid.toString() }.toLong()

data class Sword(val id: Int, val fishbone: Fishbone)

val fishboneComparator = Comparator<Fishbone> { a, b ->
    require(a.size == b.size) { "different lengths" }
    a.indices.firstNotNullOfOrNull { i -> (a[i].score() - b[i].score()).takeIf { it != 0 } } ?: 0
}

val swordComparator = compareBy<Sword> { it.fishbone.quality() }
    .thenBy(fishboneComparator) { it.fishbone }
    .thenBy { it.id }
