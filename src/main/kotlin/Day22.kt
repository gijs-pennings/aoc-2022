fun main() {
    val lines = readInput(22)
    val map = lines.dropLast(2)
    val path = lines.last()

    val w = map.maxOf { it.length }
    val h = map.size
    val nodes = List(w) { x ->
        List(h) { y ->
            Node(Pos(x, y), map[y].getOrNull(x) ?: ' ')
        }
    }
    for (u in nodes.flatten())
        for (f in Facing.VALS) {
            var p = u.pos
            do {
                p = (p + f.d).floorMod(w, h)
            } while (nodes[p].repr == ' ')
            val v = nodes[p]
            u.adj[f.ordinal] = if (v.repr == '.') v else u  // self-loop if neighbor is wall
        }

    var i = 0
    var f = Facing.RIGHT
    var u = nodes.map { it[0] }.first { it.repr == '.' }
    while (i < path.length)
        if (path[i].isDigit()) {
            val s = path.drop(i).takeWhile { it.isDigit() }
            i += s.length
            repeat(s.toInt()) {
                u = u.adj[f.ordinal]
            }
        } else {
            f = f.turn(path[i] == 'R')
            i++
        }

    println(1000 * (u.pos.y + 1) + 4 * (u.pos.x + 1) + f.ordinal)
}

private fun Pos.floorMod(w: Int, h: Int) = Pos(Math.floorMod(x, w), Math.floorMod(y, h))

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

private class Node(
    val pos: Pos,
    val repr: Char
) {
    val adj = Array(Facing.NUM) { this }
}
