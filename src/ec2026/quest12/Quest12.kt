package ec2026.quest12

import coords.Direction
import coords.move
import coords.pair.Pos
import coords.pair.col
import coords.pair.row
import go
import provideInput
import yearAndQuestFromPackage

val exampleA = """
    ........#.
    .##.......
    .##..####.
    .##..####.
    .....####.
    .###......
""".trimIndent()

val exampleB = """
    ...............................
    .####....#####..#######........
    .####....#####..#######........
    .####....#####..#######........
    .........#####.......##........
    .........########....##........
    .........########....##........
    ...............................
    .######....#.....#.....###.....
    ...........#...........###.....
    ...........#......########.....
    ...........#......########.....
    ...........#......########.....
    ..................########.....
    ...............................
""".trimIndent()

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1 ex", 7) { part1(exampleA) }
    go("part1") { part1(provideInput(year, quest, 1)) }
    go("part2 ex", 23) { part2(exampleB) }
//    go("part2 ex", exampleA) { part2(exampleA) }
    go("part2", 2012) { part2(provideInput(year, quest, 2)) }
//    go("part3 ex", exampleA) { part3(exampleA) }
    go("part3") { part3(provideInput(year, quest, 3)) }
}

fun part1(data: String): Any {
    val arrs = data.lines().map { it.toCharArray() }
    var result = 0;

    arrs.forEachIndexed { r, arr ->
        arr.forEachIndexed { c, ch ->
            if (ch == '#') {
                val w = ((c..arr.lastIndex).firstOrNull { arr[it] != '#' } ?: arr.size) - c
                val h = ((r..arrs.lastIndex).firstOrNull { arrs[it][c] != '#' } ?: arrs.size) - r
                result += minOf(w, h)
                repeat(w) { dc ->
                    repeat(h) { dr ->
                        arrs[r + dr][c + dc] = '.'
                    }
                }
            }
        }
    }

    return result
}

fun part2(data: String): Any {
    val figures = findFigures(data)
    return figures.sumOf { visited ->
        val (rows, cols) = cover(visited) ?: error("WTF")

        val shots = if (rows.first to cols.first in visited &&
            rows.last to cols.first in visited &&
            rows.first to cols.last in visited &&
            rows.last to cols.last in visited
        ) {
            //rectangle
            minOf(rows.last - rows.first, cols.last - cols.first) + 1
        } else {
            //L-Shape
            lShape(cols, rows, visited)
        }
        shots//.also { println(it) }
    }
}

fun part3(data: String): Any {
    val figures = findFigures(data)
    return figures.sumOf { visited ->
        val (rows, cols) = cover(visited) ?: error("WTF")

        val shots = if (rows.first to cols.first in visited &&
            rows.last to cols.first in visited &&
            rows.first to cols.last in visited &&
            rows.last to cols.last in visited
        ) {
            //rectangle
            TODO()
        } else {
            //L-Shape
            lShape(cols, rows, visited)
        }
        shots//.also { println(it) }
    }
}


private fun findFigures(data: String): List<MutableSet<Pos>> {
    val arrs = data.lines().map { it.toCharArray() }.toTypedArray()

    val figures = buildList {
        arrs.forEachIndexed { r, arr ->
            arr.forEachIndexed { c, ch ->
                if (ch == '#') {
                    val visited = mutableSetOf(Pos(r, c))
                    val toGo = mutableListOf(Pos(r, c))
                    arrs[r][c] = '.'
                    while (toGo.isNotEmpty()) {
                        val next = toGo.removeFirst()
                        Direction.entries.map(next::move)
                            .filter { (r1, c1) -> r1 in arrs.indices && c1 in arrs[r1].indices && arrs[r1][c1] == '#' }
                            .filter { it !in visited }
                            .forEach { visited += it; toGo += it; arrs[it.row][it.col] = '.' }
                    }
                    add(visited)
                }
            }
        }
    }
    return figures
}

private fun lShape(
    cols: IntRange,
    rows: IntRange,
    visited: MutableSet<Pos>
): Int {
    val l1 = cols.first { rows.first to it in visited && rows.last to it in visited }
    val r1 = cols.last { rows.first to it in visited && rows.last to it in visited }
    val t1 = rows.first { it to cols.first in visited && it to cols.last in visited }
    val b1 = rows.last { it to cols.first in visited && it to cols.last in visited }

    // could be optimized HEAVILY, in spare time
    val commonShotVertical = b1 - t1 + 1 + calc(visited.filter { it.row !in t1..b1 }.toSet())
    val commonShotHorizontal = r1 - l1 + 1 + calc(visited.filter { it.col !in l1..r1 }.toSet())

    return minOf(commonShotHorizontal, commonShotVertical)
}

private fun calc(points: Set<Pos>): Int {
    val (rows, cols) = cover(points) ?: return 0
    return minOf(rows.last - rows.first, cols.last - cols.first) + 1
}

fun cover(points: Set<Pos>) = if (points.isEmpty()) null else {
    var left = Int.MAX_VALUE
    var right = Int.MIN_VALUE
    var top = Int.MAX_VALUE
    var bottom = Int.MIN_VALUE
    points.forEach { (r, c) ->
        left = left.coerceAtMost(c)
        right = right.coerceAtLeast(c)
        top = top.coerceAtMost(r)
        bottom = bottom.coerceAtLeast(r)
    }
    top..bottom to left..right
}
