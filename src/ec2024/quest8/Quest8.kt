package ec2024.quest8

import go
import provideInput
import yearAndQuestFromPackage
import kotlin.math.sqrt


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 8677007) { part1(provideInput(year, quest, 1)) }
    go("part2", 125560657) { part2(provideInput(year, quest, 2)) }
    go("part3", 41067) { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val provided = data.toInt()
    val layers = sqrt(provided - 1.0).toInt() + 1
    val sum = layers * layers
    val diff = sum - provided
    val base = 2 * layers - 1
    return diff * base
}

fun part2(data: String): Any {
    val priests = data.toInt()
    val provided = 20240000
    val acolytes = 1111
    val shrine = generateSequence(Shrine()) { it.addLayer2(priests, acolytes) }.first { it.used >= provided }
    return (shrine.used - provided) * shrine.base
}

fun part3(data: String): Any {
    val priests = data.toInt()
    val provided = 202400000
    val acolytes = 10
    val shrine = generateSequence(Shrine()) { it.addLayer3(priests, acolytes) }.first { it.used >= provided }
    return shrine.used - provided
}

data class Shrine(
    val heights: List<Int> = listOf(1),
    val lastThickness: Int = 1,
    val total: Long = heights.sum() * 2L - heights.first(),
    val empty: Int = 0
)

fun Shrine.addLayer2(priests: Int, acolytes: Int): Shrine {
    val thickness = (lastThickness * priests) % acolytes
    val newHeights = heights.map { it + thickness } + thickness
    val newBase = newHeights.size * 2 - 1
    return Shrine(newHeights, thickness, total + newBase * thickness)
}

fun Shrine.addLayer3(priests: Int, acolytes: Int): Shrine {
    val thickness = (lastThickness * priests) % acolytes + acolytes
    val newHeights = heights.map { it + thickness } + thickness
    val newBase = newHeights.size * 2 - 1
    fun calcEmpty(h:Int): Int = ((1L * h * newBase * priests) % acolytes).toInt()
    val newEmpty = newHeights.withIndex().sumOf { (i, h) ->
        when(i) {
            0 -> calcEmpty(h)
            newHeights.size - 1 -> 0
            else -> calcEmpty(h) * 2
        }
    }
    return Shrine(newHeights, thickness, total + newBase * thickness, newEmpty)
}

val Shrine.base: Int get() = heights.size * 2 - 1
val Shrine.used: Long get() = total - empty
