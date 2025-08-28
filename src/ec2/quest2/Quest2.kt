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

    fun draw(): Char = current ?: iterator.next().also { drawn++; current = it }
}


fun part1(data: String): Any {
    val arrows = Arrows()
    data.forEach { balloon->
        if (balloon != arrows.draw()) arrows.destroy()
    }
    return arrows.drawn
}

fun part2(data: String): Any = "2"

fun part3(data: String): Any = "3"

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("e1", 7) { part1("GRBGGGBBBRRRRRRRR") }
    go("part1", 134) { part1(provideInput(year, quest, 1)) }
    go("part2", "") { part2(provideInput(year, quest, 2)) }
    go("part3", "") { part3(provideInput(year, quest, 3)) }
}
