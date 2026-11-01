package ec2026.quest5

import coords.findAll
import coords.pair.Pos
import coords.pair.col
import coords.pair.row
import display
import provideInput
import useGraphics
import yearAndQuestFromPackage
import java.awt.Color
import java.awt.Point
import java.awt.image.BufferedImage
import java.lang.Thread.sleep
import javax.swing.Timer
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.update
import kotlin.math.roundToInt
import kotlin.random.Random

data class AnimState(
    val center: Pos,
    val size: Double,
    val ones: Set<Pos>,
    val land: Set<Pos> = emptySet(),
    val beach: Set<Pos> = emptySet(),
    val forest: Set<Pos> = emptySet()
)


internal fun findLandStepByStep(map: List<String>, center: Pos = findCenter(map)): Set<Pos> {
    val land = mutableSetOf(center)
    do {
        val delta = findLandDelta(map, land)
        land += delta
    } while (delta.isNotEmpty())
    return land.toSet()
}

internal fun findLandDelta(map: List<String>, previous: Set<Pos>) = previous.flatMap { it.adjacent() }.toSet()
    .let { it - previous }
    .filter { it.adjacent().count { p -> p in previous || is1(map, p) } >= 5 }


class Quest5Video {
    private val bgColor = Color(15, 15, 35)
    private val fgColor = Color(238, 214, 153)

    enum class Tile(val ch: Char, val color: Color) {
        Sea('~', Color.BLUE),
        One('^', Color.BLUE.brighter()),
        Grass('#', Color.GREEN.brighter()),
        Sand('=', Color.YELLOW),
        Tree('@', Color.GREEN.darker())
    }

    operator fun AnimState.get(pos: Pos) = when {
        pos in forest -> Tile.Tree
        pos in beach -> Tile.Sand
        pos in land -> Tile.Grass
        pos in ones -> Tile.One
        else -> Tile.Sea
    }

    fun paintOnImage(state: AnimState, image: BufferedImage) = image.useGraphics { g ->
        g.color = bgColor
        g.fillRect(0, 0, image.width, image.height)
        g.color = fgColor
        val scale = 1 / state.size
        val half = minOf(image.width, image.height) / 2.0
        val cell = half * scale
        g.font = g.font.deriveFont(cell.toFloat())
        val fm = g.fontMetrics
        val xCharOffset = - fm.charWidth('#') / 2.0f
        val yCharOffset = (fm.ascent - fm.descent) / 2.0f

        val (rows, cols) = clipped(state)
        rows.forEach { r ->
            cols.forEach { c ->
                val p = Pos(r, c)
                val tile = state[p]

                val cx = image.width / 2.0 +
                        (c - state.center.col+ Random.nextDouble(-0.1, 0.1)) * cell
                val cy = image.height / 2.0 +
                        (r - state.center.row+ Random.nextDouble(-0.1, 0.1)) * cell * state.center.col / state.center.row

                g.color = tile.color
                g.translate(cx, cy)
                g.drawString(tile.ch.toString(), xCharOffset, yCharOffset)
                g.translate(-cx, -cy)
            }
        }

    }

    private fun clipped(state: AnimState): Pair<IntRange, IntRange> {
        val interesting = state.size.roundToInt() + 1
        val rows = state.center.first - interesting..state.center.first + interesting
        val cols = state.center.second - interesting..state.center.second + interesting
        return rows to cols
    }
}

@OptIn(ExperimentalAtomicApi::class)
fun part5video(data: String) {
    val map = data.lines()
    val center = findCenter(map)
    val animState = AtomicReference(AnimState(center, 5.0, map.findAll('1'), land = setOf(center)))
    val video = Quest5Video()
    display(
        animState,
        "Quest 5: The Cartograffity",
        location = Point(300, 200),
        refreshDelay = 33,
        op = video::paintOnImage
    )
    val zoomTimer = Timer(10) { animState.update { it.copy(size = it.size + 0.08) } }.apply { start() }
    sleep(1000)

    val runTimer = Timer(160) {
        animState.update {
            val land = it.land + findLandDelta(map, it.land)
            val beach = findBeach(land)
            val forest = findForest(land - beach).toMutableSet()
            repeat(2) { forest += growForest(forest, beach) }
            it.copy(land = land, beach = beach, forest = forest)
        }
    }.apply { start() }

}

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    part5video(provideInput(year, quest, 3))
}
