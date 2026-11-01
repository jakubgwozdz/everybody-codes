package ec2026.quest5

import coords.findAll
import coords.pair.Pos
import coords.pair.col
import coords.pair.row
import display
import provideInput
import useGraphics
import withAlpha
import yearAndQuestFromPackage
import java.awt.Color
import java.awt.Point
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.lang.Thread.sleep
import javax.swing.Timer
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.update
import kotlin.math.roundToInt

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

    private val charsBelow = 15.0
    private val rectsFrom = 65.0

    fun paintOnImage(state: AnimState, image: BufferedImage) = image.useGraphics { g ->
        g.color = bgColor
        g.fillRect(0, 0, image.width, image.height)
        val half = minOf(image.width, image.height) / 2.0
        val cellW = half / state.size
        val cellH = cellW * state.center.col / state.center.row

        val fm = g.getFontMetrics(g.font.deriveFont(cellW.toFloat()))
        val xCharOffset = -fm.charWidth('#') / 2.0 / cellW
        val yCharOffset = (fm.ascent - fm.descent) / 2.0 / cellH

        val drawRects = state.size >= charsBelow
        val drawChars = state.size < rectsFrom
        val alpha = (((state.size - charsBelow) / (rectsFrom - charsBelow)).coerceIn(0.0, 1.0) * 255).toInt()

        val saved = g.transform
        g.translate(image.width / 2.0, image.height / 2.0)
        g.scale(cellW, cellH)
        g.translate(-(state.center.col + 0.5), -(state.center.row + 0.5))
        g.font = g.font.deriveFont(1f)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)

        val (rows, cols) = clipped(state)
        rows.forEach { r ->
            cols.forEach { c ->
                val tile = state[Pos(r, c)]
                if (drawRects) {
                    g.color = if (drawChars) tile.color.withAlpha(alpha) else tile.color
                    g.fillRect(c, r, 1, 1)
                }
                if (drawChars) {
                    g.color = tile.color
                    g.drawString(
                        tile.ch.toString(),
                        (c + 0.5 + xCharOffset).toFloat(),
                        (r + 0.5 + yCharOffset).toFloat()
                    )
                }
            }
        }
        g.transform = saved
    }

    private fun clipped(state: AnimState): Pair<IntRange, IntRange> {
        val interesting = state.size.roundToInt() + 3
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
//    val zoomTimer = Timer(10) { animState.update { it.copy(size = it.size + 0.08) } }.apply { start() }
    sleep(1000)

    val runTimer = Timer(160) {
        val land = animState.load().let { it.land + findLandDelta(map, it.land) }
        val beach = findBeach(land)
        val forest = findForest(land - beach).toMutableSet()
        repeat(2) { forest += growForest(forest, beach) }
        animState.update {
            it.copy(land = land, beach = beach, forest = forest)
        }
    }.apply { start() }

}

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    part5video(provideInput(year, quest, 3))
}
