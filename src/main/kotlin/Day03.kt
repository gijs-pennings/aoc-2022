fun main() {
    val lines = readInput(3)
    println(part1(lines.map { it.chunked(it.length / 2) }))
    println(part1(lines.chunked(3).map { listOf(it[0].intersect(it[1]), it[2]) }))
}

private fun part1(rucksacks: List<List<String>>) = rucksacks
    .map { it[0].intersect(it[1]).first() }
    .sumOf { if (it.isLowerCase()) it - 'a' + 1 else it - 'A' + 27 }

private fun String.intersect(s: String) = toList().intersect(s.toSet()).joinToString("")
