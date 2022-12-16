import java.io.File

const val INF = 1_000_000_000

fun readInput(day: Int) = File("input/day${day.toString().padStart(2, '0')}.txt").readLines()
