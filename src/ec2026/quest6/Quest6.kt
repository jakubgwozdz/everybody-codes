package ec2026.quest6

import go
import provideInput
import yearAndQuestFromPackage

val ex1 = """
    
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1",0) { part1(ex1) }
    go("part1") { part1(provideInput(year, quest, 1)) }
//    go("part2") { part2(provideInput(year, quest, 2)) }
//    go("part3") { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    return data
}

fun part2(data: String): Any {
    return data
}

fun part3(data: String): Any {
    return data
}
