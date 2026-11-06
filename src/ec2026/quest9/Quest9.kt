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

fun part1(data: String) = parseAndCalculate(data) { growth, limit ->
    var elapsed = 0L
    val branches = mutableListOf(0L)
    while (branches.sum() + branches.count() / 2 < limit) {
        elapsed++
        branches.grow(growth)
    }
    elapsed
}

private fun parseAndCalculate(data: String, op: (Long, Long) -> Long): Long = data.lines().map {
    it.split(",").map { it.substringAfter("=").toLong() }
}.sumOf { (_, growth, limit) -> op(growth, limit) }

data class Counter(var length: Long, var count: Long)

fun part2(data: String) = parseAndCalculate(data) { growth, limit ->
    var elapsed = 0L
    val bc = mutableListOf(Counter(0, 1))
    while (bc.sumOf { it.count } / 2 + 1 < limit) {
        elapsed += bc.growUntil(growth)
        check(bc.zipWithNext().all { (a, b) -> a.length > b.length }) { "mismatch order in $bc" }
    }
    elapsed.coerceAtLeast(1)
}

private fun MutableList<Long>.grow(growth: Long) = indices.forEach { i ->
    this[i]++
    if (this[i] == growth) {
        val t = this[i]
        this[i] = t / 2
        add(t / 2)
        add(1)
    }
}

private fun MutableList<Counter>.growUntil(growth: Long): Long {
    val (len0, cnt0) = removeFirst()
    val delta = growth - len0
    forEach { it.length += delta }
    add(Counter(1, cnt0))
    val index = binarySearchDesc(growth / 2)
    if (index >= 0) {
        check(this[index].length == growth / 2)
        this[index].count += cnt0 * 2
    } else {
        add(-index - 1, Counter(growth / 2, cnt0 * 2))
    }
    return delta
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
// 861560957592475 correct/correct -> incorrect
