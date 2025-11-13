package ec2025.quest8

import provideInput
import yearAndQuestFromPackage
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    generateImage("image1", provideInput(year, quest, 1), 32)
    generateImage("image2", provideInput(year, quest, 2), 256)
    generateImage("image3", provideInput(year, quest, 3), 256)
}

val r = 450
val offset = 500

fun generateImage(name: String, data: String, size: Int) {
    val pairs = listOf(1 to 15)
//    val pairs = data.parseToPairs()
    val vertices = (1..size).associateWith {
        val a = (it - 1.0) / size * 2 * PI
        val x = offset + r * cos(a)
        val y = offset + r * sin(a)
        x.toInt() to y.toInt()
    }
    val array = Array(size) { IntArray(size) { 0 } }
    pairs.forEach { (a, b) ->
        val (x1, y1) = vertices[a] ?: error("no vertex $a")
        val (x2, y2) = vertices[b] ?: error("no vertex $b")
        array.bresenham(x1, y1, x2, y2)
    }

}

private fun Array<IntArray>.bresenham(x1: Int, y1: Int, x2: Int, y2: Int) {
    // Bresenham's line algorithm
    var px = x1
    var py = y1
    val dx = abs(x2 - x1)
    val dy = abs(y2 - y1)
    val sx = if (x1 < x2) 1 else -1
    val sy = if (y1 < y2) 1 else -1
    var err = dx - dy

    while (true) {
        this[py][px]++

        if (px == x2 && py == y2) break

        val e2 = 2 * err
        if (e2 > -dy) {
            err -= dy
            px += sx
        }
        if (e2 < dx) {
            err += dx
            py += sy
        }
    }
}
