import kotlin.math.max
import kotlin.math.min

private var dist: Array<IntArray> = emptyArray()

fun main() {
    val lines = readInput(16)
    val n = lines.size
    val nameToIndex = lines.withIndex().associate { it.value.substring(6, 8) to it.index }
    val nodes = lines.mapIndexed { i, line -> Node(i, line.drop(23).takeWhile(Char::isDigit).toInt()) }
    lines.forEachIndexed { i, line ->
        nodes[i].adj.addAll(
            line.substringAfter("valve").substringAfter(' ').split(", ").map { nodes[nameToIndex[it]!!] }
        )
    }

    dist = Array(n) { IntArray(n) { INF } }
    for (i in nodes.indices) dist[i][i] = 0
    for (u in nodes)
        for (v in u.adj)
            dist[u.i][v.i] = 1
    for (i in nodes.indices)
        for (j in nodes.indices)
            for (k in nodes.indices)
                dist[j][k] = min(dist[j][k], dist[j][i] + dist[i][k])

    val first = nodes[nameToIndex["AA"]!!]
    println(backtrack(Mob(first), Mob(first), nodes.filter { it.flow > 0 }.toSet()))
}

private fun backtrack(
    m1: Mob,
    m2: Mob,
    nodes: Set<Node>,  // list of unvisited next nodes
): Int {
    var best = m1.totalFlow + m2.totalFlow

    if (m1.time > 0 && m1.time >= m2.time) {
        // option 0 : wait at current node
        best = max(best, backtrack(Mob(m1.u, 0, m1.fpm, m1.totalFlow + m1.time * m1.fpm), m2, nodes))

        // options 1..nodes.size : visit next node
        if (m1.time > 1)
            for (v in nodes) {
                val t = dist[m1.u.i][v.i] + 1
                if (t >= m1.time) continue
                best = max(best, backtrack(
                    Mob(v, m1.time - t, m1.fpm + v.flow, m1.totalFlow + t * m1.fpm),
                    m2,
                    nodes - v
                ))
            }
    }

    if (m2.time > 0 && m2.time >= m1.time) {
        // option 0 : wait at current node
        best = max(best, backtrack(m1, Mob(m2.u, 0, m2.fpm, m2.totalFlow + m2.time * m2.fpm), nodes))

        // options 1..nodes.size : visit next node
        if (m2.time > 1)
            for (v in nodes) {
                val t = dist[m2.u.i][v.i] + 1
                if (t >= m2.time) continue
                best = max(best, backtrack(
                    m1,
                    Mob(v, m2.time - t, m2.fpm + v.flow, m2.totalFlow + t * m2.fpm),
                    nodes - v
                ))
            }
    }

    return best
}

private data class Node(
    val i: Int,
    val flow: Int
) {
    val adj = mutableListOf<Node>()
}

private data class Mob(
    val u: Node,              // current node
    val time: Int      = 26,  // leftover time
    val fpm: Int       =  0,  // flow per minute
    val totalFlow: Int =  0   // total flow so far
)
