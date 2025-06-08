package ec1.quest2

import go
import provideInput
import yearAndQuestFromPackage

val exampleA = """
    ADD id=1 left=[10,A] right=[30,H]
    ADD id=2 left=[15,D] right=[25,I]
    ADD id=3 left=[12,F] right=[31,J]
    ADD id=4 left=[5,B] right=[27,L]
    ADD id=5 left=[3,C] right=[28,M]
    SWAP 1
    SWAP 5
    ADD id=6 left=[20,G] right=[32,K]
    ADD id=7 left=[4,E] right=[21,N]
""".trimIndent()

data class Node(var rank: Int, var symbol: String, var left: Node? = null, var right: Node? = null)

fun parseValue(line: String, key: String) = line.substringAfter("$key=").substringBefore(" ")
fun parsePair(line: String, key: String) = parseValue(line, key).substringAfter("[").substringBefore("]")
    .split(",").let { (rank, symbol) -> rank.toInt() to symbol }

fun Node.add(other: Node) {
    var n: Node = this
    while (true) {
        when {
            n.rank > other.rank -> {
                val next = n.left
                if (next != null) n = next
                else {
                    n.left = other
                    return
                }
            }
            n.rank < other.rank -> {
                val next = n.right
                if (next != null) n = next
                else {
                    n.right = other
                    return
                }
            }
            else -> error("Rank ${other.rank} already in $this")
        }
    }
}

fun Node.busiest() = generateSequence(listOf(this)) { prev ->
    prev.flatMap { listOf(it.left, it.right) }.filterNotNull().ifEmpty { null }
}.maxBy { it.size }.joinToString("") { it.symbol }

fun part1(data: String) = solve(data) { l, r -> error("SWAP not supported") }

fun part2(data: String) = solve(data) { l, r ->
    val tmp = l.rank to l.symbol
    l.rank = r.rank
    l.symbol = r.symbol
    r.rank = tmp.first
    r.symbol = tmp.second
}

private fun solve(data: String, swapOp: (Node, Node) -> Unit): String {
    val nodes = mutableMapOf<String, Pair<Node, Node>>()
    var leftTree: Node? = null
    var rightTree: Node? = null
    data.lineSequence().forEach { line ->
        when {
            line.startsWith("ADD") -> {
                val id = parseValue(line, "id")
                val left = parsePair(line, "left").let { (rank, symbol) -> Node(rank, symbol) }
                val right = parsePair(line, "right").let { (rank, symbol) -> Node(rank, symbol) }
                nodes[id] = left to right
                if (leftTree == null) leftTree = left else leftTree.add(left)
                if (rightTree == null) rightTree = right else rightTree.add(right)
            }
            line.startsWith("SWAP") -> {
                val id = line.substringAfter("SWAP ")
                val (left, right) = nodes[id]!!
                swapOp(left, right)
            }
            else -> error("Unknown line `$line`")
        }
    }
    return leftTree!!.busiest() + rightTree!!.busiest()
}

fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", "QUACK!GSJXWNSM") { part1(provideInput(year, quest, 1)) }
    go("part2", "MGFLNK") { part2(exampleA) }
    go("part2", "QUACK!BXXFYHTSRMFSHW") { part2(provideInput(year, quest, 2)) }
//    go("part3", 564392275180979) { part3(provideInput(year, quest, 3)) }
}
