fun main() {
    val elves = mutableListOf(mutableListOf<Int>())
    for (line in readInput(1))
        if (line.isEmpty())
            elves.add(mutableListOf())
        else
            elves.last().add(line.toInt())
    val sums = elves.map { it.sum() }.sortedDescending()
    println(sums.first())
    println(sums.take(3).sum())
}
