import java.io.File

const val INF = 1_000_000_000

data class Pos(
    val x: Int,
    val y: Int
) {
    fun floorMod(w: Int, h: Int) = Pos(Math.floorMod(x, w), Math.floorMod(y, h))
    fun isWithin(w: Int, h: Int) = x in 0 until w && y in 0 until h

    operator fun plus(p: Pos) = Pos(x + p.x, y + p.y)
}

operator fun Int.times(p: Pos) = Pos(this * p.x, this * p.y)

operator fun <T> List<List<T>>.get(p: Pos) = this[p.x][p.y]
operator fun <T> List<MutableList<T>>.set(p: Pos, x: T) { this[p.x][p.y] = x }

fun readInput(day: Int) = File("input/day${day.toString().padStart(2, '0')}.txt").readLines()
