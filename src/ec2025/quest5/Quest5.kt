package ec2025.quest5

import go
import provideInput
import yearAndQuestFromPackage

val ex3 = """
    1:7,1,9,1,6,9,8,3,7,2
    2:6,1,9,2,9,8,8,4,3,1
    3:7,1,9,1,6,9,8,3,8,3
    4:6,1,9,2,8,8,8,4,3,1
    5:7,1,9,1,6,9,8,3,7,3
    6:6,1,9,2,8,8,8,4,3,5
    7:3,7,2,2,7,4,4,6,3,1
    8:3,7,2,2,7,4,4,6,3,7
    9:3,7,2,2,7,4,1,6,3,7
""".trimIndent()

val ex3a = """
    1:7,1,9,1,6,9,8,3,7,2
    2:7,1,9,1,6,9,8,3,7,2
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 6462634285) { part1(provideInput(year, quest, 1)) }
    go("part2", 8994899313648) { part2(provideInput(year, quest, 2)) }
    go("part3ex", 260) { part3(ex3) }
    go("part3exa", 4) { part3(ex3a) }
    go("part3", 30329994) { part3(provideInput(year, quest, 3)) }
}

typealias Segment = Triple<Int?, Int, Int?>
typealias Fishbone = List<Segment>

fun part1(data: String): Any = quality(data.substringAfter(":").toFishbone())

fun part2(data: String) = data.lines()
    .map { quality(it.substringAfter(":").toFishbone()) }
    .let { it.max() - it.min() }

fun part3(data: String) = data.lines()
    .map { it.split(":") }
    .map { (id, fishbone) -> id.toInt() to fishbone.toFishbone() }
    .sortedWith(
        compareByDescending<Pair<Int, Fishbone>> { (_, fishbone) -> quality(fishbone) }
            .thenByDescending(fishboneComparator) { (_, fishbone) -> fishbone }
            .thenByDescending { (id, _) -> id })
    .map { (id, _) -> id }
    .withIndex()
    .sumOf { (i, id) -> (i + 1) * id }

private fun quality(fishbone: Fishbone) =
    fishbone.joinToString("") { it.second.toString() }.toLong()

private fun String.toFishbone(): Fishbone {
    fun MutableList<Segment>.append(num: Int) {
        forEachIndexed { index, triple ->
            if (triple.first == null && triple.second > num) {
                this[index] = Triple(num, triple.second, triple.third)
                return
            }
            if (triple.third == null && triple.second < num) {
                this[index] = Triple(triple.first, triple.second, num)
                return
            }
        }
        add(Triple(null, num, null))
    }

    val numbers = split(",").map { it.toInt() }
    val fishbone = mutableListOf<Segment>()
    numbers.forEach { num -> fishbone.append(num) }
    return fishbone.toList()
}

fun Segment.score() = listOfNotNull(first, second, third).joinToString("").toInt()

internal val fishboneComparator = Comparator<Fishbone> { a, b ->
    a.indices.forEach { i ->
        val a1 = a[i].score()
        val b1 = b[i].score()
        if (a1 != b1) return@Comparator a1 - b1
    }
    0
}
