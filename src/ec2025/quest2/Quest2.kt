package ec2025.quest2

import go
import provideInput
import yearAndQuestFromPackage

data class Complex(val re: Long, val im: Long) {
    override fun toString(): String = "[$re,$im]"
}

operator fun Complex.plus(other: Complex) = Complex(re + other.re, im + other.im)
operator fun Complex.times(other: Complex) = Complex(re * other.re - im * other.im, re * other.im + im * other.re)
operator fun Complex.div(other: Complex) = Complex(re / other.re, im / other.im)
fun String.toComplex() = Complex(
    substringAfter("[").substringBefore(",").toLong(),
    substringAfter(",").substringBefore("]").toLong()
)


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", "[222150,666550]") { part1(provideInput(year, quest, 1)) }
    go("part2", 565) { part2(provideInput(year, quest, 2)) }
    go("part3", 53482) { part3(provideInput(year, quest, 3)) }
}

private fun parse(data: String): Map<String, Complex> = data.lines().associate { line ->
    val (key, v) = line.split("=")
    key to v.toComplex()
}

fun part1(data: String): Any {
    val a = parse(data)["A"] ?: error("no A in $data")
    var result = Complex(0, 0)
    repeat(3) {
        result *= result
        result /= Complex(10, 10)
        result += a
    }
    return result
}

fun part2(data: String): Any {
    val a = parse(data)["A"] ?: error("no A in $data")
    return (0..100L).sumOf { r ->
        (0..100L).count { c -> (a + Complex(r * 10, c * 10)).inSet() }
    }
}

fun Complex.inSet(): Boolean {
    var c = Complex(0, 0)
    repeat(100) {
        c *= c
        c /= Complex(100000, 100000)
        c += this
        if (c.re !in -1000000..1000000 || c.im !in -1000000..1000000) return false
    }
    return true
}

fun part3(data: String): Any {
    val a = parse(data)["A"] ?: error("no A in $data")
    return (0..1000L).sumOf { r ->
        (0..1000L).count { c -> (a + Complex(r, c)).inSet() }
    }
}
