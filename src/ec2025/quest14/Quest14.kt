package ec2025.quest14

import coords.findAll
import coords.ne
import coords.nw
import coords.pair.Pos
import coords.se
import coords.sw
import go
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 498) { part1(provideInput(year, quest, 1)) }
    go("part2", 1171073) { part2(provideInput(year, quest, 2)) }
    go("part3") { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String) = states(parse(data)).take(10).sumOf { it.first.size }

private fun states(initial: Pair<MutableSet<Pos>, MutableSet<Pos>>) =
    generateSequence(initial) { (active, inactive) ->
        val toDeactivate = active.filter {
            sequenceOf(it.ne(), it.nw(), it.sw(), it.se()).count { c -> c in active } % 2 == 0
        }.toSet()
        val toActivate = inactive.filter {
            sequenceOf(it.ne(), it.nw(), it.sw(), it.se()).count { c -> c in active } % 2 == 0
        }.toSet()
        active += toActivate
        active -= toDeactivate
        inactive += toDeactivate
        inactive -= toActivate
        active to inactive
//    (active + toActivate - toDeactivate) to (inactive + toDeactivate - toActivate)
    }.drop(1)

private fun parse(data: String): Pair<MutableSet<Pos>, MutableSet<Pos>> {
    val active = data.lines().findAll('#').toMutableSet()
    val inactive = data.lines().findAll('.').toMutableSet()
    return active to inactive
}

fun part2(data: String) = states(parse(data)).take(2025).sumOf { it.first.size }

fun part3(data: String): Any {
    val (active, inactive) = parse(data)
    val initial = mutableSetOf<Pos>() to (-13..<21).flatMap { r -> (-13..<21).map { c -> Pos(r, c) } }.toMutableSet()
    val count = 1000000000
    return states(initial).take(count).withIndex()
        .filter { (index, state) ->
            val (active1, inactive1) = state
            active1.containsAll(active) && inactive1.containsAll(inactive)
        }
        .onEach { println("round ${it.index + 1}") }
        .sumOf { it.value.first.size }
}
