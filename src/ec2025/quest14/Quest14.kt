package ec2025.quest14

import coords.findAll
import coords.ne
import coords.nw
import coords.se
import coords.sw
import go
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1",498) { part1(provideInput(year, quest, 1)) }
    go("part2") { part2(provideInput(year, quest, 2)) }
//    go("part3") { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String, count:Int = 10): Any {
    val active = data.lines().findAll('#').toMutableSet()
    val inactive = data.lines().findAll('.').toMutableSet()
    var result = 0
    repeat(10) {
        val toDeactivate = active.filter {
            sequenceOf(it.ne(),it.nw(), it.sw(), it.se()).count { c -> c in active } % 2 == 0
        }.toSet()
        val toActivate = inactive.filter {
            sequenceOf(it.ne(),it.nw(), it.sw(), it.se()).count { c -> c in active } % 2 == 0
        }.toSet()
        active += toActivate
        active -= toDeactivate
        inactive += toDeactivate
        inactive -= toActivate
        result += active.size
    }
    return result
}

// 5715 wrong
fun part2(data: String) = part1(data, 2025)

fun part3(data: String): Any {
    return data
}
