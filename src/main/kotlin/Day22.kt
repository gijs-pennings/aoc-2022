private lateinit var nodes: List<List<Node>>
private val w get() = nodes.size
private val h get() = nodes[0].size

fun main() {
    val lines = readInput(22)
    val map = lines.dropLast(2)
    val path = lines.last()

    nodes = List(map.maxOf { it.length }) { x ->
        List(map.size) { y ->
            Node(FullPos.fromGlobal(x, y), map[y].getOrNull(x) ?: ' ')
        }
    }
    for (u in nodes.flatten())
        if (u.repr != ' ')
            for (f in Facing.VALS) {
                val pair = next(u, f)
                if (pair.first.repr == '.') u.adj[f.ordinal] = pair  // keep self-loop if neighbor is wall
            }

    var i = 0
    var f = Facing.RIGHT
    var u = nodes.map { it[0] }.first { it.repr == '.' }
    while (i < path.length)
        if (path[i].isDigit()) {
            val s = path.drop(i).takeWhile { it.isDigit() }
            i += s.length
            repeat(s.toInt()) {
                val pair = u.adj[f.ordinal]
                u = pair.first
                f = pair.second
            }
        } else {
            f = f.turn(path[i] == 'R')
            i++
        }

    println(1000 * (u.pos.global.y + 1) + 4 * (u.pos.global.x + 1) + f.ordinal)
}

private fun next(u: Node, f: Facing): Pair<Node, Facing> {
    val global = u.pos.global + f.d
    if (global.isWithin(w, h)) {
        val v = nodes[global]
        if (v.repr != ' ') return Pair(v, f)
    }

    val local = u.pos.local
    return when (u.pos.side) {
        0 -> {
            if (f == Facing.LEFT)
                Pair(nodes[FullPos.fromLocal(0, 49 - local.y, 3)], Facing.RIGHT)
            else // up
                Pair(nodes[FullPos.fromLocal(0, local.x, 5)], Facing.RIGHT)
        }
        1 -> when (f) {
            Facing.RIGHT -> Pair(nodes[FullPos.fromLocal(49, 49 - local.y, 4)], Facing.LEFT)
            Facing.DOWN -> Pair(nodes[FullPos.fromLocal(49, local.x, 2)], Facing.LEFT)
            Facing.UP -> Pair(nodes[FullPos.fromLocal(local.x, 49, 5)], Facing.UP)
            else -> throw IllegalStateException()
        }
        2 -> {
            if (f == Facing.RIGHT)
                Pair(nodes[FullPos.fromLocal(local.y, 49, 1)], Facing.UP)
            else // left
                Pair(nodes[FullPos.fromLocal(local.y, 0, 3)], Facing.DOWN)
        }
        3 -> {
            if (f == Facing.LEFT)
                Pair(nodes[FullPos.fromLocal(0, 49 - local.y, 0)], Facing.RIGHT)
            else // up
                Pair(nodes[FullPos.fromLocal(0, local.x, 2)], Facing.RIGHT)
        }
        4 -> {
            if (f == Facing.RIGHT)
                Pair(nodes[FullPos.fromLocal(49, 49 - local.y, 1)], Facing.LEFT)
            else // down
                Pair(nodes[FullPos.fromLocal(49, local.x, 5)], Facing.LEFT)
        }
        5 -> when (f) {
            Facing.RIGHT -> Pair(nodes[FullPos.fromLocal(local.y, 49, 4)], Facing.UP)
            Facing.DOWN -> Pair(nodes[FullPos.fromLocal(local.x, 0, 1)], Facing.DOWN)
            Facing.LEFT -> Pair(nodes[FullPos.fromLocal(local.y, 0, 0)], Facing.DOWN)
            else -> throw IllegalStateException()
        }
        else -> throw IllegalStateException("Illegal side: " + u.pos.side)
    }
}

private operator fun <T> List<List<T>>.get(p: FullPos) = get(p.global)

private enum class Facing(x: Int = 0, y: Int = 0) {
    RIGHT(x =  1),
    DOWN (y =  1),
    LEFT (x = -1),
    UP   (y = -1);

    val d = Pos(x, y)
    fun turn(right: Boolean) = VALS[Math.floorMod(ordinal + if (right) 1 else -1, NUM)]

    companion object {
        val VALS = values().toList()
        val NUM = VALS.size
    }
}

private class FullPos private constructor(
    val global: Pos,
    val local: Pos,
    val side: Int,
) {
    companion object {
        fun fromGlobal(p: Pos) = FullPos(p, p.floorMod(50, 50), getSide(p))
        fun fromGlobal(x: Int, y: Int) = fromGlobal(Pos(x, y))
        fun fromLocal(p: Pos, s: Int) = FullPos(getGlobal(p, s), p, s)
        fun fromLocal(x: Int, y: Int, s: Int) = fromLocal(Pos(x, y), s)

        private fun getSide(p: Pos): Int {
            val sx = p.x / 50
            val sy = p.y / 50
            return when (sy) {
                0 -> sx - 1
                1 -> 2
                2 -> sx + 3
                3 -> 5
                else -> throw IllegalStateException()
            }
        }

        private fun getGlobal(local: Pos, side: Int) = local + 50 * when (side) {
            0 -> Pos(1, 0)
            1 -> Pos(2, 0)
            2 -> Pos(1, 1)
            3 -> Pos(0, 2)
            4 -> Pos(1, 2)
            5 -> Pos(0, 3)
            else -> throw IllegalStateException()
        }
    }
}

private class Node(
    val pos: FullPos,
    val repr: Char
) {
    val adj = Facing.VALS.map { Pair(this, it) }.toTypedArray()
}
