package ec2025.quest17

import coords.Direction
import coords.findAll
import coords.move
import coords.pair.Pos
import coords.pair.col
import coords.pair.row
import go
import logged
import provideInput
import search.astar
import yearAndQuestFromPackage
import kotlin.math.PI
import kotlin.math.atan2


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 1560) { part1(provideInput(year, quest, 1)) }
    go("part2", 66807) { part2(provideInput(year, quest, 2)) }
    val part3ex1 = """
        2645233S5466644
        634566343252465
        353336645243246
        233343552544555
        225243326235365
        536334634462246
        666344656233244
        6426432@2366453
        364346442652235
        253652463426433
        426666225623563
        555462553462364
        346225464436334
        643362324542432
        463332353552464
    """.trimIndent()

    val part3ex3 = """
        5441525241225111112253553251553
        133522122534119S911411222155114
        3445445533355599933443455544333
        3345333555434334535435433335533
        5353333345335554434535533555354
        3533533435355443543433453355553
        3553353435335554334453355435433
        5435355533533355533535335345335
        4353545353545354555534334453353
        4454543553533544443353355553453
        5334554534533355333355543533454
        4433333345445354553533554555533
        5554454343455334355445533453453
        4435554534445553335434455334353
        3533435453433535345355533545555
        534433533533535@353533355553345
        4453545555435334544453344455554
        4353333535535354535353353535355
        4345444453554554535355345343354
        3534544535533355333333445433555
        3535333335335334333534553543535
        5433355333553344355555344553435
        5355535355535334555435534555344
        3355433335553553535334544544333
        3554333535553335343555345553535
        3554433545353554334554345343343
        5533353435533535333355343333555
        5355555353355553535354333535355
        4344534353535455333455353335333
        5444333535533453535335454535553
        3534343355355355553543545553345
    """.trimIndent()
    go("part3ex1", 592) { part3(part3ex1) }
    go("part3ex3", 3180) { part3(part3ex3) }
    go("part3", 48640) { part3(provideInput(year, quest, 3)) }
}

fun Pos.dist2(r: Int, c: Int) = (row - r) * (row - r) + (col - c) * (col - c)
fun Pos.angleTo(p: Pos): Double {
    val dCol = p.col - col
    val dRow = row - p.row
    return (atan2(dCol.toDouble(), dRow.toDouble()) + 2 * PI) % (2 * PI)
}

fun part1(data: String): Any {
    val grid = data.lines().map { line -> line.map { chr -> chr.digitToIntOrNull() ?: 0 }.toIntArray() }
    val volcano = data.lines().findAll('@').single()
    return grid.indices.sumOf { r ->
        grid[r].indices.sumOf { c ->
            if (volcano.dist2(r, c) in 1..10 * 10) grid[r][c] else 0
        }
    }
}

fun part2(data: String): Any {
    val grid = data.lines().map { line -> line.map { chr -> chr.digitToIntOrNull() ?: 0 }.toIntArray() }
    val volcano = data.lines().findAll('@').single()
    var step = 0
    val steps = buildMap {
        while (true) {
            step++
            val range = (step - 1) * (step - 1) + 1..step * step
            val d = grid.indices.sumOf { r ->
                grid[r].indices.sumOf { c -> if (volcano.dist2(r, c) in range) grid[r][c] else 0 }
            }
            put(step, d)
            if (d == 0) break
        }
    }
    return steps.maxBy { it.value }.let { it.key * it.value }
}

fun part3(data: String): Any {
    val grid = data.lines().map { line -> line.map { chr -> chr.digitToIntOrNull() ?: 0 }.toIntArray() }
    val volcano = data.lines().findAll('@').single()
    val start = data.lines().findAll('S').single()
    return (1..volcano.row).firstNotNullOf { step ->
//        step.logged("step")
        solve(grid, start, volcano, step)?.takeIf { it < step * 30 }?.let { it * (step - 1) }
    }
}

fun solve(grid: List<IntArray>, start: Pos, volcano: Pos, step: Int): Int? {
    val phases = 12
    val tolerance = phases / 4
    return astar(
        start = 0 to start,
        endPredicate = { (phase, pos) -> phase > phases - tolerance && pos == start },
        heuristics = { (phase, pos) -> phases - phase },
        neighbours = { (phase, pos) ->
            Direction.entries.mapNotNull { d ->
                pos.move(d).takeIf { (r, c) ->
                    r in grid.indices && c in grid[r].indices && volcano.dist2(r, c) > (step - 1) * (step - 1)
                }?.let { next ->
                    val nextPhase = (volcano.angleTo(next) / (2 * PI) * phases).toInt()
                        .takeIf { it in phase - tolerance..phase + tolerance } ?: phase
                    nextPhase to next to grid[next.row][next.col]
                }
            }
        })
//        ?.also { l -> printlnPath(l, grid) }
        ?.map { (phase, pos) -> pos }?.sumOf { (r, c) -> grid[r][c] }
}

private fun printlnPath(l: List<Pair<Int, Pos>>, grid: List<IntArray>) {
    val set = l.map { (phase, pos) ->pos }.toSet()
    grid.indices.forEach { r ->
        grid[r].indices.forEach { c ->
            print(if (r to c in set) grid[r][c] else '.')
        }
        println()
    }
//    debug {
//        l.windowed(2).map { (a, b) ->
//            val dir = Direction.entries.first { a.pos.move(it) == b.pos }
//            "$dir->${b.pos}(s${b.phase}) '${grid[b.pos.row][b.pos.col]}'"
//        }
//    }
}


