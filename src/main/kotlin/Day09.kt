import kotlin.math.abs
import kotlin.math.max

fun main() {
    val n = 10  // use n=2 for part 1
    val knots = List(n) { MutablePos() }
    val allTailPos = mutableSetOf(knots.last().copy())
    for (line in readInput(9)) {
        repeat(line.substring(line.indexOf(' ') + 1).toInt()) {
            knots.first().move(line[0])
            for (i in 1 until knots.size) {
                val H = knots[i-1]
                val T = knots[i]
                if (T.dist(H) > 1) {
                    if (H.x != T.x) T.x += if (H.x > T.x) 1 else -1
                    if (H.y != T.y) T.y += if (H.y > T.y) 1 else -1
                }
            }
            allTailPos.add(knots.last().copy())
        }
    }
    println(allTailPos.size)
}

private data class MutablePos(
    var x: Int = 0,
    var y: Int = 0
) {
    fun dist(p: MutablePos) = max(abs(x - p.x), abs(y - p.y))
    fun move(dir: Char) {
        when (dir) {
            'R' -> x++
            'L' -> x--
            'U' -> y++
            'D' -> y--
        }
    }
}
