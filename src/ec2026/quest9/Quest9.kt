package ec2026.quest9

import go
import provideInput
import yearAndQuestFromPackage

val exampleA = """
    id=1, growth=3, limit=10
    id=2, growth=5, limit=20
    id=3, growth=7, limit=30
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1 ex", 27) { part1(exampleA) }
    go("part1", 221) { part1(provideInput(year, quest, 1)) }
    go("part2 ex", 42) { part2(exampleA) }
    go("part2", 15507) { part2(provideInput(year, quest, 2)) }
    go("part3", 861560957592476) { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String) = parseAndCalculate(
    data,
    growWhile = { _, limit -> sumOf { it.count } / 2 + sumOf { it.count * it.length } < limit },
    growBy = { _, _ -> 1L }
)

fun part2(data: String) = parseAndCalculate(
    data,
    growWhile = { _, limit -> sumOf { it.count } / 2 + 1 < limit },
    growBy = { growth, _ -> growth - this[0].length }
)

private fun parseAndCalculate(
    data: String,
    growWhile: List<Counter>.(Long, Long) -> Boolean,
    growBy: List<Counter>.(Long, Long) -> Long
): Long = data.lines().map {
    it.split(",").map { it.substringAfter("=").toLong() }
}.sumOf { (_, growth, limit) ->
    var elapsed = 0L
    val bc = mutableListOf(Counter(0, 1))
    while (bc.growWhile(growth, limit)) {
        val delta = bc.growBy(growth, limit)
        bc.forEach { it.length += delta }
        if (bc[0].length == growth) bc.splitFirst()
        elapsed += delta
    }
    elapsed.coerceAtLeast(1)
}

data class Counter(var length: Long, var count: Long)

private fun MutableList<Counter>.splitFirst() {
    val (len0, cnt0) = removeFirst()
    add(Counter(1, cnt0))
    val index = binarySearchDesc(len0 / 2)
    if (index >= 0) {
        check(this[index].length == len0 / 2)
        this[index].count += cnt0 * 2
    } else {
        add(-index - 1, Counter(len0 / 2, cnt0 * 2))
    }
}

private fun List<Counter>.binarySearchDesc(element: Long): Int {

    var low = 0
    var high = size - 1

    while (low <= high) {
        val mid = (low + high).ushr(1) // safe from overflows
        val midVal = get(mid)
        val cmp = -compareValues(midVal.length, element)

        if (cmp < 0)
            low = mid + 1
        else if (cmp > 0)
            high = mid - 1
        else
            return mid // key found
    }
    return -(low + 1)  // key not found
}

fun part3(data: String) = part2(data)
