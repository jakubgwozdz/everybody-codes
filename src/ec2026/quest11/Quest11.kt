package ec2026.quest11

import go
import provideInput
import yearAndQuestFromPackage

val exampleA = """
    portals: 10

    5 10
    2 7
""".trimIndent()

val exampleB = """
    portals: 32

    1 10
    20 30
    15 29
    13 10
""".trimIndent()

val exampleC = """
    portals: 64

    1 40
    3 35
    5 42
    7 50
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1 ex", 60) { part1(exampleA) }
    go("part1") { part1(provideInput(year, quest, 1)) }
    go("part2 ex", 6) { part2(exampleA) }
    go("part2") { part2(provideInput(year, quest, 2)) }
    go("part3 ex", 854) { part3(exampleB) }
    go("part3 ex", 9634) { part3(exampleC) }
    go("part3", 27952) { part3(provideInput(year, quest, 3)) }
}

fun parse(data: String) = data.lines().first().substringAfter("portals: ").toInt() to
        data.lines().drop(2).map { it.split(" ") }
            .map { (a, b) -> a.toInt() - 1 to b.toInt() - 1 }

fun part1(data: String): Any {

    val (portals, shortcuts) = parse(data)

    val edges = buildMap {
        repeat(portals) { p ->
            put(p to (p + 1).mod(portals), 1)
            put((p + 1).mod(portals) to p, 1)
        }
    }

    val r1 = portals * (portals / 2).let { it * it }

    val extraConnections = buildMap {
        shortcuts.forEach { (s, d) ->
            put(s to d, 1)
            put(d to s, 1)
        }
    }

    val r2 = calculateFloydWarshall(portals, edges + extraConnections).sum()
    return r1 - r2
}

fun part2(data: String): Any {
    val (portals, shortcuts) = parse(data)

    val edges = buildMap {
        repeat(portals) { p ->
            repeat(3) {
                put(p to (p + 1 + it).mod(portals), 1)
                put((p + 1 + it).mod(portals) to p, 1)
            }
        }
    }

    val r1 = calculateFloydWarshall(portals, edges).sum()

    // shortcuts
    val extraConnections = buildMap {
        shortcuts.forEach { (s, d) ->
            put(s to d, 1)
            put(d to s, 1)
        }
    }

    val r2 = (0..<portals).minOf { s ->
        (s + 4..<portals).minOfOrNull { d ->
            val lastConnection = buildMap {
                put(s to d, 1)
                put(d to s, 1)
            }
            calculateFloydWarshall(portals, edges + extraConnections + lastConnection).sum()
        } ?: Int.MAX_VALUE
    }

    return r1 - r2
}

fun part3(data: String): Any {
    val (portals, shortcuts) = parse(data)

    val edges = buildMap {
        repeat(portals) { p ->
            repeat(3) {
                put(p to (p + 1 + it).mod(portals), 1)
                put((p + 1 + it).mod(portals) to p, 1)
            }
        }
    }

    val r1 = calculateFloydWarshall(portals, edges).sum()
//    println(state)

    // shortcuts
    val extraConnections = buildMap {
        shortcuts.forEach { (s, d) ->
            put(s to d, 1)
            put(d to s, 1)
        }
    }

    val r2 = (0..<portals / 8).minOf { o ->

        permutations(8).filter { it.chunked(2).all { (a, b) -> a < b } }
            .minOf { list ->
                val lastConnections = buildMap {
                    list.chunked(2).forEach { (s0, d0) ->
                        val s = o + (portals / 8) * s0
                        val d = (o + (portals / 8) * d0) % portals
                        put(s to d, 1)
                        put(d to s, 1)
                    }
                }
                calculateFloydWarshall(portals, edges + extraConnections + lastConnections).sum()
            }

    }
    return r1 - r2
}

fun permutations(n: Int): Sequence<List<Int>> {
    val a = IntArray(n) { it }
    return sequenceOf(a.toList()) + generateSequence {
        var i = a.size - 1
        while (i > 0 && a[i] <= a[i - 1]) i--
        if (i <= 0) return@generateSequence null
        var j = a.size - 1
        while (a[j] <= a[i - 1]) j--
        a.swap(i - 1, j)
        j = a.size - 1
        while (i < j) a.swap(i++, j--)
        a.toList()
    }
}

fun IntArray.swap(i: Int, j: Int) {
    val v = this[i]
    this[i] = this[j]
    this[j] = v
}

fun calculateFloydWarshall(
    size: Int, edges: Map<Pair<Int, Int>, Int>
): Array<IntArray> {
    val even = Array(size) { IntArray(size) { size } }
    val odd = Array(size) { IntArray(size) { size } }

    var current = even
    var prev = odd
    fun swap() {
        val tmp = prev
        prev = current
        current = tmp
    }

    edges.forEach { (p, d) -> current[p.first][p.second] = d }

    repeat(size) { k ->
        swap()
        repeat(size) { i ->
            repeat(size) { j ->
                current[i][j] = prev[i][j].coerceAtMost(prev[i][k] + prev[k][j])
            }
        }
    }
    return current
}

fun Array<IntArray>.sum() = 2 * indices.sumOf { i -> (i + 1..this[i].lastIndex).fold(0) { acc, j -> acc + this[i][j] } }
