package ec1.quest2

import go
import provideInput
import yearAndQuestFromPackage

val example1 = """
    ADD id=1 left=[10,A] right=[30,H]
    ADD id=2 left=[15,D] right=[25,I]
    ADD id=3 left=[12,F] right=[31,J]
    ADD id=4 left=[5,B] right=[27,L]
    ADD id=5 left=[3,C] right=[28,M]
    ADD id=6 left=[20,G] right=[32,K]
    ADD id=7 left=[4,E] right=[21,N]
""".trimIndent()

data class Node(var rank: Int, var symbol: String, var left: Node? = null, var right: Node? = null)

fun parseValue(line: String, key: String) = line.substringAfter("$key=").substringBefore(" ")
fun parsePair(line: String, key: String) = parseValue(line, key).substringAfter("[").substringBefore("]")
    .split(",").let { (rank, symbol) -> rank.toInt() to symbol }

fun Node?.append(other: Node): Node {
    if (this == null) return other
    var n: Node = this
    while (true) {
        if (n.rank > other.rank) {
            val next = n.left
            if (next != null) n = next
            else {
                n.left = other
                return this
            }
        } else if (n.rank < other.rank) {
            val next = n.right
            if (next != null) n = next
            else {
                n.right = other
                return this
            }
        } else error("Rank ${other.rank} already in $this")
    }
}

fun Node.busiest() = generateSequence(listOf(this)) { prev ->
    prev.flatMap { listOf(it.left, it.right) }.filterNotNull().ifEmpty { null }
}.maxBy { it.size }.joinToString("") { it.symbol }

fun part1(data: String): Any {
    val (n1, n2) = data.lineSequence().fold<String, Pair<Node?, Node?>>(null to null) { (n1, n2), line ->
        val left = n1.append(parsePair(line, "left").let { (rank, symbol) -> Node(rank, symbol) })
        val right = n2.append(parsePair(line, "right").let { (rank, symbol) -> Node(rank, symbol) })
        left to right
    }
    return n1!!.busiest() + n2!!.busiest()
}


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", "CFGNLK") { part1(example1) }
    go("part1", "QUACK!GSJXWNSM") { part1(provideInput(year, quest, 1)) }
//    go("part2", 164051258244282) { part2(provideInput(year, quest, 2)) }
//    go("part3", 564392275180979) { part3(provideInput(year, quest, 3)) }
}
