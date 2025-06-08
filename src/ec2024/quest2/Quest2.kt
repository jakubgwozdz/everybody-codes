package ec2024.quest2

import provideInput

fun main() {
    part1(provideInput(2024, 2, 1)).also { println("part1: $it") }
    part2(provideInput(2024, 2, 2)).also { println("part2: $it") }
    part3(provideInput(2024, 2, 3)).also { println("part3: $it") }
}

fun part1(data: String): Any {
    val (words, _, inscription) = data.lines()
    val wordsList = words.substringAfter(":").split(",")
    return wordsList.sumOf { Regex(it).findAll(inscription).count() }
}

fun part2(data: String): Any {
    val wordsList = data.lineSequence().first().substringAfter(":").split(",")
        .let { it + it.map { it.reversed() } }.toSet()
    return data.lines().drop(1).sumOf { inscription ->
        val checked = BooleanArray(inscription.length)
        inscription.indices.forEach { i ->
            wordsList.forEach { word ->
                if (inscription.startsWith(word, i)) repeat(word.length) { checked[i + it] = true }
            }
        }
        checked.count { it }
    }
}

fun part3(data: String): Any {
    val wordsList = data.lineSequence().first().substringAfter(":").split(",")
        .let { it + it.map { it.reversed() } }.toSet()

    val lines = data.lines().drop(2)
    val used = lines.map { BooleanArray(it.length) }

    lines.forEachIndexed { i, line ->
        line.indices.forEach { j ->
            wordsList.forEach { word ->
                if ((line + line).startsWith(word, j)) repeat(word.length) { used[i][(j + it) % line.length] = true }
            }
        }
    }

    val transposed = lines.first().indices.map { j ->
        lines.map { it[j] }.joinToString("")
    }

    transposed.forEachIndexed { i, line ->
        line.indices.forEach { j ->
            wordsList.forEach { word ->
                if (line.startsWith(word, j)) repeat(word.length) { used[j+it][i] = true }
            }
        }
    }

    return used.sumOf { it.count { it } }
}
