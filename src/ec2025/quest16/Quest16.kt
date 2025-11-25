package ec2025.quest16

import go
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 189) { part1(provideInput(year, quest, 1)) }
    go("part2", 115075344384) { part2(provideInput(year, quest, 2)) }
    val part3ex1 = """
        1,2,2,2,2,3,1,2,3,3,1,3,1,2,3,2,1,4,1,3,2,2,1,3,2,2
    """.trimIndent()
    go("part3ex1", 94439495762954) { part3(part3ex1) }
    go("part3", 94286510189249) { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String) = countBlocks(data.split(",").map { it.toLong() }, 90)
fun part2(data: String) = findSpell(data).reduce(Long::times)

private fun countBlocks(spell: List<Long>, columns: Long) = spell.sumOf { columns / it }

private fun findSpell(data: String): List<Long> {
    val ints = data.split(",").map { it.toInt() }.toIntArray()
    val parts = mutableListOf<Long>()
    repeat(ints.size) { i ->
        if (ints[i] == 1) {
            parts += (i + 1L)
            for (j in i until ints.size step (i + 1)) ints[j]--
        }
        check(ints[i] == 0) { "${ints[i]} at $i" }
    }
    return parts.toList()
}

fun part3(data: String): Any {
    val spell = findSpell(data)
    val target = 202520252025000
    var min = 0L
    var max = target
    while (true) {
        val mid = (min + max) / 2
        if (mid == min) return min
        val blocks = countBlocks(spell, mid)
        if (blocks > target) max = mid else min = mid
    }
}
