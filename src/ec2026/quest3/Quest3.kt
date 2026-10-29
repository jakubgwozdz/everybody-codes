package ec2026.quest3

import go
import provideInput
import yearAndQuestFromPackage
import java.math.BigInteger

private val ex1 = """
    ISO-3210 7654
    ISO-8742 2468
    ISO-1111 1111
""".trimIndent()

private val ex3 = """
    ISO2-3 23
    ISO2-61 149
    ISO2-978 99
    ISO2-5887 5923
    ISO2-76988 123749
    ISO2-435657 929977
    ISO2-3949 870737
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1ex", 1478) { iso1(ex1) }
    go("part1", 16631) { iso1(provideInput(year, quest, 1)) }
    go("part2", 23784260204) { iso1(provideInput(year, quest, 2)) }
    go("part3ex", 50987) { iso2(ex3) }
    go("part3", 360187) { iso2(provideInput(year, quest, 3)) }
}

fun iso1(data: String) = data.lines().sumOf { line ->
    val (standard, encoded) = parse(line)
    standard.decode(encoded)
}

fun iso2(data: String) = data.lines().sumOf { line ->
    val (standard, encoded) = parse(line)
    standard.decode(encoded).takeIf {
        encoded == standard.encode(it, encoded.length)!!.dropWhile { it == '0' }
    } ?: 0
}

private fun parse(line: String) =
    line.substringBefore(' ').substringAfter('-').map { it.digitToInt().toBigInteger() } to
            line.substringAfter(' ')

private fun List<BigInteger>.decode(encoded: String) =
    encoded.indices.sumOf { i -> nth(i) * encoded[encoded.length - 1 - i].digitToInt() }

private fun List<BigInteger>.nth(i: Int) = this[size - 1 - (i % size)].pow(i).toLong()

private fun List<BigInteger>.encode(value: Long, i: Int): String? {
    val n = nth(i)
    val maxDoable = (0..<i).sumOf { nth(it) } * 9
    val max = (value / n).coerceIn(0L..9)
    val min = ((value - maxDoable) / n).coerceIn(0L..9)
    return (min..max).firstNotNullOfOrNull { d ->
        when {
            i > 0 -> encode(value - d * n, i - 1)?.let { "$d$it" }
            value == d * n -> "$d"
            else -> null
        }
    }
}
