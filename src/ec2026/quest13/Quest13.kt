package ec2026.quest13

import go
import provideInput
import yearAndQuestFromPackage

val exampleA = """
    hands 6:8:12
    direction CW:CCW:CW
    current 2:4:8
    move 9:7:4
""".trimIndent()

val exampleB = """
    hands 6:8:12
    direction CW:CCW:CW
    current 2:4:8
    move 9:7:4

    hands 6:8:12
    direction CW:CCW:CW
    current 1:2:2
    move 10:12:15

    hands 6:8:12
    direction CW:CCW:CW
    current 1:2:2
    move 10:12:20
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1 ex", 5) { part1(exampleA) }
    go("part1", 30) { part1(provideInput(year, quest, 1)) }
    go("part2 ex", 21) { part2(exampleB) }
    go("part2", 131400) { part2(provideInput(year, quest, 2)) }
//    go("part3 ex", exampleA) { part3(exampleA) }
    go("part3") { part3(provideInput(year, quest, 3)) }
}

data class Hand(
    val pos: Long, val count: Long, val speed: Long, val moves: Long,
)

fun part1(data: String) = parse(data).sumOf { clock ->
    calcClock(clock)
}

private fun calcClock(clock: List<Hand>): Long {
    var result = 0L
    (0..<clock.lastIndex).forEach { i ->
        (i + 1..clock.lastIndex).forEach { j ->
            result += calc(clock[i], clock[j])
        }
    }
    return result
}

private fun Hand.normalize(newCount: Long) = copy(
    pos = pos * newCount / count,
    count = newCount,
    speed = speed * newCount / count,
)

// 29 incorrect
private fun calc(h1: Hand, h2: Hand): Long {
//    println("$h1 vs $h2 => ")
//    val total = h1.count * h2.count
//    println("  normalized $n1 vs $n2")
//    val meetings = analytical(h1, h2)

    val stepByStep = stepByStep(h1, h2)

//    println("  meetings $meetings")
//    println("  step by step $stepByStep")
//    check(stepByStep == meetings)
    return stepByStep
//    return meetings
}

private fun stepByStep(h1: Hand, h2: Hand): Long {
    if (h1.count != h2.count) {
        val total = h1.count * h2.count
        return stepByStep(h1.normalize(total), h2.normalize(total))
    }

    var stepByStep = 0L
    var p1 = h1.pos
    var p2 = h2.pos
    repeat(maxOf(h1.moves, h2.moves).toInt()) { move ->
        val ep1 = if (move >= h1.moves) p1 else (p1 + h1.speed)
        val ep2 = if (move >= h2.moves) p2 else (p2 + h2.speed)
//        println("  $p1 => $ep1; $p2 => $ep2; signals: $stepByStep")
        if (met(p1, p2, ep1, ep2, h1.count)) {
            stepByStep++
//            println("  PING")
        }
        p1 = ep1.mod(h1.count)
        p2 = ep2.mod(h1.count)
    }
    return stepByStep
}

private fun met(
    sp1: Long,
    sp2: Long,
    ep1: Long,
    ep2: Long,
    total: Long
): Boolean {
    return met0(sp1, sp2, ep1, ep2)
            || met0(sp1 + total, sp2, ep1 + total, ep2)
            || met0(sp1, sp2 + total, ep1, ep2 + total)
}

private fun met0(sp1: Long, sp2: Long, ep1: Long, ep2: Long): Boolean =
    sp1 > sp2 && ep1 <= ep2 || sp1 < sp2 && ep1 >= ep2

private fun parse(data: String): Sequence<List<Hand>> = data.splitToSequence("\n\n").map { clock ->
    val input = clock.lines().associate { l -> l.substringBefore(" ") to l.substringAfter(" ").split(":") }
    val inputHands = input["hands"]!!
    val inputDirection = input["direction"]!!
    val inputCurrent = input["current"]!!
    val inputMove = input["move"]!!
    inputHands.mapIndexed { index, count ->
        Hand(
            pos = inputCurrent[index].toLong() % count.toLong(),
            count = count.toLong(),
            speed = if (inputDirection[index] == "CW") 1 else -1,
            moves = inputMove[index].toLong(),
        )
    }
}

fun part2(data: String) = part1(data)

fun part3(data: String) = part2(data)
