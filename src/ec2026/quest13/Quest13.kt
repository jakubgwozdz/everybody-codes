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

fun part2(data: String) = part1(data)

fun part3(data: String) = part2(data)

private fun calcClock(clock: List<Hand>): Long {
    var result = 0L
    (0..<clock.lastIndex).forEach { i ->
        (i + 1..clock.lastIndex).forEach { j ->
            result += calcTwoHands(clock[i], clock[j])
        }
    }
    return result
}

private fun Pair<Hand, Hand>.normalizePair(): Pair<Hand, Hand> {
    var h1 = first
    var h2 = second
    if (h1.count != h2.count) {
        val newCount = h1.count * h2.count
        h1 = h1.copy(
            pos = h1.pos * newCount / h1.count,
            count = newCount,
            speed = h1.speed * newCount / h1.count,
        )
        h2 = h2.copy(
            pos = h2.pos * newCount / h2.count,
            count = newCount,
            speed = h2.speed * newCount / h2.count,
        )
    }
    if (h1.speed == h2.speed && h1.moves != 0L && h2.moves != 0L) {
        val phase1moves = minOf(h1.moves, h2.moves)
        h1 = h1.copy(moves = h1.moves - phase1moves)
        h2 = h2.copy(moves = h2.moves - phase1moves)
    }
    if (h1.moves == 0L && h1.speed != 0L) {
        h1 = h1.copy(speed = 0)
    }
    if (h2.moves == 0L && h2.speed != 0L) {
        h2 = h2.copy(speed = 0)
    }
    if (h1.speed > h2.speed) {
        val t = h1
        h1 = h2
        h2 = t
    }
    if (h1.pos != 0L) {
        h2 = h2.copy(pos = (h2.pos - h1.pos).mod(h2.count))
        h1 = h1.copy(pos = 0)
    }
    if (h2.moves == 0L && h1.moves != 0L) {
        h1 = h1.copy(speed = -h1.speed)
        h2 = h2.copy(pos = (-h2.pos).mod(h2.count))
        val tmp = (h1 to h2).normalizePair()
        h1 = tmp.first
        h2 = tmp.second
    }
    return h1 to h2
}

private fun calcTwoHands(h1: Hand, h2: Hand): Long {
    val (h1a, h2a) = (h1 to h2).normalizePair()

    if (h1a.speed == h2a.speed && h1a.moves == h2a.moves) return 0
    val count = h1a.count.also { check(it == h2a.count) }

    // phase 1 - both hands move

    val phase1moves = minOf(h1a.moves, h2a.moves)
    val phase1speed = h2a.speed - h1a.speed // relative to hand 1
    val phase1h2pos = h2a.pos + phase1speed * phase1moves
    val phase1signals = phase1h2pos / count
    val h1b = h1a.copy(moves = h1a.moves - phase1moves)
    val h2b = h2a.copy(pos = phase1h2pos % count, moves = h2a.moves - phase1moves)

    // phase 2 - only second hand moves

    val (h1c, h2c) = (h1b to h2b).normalizePair()
    check(h1c.moves == 0L)
    val phase2speed = h2c.speed
    val phase2h2pos = h2c.pos + phase2speed * h2c.moves
    val phase2signals = phase2h2pos / count

    return phase1signals + phase2signals
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
