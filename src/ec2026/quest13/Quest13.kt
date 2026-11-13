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
    go("part3", 1266059882463) { part3(provideInput(year, quest, 3)) }
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
            result += calcTwoHands(clock[i], clock[j])
        }
    }
    return result
}

private fun Hand.normalize(newCount: Long) = copy(
    pos = pos * newCount / count,
    count = newCount,
    speed = speed * newCount / count,
)

private fun calcTwoHands(h1: Hand, h2: Hand): Long {
    if (h1.count != h2.count) {
        return calcTwoHands(h1.normalize(h1.count * h2.count), h2.normalize(h1.count * h2.count))
    }
    if (h1.speed == h2.speed && h1.moves == h2.moves) {
        return 0
    }
    val phase1moves = minOf(h1.moves, h2.moves)
    if (h1.speed == h2.speed && h1.moves != 0L && h2.moves != 0L) {
        return calcTwoHands(h1.copy(moves = h1.moves - phase1moves), h2.copy(moves = h2.moves - phase1moves))
    }
    if (h1.speed > h2.speed) {
        return calcTwoHands(h2, h1)
    }
    if (h1.pos != 0L) {
        return calcTwoHands(h1.copy(pos = 0), h2.copy(pos = (h2.pos - h1.pos).mod(h2.count)))
    }
    if (h1.moves == 0L && h1.speed != 0L) {
        return calcTwoHands(h1.copy(speed = 0), h2)
    }
    if (h2.moves == 0L && h2.speed != 0L) {
        return calcTwoHands(h1, h2.copy(speed = 0))
    }
    if (h2.moves == 0L) {
        return calcTwoHands(h1.copy(speed = -h1.speed), h2.copy(pos = (-h2.pos).mod(h2.count)))
    }
    if (phase1moves > 0) {
        val h2pos = h2.pos + (h2.speed - h1.speed) * phase1moves
        val signals = h2pos / h2.count
        return signals + calcTwoHands(
            h1.copy(moves = h1.moves - phase1moves),
            h2.copy(pos = h2pos % h2.count, moves = h2.moves - phase1moves)
        )
    } else {
        val phase2moves = maxOf(h1.moves, h2.moves) - phase1moves
        val h2pos = h2.pos + h2.speed * phase2moves
        return h2pos / h2.count
    }
}

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
