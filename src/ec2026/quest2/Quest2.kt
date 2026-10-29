package ec2026.quest2

import go
import provideInput
import yearAndQuestFromPackage

private val ex1 = """
    4<3 2<5 3>1 1>2 1<5 4<1 3>2 4<2 3>5 4<5
    4>1 2>1 3>2 1<5 4>5 2<5 3>1 4>3 2<4 5<3
    5>1 5>2 4>1 2<4 3>2 3<4 1>2 3>5 1<3 4>5
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1ex", 122157) { solve(ex1) }
    go("part1", 375864) { solve(provideInput(year, quest, 1)) }
    go("part2", 57086848791) { solve(provideInput(year, quest, 2)) }
    go("part3", 602969890572) { solve(provideInput(year, quest, 3)) }
}

fun solve(data: String) = data.lines().sumOf { line ->
    val lesser = mutableMapOf<Char, MutableSet<Char>>()
    val greater = mutableMapOf<Char, MutableSet<Char>>()
    line.split(" ").forEach {
        val (a, b, c) = it.toCharArray()
        val (lt, gt) = when (b) {
            '<' -> a to c
            '>' -> c to a
            else -> error("Unexpected '$it'")
        }
        greater.putIfAbsent(lt, mutableSetOf())
        greater.putIfAbsent(gt, mutableSetOf())
        lesser.putIfAbsent(lt, mutableSetOf())
        lesser.putIfAbsent(gt, mutableSetOf())
        lesser[gt]!! += lt
        greater[lt]!! += gt
    }
    buildString {
        while (greater.isNotEmpty()) {
            val gst = greater.firstNotNullOf { (k, v) -> k.takeIf { v.isEmpty() } }
            append(gst)
            greater.remove(gst)
            lesser[gst]!!.forEach { greater[it]!!.remove(gst) }
        }
    }.toLong()
}
