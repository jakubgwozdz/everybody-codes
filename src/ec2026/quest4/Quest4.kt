package ec2026.quest4

import go
import provideInput
import yearAndQuestFromPackage

private val ex1 = """
    id=1 quest=1 part=1 time=187
    id=3 quest=1 part=1 time=413
    id=5 quest=1 part=3 time=744
    id=2 quest=1 part=3 time=745
    id=4 quest=1 part=2 time=558
    id=1 quest=1 part=2 time=299
    id=1 quest=1 part=3 time=362
    id=4 quest=1 part=3 time=1268
    id=4 quest=1 part=1 time=457
    id=5 quest=1 part=1 time=257
    id=2 quest=1 part=1 time=192
    id=3 quest=1 part=3 time=1168
    id=5 quest=1 part=2 time=346
    id=3 quest=1 part=2 time=455
    id=2 quest=1 part=2 time=562
""".trimIndent()

private val ex2 = """
    id=2 quest=1 part=1 time=100
    id=4 quest=2 part=1 time=150
    id=1 quest=2 part=1 time=140
    id=1 quest=1 part=2 time=180
    id=2 quest=2 part=1 time=130
    id=4 quest=1 part=1 time=110
    id=1 quest=2 part=2 time=210
    id=2 quest=2 part=2 time=240
    id=1 quest=2 part=3 time=330
    id=2 quest=1 part=2 time=170
    id=3 quest=1 part=1 time=90
    id=4 quest=1 part=2 time=175
    id=3 quest=2 part=1 time=120
    id=1 quest=1 part=1 time=120
    id=3 quest=2 part=2 time=220
    id=2 quest=2 part=3 time=350
    id=3 quest=1 part=2 time=160
""".trimIndent()

private val ex3 = """
    id=2 quest=1 part=1 time=90
    id=4 quest=2 part=1 time=150
    id=1 quest=2 part=1 time=130
    id=1 quest=1 part=2 time=180
    id=2 quest=2 part=1 time=130
    id=4 quest=1 part=1 time=110
    id=1 quest=2 part=2 time=210
    id=2 quest=2 part=2 time=220
    id=1 quest=2 part=3 time=330
    id=2 quest=1 part=2 time=170
    id=3 quest=1 part=1 time=90
    id=4 quest=1 part=2 time=170
    id=3 quest=2 part=1 time=120
    id=1 quest=1 part=1 time=120
    id=3 quest=2 part=2 time=220
    id=2 quest=2 part=3 time=350
    id=3 quest=1 part=2 time=170
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1ex", 27598) { part1(ex1) }
    go("part1", 207424) { part1(provideInput(year, quest, 1)) }
    go("part2ex", 6470) { part2(ex2) }
    go("part2", 618032094) { part2(provideInput(year, quest, 2)) }
    go("part3ex", 2998) { part3(ex2) }
    go("part3ex", 3004) { part3(ex3) }
    go("part3", 7291340) { part3(provideInput(year, quest, 3)) }
}

data class Entry(val id: Int, val quest: Int, val part: Int, val time: Int)

fun part1(data: String) = parse(data)
    .groupBy { it.id }
    .map { (_, v) -> v.sumOf { it.time } }
    .sorted()
    .checksum()

fun part2(data: String) = parse(data)
    .groupBy { it.id }
    .map { (_, v) -> v.count() to v.sumOf { it.time } }
    .sortedWith(compareByDescending<Pair<Int, Int>> { it.first }.thenBy { it.second })
    .map { it.second }
    .checksum()

fun part3(data: String) = parse(data)
    .groupBy { it.quest }
    .values
    .map { entries ->
        val p1 = entries.filter { it.part == 1 }.associateBy { it.id }
        val p2 = entries.filter { it.part == 2 }.associateBy { it.id }
        val p3 = entries.filter { it.part == 3 }.associateBy { it.id }

        val p1score = p1.mapValues { (_, v) -> v.time }.score(50)
        val p2score = p2.mapValues { (id, v) -> v.time + p1[id]!!.time }.score(100)
        val p3score = p3.mapValues { (id, v) -> v.time + p1[id]!!.time + p2[id]!!.time }.score(150)

        (p1score.keys + p2score.keys + p3score.keys).associateWith {
            (p1score[it] ?: 0) + (p2score[it] ?: 0) + (p3score[it] ?: 0)
        }
    }
    .reduce { p1, p2 -> (p1.keys + p2.keys).associateWith { (p1[it] ?: 0) + (p2[it] ?: 0) } }
    .values.toList().sortedDescending().checksum()

// times to scores
private fun Map<Int, Int>.score(max: Int) = toList()
    .sortedBy { (_, time) -> time }
    .let { ordered ->
        val keys = ordered.map { (k, _) -> k }
        val values = ordered.map { (_, v) -> v }
        keys.zip(values.orderNumbers { (max - it).coerceAtLeast(0) })
    }.toMap()

private fun List<Int>.checksum() = this.zip(orderNumbers { it + 1 }).fold(0) { acc, (score, pos) -> acc + pos * score }

private fun List<Int>.orderNumbers(op: (Int) -> Int) = Array(size) { 0 }.also {
    it.indices.forEach { i ->
        it[i] = if (i > 0 && this[i] == this[i - 1]) it[i - 1] else op(i)
    }
}

private fun parse(data: String) = data.lines().map { line ->
    val (id, quest, part, time) = line.split(" ").map { it.substringAfter("=").toInt() }
    Entry(id, quest, part, time)
}
