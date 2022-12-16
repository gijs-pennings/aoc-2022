fun main() {
    val lines = readInput(12)
    val w = lines[0].length
    val h = lines.size
    val grid = Array(w) { x -> Array(h) { y -> Node(x, y, lines[y][x]) } }
    val nodes = grid.flatten()
    for (n in nodes) {
        if (n.x-1 >= 0 && n.h <= grid[n.x-1][n.y].h + 1) n.adjRev.add(grid[n.x-1][n.y])
        if (n.x+1 <  w && n.h <= grid[n.x+1][n.y].h + 1) n.adjRev.add(grid[n.x+1][n.y])
        if (n.y-1 >= 0 && n.h <= grid[n.x][n.y-1].h + 1) n.adjRev.add(grid[n.x][n.y-1])
        if (n.y+1 <  h && n.h <= grid[n.x][n.y+1].h + 1) n.adjRev.add(grid[n.x][n.y+1])
    }

    val first = nodes.first { it.isEnd }
    first.dist = 0
    val queue = ArrayDeque<Node>()
    queue.add(first)
    while (queue.isNotEmpty()) {
        val n = queue.removeFirst()
        for (m in n.adjRev.filter { !it.visited }) {
            m.dist = n.dist + 1
            queue.addLast(m)
        }
    }

    println(nodes.filter { it.isStart }.minOf { it.dist })
}

private data class Node(val x: Int, val y: Int, val c: Char) {
    val h = when (c) {
        'S' -> 0
        'E' -> 25
        else -> c - 'a'
    }
    val isStart = h == 0  // use `c == 'S'` for part 1
    val isEnd = c == 'E'

    val adjRev = mutableListOf<Node>()
    var dist = INF
    val visited get() = dist < INF
}
