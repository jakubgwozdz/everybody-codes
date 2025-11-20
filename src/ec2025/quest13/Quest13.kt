package ec2025.quest13

import go
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    val p1ex1 = """
        72
        58
        47
        61
        67
    """.trimIndent()
    go("p1ex1", 67) { part1(p1ex1) }
    go("part1", 797) { part1(provideInput(year, quest, 1)) }
    val p2ex1 = """
        10-15
        12-13
        20-21
        19-23
        30-37
    """.trimIndent()
    go("p2ex1", 30) { part2(p2ex1) }
    go("part2") { part2(provideInput(year, quest, 2)) }
    go("part3") { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val input = data.lines().map { it.toInt() }
    val dial = IntArray(input.size + 1)
    dial[0] = 1
    var i = 1
    input.forEach { d ->
        if (i > 0) {
            dial[i] = d
            i = -i
        } else {
            dial[dial.size + i] = d
            i = -i + 1
        }
    }
    return dial[2025 % dial.size]
}

fun part2(data: String): Any {
    val ranges = data.lines().map { it.split('-').let { (a, b) -> a.toInt()..b.toInt() } }
    val size = ranges.sumOf { it.last - it.first + 1 }
    val dial = IntArray(size + 1)
    dial[0] = 1
    var i = 1
    var j = dial.size - 1
    var clockwise = true
    ranges.forEach { r ->
        if (clockwise) {
            r.forEach { d -> dial[i++] = d }
        } else {
            r.forEach { d -> dial[j--] = d }
        }
        clockwise = !clockwise
    }
    return dial[20252025 % dial.size]
}

fun part3(data: String): Any {
    return data
}
