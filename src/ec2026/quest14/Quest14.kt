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
    go("part3", 1728) { part3(provideInput(year, quest, 3)) }
}

const val FRAME_CHAR = ' '

typealias Edge = Triple<Char, Char, Int>

fun parse(data: String): List<Edge> {
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

    return counters.entries.flatMap { (ch1, counts) ->
        counts.entries.mapNotNull { (ch2, count) ->
            if (ch2 <= ch1) null
            else Edge(ch1, ch2, count)
        }
    }
}

fun part1(data: String) = parse(data).sumOf { it.third }

fun part2(data: String) = minimalSpanningTree(parse(data))

private fun minimalSpanningTree(edges: Collection<Edge>): Int {
    var result = 0
    val disjointSets = mutableListOf<Set<Char>>()

    val sortedEdges = edges.sortedBy { it.third }

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
    val edges = parse(data)
    val vertices = buildSet {
        edges.forEach { (v1, v2, _) ->
            if (v1 != FRAME_CHAR) add(v1)
            if (v2 != FRAME_CHAR) add(v2)
        }
    }.sorted()
    return vertices.flatMapIndexed { i1, v1 ->
        (i1 + 1..vertices.lastIndex).map { i2 -> v1 to vertices[i2] }
    }.minOf { (ch1, ch2) ->
        val edges1 = edges.map {
            when {
                it.first == ch1 -> it.copy(third = 0)
                it.first == ch2 -> it.copy(third = 0)
                it.second == ch1 -> it.copy(third = 0)
                it.second == ch2 -> it.copy(third = 0)
                else -> it
            }
        }
        minimalSpanningTree(edges1)
    }
}
