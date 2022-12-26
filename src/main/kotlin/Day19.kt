import kotlin.math.max

private const val MAX_TIME = 24  // set MAX_TIME=32 for part 2

fun main() {
    val recipes = readInput(19).map { line ->
        val nums = Regex("\\d+").findAll(line).map { it.value.toInt() }.toList()
        mapOf(
            RobotType.ORE      to Resources(ore = nums[1]),
            RobotType.CLAY     to Resources(ore = nums[2]),
            RobotType.OBSIDIAN to Resources(ore = nums[3], clay = nums[4]),
            RobotType.GEODE    to Resources(ore = nums[5], obsidian = nums[6])
        )
    }
    // for part 2, take only the first three recipes and simply multiply the results of `findMaxGeodes`
    println(recipes.mapIndexed { i, costs ->
        println(i+1)
        globalBest = 0
        (i+1) * findMaxGeodes(costs)
    }.sum())
}

private var globalBest = 0  // beware! global variable
private fun findMaxGeodes(costs: Map<RobotType, Resources>, state: State = State()): Int {
    var geodes = 0
    for ((type, cost) in costs) {
        val next = state.build(type, cost) ?: continue
        if (next.geodeUpperbound <= globalBest) continue  // prune
        geodes = max(
            geodes,
            if (next.isFinal) next.resources.geode else findMaxGeodes(costs, next)
        )
        globalBest = max(globalBest, geodes)
    }
    return geodes
}

private enum class RobotType(
    val indicator: Resources
) {
    ORE     (Resources(ore      = 1)),
    CLAY    (Resources(clay     = 1)),
    OBSIDIAN(Resources(obsidian = 1)),
    GEODE   (Resources(geode    = 1))
}

private data class Resources(
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geode: Int = 0
) : AbstractList<Int>() {
    operator fun minus(r: Resources) = plus(-1 * r)
    operator fun plus(r: Resources) = Resources(ore + r.ore, clay + r.clay, obsidian + r.obsidian, geode + r.geode)

    override val size = 4
    override fun get(index: Int): Int = when (index) {
        0 -> ore
        1 -> clay
        2 -> obsidian
        3 -> geode
        else -> throw IndexOutOfBoundsException()
    }
}

private operator fun Int.times(r: Resources) = Resources(this * r.ore, this * r.clay, this * r.obsidian, this * r.geode)

private class State(
    val time: Int = 0,
    val resources: Resources = Resources(),
    val robots: Resources = Resources(ore = 1)
) {
    val isFinal get() = time == MAX_TIME
    val geodeUpperbound: Int get() {
        val t = MAX_TIME - time
        return resources.geode + t * robots.geode + (t-1) * t / 2  // last term: no. geodes if we could build new robot each minute
    }

    init {
        if (time > MAX_TIME) println("too late")
    }

    fun build(type: RobotType, cost: Resources): State? {
        var t = 0
        cost.forEachIndexed { i, c ->
            if (c > 0)
                if (robots[i] > 0)
                    t = max(t, (c - resources[i] + robots[i] - 1) / robots[i])  // time to gather resources (ceil)
                else
                    return null  // impossible
        }
        t++  // time to build
        return if (time + t < MAX_TIME)
            State(time + t, resources + t * robots - cost, robots + type.indicator)
        else
            State(MAX_TIME, resources + (MAX_TIME - time) * robots, robots)
    }
}
