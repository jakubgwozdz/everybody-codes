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

data class Segment(val left: Int?, val mid: Int, val right: Int?)

typealias Fishbone = List<Segment>

data class Sword(val id: Int, val fishbone: Fishbone)

fun part1(data: String): Any = data.toSword().fishbone.quality()

fun part2(data: String) = data.lines()
    .map { it.toSword().fishbone.quality() }
    .let { it.max() - it.min() }

fun part3(data: String) = data.lines()
    .map { it.toSword() }
    .sortedWith(swordComparator.reversed())
    .map { (id, _) -> id }
    .withIndex()
    .sumOf { (i, id) -> (i + 1) * id }

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

private fun Fishbone.quality() = joinToString("") { it.mid.toString() }.toLong()

private fun Segment.score() = listOfNotNull(left, mid, right).joinToString("").toInt()

private val fishboneComparator = Comparator<Fishbone> { a, b ->
    a.indices.forEach { i ->
        val a1 = a[i].score()
        val b1 = b[i].score()
        if (a1 != b1) return@Comparator a1 - b1
    }
    0
}

private val swordComparator = compareBy<Sword> { (_, fishbone) -> fishbone.quality() }
    .thenBy(fishboneComparator) { (_, fishbone) -> fishbone }
    .thenBy { (id, _) -> id }

