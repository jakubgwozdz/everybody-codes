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
import java.awt.Graphics2D
import java.awt.Point
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.lang.Thread.sleep
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.update
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime

enum class Tile(val ch: Char, val color: Color) {
    Sea('~', Color.BLUE),
    One('^', Color.BLUE.brighter()),
    Grass('#', Color.GREEN.brighter()),
    Sand('=', Color.YELLOW),
    Tree('@', Color.GREEN.darker())
}

data class AnimState(
    val center: Pos,
    val visible: Double,
    val ones: Set<Pos>,
    val land: Set<Pos> = emptySet(),
    val beach: Set<Pos> = emptySet(),
    val forest: Set<Pos> = emptySet(),
    val lastDelta: Set<Pos> = emptySet(),
    val previousMap: List<List<Tile>>,
    val currentMap: List<List<Tile>>,
    val phase: Double = 1.0,
    val generation: Int = 0,
)

internal fun findLandDelta(map: List<String>, land: Set<Pos>, lastDelta: Set<Pos>) =
    lastDelta.flatMap { it.adjacent() }.toSet()
        .filter { it.adjacent().count { p -> p in lastDelta || is1(map, p) } >= 5 } - land

class Quest5Video {
    private val bgColor = Color(15, 15, 35)
    private val fgColor = Color(238, 214, 153)

    operator fun AnimState.get(pos: Pos) = when {
        pos in forest -> Tile.Tree
        pos in beach -> Tile.Sand
        pos in land -> Tile.Grass
        pos in ones -> Tile.One
        else -> Tile.Sea
    }

    private val charsBelow = 15.0
    private val rectsFrom = 65.0

    fun paintOnImage(state: AnimState, image: BufferedImage) = measureTime {
        paintOnImageInt(state, image)
    }.also {
        println(it)
    }

    fun paintOnImageInt(state: AnimState, image: BufferedImage) = image.useGraphics { g ->
        val half = minOf(image.width, image.height) / 2.0
        val cellW = half / state.visible
        val cellH = cellW * state.center.col / state.center.row

        val fm = g.getFontMetrics(g.font.deriveFont(cellW.toFloat()))
        val xCharOffset = -fm.charWidth('#') / 2.0 / cellW
        val yCharOffset = (fm.ascent - fm.descent) / 2.0 / cellH

        val drawRects = state.visible >= charsBelow
        val drawChars = state.visible < rectsFrom
        val alpha = (((state.visible - charsBelow) / (rectsFrom - charsBelow)).coerceIn(0.0, 1.0) * 255).toInt()

        g.color = if (drawChars) bgColor else Tile.Sea.color
        g.fillRect(0, 0, image.width, image.height)

        val saved = g.transform
        g.translate(image.width / 2.0, image.height / 2.0)
        g.scale(cellW, cellH)
        g.translate(-(state.center.col + 0.5), -(state.center.row + 0.5))
        g.font = g.font.deriveFont(1f)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
//        val phase = state.generation % 10
//        val tileAlphaNext = if (phase < 5) 255 * phase / 4 else 255
//        val tileAlphaPrev = if (phase < 5) 255 else 255 * (9 - phase) / 5

        val (rows, cols) = clipped(state)
        rows.forEach { r ->
            cols.forEach { c ->
                val nextTile = state[Pos(r, c)]
//                val nextTile = state.currentMap[Pos(r, c)] ?: Tile.Sea
//                val prevTile = state.previousMap[Pos(r, c)] ?: Tile.Sea
//                if (prevTile == nextTile) {
                g.drawTile(nextTile, c, r, drawRects, drawChars, alpha, 255, xCharOffset, yCharOffset)
//                } else {
//                    g.drawTile(prevTile, c, r, drawRects, drawChars, alpha, tileAlphaPrev, xCharOffset, yCharOffset)
//                    g.drawTile(nextTile, c, r, drawRects, drawChars, alpha, tileAlphaNext, xCharOffset, yCharOffset)
//                }
            }
        }
        g.transform = saved
    }

    private fun Graphics2D.drawTile(
        tile: Tile,
        c: Int,
        r: Int,
        drawRects: Boolean,
        drawChars: Boolean,
        globalBgAlpha: Int,
        tileAlpha: Int,
        xCharOffset: Double,
        yCharOffset: Double
    ) {
        if (drawRects) {
            this.color =
                if (drawChars) tile.color.withAlpha(globalBgAlpha * tileAlpha / 255) else tile.color.withAlpha(tileAlpha)
            fillRect(c, r, 1, 1)
        }
        if (drawChars) {
            this.color = tile.color.withAlpha(tileAlpha)
            drawString(
                tile.ch.toString(),
                (c + 0.5 + xCharOffset).toFloat(),
                (r + 0.5 + yCharOffset).toFloat()
            )
        }
    }

    private fun clipped(state: AnimState): Pair<IntRange, IntRange> {
        val interesting = (state.visible * 1.1).roundToInt()
        val (cx, cy) = state.center
        val rows = (cx - interesting).coerceAtLeast(0)..(cx + interesting).coerceAtMost(cx * 2 + 1)
        val cols = (cy - interesting).coerceAtLeast(0)..(cy + interesting).coerceAtMost(cy * 2 + 1)
        return rows to cols
    }
}

@OptIn(ExperimentalAtomicApi::class)
fun part5video(data: String) {
    val map = data.lines()
    val center = findCenter(map)
    val tmp = map.map { it.map { if (it == '1') Tile.One else Tile.Sea } }
    val animState = AtomicReference(
        AnimState(
            center,
            visible = 3.0,
            ones = map.findAll('1'),
            previousMap = tmp,
            currentMap = tmp,
        )
    )
    val video = Quest5Video()
    display(
        animState,
        "Quest 5: The Cartograffity",
        location = Point(300, 200),
        refreshDelay = 16,
        op = video::paintOnImage
    )

    do {
        val nextTick = Clock.System.now() + 10.milliseconds
        val prev = animState.load()
        when {
            prev.generation == 91 -> animState.update { it.copy(lastDelta = setOf(it.center)) }
            prev.generation % 10 == 0 -> {
//                val newMap = prev.currentMap.map { it.toMutableList() }
                val delta = findLandDelta(map, prev.land, prev.lastDelta).toSet()
                val land = prev.land + prev.lastDelta + delta
                val beach = findBeach(land)
                val forest = findForest(land - beach).toMutableSet()
                repeat(2) { forest += growForest(forest, beach) }
//                delta.forEach { p ->
//                    newMap[p] = when (p) {
//                        in beach -> Tile.Sand
//                        in forest -> Tile.Tree
//                        else -> Tile.Grass
//                    }
//                }
//                beach.forEach { p -> newMap[p] = Tile.Sand }
//                forest.forEach { p ->  newMap[p] = Tile.Tree }
                animState.update {
                    it.copy(
                        land = land,
                        beach = beach,
                        forest = forest,
                        lastDelta = delta,
//                        previousMap = it.currentMap,
//                        currentMap = newMap,
                    )
                }
            }
        }

        animState.update { it.copy(generation = it.generation + 1, visible = it.visible + 0.1) }
        val delay = (nextTick - Clock.System.now()).inWholeMilliseconds
        if (delay > 0) sleep(delay)
    } while (prev.lastDelta.isNotEmpty() || prev.land.isEmpty())
    println("size: " + animState.load().forest.size)

}

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    part5video(provideInput(year, quest, 3))
}
