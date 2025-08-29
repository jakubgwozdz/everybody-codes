package ec2.quest2

import go
import provideInput
import yearAndQuestFromPackage

class Arrows() {
    var drawn = 0
        private set
    private var current: Char? = null
    private val iterator = sequence {
        while (true) {
            yield('R')
            yield('G')
            yield('B')
        }
    }.iterator()

    fun destroy() {
        current = null
    }

    fun drawReusable(): Char = current ?: iterator.next().also { drawn++; current = it }
    fun drawAndDestroy(): Char = current?.also { current == null } ?: iterator.next().also { drawn++ }
}


fun part1(data: String): Any {
    val arrows = Arrows()
    data.forEach { balloon ->
        if (balloon != arrows.drawReusable()) arrows.destroy()
    }
    return arrows.drawn
}

fun part2and3(allBalloons: CharArray): Any {
    val arrows = Arrows()
    val remaining = allBalloons.toMutableList()
    while (remaining.isNotEmpty()) {
        if (remaining.size % 2 == 0) {
//            println("optimized run for ${remaining.size}")
            val marks = BooleanArray(remaining.size)
            var size = remaining.size
            repeat(remaining.size / 2) { i0 ->
                val arrow = arrows.drawAndDestroy()
                if (size % 2 == 0 && remaining[i0] == arrow) {
                    marks[remaining.size - (size / 2)] = true
                    size--
                }
                marks[i0] = true
                size--
            }
            val newList = buildList {
                remaining.forEachIndexed { index, balloon ->
                    if (!marks[index]) add(balloon)
                }
            }
            remaining.clear()
            remaining.addAll(newList)
        } else {
//            println("regular run for ${remaining.size}")
            arrows.drawAndDestroy()
            remaining.removeFirst()
        }
    }
    return arrows.drawn
}

fun part2(data: String): Any = part2and3(buildString { repeat(100) { this.append(data) } }.toCharArray())

fun part3(data: String): Any = part2and3(buildString { repeat(100000) { this.append(data) } }.toCharArray())

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("e1", 7) { part1("GRBGGGBBBRRRRRRRR") }
    go("part1", 134) { part1(provideInput(year, quest, 1)) }
    go("part2", 21305) { part2(provideInput(year, quest, 2)) }
    go("part3", 21503122) { part3(provideInput(year, quest, 3)) }
}
