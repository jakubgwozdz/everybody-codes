package ec4.quest1

import go
import provideInput
import yearAndQuestFromPackage

val ex1 = """
    1,1,1,1,1
    5,1,2,3,4,5,1,2,3,4
    2,1,1,2,1,1,2,1,1,2,1,1
    5,1,2,1,2,7,1,2,1,2,7,1,2,1,2
""".trimIndent()

val ex3 = """
    5,3,1,1
    5,3,1,1,5,1,1,3,4,8,1,1
    5,3,1,1,5,1,1,3,4,8,2,1
    10,9,9,8,8,7,7,6,6,5,5,4,4,3,3,2,2,1
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1ex", 34) { part1(ex1) }
    go("part1", 321) { part1(provideInput(year, quest, 1)) }
    go("part2ex", 43) { part2(ex1) }
    go("part2") { part2(provideInput(year, quest, 2)) }
    go("part3ex", 27) { part3(ex1) }
    go("part3ex", 35) { part3(ex3) }
    go("part3") { part3(provideInput(year, quest, 3)) }
}

fun calcSec1(seq: List<Int>): Int {
    var curr = 0
    val used = mutableSetOf(curr)
    seq.forEach { n ->
        val goBack = curr - n !in used && curr - n > 0
        if (goBack) curr -= n
        else curr += n
        used += curr
    }
    return curr
}

fun calcSec2(seq: List<Int>): Int {
    val used = mutableSetOf(0)
    var curr = 0
    seq.forEach { n ->
        val goBack = curr - n !in used && curr - n > 0
        if (goBack) curr -= n
        else {
            curr += n
            while (curr in used) curr++
        }
        used += curr
    }
    return curr
}

fun calcSec3(seq: List<Int>): Int {
    val used = mutableSetOf(0)
    val upArcs = mutableSetOf<IntRange>()
    val downArcs = mutableSetOf<IntRange>()
    var curr = 0
    var up = true
    seq.forEach { n ->
        val arcs = if (up) upArcs else downArcs
        val goingBack = curr - n
        val goBack = goingBack !in used && goingBack > 0 && arcs.none { (curr in it) xor (goingBack in it) }
        if (goBack) {
            arcs += goingBack..curr
            curr = goingBack
            up = !up
            used += curr
        } else {
            var max = Int.MAX_VALUE
            arcs.forEach { if (curr in it && it.last < max) max = it.last - 1 }
            val skipped = arcs.filter { it.first > curr && it.last <= max }
            var next = curr + n
            while ((next in used || skipped.any { next in it }) && next <= max) next++
            if (next <= max) {
                arcs += curr..next
                curr = next
                up = !up
                used += curr
            }
        }

    }
    return curr
}


fun solve(data: String, op: (List<Int>) -> Int): Int = data.lines().sumOf { line ->
    line.split(",").map(String::toInt).let(op)
}

fun part1(data: String): Any = solve(data, ::calcSec1)
fun part2(data: String): Any = solve(data, ::calcSec2)
fun part3(data: String): Any = solve(data, ::calcSec3)
