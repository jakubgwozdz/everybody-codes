package ec2025.quest14

import coords.findAll
import coords.ne
import coords.nw
import coords.pair.Pos
import coords.se
import coords.sw
import go
import logged
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 498) { part1(provideInput(year, quest, 1)) }
    go("part2", 1171073) { part2(provideInput(year, quest, 2)) }
    go("part3", 978754588) { part3(provideInput(year, quest, 3)) }
}

data class State(val active: Set<Pos>, val inactive: Set<Pos>) {
    val value get() = active.size.toLong()
}

private fun states(initial: State) =
    generateSequence(initial) { state ->
        val toDeactivate = state.active.filter {
            sequenceOf(it.ne(), it.nw(), it.sw(), it.se()).count { c -> c in state.active } % 2 == 0
        }
        val toActivate = state.inactive.filter {
            sequenceOf(it.ne(), it.nw(), it.sw(), it.se()).count { c -> c in state.active } % 2 == 0
        }
        State(state.active + toActivate - toDeactivate.toSet(), state.inactive + toDeactivate - toActivate.toSet())
    }.drop(1)

private fun parse(data: String): State {
    val active = data.lines().findAll('#').toMutableSet()
    val inactive = data.lines().findAll('.').toMutableSet()
    return State(active, inactive)
}

fun part1(data: String) = states(parse(data)).take(10).sumOf { it.value }

fun part2(data: String) = states(parse(data)).take(2025).sumOf { it.value }

fun part3(data: String): Any {
    val (active, inactive) = parse(data)
    val initial = State(
        mutableSetOf(),
        (-13..<21).flatMap { r -> (-13..<21).map { c -> Pos(r, c) } }.toMutableSet(),
    )
    val count = 1000000000
    val found = mutableMapOf<State, Int>()
    val iterator = states(initial).withIndex().iterator()
    while (true) {
        val (index, state) = iterator.next()
        if (!state.active.containsAll(active) || !state.inactive.containsAll(inactive)) continue
        if (state in found) {
            val prev = found[state]!!.logged("valid at $index, prev at")
            val values = found.entries.associate { (state, index) -> index to state.value }

            val before = values.filterKeys { it < prev }.logged("before")
            val cycle = values.filterKeys { it >= prev }.logged("cycle")
            val cycleLength = index - prev
            val cycles = count / cycleLength
            val remaining = values.mapKeys { (k, _) -> k + cycles * cycleLength }
                .filterKeys { it <= count }.logged("remaining")

            return before.values.sum() + cycles * cycle.values.sum() + remaining.values.sum()
        } else {
            found[state] = index.logged("valid at")
        }
    }
}
