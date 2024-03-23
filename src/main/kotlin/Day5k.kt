import kotlin.sequences.sequence

object Day5k {
    @JvmStatic
    fun main(args: Array<String>) {
        println(process(example))
        println(process(input()))
    }

    val number = """(\d+)""".toRegex()

    fun makeSeq(input: Iterator<String>): List<Long> {
        val list = number.findAll(input.next()).map { it.value.toLong() }.toList()
        input.next() // skip blank line
        return list
    }

    fun makeMap(input: Iterator<String>): ((Long) -> Long)? =
        if (input.hasNext()) {
            input.next() // skip section header
            val ranges = input.asSequence()
                .takeWhile { it.trim().isNotEmpty() }
                .map { line ->
                    val numbers = number.findAll(line).map { it.value.toLong() }
                    Triple(numbers.elementAt(0), numbers.elementAt(1), numbers.elementAt(2))
                }
                .toList()
            ;
            { i: Long ->
                ranges
                    .find { (_, s, l) -> (s until s + l).contains(i) }
                    .let { if (it != null) it.first + i - it.second else i }
            }
        } else null

    fun process(input: Iterator<String>): Pair<Long, Long> {
        val seeds = makeSeq(input)
        val allMaps = generateSequence { makeMap(input) }
        val seedToLocation = allMaps.toList().reversed().reduce { f, g -> { f(g(it)) } }
        val part1 = seeds.map(seedToLocation).minOrNull() ?: 0
        val part2 = seeds.windowed(2, 2).map { p ->
            (p.first() until p.first() + p.last()).map(seedToLocation).minOrNull() ?: 0
        }.minOrNull() ?: 0
        return Pair(part1, part2)
    }

    fun input() = java.io.File("data/day5input.txt").readLines().iterator()

    val example: Iterator<String> = """
        seeds: 79 14 55 13
        
        seed-to-soil map:
        50 98 2
        52 50 48
        
        soil-to-fertilizer map:
        0 15 37
        37 52 2
        39 0 15
        
        fertilizer-to-water map:
        49 53 8
        0 11 42
        42 0 7
        57 7 4
        
        water-to-light map:
        88 18 7
        18 25 70
        
        light-to-temperature map:
        45 77 23
        81 45 19
        68 64 13
        
        temperature-to-humidity map:
        0 69 1
        1 0 69
        
        humidity-to-location map:
        60 56 37
        56 93 4
        """.trimIndent().split("\n").iterator()
}
