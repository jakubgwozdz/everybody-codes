package ec2026.quest14

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

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1 ex", 67) { part1(exampleA) }
    go("part1") { part1(provideInput(year, quest, 1)) }
//    go("part2 ex", exampleA) { part2(exampleA) }
//    go("part2") { part2(provideInput(year, quest, 2)) }
//    go("part3 ex", exampleA) { part3(exampleA) }
//    go("part3") { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val map = data.lines()
    return map.indices.sumOf { r ->
        map[r].withIndex().sumOf { (c, ch) ->
            val p = Pos(r, c)
            var count = 0
            if (map[p.n()].let { it == null || it > ch }) count++
            if (map[p.e()].let { it == null || it > ch }) count++
            if (map[p.s()].let { it == null || it > ch }) count++
            if (map[p.w()].let { it == null || it > ch }) count++
            count
        }
    }
}

fun part2(data: String): Any {
    return data
}

fun part3(data: String): Any {
    return data
}
