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
            plug.split(' ').let { (c, s) -> Pair<String, String>(c, s) },
            leftSocket.split(' ').let { (c, s) -> Pair<String, String>(c, s) },
            rightSocket.split(' ').let { (c, s) -> Pair<String, String>(c, s) },
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
) {
    fun matchesWeakly(socket: Pair<String, String>) = plug.first == socket.first || plug.second == socket.second
}


data class Tree(
    val current: Node,
    var left: Tree? = null,
    var right: Tree? = null,
) {
    fun addRequireStrong(node: Node): Boolean = when {
        left == null && node.plug == current.leftSocket -> true.also { left = Tree(node) }
        left?.addRequireStrong(node) == true -> true
        right == null && node.plug == current.rightSocket -> true.also { right = Tree(node) }
        right?.addRequireStrong(node) == true -> true
        else -> false
    }

    fun matchesWeakly(socket: Pair<String, String>) = current.plug.first == socket.first || current.plug.second == socket.second

    fun addAllowingWeak(node: Node): Boolean = when {
        left == null && node.matchesWeakly(current.leftSocket) -> true.also { left = Tree(node) }
        left?.addAllowingWeak(node) == true -> true
        right == null && node.matchesWeakly(current.rightSocket) -> true.also { right = Tree(node) }
        right?.addAllowingWeak(node) == true -> true
        else -> false
    }

    fun addReplacing(branch: Tree): Tree? {
        var b = branch
        if (left == null && b.current.matchesWeakly(current.leftSocket)) {
            left = b
            return null
        }
        if (left != null && left?.current?.plug != current.leftSocket && b.current.plug == current.leftSocket) {
            val t = left!!
            left = b
            b = t
        } else if (left != null) b = left?.addReplacing(b) ?: return null
        if (right == null && b.current.matchesWeakly(current.rightSocket)) {
            right = b
            return null
        }
        if (right != null && right?.current?.plug != current.rightSocket && b.current.plug == current.rightSocket) {
            val t = right!!
            right = b
            b = t
        } else if (right != null) b = right?.addReplacing(b) ?: return null
        return b
    }

    fun flatten(): List<Node> = left?.flatten().orEmpty() + current + right?.flatten().orEmpty()

    fun checksum() = flatten().mapIndexed { index, node -> (index + 1) * node.id }.sum()
}

fun part1(data: String): Any {
    val nodes = parse(data)
    val tree = Tree(nodes.first())
    nodes.drop(1).forEach { tree.addRequireStrong(it) }
    tree.play()
    return tree.checksum()
}

fun part2(data: String): Any {
    val nodes = parse(data)
    val tree = Tree(nodes.first())
    nodes.drop(1).forEach { tree.addAllowingWeak(it) }
    tree.play()
    return tree.checksum()
}

fun part3(data: String): Any {
    val nodes = parse(data).map { Tree(it) }.toMutableList()
    val tree = nodes.removeFirst()
    while (nodes.isNotEmpty()) {
        val it = nodes.removeFirst()
        tree.addReplacing(it)?.let { nodes.addFirst(it) }
    }
    tree.play()
    return tree.checksum()
}

fun Tree.play() {
    val synth = MidiSystem.getSynthesizer()
    synth.open()
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
    synth.close()
}
