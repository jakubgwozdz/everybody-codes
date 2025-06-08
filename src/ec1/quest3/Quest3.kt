package ec1.quest3

import go
import provideInput
import yearAndQuestFromPackage

val exampleA = """
    x=12 y=2
    x=8 y=4
    x=7 y=1
    x=1 y=5
    x=1 y=3
""".trimIndent()

val exampleB = """
    x=3 y=1
    x=3 y=9
    x=1 y=5
    x=4 y=10
    x=5 y=3
""".trimIndent()

fun part1(data: String) = parse(data).sumOf { (x, y) ->
    val loop = x + y - 1
    val x100 = (x - 1 + 100) % loop + 1 // 1-based
    val y100 = (loop + 1 - x100)
    x100 + 100 * y100
}

fun part2(data: String): Any {
    val initial = parse(data)
    val remainders = initial.map { it.second }
    val moduli = initial.map { it.first + it.second - 1L }
    // crt
    val product = moduli.reduce(Long::times)
    return remainders.zip(moduli).sumOf { (rem, mod) ->
        val partialProduct = product / mod
        val inverse = partialProduct.modInv(mod)
        rem * partialProduct * inverse
    } % product - 1
}

fun Long.modInv(mod: Long): Long {
    var a = this
    var b = mod
    var x = 0L
    var y = 1L

    while (a > 1) {
        val q = a / b
        var t = b
        b = a % b
        a = t
        t = x
        x = y - q * x
        y = t
    }
    return if (y < 0) y + mod else y
}

fun part3(data: String) = part2(data)

private fun parse(data: String): List<Pair<Int, Int>> = data.lines().map { line ->
    line.split(' ')
        .associate { it.substringBefore('=') to it.substringAfter('=') }
        .let { Pair(it["x"]!!.toInt(), it["y"]!!.toInt()) }
}

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 3343) { part1(provideInput(year, quest, 1)) }
    go("part2exA", 14) { part2(exampleA) }
    go("part2exB", 13659) { part2(exampleB) }
    go("part2", 1098490) { part2(provideInput(year, quest, 2)) }
    go("part3", 95331589243) { part3(provideInput(year, quest, 3)) }
}
