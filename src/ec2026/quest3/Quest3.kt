package ec2026.quest3

import go
import provideInput
import yearAndQuestFromPackage

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
    val (_, standard, encoded) = line.split(' ', '-')
    val key = Key(standard, encoded.length)
    key.decode(encoded)
}

fun iso2(data: String) = data.lines().sumOf { line ->
    val (_, standard, encoded) = line.split(' ', '-')
    val key = Key(standard, encoded.length)
    key.decode(encoded).takeIf { key.encode(it) == encoded } ?: 0
}

class Key(standard: String, val length: Int) {

    // reversed
    val coeffs = (0..length).map { i ->
        standard[standard.length - 1 - (i % standard.length)].digitToInt().pow(i)
    }

    fun decode(encoded: String) =
        encoded.indices.sumOf { i -> coeffs[i] * encoded[encoded.length - 1 - i].digitToInt() }

    fun encode(value: Long) = encode(value, length)?.dropWhile { it == '0' }

    // cache for faster encoding
    private val maxWithout: List<Long> = (1..length).runningFold(0L) { acc, i -> acc + coeffs[i - 1] * 9 }

    private fun encode(value: Long, i: Int): String? {
        val coeff = coeffs[i]
        val min = ((value - maxWithout[i]) / coeff).coerceIn(0L..9)
        val max = (value / coeff).coerceIn(0L..9)
        return (min..max).firstNotNullOfOrNull { d ->
            when {
                i > 0 -> encode(value - d * coeff, i - 1)?.let { "$d$it" }
                value == d * coeff -> "$d"
                else -> null
            }
        }
    }
}

private fun Int.pow(e: Int) = toBigInteger().pow(e).toLong()
