package ec2025.quest9

import go
import loggedTimed
import provideInput
import yearAndQuestFromPackage
import kotlin.time.measureTimedValue


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 6290) { part1(provideInput(year, quest, 1)) }
    go("part2", 322339) { part2(provideInput(year, quest, 2)) }
    go("part3", 40269) { part3(provideInput(year, quest, 3)) }
}

data class Scale(val id: Int, val dna: String)

fun degree(child: Scale, parents: Pair<Scale, Scale>) =
    child.dna.indices.count { child.dna[it] == parents.first.dna[it] } *
            child.dna.indices.count { child.dna[it] == parents.second.dna[it] }

fun matchParentsToChildren(scales: List<Scale>): List<Pair<Scale, Pair<Scale, Scale>>> = measureTimedValue {
    val pairs = (0..<scales.lastIndex).asSequence().flatMap { i1 -> (i1 + 1..scales.lastIndex).map { i2 -> i1 to i2 } }
        .map { (i1, i2) -> scales[i1] to scales[i2] }
    scales.mapNotNull { child ->
        pairs.firstOrNull { (p1, p2) ->
            p1.id != child.id && p2.id != child.id &&
                    child.dna.indices.all { child.dna[it] == p1.dna[it] || child.dna[it] == p2.dna[it] }
        }?.let { child to it }
    }
}.loggedTimed("matchParentsToChildren()")

private fun String.parse(): List<Scale> = measureTimedValue {
    lines().map { it.split(":") }.map { (id, dna) -> Scale(id.toInt(), dna) }
}.loggedTimed("parse()")

fun part1(data: String) = part2(data)

fun part2(data: String) = matchParentsToChildren(data.parse()).sumOf { (child, parents) -> degree(child, parents) }

fun part3(data: String): Any {
    val scales = data.parse()
    val childrenWithParents = matchParentsToChildren(scales)

    val largest = measureTimedValue {
//        val indices = scales.indices.associateBy { scales[it].id }
//        val uf = UnionFind<Int>(scales.size) { indices[it]!! }
        val uf = UnionFind<Int>(scales.size) { it - 1 }
        childrenWithParents.forEach { (child, parents) ->
            uf.union(child.id, parents.first.id)
            uf.union(child.id, parents.second.id)
        }

        scales.map { it.id }.groupBy { uf.find(it) }.maxBy { it.value.size }
    }.loggedTimed("union-find")
    return largest.value.sum()
}

class UnionFind<T>(size: Int, val indexOp: (T) -> Int) {
    private val parent = IntArray(size) { it }
    private val rank = IntArray(size) { 0 }

    fun findByIndex(i: Int): Int {
        if (parent[i] != i) parent[i] = findByIndex(parent[i])
        return parent[i]
    }

    fun find(e: T): Int = findByIndex(indexOp(e))

    fun unionByIndex(i1: Int, i2: Int) {
        val rootX = findByIndex(i1)
        val rootY = findByIndex(i2)
        if (rootX != rootY) when {
            rank[rootX] > rank[rootY] -> parent[rootY] = rootX
            rank[rootX] < rank[rootY] -> parent[rootX] = rootY
            else -> parent[rootY] = rootX.also { rank[rootX]++ }
        }
    }

    fun union(e1: T, e2: T) = unionByIndex(indexOp(e1), indexOp(e2))
}

