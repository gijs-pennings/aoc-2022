fun main() {
    val guide = readInput(2).map { listOf(it[0] - 'A', it[2] - 'X') }
    println(guide.sumOf { it[1]+1 + it.outcome() })
    println(guide.sumOf { it.myMove() + 3*it[1] })
}

// this = [opponent's move, my move]
private fun List<Int>.outcome() = (this[1] - this[0] + 4) % 3 * 3

// this = [opponent's move, strategy]
private fun List<Int>.myMove() = (sum() + 2) % 3 + 1
