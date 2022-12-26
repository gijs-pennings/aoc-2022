import java.io.File

const val INF = 1_000_000_000

data class Pos(
    val x: Int,
    val y: Int
) {
    operator fun plus(p: Pos) = Pos(x + p.x, y + p.y)
}

fun readInput(day: Int) = File("input/day${day.toString().padStart(2, '0')}.txt").readLines()
