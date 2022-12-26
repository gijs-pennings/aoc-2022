import java.util.*

private const val N_ROUNDS = 1000  // use N_ROUNDS=10 for part 1
private val NEIGHBORS_DELTA = listOf(
    Pos( 0,  1),
    Pos( 1,  1),
    Pos( 1,  0),
    Pos( 1, -1),
    Pos( 0, -1),
    Pos(-1, -1),
    Pos(-1,  0),
    Pos(-1,  1)
)

fun main() {
    val lines = readInput(23)
    val w = lines[0].length
    val h = lines.size

    val elves = mutableListOf<Elf>()
    for (x in 0 until w)
        for (y in 0 until h)
            if (lines[h - y - 1][x] == '#')
                elves += Elf(Pos(x + N_ROUNDS, y + N_ROUNDS))

    val nOccupying = Array(w + 2 * N_ROUNDS) { IntArray(h + 2 * N_ROUNDS) }
    nOccupying.fill(elves.map { it.pos })

    val dirs = ArrayDeque(Dir.values().toList())
    repeat(N_ROUNDS) { round ->
        // first half: determine proposed directions
        for (e in elves)
            if (NEIGHBORS_DELTA.any { nOccupying[e.pos + it] > 0 })
                e.propDir = dirs.firstOrNull { d ->
                    d.checkEmptyDelta.all { nOccupying[e.pos + it] == 0 }
                }

        // second half: move each elf with no conflicts
        nOccupying.fill(elves.map { it.propPos })
        var moved = 0
        for (e in elves)
            if (e.propDir != null) {
                if (nOccupying[e.propPos] == 1) { e.pos = e.propPos; moved++ }
                e.propDir = null
            }
        if (N_ROUNDS != 10 && moved == 0) {
            println("round ${round + 1}")  // part 2
            return
        }
        nOccupying.fill(elves.map { it.pos })

        // prepare for next round
        dirs.addLast(dirs.removeFirst())
    }

    val (x0, x1) = nOccupying.getFirstAndLastColumns()
    val (y0, y1) = nOccupying.transposed().getFirstAndLastColumns()
    println("${(x1 - x0) * (y1 - y0) - elves.size} empty tiles")  // part 1
}

private fun Array<IntArray>.fill(ps: Iterable<Pos>) {
    for (col in this) col.fill(0)
    for (p in ps) this[p.x][p.y]++
}

private fun Array<IntArray>.getFirstAndLastColumns() = Pair(
    indexOfFirst { it.any { v -> v > 0 } },
    indexOfLast { it.any { v -> v > 0 } } + 1
)

private fun Array<IntArray>.transposed() = Array(this[0].size) { y ->
    IntArray(this.size) { x -> this[x][y] }
}

private operator fun Array<IntArray>.get(i: Pos) = this[i.x][i.y]



private data class Elf(var pos: Pos) {
    var propDir: Dir? = null
    val propPos get() = if (propDir == null) pos else pos + propDir!!.delta
}

private enum class Dir(
    val delta: Pos,
    val checkEmptyDelta: List<Pos>
) {
    NORTH(y = +1),
    SOUTH(y = -1),
    WEST(x = -1),
    EAST(x = +1);

    constructor(x: Int = 0, y: Int = 0) : this(Pos(x, y), listOf(
        if (x == 0) Pos(-1, y) else Pos(x, -1),
        Pos(x, y),
        if (x == 0) Pos(+1, y) else Pos(x, +1)
    ))
}
