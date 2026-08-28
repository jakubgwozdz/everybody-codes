package ec4.quest3

import go
import provideInput
import yearAndQuestFromPackage

val ex1 = """
    width=30
    height=10
    horizontal-offsets=10011
    vertical-offsets=11011
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1ex", 27) { part1(ex1) }
    go("part1") { part1(provideInput(year, quest, 1)) }
    go("part2ex", 15) { part2(ex1) }
    go("part2", 11447) { part2(provideInput(year, quest, 2)) }
    go("part3", 549738103115944) { part3(provideInput(year, quest, 3)) }
}

fun List<Int>.matches(which: Int, where: Int): Boolean = (where - this[which % this.size]) % 2 == 0

fun part1(data: String): Any = calculateOddAndEven(data).let { (odd, even) -> odd + even }
fun part2(data: String): Any = calculateOddAndEven(data).let { (odd, even) -> maxOf(odd, even) }
fun part3(data: String): Any = calculateOddAndEven(data).let { (odd, even) -> maxOf(odd, even) }

private fun calculateOddAndEven(data: String): Pair<Long, Long> {
    val parsed = data.reader().readLines().associate { it.substringBefore("=") to it.substringAfter("=") }
    val ho = parsed["horizontal-offsets"]!!.map { it.digitToInt() }
    val vo = parsed["vertical-offsets"]!!.map { it.digitToInt() }
    val width = parsed["width"]!!.toInt()
    val height = parsed["height"]!!.toInt()

    var odd = 0L
    var even = 0L

    var firstOdd = false

    var oddSnapshotRow = 0L
    var evenSnapshotRow = 0L

    var r = 0
    while (r < height) {
        if (r == 1) {
            oddSnapshotRow = odd
            evenSnapshotRow = even
        }
        val round1 = ho.size * 2
        if (r == 1 + round1) {
            val repetitions = (height - r) / round1
            odd += (odd - oddSnapshotRow) * repetitions
            even += (even - evenSnapshotRow) * repetitions
            r += repetitions * round1
        }
        if (r > 0 && ho.matches(r, 0)) firstOdd = !firstOdd

        if (ho.matches(r, 0) == ho.matches(r + 1, 0)) {

            var nextOdd = firstOdd
            var c = 0
            var oddSnapshotCol = 0L
            var evenSnapshotCol = 0L
            while (c < width) {
                if (c == 1) {
                    oddSnapshotCol = odd
                    evenSnapshotCol = even
                }
                val round2 = vo.size * 2
                if (c == 1 + round2) {
                    val repetitions = (width - c) / round2
                    odd += (odd - oddSnapshotCol) * repetitions
                    even += (even - evenSnapshotCol) * repetitions
                    c += repetitions * round2
                }
                val top = ho.matches(r, c)
                val bottom = ho.matches(r + 1, c)
                val left = vo.matches(c, r)
                val right = vo.matches(c + 1, r)
                if (c > 0 && left) nextOdd = !nextOdd
                val isolated = top && bottom && left && right
                if (isolated) {
                    if (nextOdd) odd++ else even++
                }
                c++
            }
        }
        r++
    }
    return odd to even
}
