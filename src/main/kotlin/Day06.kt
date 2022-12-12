fun main() {
    val n = 14  // use n=4 for part 1
    println(
        readInput(6)[0]
            .windowed(n)
            .indexOfFirst { it.toCharArray().distinct().size == n } + n
    )
}
