package ec2025.quest8

import debug
import provideInput
import yearAndQuestFromPackage
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
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

val offset = 250
val r = offset * 9 / 10

fun generateImage(name: String, data: String, size: Int) {
//    val pairs = listOf(1 to 15)
    val pairs = data.parseToPairs()
    val vertices = (1..size).associateWith {
        val a = (it - 1.0) / size * 2 * PI
        val x = offset + r * sin(a)
        val y = offset - r * cos(a)
        x.toInt() to y.toInt()
    }
    val array = Array(offset * 2) { IntArray(offset * 2) { 0 } }
    pairs.forEach { (a, b) ->
        val (x1, y1) = vertices[a] ?: error("no vertex $a")
        val (x2, y2) = vertices[b] ?: error("no vertex $b")
        array.bresenham(x1, y1, x2, y2)
    }
    val max = array.maxOf { it.max() }
    val image = BufferedImage(offset * 2, offset * 2, BufferedImage.TYPE_INT_RGB)
    val color = Color(238, 214, 153)
    val op: Int.(Int) -> Int = { value: Int -> this * (max - value) / max }
    array.forEachIndexed { y, row ->
        row.forEachIndexed { x, value ->
            if ((x - offset) * (x - offset) + (y - offset) * (y - offset) < r * r) {
                val rgb = Color(
                    color.red.op(value).op(value).op(value).op(value).op(value),
                    color.green.op(value).op(value).op(value).op(value),
                    color.blue.op(value).op(value).op(value),
                ).rgb
                image.setRGB(x, y, rgb)
            }
        }
    }

    ImageIO.write(image, "png", File("local/$name.png".debug()))
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
