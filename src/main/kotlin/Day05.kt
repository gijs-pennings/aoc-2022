fun main() {
    val n = 14
    println(
        readInput(5)[0]
            .windowed(n)
            .indexOfFirst { it.toCharArray().distinct().size == n } + n
    )
}
