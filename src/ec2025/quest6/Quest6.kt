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
    go("part3", 1664411854) { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val mentors = mutableMapOf<Char, Int>()
    var result = 0
    data.forEach { ch ->
        when {
            ch.isUpperCase() -> mentors[ch] = (mentors[ch] ?: 0) + 1
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
            ch.isUpperCase() -> mentors[ch] = (mentors[ch] ?: 0) + 1
            else -> result += mentors[ch.uppercaseChar()] ?: 0
        }
    }
    return result
}

fun part3(data: String, repeats: Long = 1000, dist: Int = 1000): Any {
    val positions = buildMap {
        data.forEachIndexed { index, ch -> getOrPut(ch) { mutableSetOf() }.add(index) }
    }
    return positions.entries.filter { (ch) -> ch.isLowerCase() }.sumOf { (ch, mentees) ->
        val mentors = positions[ch.uppercaseChar()].orEmpty()
        mentees.sumOf { index ->
            repeats * mentors.count { it in (index - dist..index + dist) } +
                    (repeats - 1) * mentors.count { it >= data.length + index - dist } +
                    (repeats - 1) * mentors.count { it <= index - data.length + dist }
        }
    }
}
