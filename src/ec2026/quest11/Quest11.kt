package ec2026.quest11

import go
import provideInput
import yearAndQuestFromPackage

val exampleA = """
    portals: 10

    5 10
    2 7
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1 ex", 60) { part1(exampleA) }
    go("part1") { part1(provideInput(year, quest, 1)) }
    go("part2 ex", 6) { part2(exampleA) }
    go("part2") { part2(provideInput(year, quest, 2)) }
//    go("part3 ex", exampleA) { part3(exampleA) }
//    go("part3") { part3(provideInput(year, quest, 3)) }
}

fun self(portals: Int) = (0..<portals).associate { (it to it) to 0 }
fun ring(portals: Int) = (0..<portals).associate { (it to (it + 1) % portals) to 1 } +
        (0..<portals).associate { ((it + 1) % portals to it) to 1 }

data class State(
    val portals: Int,
    val edges: Map<Pair<Int, Int>, Int>,
) {

    fun reset() {
//        cache.clear()
//        cache.putAll(initialCache(portals))
    }

    fun add(src: Int, dst: Int, distance: Int = 1) {
//        cache[key(src, dst)] = distance
//        cache[key(dst, src)] = distance
    }

    private fun key(src: Int, dst: Int): Pair<Int, Int> {
        val s = src.mod(portals)
        val d = dst.mod(portals)
        return s to d
//        return if (s <= d) s to d else d to s
    }

//    fun calcDistance(src: Int, dst: Int): Int = calcDistance(key(src, dst))
//    fun calcDistance(key: Pair<Int, Int>): Int {
//        val existing = cache[key]
//        if (existing != null) return existing
//        val (src, dst) = key
//        listOf(src - 1 to dst, src to dst - 1, src + 1 to dst, src to dst + 1)
//            .map { (s, d) -> key(s, d) }
//            .forEach { nextK ->
//                cache[key] = (cache[key] ?: portals).coerceAtMost(calcDistance(nextK) + 1)
//            }
//        return cache[key]!!
//    }
//
//    fun calcRow(src: Int) = (0..<portals).sumOf { calcDistance(src, it) }

    private val cache = Array(portals) { Array(portals) { null as Int? } }

    fun calc(): Int {
        val even = Array(portals) { IntArray(portals) { portals } }
        val odd = Array(portals) { IntArray(portals) { portals } }

        var current = even
        var prev = odd
        fun swap() {
            val tmp = prev
            prev = current
            current = tmp
        }
//        fun current() = if (currentEven) even else odd
//        fun prev() = if (currentEven) odd else even

        edges.forEach { (p, d) -> current[p.first][p.second] = d }

        repeat(portals) { k ->
            swap()
            repeat(portals) { i ->
                repeat(portals) { j ->
                    current[i][j] = prev[i][j].coerceAtMost(prev[i][k] + prev[k][j])
                }
            }
        }
        repeat(portals) { i ->
            repeat(portals) { j ->
                cache[i][j] = current[i][j]
            }
        }

        return (0..<portals).sumOf { current[it].sum() }
    }

    override fun toString(): String = (0..<portals).joinToString("\n") { src ->
        (0..<portals).joinToString("") { dst -> cache[src][dst]?.let { "%3d".format(it) } ?: "   " }
    }

//    fun clone(): State {
//        return copy(cache = cache.toMutableMap())
//    }
}

private fun initialCache(portals: Int) = (0..<portals).associate { (it to it) to 0 }

fun parse(data: String) = data.lines().first().substringAfter("portals: ").toInt() to
        data.lines().drop(2).map { it.split(" ") }
            .map { (a, b) -> a.toInt() - 1 to b.toInt() - 1 }

fun part1(data: String): Any {

    val (portals, shortcuts) = parse(data)

    val edges = buildMap {
        repeat(portals) { p ->
            put(p to p, 0)
            put(p to (p + 1).mod(portals), 1)
            put((p + 1).mod(portals) to p, 1)
        }
    }

    val state = State(portals, edges)

    val r1 = state.portals * (state.portals / 2).let { it * it }

    val extraConnections = buildMap {
        shortcuts.forEach { (s, d) ->
            put(s to d, 1)
            put(d to s, 1)
        }
    }

    val r2 = State(portals, edges + extraConnections).calc()
    return r1 - r2
}

fun part2(data: String): Any {
    val (portals, shortcuts) = parse(data)

    val edges = buildMap {
        repeat(portals) { p ->
            put(p to p, 0)
            repeat(3) {
                put(p to (p + 1 + it).mod(portals), 1)
                put((p + 1 + it).mod(portals) to p, 1)
            }
        }
    }

    val state = State(portals, edges)

    val r1 = state.calc()
//    println(state)

    // shortcuts
    val extraConnections = buildMap {
        shortcuts.forEach { (s, d) ->
            put(s to d, 1)
            put(d to s, 1)
        }
    }

    val r2 = (0..<portals).minOf { s->
        (s+4..<portals).minOfOrNull { d->
            val lastConnection = buildMap {
                put(s to d, 1)
                put(d to s, 1)
            }
            State(portals, edges + extraConnections + lastConnection).calc()
        }?:Int.MAX_VALUE
    }

    return r1 - r2
}

fun part3(data: String): Any {
    return data
}
