package ec2025.quest11

import go
import logged
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 281) { part1(provideInput(year, quest, 1)) }

    val p2ex2 = """
        805
        706
        179
        48
        158
        150
        232
        885
        598
        524
        423
    """.trimIndent()
    go("part2", 1579) { part2(p2ex2) }
    go("part2") { part2(provideInput(year, quest, 2)) }
    go("part3") { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val arr = data.lines().map { it.toInt() }.toIntArray()
    var firstPhase = true
    repeat(10) {
        arr.indices.windowed(2).forEach { (i1, i2) ->
            if (firstPhase && arr[i1] > arr[i2]) {
                arr[i1]--
                arr[i2]++
            } else if (!firstPhase && arr[i1] < arr[i2]) {
                arr[i2]--
                arr[i1]++
            }
        }
        if (firstPhase && arr.indices.windowed(2).all { (i1, i2) -> arr[i1] <= arr[i2] }) firstPhase = false
    }
    return arr.withIndex().sumOf { (i, v) -> (i + 1L) * v }
}

fun part2(data: String): Any = data.lines().map { it.toLong() }.toLongArray()
    .run { this.phase1() + this.phase2() }

fun part3(data: String): Any = data.lines().map { it.toLong() }.toLongArray()
    .run { this.phase1() + this.phase2() }

private fun LongArray.phase1(): Long {
    var result = 0L
    var finished = false
    while (!finished) {
        finished = true
        repeat(lastIndex) {
            if (this[it] > this[it + 1]) {
                finished = false
                this[it]--
                this[it + 1]++
            }
        }
        if (!finished) result += 1
    }
    return result
        .logged("phase1")
}

private fun LongArray.phase2(): Long {
    var result = 0L
    val sum = sum()
    val mid = sum / size
    forEachIndexed { i,v ->
        if (v < mid) result += (mid-v)
        this[i] = mid
    }
    return result
}
