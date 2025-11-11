package ec2025.quest6

import go
import provideInput
import yearAndQuestFromPackage


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 147) { part1(provideInput(year, quest, 1)) }
    go("part2", 3949) { part2(provideInput(year, quest, 2)) }
    go("part3a", 34) { part3("AABCBABCABCabcabcABCCBAACBCa", 1, 10) }
    go("part3a", 72) { part3("AABCBABCABCabcabcABCCBAACBCa", 2, 10) }
    go("part3") { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val mentors = mutableMapOf<Char, Int>()
    var result = 0
    data.forEach { ch ->
        when {
            ch.isUpperCase() -> mentors.compute(ch) { _, i -> (i ?: 0) + 1 }
            ch == 'a' -> result += mentors[ch.uppercaseChar()] ?: 0
        }
    }
    return result
}

fun part2(data: String): Any {
    val mentors = mutableMapOf<Char, Int>()
    var result = 0
    data.forEach { ch ->
        when {
            ch.isUpperCase() -> mentors.compute(ch) { _, i -> (i ?: 0) + 1 }
            else -> result += mentors[ch.uppercaseChar()] ?: 0
        }
    }
    return result
}

//1664251015 first char ok, len ok
fun part3(data: String, repetitions: Long = 1000, maxDistance: Int = 1000): Any {
    val mentors = mutableMapOf<Char, MutableSet<Int>>()
    data.forEachIndexed { index, ch ->
        if (ch.isUpperCase()) mentors.getOrPut(ch) { mutableSetOf() }.add(index)
    }
    return data.withIndex().filter { (_, ch) -> ch.isLowerCase() }.sumOf { (index, ch) ->
        val m = mentors[ch.uppercaseChar()].orEmpty()
        var result = repetitions * m.count { it in (index - maxDistance..index + maxDistance) }
        if (index < maxDistance)
            result += (repetitions - 1) * m.count { it >= data.length + index - maxDistance }
        if (index > data.length - maxDistance)
            result += (repetitions - 1) * m.count { it <= index - data.length + maxDistance }
        result
    }
}
