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

    sealed interface AddResult {
        object Accepted : AddResult
        data class DeniedOrReplaced(val remaining: Node) : AddResult
    }

    fun matchesStrongly(socket: Pair<String, String>) = plug == socket
    fun matchesWeakly(socket: Pair<String, String>) = plug.first == socket.first || plug.second == socket.second

    fun addRequireStrong(node: Node): AddResult = when {
        left == null && node.matchesStrongly(leftSocket) -> AddResult.Accepted.also { left = node }
        left?.addRequireStrong(node) == AddResult.Accepted -> AddResult.Accepted
        right == null && node.matchesStrongly(rightSocket) -> AddResult.Accepted.also { right = node }
        right?.addRequireStrong(node) == AddResult.Accepted -> AddResult.Accepted
        else -> AddResult.DeniedOrReplaced(node)
    }

    fun addAllowingWeak(node: Node): AddResult = when {
        left == null && node.matchesWeakly(leftSocket) -> AddResult.Accepted.also { left = node }
        left?.addAllowingWeak(node) == AddResult.Accepted -> AddResult.Accepted
        right == null && node.matchesWeakly(rightSocket) -> AddResult.Accepted.also { right = node }
        right?.addAllowingWeak(node) == AddResult.Accepted -> AddResult.Accepted
        else -> AddResult.DeniedOrReplaced(node)
    }

    fun addReplacing(branch: Node): Node? {
        var b = branch
        if (left == null && b.matchesWeakly(leftSocket)) {
            left = b
            return null
        }
        if (left != null && left?.plug != leftSocket && b.plug == leftSocket) {
            val t = left!!
            left = b
            b = t
        } else if (left != null) b = left?.addReplacing(b) ?: return null
        if (right == null && b.matchesWeakly(rightSocket)) {
            right = b
            return null
        }
        if (right != null && right?.plug != rightSocket && b.plug == rightSocket) {
            val t = right!!
            right = b
            b = t
        } else if (right != null) b = right?.addReplacing(b) ?: return null
        return b
    }

    fun flatten(): List<Node> = left?.flatten().orEmpty() + this + right?.flatten().orEmpty()

    fun checksum() = flatten().mapIndexed { index, node -> (index + 1) * node.id }.sum()
}

fun part1(data: String): Any {
    val nodes = parse(data).toMutableList()
    val tree = nodes.removeFirst()
    while (nodes.isNotEmpty()) {
        val it = nodes.removeFirst()
        tree.addRequireStrong(it)
            .also { if (it is Node.AddResult.DeniedOrReplaced) nodes.addFirst(it.remaining) }
    }
    tree.play()
    return tree.checksum()
}

fun part2(data: String): Any {
    val nodes = parse(data).toMutableList()
    val tree = nodes.removeFirst()
    while (nodes.isNotEmpty()) {
        val it = nodes.removeFirst()
        tree.addAllowingWeak(it)
            .also { if (it is Node.AddResult.DeniedOrReplaced) nodes.addFirst(it.remaining) }
    }
    tree.play()
    return tree.checksum()
}

fun part3(data: String): Any {
    val nodes = parse(data).toMutableList()
    val tree = nodes.removeFirst()
    while (nodes.isNotEmpty()) {
        val it = nodes.removeFirst()
        tree.addReplacing(it)?.let { nodes.addFirst(it) }
    }
    tree.play()
    return tree.checksum()
}

fun Node.play() {
    return
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
