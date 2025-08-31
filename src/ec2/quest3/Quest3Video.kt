@file:OptIn(ExperimentalAtomicApi::class)

package ec2.quest3

import display
import provideInput
import useGraphics
import yearAndQuestFromPackage
import java.awt.Color
import java.awt.image.BufferedImage
import java.lang.Thread.sleep
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

data class AnimState(val taken: List<BooleanArray>)

class Quest3Video() {
    private val bgColor = Color(15, 15, 35)
    private val fgColor = Color(238, 214, 153)

    fun paintOnImage(state: AnimState, image: BufferedImage) = image.useGraphics { g ->
        g.color = bgColor
        g.fillRect(0, 0, image.width, image.height)
    }.also {
        state.taken.forEachIndexed { row, line ->
            line.forEachIndexed { col, value ->
                image.setRGB(col + 40, row + 40, if (value) fgColor.rgb else bgColor.rgb)
            }
        }
    }
}

fun part3video(data: String) {
    val dice = parseDice(data)
    val grid = parseGrid(data)
    val takenCoins = grid.map { BooleanArray(it.size) }

    val animState = AtomicReference(AnimState(takenCoins.map { it.clone() }))
    val video = Quest3Video()

    display(animState, "Quest 3: The Dice that Never Lie (Unless I Tell Them To)", op = video::paintOnImage)

//    sleep(10000) // prepare recording :P

    var prevSize = 0
    var i = 0

//    val toCheck = dice.map { die -> die to grid.placesWithFace(die.roll()) }
//        .toMutableList()
//
//

    dice.reversed().forEach { die ->
        var toCheck = grid.placesWithFace(die.roll())
        while (toCheck.isNotEmpty()) {
            toCheck.forEach { (row, col) -> takenCoins[row][col] = true }
            toCheck = grid.possibleMoves(toCheck, die.roll())
            val newSize = takenCoins.sumOf { row -> row.count { it } }
            if (newSize > prevSize) {
                prevSize = newSize
                i++
                animState.store(AnimState(takenCoins.map { it.clone() }))
                sleep(1L)
//                println("$i -> $newSize")
            }
        }
    }
    println(takenCoins.sumOf { row -> row.count { it } })
}

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    part3video(provideInput(year, quest, 3))
}
