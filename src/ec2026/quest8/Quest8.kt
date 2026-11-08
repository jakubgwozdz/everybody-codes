package ec2026.quest8

import coords.e
import coords.get
import coords.ne
import coords.nw
import coords.pair.Pos
import coords.se
import coords.set
import coords.sw
import coords.w
import go
import provideInput
import yearAndQuestFromPackage

val exampleA = """
    ....1.9.6.1.7....
    ...9.2.3.3.8.1...
    ..7.8.3.5.1.4.3..
    .4.6.7.4.1.1.8.9.
    4.5.3.6.5.8.9.4.9
    .9.5.1.5.6.2.8.9.
    ..7.7.6.4.6.2.4..
    ...9.3.8.3.8.4...
    ....1.6.6.2.8....
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1 ex", 32) { part1(exampleA) }
    go("part1") { part1(provideInput(year, quest, 1)) }
    go("part2 ex", 187) { part2(exampleA) }
    go("part2") { part2(provideInput(year, quest, 2)) }
    go("part3 ex",899889) { part3(exampleA) }
    go("part3",899311) { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val map = data.lines().map { line -> line.map { it.digitToIntOrNull() } }
    return calculateP1P2(map, getRowsSE(map))
}

fun part2(data: String): Any {
    val map = data.lines().map { line -> line.map { it.digitToIntOrNull() } }
    return listOf(
        getRowsSE(map),
        getRowsSW(map),
        getRowsW(map),
        getRowsNW(map),
        getRowsNE(map),
        getRowsE(map)
    ).sumOf { calculateP1P2(map, it) }
}

private fun calculateP1P2(
    map: List<List<Int?>>,
    rows: List<List<Pos>>,
): Int = rows.sumOf { row ->
    row.fold(0 to 0) { (total, shaded), p ->
        val h = map[p]!!
        val nextShadow = h.coerceAtLeast(shaded) - 1
        val energized = h > shaded
        (if (energized) total + 1 else total) to nextShadow
    }.first
}

fun part3(data: String): Any {
    val initial = data.lines().map { line -> line.map { it.digitToIntOrNull() } }
    val rowsList = listOf(
        getRowsSE(initial),
        getRowsSW(initial),
        getRowsW(initial),
        getRowsNW(initial),
        getRowsNE(initial),
        getRowsE(initial)
    )
    var round = 0
    var grown = 0
    var energy = 0
    fun nextRows() = rowsList[round++ % 6]
    val mutableState = initial.map { it.toMutableList() }
    while (grown < 100000) {
        val rows = nextRows()
        rows.forEach { row ->
            var shadow = 0
            row.forEach { p ->
                val h = mutableState[p]!!
                if (h <= 0) mutableState[p] = h + 1
                else if (h == 9) {
                    mutableState[p] = -2
                    energy++
                    grown++
                } else if (h > shadow) {
                    mutableState[p] = h + 1
                    energy++
                }
                shadow = h.coerceAtLeast(shadow) - 1
            }
        }
//        println("Round: ${round+1}")
//        println()
//        mutableState.forEach { p -> println(p.joinToString("") { d-> if (d==null) "." else if (d <= 0) "X" else "$d" })}
//        if (round == 10) TODO()
    }
    return energy
}

private fun generateRow(
    map: List<List<Int?>>,
    start: Pos,
    move: (Pos) -> Pos
): List<Pos> = generateSequence(start) { prev -> move(prev).takeIf { map[it] != null } }.toList()

private fun getRowsSE(map: List<List<Int?>>) = map.first().indices.mapNotNull { c ->
    Pos(map.indexOfFirst { it[c] != null }, c).takeIf { map[it.nw()] == null }
}.map { generateRow(map, it) { p: Pos -> p.se() } }

private fun getRowsSW(map: List<List<Int?>>) = map.first().indices.mapNotNull { c ->
    Pos(map.indexOfFirst { it[c] != null }, c).takeIf { map[it.ne()] == null }
}.map { generateRow(map, it) { p: Pos -> p.sw() } }

private fun getRowsW(map: List<List<Int?>>) = map.mapIndexed { r, line ->
    Pos(r, line.indexOfLast { it != null })
}.map { generateRow(map, it) { p: Pos -> p.w().w() } }

private fun getRowsNW(map: List<List<Int?>>) = map.first().indices.mapNotNull { c ->
    Pos(map.indexOfLast { it[c] != null }, c).takeIf { map[it.se()] == null }
}.map { generateRow(map, it) { p: Pos -> p.nw() } }

private fun getRowsNE(map: List<List<Int?>>) = map.first().indices.mapNotNull { c ->
    Pos(map.indexOfLast { it[c] != null }, c).takeIf { map[it.sw()] == null }
}.map { generateRow(map, it) { p: Pos -> p.ne() } }

private fun getRowsE(map: List<List<Int?>>) = map.mapIndexed { r, line ->
    Pos(r, line.indexOfFirst { it != null })
}.map { generateRow(map, it) { p: Pos -> p.e().e() } }

