package ec2026.quest6

import go
import provideInput
import yearAndQuestFromPackage

val ex1 = """
    BRDS:FLWR,SLTT,YSTS
    FLWR:SLTT,YSTS
    SLTT:YSTS,MLKK,BTTR
    NULL:BRDS,FLWR,SLTT
    YSTS:BRDS,FLWR
    MLKK:SPCS,NULL
    HNYN:BRDS,FLWR,SLTT,YSTS
""".trimIndent()

val ex3 = """
    MEAL:BRDS,CAKE,DOUG
    BRDS:DOUG,FLWR
    CAKE:DOUG,FLWR,NULLv2,NULLv3
    DOUG:FLWR,YSTS,SLTT,NULLv1
    FLWR:BRDS,DOUG
    YSTS:SPCS
    SOUP:BRDS,CAKE,DOUG,FLWR
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1 ex", 7) { part1(ex1) }
    go("part1", 43) { part1(provideInput(year, quest, 1)) }
    go("part2 ex", 6) { part2(ex1) }
    go("part2", 804) { part2(provideInput(year, quest, 2)) }
    go("part3 ex", 3) { part3(ex3) }
    go("part3") { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String) = findAllDependencies(parse(data), "NULL").size

fun part2(data: String) = findAllDependencies(parse(data).inverse(), "NULL").size

private fun Map<String, List<String>>.inverse() = buildMap {
    this@inverse.forEach<String, List<String>> { (k, v) ->
        v.forEach { getOrPut(it) { mutableListOf() }.add(k) }
    }
}

fun part3(data: String): Any {
    val recipe = parse(data)
    return recipe.keys.count { product ->
        findAllDependencies(recipe, product).count { it.startsWith("NULL") } > 1
    }
}

private fun parse(data: String): Map<String, List<String>> {
    val recipe = data.lines().associate { line ->
        line.substringBefore(':') to line.substringAfter(':').split(',')
    }
    return recipe
}

fun findAllDependencies(recipe: Map<String, Collection<String>>, start: String): Set<String> {
    val result = mutableSetOf<String>()
    val toCheck = mutableListOf(start)
    while (toCheck.isNotEmpty()) {
        val next = toCheck.removeFirst()
        recipe[next]?.forEach {
            if (it !in result && it != start) {
                result += it
                toCheck += it
            }
        }
    }
    return result
}

