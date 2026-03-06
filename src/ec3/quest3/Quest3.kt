package ec3.quest3

import ec3.quest3.Node.AddResult.AcceptedDirectly
import ec3.quest3.Node.AddResult.AcceptedIndirectly
import ec3.quest3.Node.AddResult.DeniedOrReplaced
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
        object AcceptedDirectly : AddResult
        object AcceptedIndirectly : AddResult
        data class DeniedOrReplaced(val remaining: Node) : AddResult
    }

    fun matchesStrongly(socket: Pair<String, String>) = plug == socket
    fun matchesWeakly(socket: Pair<String, String>) = plug.first == socket.first || plug.second == socket.second

    fun tryStrong(branch: Node): AddResult {
        var b = branch
        val l = left
        if (l == null) {
            if (branch.matchesStrongly(leftSocket)) {
                left = b
                return AcceptedDirectly
            }
        } else {
            val remaining = l.tryStrong(b)
            if (remaining is DeniedOrReplaced) b = remaining.remaining
            else return AcceptedIndirectly
        }
        val r = right
        if (r == null) {
            if (branch.matchesStrongly(rightSocket)) {
                right = b
                return AcceptedDirectly
            }
        } else {
            val remaining = r.tryStrong(b)
            if (remaining is DeniedOrReplaced) b = remaining.remaining
            else return AcceptedIndirectly
        }
        return DeniedOrReplaced(b)
    }

    fun tryWeak(branch: Node): AddResult {
        var b = branch
        val l = left
        if (l == null) {
            if (branch.matchesWeakly(leftSocket)) {
                left = b
                return AcceptedDirectly
            }
        } else {
            val remaining = l.tryWeak(b)
            if (remaining is DeniedOrReplaced) b = remaining.remaining
            else return AcceptedIndirectly
        }
        val r = right
        if (r == null) {
            if (branch.matchesWeakly(rightSocket)) {
                right = b
                return AcceptedDirectly
            }
        } else {
            val remaining = r.tryWeak(b)
            if (remaining is DeniedOrReplaced) b = remaining.remaining
            else return AcceptedIndirectly
        }
        return DeniedOrReplaced(b)
    }

    fun tryWeak1(node: Node): AddResult = when {
        left == null && node.matchesWeakly(leftSocket) -> AcceptedDirectly.also { left = node }
        left?.tryWeak1(node) == AcceptedDirectly -> AcceptedDirectly
        right == null && node.matchesWeakly(rightSocket) -> AcceptedDirectly.also { right = node }
        right?.tryWeak1(node) == AcceptedDirectly -> AcceptedDirectly
        else -> DeniedOrReplaced(node)
    }

    fun tryReplacingWeaker(branch: Node): AddResult {
        var b = branch
        val l = left
        if (l == null) {
            if (b.matchesWeakly(leftSocket)) {
                left = b
                return AcceptedDirectly
            }
        } else if (!l.matchesStrongly(leftSocket) && b.matchesStrongly(leftSocket)) {
            left = b
            b = l
        } else {
            val remaining = l.tryReplacingWeaker(b)
            if (remaining is DeniedOrReplaced) b = remaining.remaining
            else return AcceptedIndirectly
        }
        val r = right
        if (r == null) {
            if (b.matchesWeakly(rightSocket)) {
                right = b
                return AcceptedDirectly
            }
        } else if (!r.matchesStrongly(rightSocket) && b.matchesStrongly(rightSocket)) {
            right = b
            b = r
        } else {
            val remaining = r.tryReplacingWeaker(b)
            if (remaining is DeniedOrReplaced) b = remaining.remaining
            else return AcceptedIndirectly
        }
        return DeniedOrReplaced(b)
    }

    fun flatten(): List<Node> = left?.flatten().orEmpty() + this + right?.flatten().orEmpty()

    fun checksum() = flatten().mapIndexed { index, node -> (index + 1) * node.id }.sum()
}

fun solve(data: String, op: Node.(Node) -> Node.AddResult): Any {
    val nodes = parse(data).toMutableList()
    val tree = nodes.removeFirst()
    while (nodes.isNotEmpty()) {
        val it = nodes.removeFirst()
        tree.op(it)
            .also { if (it is DeniedOrReplaced) nodes.addFirst(it.remaining) }
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
