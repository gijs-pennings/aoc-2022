import java.io.File

fun readInput(day: Int) = File("input/day${day.toString().padStart(2, '0')}.txt").readLines()
