package ec3.quest3

import go
import provideInput
import yearAndQuestFromPackage
import javax.sound.midi.MidiSystem


fun main() {
    val (year, quest) = yearAndQuestFromPackage({ })
    go("part1", 5371) { part1(provideInput(year, quest, 1)) }
    go("part2", 319081) { part2(provideInput(year, quest, 2)) }
    go("part3", 396978) { part3(provideInput(year, quest, 3)) }
}

fun parse(data: String): List<Node> = data.reader().readLines().map { line ->
    line.split(',').map { it.substringAfter('=') }.let { (id, plug, leftSocket, rightSocket, data) ->
        Node(
            id.toInt(),
            plug.split(' ').let { (c, s) -> Pair(c, s) },
            leftSocket.split(' ').let { (c, s) -> Pair(c, s) },
            rightSocket.split(' ').let { (c, s) -> Pair(c, s) },
            data
        )
    }
}

data class Node(
    val id: Int,
    val plug: Pair<String, String>,
    val leftSocket: Pair<String, String>,
    val rightSocket: Pair<String, String>,
    val data: String,
    var left: Node? = null,
    var right: Node? = null,
) {

    fun matchesStrongly(socket: Pair<String, String>) = plug == socket
    fun matchesWeakly(socket: Pair<String, String>) = plug.first == socket.first || plug.second == socket.second

    fun Node?.attemptConsumeOneSide(
        branch: Node,
        socket: Pair<String, String>,
        tryOp: Node.(Node) -> Node?,
        testOp: (Node?, Node, Pair<String, String>) -> Boolean,
    ): Pair<Boolean, Node?> = when {
        testOp(this, branch, socket) -> true to this
        this != null -> false to tryOp(branch)
        else -> false to branch
    }

    fun attemptConsumeBothSides(
        branch: Node,
        tryOp: Node.(Node) -> Node?,
        testOp: (Node?, Node, Pair<String, String>) -> Boolean,
    ): Node? {
        val (directLeft, bl) = left.attemptConsumeOneSide(branch, leftSocket, tryOp, testOp)
        if (directLeft) left = branch
        if (bl == null) return null
        val (directRight, br) = right.attemptConsumeOneSide(bl, rightSocket, tryOp, testOp)
        if (directRight) right = bl
        return br
    }

    fun tryStrong(branch: Node): Node? =
        attemptConsumeBothSides(branch, Node::tryStrong) { curr, candidate, socket ->
            curr == null && candidate.matchesStrongly(socket)
        }

    fun tryWeak(branch: Node): Node? =
        attemptConsumeBothSides(branch, Node::tryWeak) { curr, candidate, socket ->
            curr == null && candidate.matchesWeakly(socket)
        }


    fun tryReplacingWeaker(branch: Node): Node? =
        attemptConsumeBothSides(branch, Node::tryReplacingWeaker) { curr, candidate, socket ->
            if (curr == null) candidate.matchesWeakly(socket)
            else !curr.matchesStrongly(socket) && candidate.matchesStrongly(socket)
        }

    fun flatten(): List<Node> = left?.flatten().orEmpty() + this + right?.flatten().orEmpty()

    fun checksum() = flatten().mapIndexed { index, node -> (index + 1) * node.id }.sum()
}

fun solve(data: String, op: Node.(Node) -> Node?): Any {
    val nodes = parse(data).toMutableList()
    val tree = nodes.removeFirst()
    while (nodes.isNotEmpty()) {
        val next = nodes.removeFirst()
        tree.op(next)
            ?.let { nodes.addFirst(it) }
    }
//    tree.play()
    return tree.checksum()

}

fun part1(data: String) = solve(data, Node::tryStrong)

fun part2(data: String) = solve(data, Node::tryWeak)

fun part3(data: String) = solve(data, Node::tryReplacingWeaker)

fun Node.play() {
    val synth = MidiSystem.getSynthesizer()
    synth.open()
    Thread.sleep(160)
    val channel = synth.channels[0]
    flatten().forEach {
        println(it.data)
        val notes = it.data.mapIndexed { index, ch ->
            val note = 48 + (index / 7) * 12 + when (index % 7) {
                0 -> 0
                1 -> 2
                2 -> 4
                3 -> 5
                4 -> 7
                5 -> 9
                6 -> 11
                else -> error("Unreachable")
            }
            val velocity = when (ch) {
                '1' -> 80
                '2' -> 100
                '3' -> 120
                '-' -> 0
                else -> error("Unreachable")
            }
            note to velocity
        }.filter { (note, v) -> v > 0 }
        notes.forEach { (note, v) -> channel.noteOn(note, v) }
        Thread.sleep(160)
        notes.forEach { (note, v) -> channel.noteOff(note) }
    }
    Thread.sleep(160)
    synth.close()
}
