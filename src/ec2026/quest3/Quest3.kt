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
    val (standard, encoded) = parse(line)
    val key = calculateKey(standard, encoded.length)
    decode(key, encoded)
}

fun iso2(data: String) = data.lines().sumOf { line ->
    val (standard, encoded) = parse(line)
    val key = calculateKey(standard, encoded.length)
    decode(key, encoded).takeIf {
        encode(key, it, encoded.length)!!.dropWhile { it == '0' } == encoded
    } ?: 0
}

private fun calculateKey(standard: List<Int>, length: Int): List<Pair<Long, Long>> = buildList {
    (0..length).forEach { i ->
        val coeff = standard[standard.size - 1 - (i % standard.size)].toBigInteger().pow(i).toLong()
        val maxSoFar = lastOrNull()?.let { (c, m) -> c * 9 + m } ?: 0
        add(coeff to maxSoFar)
    }
}

private fun parse(line: String) =
    line.substringBefore(' ').substringAfter('-').map { it.digitToInt() } to
            line.substringAfter(' ')

private fun decode(key: List<Pair<Long, Long>>, encoded: String) =
    encoded.indices.sumOf { i -> key[i].first * encoded[encoded.length - 1 - i].digitToInt() }

private fun encode(key: List<Pair<Long, Long>>, value: Long, i: Int): String? {
    val (coeff, maxSmaller) = key[i]
    val min = ((value - maxSmaller) / coeff).coerceIn(0L..9)
    val max = (value / coeff).coerceIn(0L..9)
    return (min..max).firstNotNullOfOrNull { d ->
        when {
            i > 0 -> encode(key, value - d * coeff, i - 1)?.let { "$d$it" }
            value == d * coeff -> "$d"
            else -> null
        }
    }
}
