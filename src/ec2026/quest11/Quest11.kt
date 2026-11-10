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


data class State(
    val portals: Int,
    private val distances: Array<IntArray> = Array(portals) { IntArray(portals) { portals } }
) {
    private val queue = mutableListOf<Pair<Pair<Int, Int>, Int>>()
    fun l(i: Int, d: Int = 1) = (i - d).mod(portals)
    fun r(i: Int, d: Int = 1) = (i + d).mod(portals)
    fun add(src: Int, dst: Int, distance: Int) {
        distances[src][dst] = distance
        queue += (l(src) to dst) to distance + 1
        queue += (r(src) to dst) to distance + 1
        queue += (src to l(dst)) to distance + 1
        queue += (src to r(dst)) to distance + 1
    }

    fun calc(): Int {
        while (queue.isNotEmpty()) {
            val (path, distance) = queue.removeFirst()
            val (src, dst) = path
            if (distances[src][dst] > distance) add(src, dst, distance)
        }

        return distances.sumOf { it.sum() }
    }

    fun println() = distances.forEach { println(it.joinToString("") { "%3d".format(it) }) }

    fun clone(): State {
        if (queue.isNotEmpty()) calc()
        return copy(distances = distances.map { it.clone() }.toTypedArray())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as State

        if (portals != other.portals) return false
        if (!distances.contentDeepEquals(other.distances)) return false
        if (queue != other.queue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = portals
        result = 31 * result + distances.contentDeepHashCode()
        result = 31 * result + queue.hashCode()
        return result
    }
}

fun parse(data: String) = State(data.lines().first().substringAfter("portals: ").toInt()) to
        data.lines().drop(2).map { it.split(" ") }
            .map { (a, b) -> a.toInt() - 1 to b.toInt() - 1 }

fun part1(data: String): Any {

    val (state, shortcuts) = parse(data)

    // preparation
    (0..<state.portals).forEach { p -> state.add(p, p, 0) }

    // shortcuts
    shortcuts.forEach { (src, dst) ->
        state.add(src, dst, 1);
        state.add(dst, src, 1)
    }

    return state.portals * (state.portals / 2).let { it * it } - state.calc()
}

fun part2(data: String): Any {
    val (state, shortcuts) = parse(data)

    // preparation
    (0..<state.portals).forEach { p ->
        state.add(state.l(p, 3), p, 1)
        state.add(state.r(p, 3), p, 1)
        state.add(p, state.l(p, 3), 1)
        state.add(p, state.r(p, 3), 1)
    }
    state.calc()
    (0..<state.portals).forEach { p ->
        state.add(state.l(p, 2), p, 1)
        state.add(state.r(p, 2), p, 1)
        state.add(p, state.l(p, 2), 1)
        state.add(p, state.r(p, 2), 1)
        state.add(p, p, 0)
    }
    val r1 = state.calc()

    state.println()

    // shortcuts
    shortcuts.forEach { (src, dst) -> state.add(src, dst, 1); state.add(dst, src, 1) }
    val r2 = 0//state.calc()
//    val r2 = (0..<state.portals).minOf { it to (0..<state.portals) }

    return r1 - r2
}

fun part3(data: String): Any {
    return data
}
