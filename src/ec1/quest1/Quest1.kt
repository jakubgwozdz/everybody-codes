package ec1.quest1

import go
import provideInput
import yearAndQuestFromPackage

fun calculate(data: String, eni: (Long, Long, Long) -> Long) = data.lineSequence()
    .maxOf { line ->
        val data = line.split(' ').associate { it.substringBefore("=") to it.substringAfter("=").toLong() }
        val a = data["A"]!!
        val b = data["B"]!!
        val c = data["C"]!!
        val x = data["X"]!!
        val y = data["Y"]!!
        val z = data["Z"]!!
        val m = data["M"]!!
        (eni(a, x, m) + eni(b, y, m) + eni(c, z, m))
//            .debug { "$line -> $it" }
    }

fun eni1(n: Long, exp: Long, mod: Long): Long {
    var score = 1L
    val remainders = mutableListOf<Long>()
    repeat(exp.toInt()) {
        score = score * n % mod
        remainders += score
    }
    return remainders.reversed().joinToString("").toLong()
}

fun eni2(n: Long, exp: Long, mod: Long): Long {
    var score = 1L
    val remainders = mutableListOf<Long>()
    var i = 0L
    var skipped = false
    while (i < exp) {
        score = score * n % mod
        remainders += score
        i++
        val lastIndexOfScore = remainders.size - 1 - remainders.indexOf(score)
        if (!skipped && lastIndexOfScore >= 5) {
            i += (((exp - i) / lastIndexOfScore / 5) - 1) * lastIndexOfScore * 5
            skipped = true
        }
    }
    return remainders.takeLast(5).reversed().joinToString("").toLong()
}

fun eni3(n: Long, exp: Long, mod: Long): Long {
    var score = 1L
    var partial = 0L
    val remainders = mutableListOf<Long>()
    var i = 0L
    var skipped = false
    val seen = mutableSetOf<Long>()
    while (i < exp) {
        score = score * n % mod
        remainders += score
        i++
        if (!skipped && score in seen) {
            val lastIndexOfScore = remainders.size - 1 - remainders.indexOf(score)
            val skips = (exp - i) / lastIndexOfScore
            i += skips * lastIndexOfScore
            partial = remainders.takeLast(lastIndexOfScore).sum() * skips
            skipped = true
        }
        seen += score
    }
    return partial + remainders.sum()
}

fun part1(data: String): Any = calculate(data, ::eni1)
fun part2(data: String): Any = calculate(data, ::eni2)
fun part3(data: String): Any = calculate(data, ::eni3)

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 7526623350) { part1(provideInput(year, quest, 1)) }
    go("part2", 164051258244282) { part2(provideInput(year, quest, 2)) }
    go("part3", 564392275180979) { part3(provideInput(year, quest, 3)) }
}
