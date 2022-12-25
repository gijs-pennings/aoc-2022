import java.util.*

fun main() {
    val lines = readInput(24)
    val blizzards = mutableListOf<Blizzard>()
    lines.forEachIndexed { y, row ->
        row.forEachIndexed { x, pos ->
            if (pos != '.' && pos != '#')
                blizzards += Blizzard(x, y, when (pos) {
                    '^' -> 1
                    '>' -> 2
                    'v' -> 3
                    '<' -> 4
                    else -> throw IllegalStateException()
                })
        }
    }

    val start = Pos(1, 0)
    val end = Pos(lines[0].length - 2, lines.size - 1)

    var s = State(Map(lines[0].length, lines.size, blizzards), setOf(start))
    while (end   !in s) s = s.next()
/* uncomment for part 2:
    s = s.replacePositions(end)
    while (start !in s) s = s.next()
    s = s.replacePositions(start)
    while (end   !in s) s = s.next()
*/
    println(s.time)
}

private data class Pos(val x: Int, val y: Int)

private data class Blizzard(
    val x: Int,
    val y: Int,
    val d: Int  // direction: 1=north, 2=east, 3=south, 4=west
) {
    fun next(w: Int, h: Int) = when (d) {
        1 -> Blizzard(x, if (y == 1) h-2 else y-1, d)
        3 -> Blizzard(x, if (y == h-2) 1 else y+1, d)
        2 -> Blizzard(if (x == w-2) 1 else x+1, y, d)
        4 -> Blizzard(if (x == 1) w-2 else x-1, y, d)
        else -> throw IllegalStateException()
    }
}

private class Map(
    val w: Int,
    val h: Int,
    private val blizzards: List<Blizzard>
) {
    private val occupiedByBlizzard = BitSet(w * h)

    init {
        for (b in blizzards) occupiedByBlizzard[b.x + b.y * w] = true
    }

    fun isValid(x: Int, y: Int): Boolean {
        if (x == 1 && y == 0 || x == w-2 && y == h-1) return true
        if (x <= 0 || x >= w-1 || y <= 0 || y >= h-1) return false
        return !occupiedByBlizzard[x + y * w]
    }

    fun next() = Map(w, h, blizzards.map { it.next(w, h) })
}

private class State(
    val map: Map,
    val possiblePositions: Set<Pos>,
    val time: Int = 0
) {
    operator fun contains(p: Pos) = p in possiblePositions

    fun next(): State {
        val nextMap = map.next()
        return State(
            nextMap,
            possiblePositions
                .flatMap { (x, y) -> listOf(Pos(x, y), Pos(x, y-1), Pos(x+1, y), Pos(x, y+1), Pos(x-1, y)) }
                .distinct()
                .filter { (x, y) -> nextMap.isValid(x, y) }
                .toSet(),
            time + 1
        )
    }

    fun replacePositions(vararg ps: Pos) = State(map, ps.toSet(), time)
}
