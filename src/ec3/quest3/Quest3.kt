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

fun part1(data: String) = solve(data) { candidate, (type, content) ->
    content == null && candidate.matchesStrongly(type)
}

fun part2(data: String) = solve(data) { candidate, (type, content) ->
    content == null && candidate.matchesWeakly(type)
}

fun part3(data: String) = solve(data) { candidate, (type, content) ->
    if (content == null) candidate.matchesWeakly(type)
    else !content.matchesStrongly(type) && candidate.matchesStrongly(type)
}


fun parse(data: String): List<Node> = data.reader().readLines().map { line ->
    line.split(',').map { it.substringAfter('=') }.let { (id, plug, leftSocket, rightSocket, data) ->
        Node(
            id.toInt(),
            plug.split(' ').let { (c, s) -> Pair(c, s) },
            Socket(leftSocket.split(' ').let { (c, s) -> Pair(c, s) }),
            Socket(rightSocket.split(' ').let { (c, s) -> Pair(c, s) }),
            data
        )
    }
}

data class Socket(
    val type: Pair<String, String>,
    var content: Node? = null,
)

data class Node(
    val id: Int,
    val plug: Pair<String, String>,
    val left: Socket,
    val right: Socket,
    val data: String,
) {

    fun matchesStrongly(socketType: Pair<String, String>) =
        plug == socketType

    fun matchesWeakly(socketType: Pair<String, String>) =
        plug.first == socketType.first || plug.second == socketType.second

    fun attemptConsume(
        branch: Node,
        testOp: (Node, Socket) -> Boolean,
    ): Node? {
        val (directLeft, bl) = attemptConsumeOneSide(branch, left, testOp)
        if (directLeft) left.content = branch
        val (directRight, br) = attemptConsumeOneSide(bl, right, testOp)
        if (directRight) right.content = bl
        return br
    }

    fun flatten(): List<Node> = left.content?.flatten().orEmpty() + this + right.content?.flatten().orEmpty()

    fun checksum() = flatten().mapIndexed { index, node -> (index + 1) * node.id }.sum()
}

fun attemptConsumeOneSide(
    branch: Node?,
    socket: Socket,
    testOp: (Node, Socket) -> Boolean,
): Pair<Boolean, Node?> = when {
    branch == null -> false to null
    testOp(branch, socket) -> true to socket.content
    socket.content != null -> false to socket.content!!.attemptConsume(branch, testOp)
    else -> false to branch
}

fun solve(data: String, testOp: (Node, Socket) -> Boolean): Any {
    val nodes = parse(data).toMutableList()
    val tree = nodes.removeFirst()
    while (nodes.isNotEmpty()) {
        val next = nodes.removeFirst()
        tree.attemptConsume(next, testOp)
            ?.let { nodes.addFirst(it) }
    }
//    tree.play()
    return tree.checksum()

}

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
