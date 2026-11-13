package ec2026.quest14

import collections.increment
import coords.e
import coords.get
import coords.n
import coords.pair.Pos
import coords.s
import coords.w
import go
import provideInput
import yearAndQuestFromPackage

val exampleA = """
    AAAEEEFFFF
    AADDDDDGFF
    BACDDIGGFF
    BABBIIIGGH
    BBBBIIIIHH
""".trimIndent()

val exampleB = """
    IIIIIILOHHHHHHHHGGGGGQSRRRRKKK
    IIIIIILOOHHHHHHGGXGGGQSRRRRKKK
    PPPPILLLOOOODDHBGGGGGQSSRRRKKK
    PPZPLLLLFFOODDBBBBGGQQQSSRKKKK
    PPPPLLLLFFFONDDDBBMGGQQSSKKJJJ
    PPPCCCCCCFFONNDDMMMGQQQSSSKJJJ
    PCCCCCCCCFFNNDDDDMAAQQQSSSKKJJ
    PPCCECCCFFNNNNDADMAATQTTSSSJJJ
    PPEEEEECCFFNNAAAMMATTTTTSTSSSJ
    PPEEEEEEEFFNNNAAAAAATTTTTTSSJJ
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1 ex", 67) { part1(exampleA) }
    go("part1", 259) { part1(provideInput(year, quest, 1)) }
    go("part2 ex", 12) { part2(exampleA) }
    go("part2 ex 2", 63) { part2(exampleB) }
    go("part2", 433) { part2(provideInput(year, quest, 2)) }
    go("part3 ex", 0) { part3(exampleA) }
    go("part3 ex", 21) { part3(exampleB) }
    go("part3") { part3(provideInput(year, quest, 3)) }
}

const val FRAME_CHAR = ' '

fun parse(data: String): Map<Char, Map<Char, Int>> {
    val map = data.lines()
    val counters = mutableMapOf<Char, MutableMap<Char, Int>>()

    fun inc(ch1: Char, ch2: Char?) {
        counters.increment(ch1, ch2 ?: FRAME_CHAR)
    }

    map.indices.forEach { r ->
        map[r].withIndex().forEach { (c, ch) ->
            val p = Pos(r, c)
            inc(ch, map[p.n()])
            inc(ch, map[p.e()])
            inc(ch, map[p.s()])
            inc(ch, map[p.w()])
        }
    }

    counters.toList().forEach { (ch1, counts) ->
        counts[FRAME_CHAR]?.let { count ->
            counters.increment(FRAME_CHAR, ch1, count)
        }
    }

    return counters.mapValues { it.value.toMap() }
}

fun part1(data: String) = parse(data).entries.sumOf { (ch1, counts) ->
    counts.entries.sumOf { (ch2, count) ->
        if (ch1 < ch2) count else 0
    }
}

fun part2(data: String) = minimalSpanningTree(parse(data))

typealias Edge = Triple<Char, Char, Int>

private fun minimalSpanningTree(edges: Map<Char, Map<Char, Int>>): Int {
    var result = 0
    val disjointSets = mutableListOf<Set<Char>>()

    val sortedEdges = edges.flatMap { (ch1, counts) ->
        counts.mapNotNull { (ch2, count) -> if (ch1 < ch2) Edge(ch1, ch2, count) else null }
    }.sortedBy { it.third }

    sortedEdges.forEach { (ch1, ch2, len) ->
        result += len
        val s1 = disjointSets.firstOrNull { ch1 in it }
        val s2 = disjointSets.firstOrNull { ch2 in it }
        when {
            s1 == null && s2 == null -> disjointSets += mutableSetOf(ch1, ch2)
            s1 == s2 -> result -= len // skip, a cycle
            s1 == null && s2 != null -> {
                disjointSets -= s2
                disjointSets += s2 + ch1
            }
            s1 != null && s2 == null -> {
                disjointSets -= s1
                disjointSets += s1 + ch2
            }
            s1 != null && s2 != null && s1 != s2 -> {
                disjointSets -= s1
                disjointSets -= s2
                disjointSets += (s1 + s2)
            }
        }
    }
    return result
}

fun part3(data: String): Any {
    val counters = parse(data)
    return counters.keys.flatMap { ch1 ->
        if (ch1 == FRAME_CHAR) emptyList()
        else counters.keys.mapNotNull { ch2 ->
            if (ch2 == FRAME_CHAR) null
            else if (ch2 < ch1) null
            else ch1 to ch2
        }
    }.minOf { (ch1, ch2) ->
        val counters1 = counters.mapValues { (chA, counts) ->
            counts.mapValues { (chB, count) -> if (chA == ch1 || chB == ch1 || chA == ch2 || chB == ch2) 0 else count }
        }
        minimalSpanningTree(counters1)
    }
}
