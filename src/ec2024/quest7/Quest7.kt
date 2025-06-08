package ec2024.quest7

import coords.e
import coords.findAll
import coords.n
import coords.pair.Pos
import coords.pair.col
import coords.pair.row
import coords.s
import coords.w
import go
import provideInput
import yearAndQuestFromPackage

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", "DHIEABKCF") { part1(provideInput(year, quest, 1)) }
    go("part2", "IJAHEDGCF") { part2(provideInput(year, quest, 2)) }
    go("part3", 3175) { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val chariots = parse(data)
    val track = listOf(0)
    return chariots.mapValues { (_, v) -> essence(v, track, 10) }
        .toList().sortedByDescending { it.second }.joinToString("") { it.first }
}

val part2track = """
    S-=++=-==++=++=-=+=-=+=+=--=-=++=-==++=-+=-=+=-=+=+=++=-+==++=++=-=-=--
    -                                                                     -
    =                                                                     =
    +                                                                     +
    =                                                                     +
    +                                                                     =
    =                                                                     =
    -                                                                     -
    --==++++==+=+++-=+=-=+=-+-=+-=+-=+=-=+=--=+++=++=+++==++==--=+=++==+++-
""".trimIndent()

fun part2(data: String): Any {
    val chariots = parse(data)
    val track = parseTrack(part2track)
    return chariots.mapValues { (_, v) -> essence(v, track, 10) }
        .toList().sortedByDescending { it.second }.joinToString("") { it.first }
}

private fun essence(plan: List<Int>, track: List<Int>, loops: Int): Int {
    var power = 10
    var total = 0
    repeat(loops * track.size) {
        power += track[it % track.size].takeIf { it != 0 } ?: plan[it % plan.size]
        if (power < 0) power = 0
        total += power
    }
    return total
}

val part3track = """
    S+= +=-== +=++=     =+=+=--=    =-= ++=     +=-  =+=++=-+==+ =++=-=-=--
    - + +   + =   =     =      =   == = - -     - =  =         =-=        -
    = + + +-- =-= ==-==-= --++ +  == == = +     - =  =    ==++=    =++=-=++
    + + + =     +         =  + + == == ++ =     = =  ==   =   = =++=
    = = + + +== +==     =++ == =+=  =  +  +==-=++ =   =++ --= + =
    + ==- = + =   = =+= =   =       ++--          +     =   = = =--= ==++==
    =     ==- ==+-- = = = ++= +=--      ==+ ==--= +--+=-= ==- ==   =+=    =
    -               = = = =   +  +  ==+ = = +   =        ++    =          -
    -               = + + =   +  -  = + = = +   =        +     =          -
    --==++++==+=+++-= =-= =-+-=  =+-= =-= =--   +=++=+++==     -=+=++==+++-
""".trimIndent()

private val cache = mutableMapOf<Triple<Int, Int, Int>, List<List<Int>>>()

fun generatePlans(pluses: Int, minuses: Int, zeros: Int): List<List<Int>> =
    cache.getOrPut(Triple(pluses, minuses, zeros)) {
        if (pluses == 0 && minuses == 0 && zeros == 0) listOf(emptyList())
        else buildList<List<Int>> {
            if (pluses > 0) generatePlans(pluses - 1, minuses, zeros).forEach { add(it + 1) }
            if (minuses > 0) generatePlans(pluses, minuses - 1, zeros).forEach { add(it + -1) }
            if (zeros > 0) generatePlans(pluses, minuses, zeros - 1).forEach { add(it + 0) }
        }
    }

fun part3(data: String): Any {
    val track = parseTrack(part3track)
    val opponent = parse(data).values.single()

    fun essenceForTrack(plan: List<Int>) = essence(plan, track, 11)

    val scores = generatePlans(5, 3, 3).associateWith(::essenceForTrack)

    val opponentScore = scores[opponent]!!
    return scores.count { it.value > opponentScore }
}


private fun parse(data: String): Map<String, List<Int>> = data.lines().associate {
    it.split(":").let {
        it[0] to it[1].split(",").map { c -> charAsDelta(c.single()) }
    }
}

private fun charAsDelta(c: Char): Int = when (c) {
    '-' -> -1
    '=', 'S' -> 0
    '+' -> 1
    else -> error("Invalid char $c")
}


private fun parseTrack(track: String): List<Int> {
    val lines = track.lines()
    val s = lines.findAll('S').single()
    val nonBlank = lines.flatMapIndexed { r, line ->
        line.indices.filterNot { c -> line[c].isWhitespace() }.map { c -> Pos(r, c) }
    }

    return generateSequence(s to s.e()) { (from, pos) ->
        listOf(pos.n(), pos.e(), pos.s(), pos.w())
            .filter { it in nonBlank }
            .single { it != from }
            .let { pos to it }
    }
        .take(nonBlank.size)
        .map { (_, pos) -> charAsDelta(lines[pos.row][pos.col]) }.toList()
}

