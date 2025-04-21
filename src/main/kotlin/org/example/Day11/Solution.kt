package org.example.Day11

import java.io.File
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path

val currentYear = BigInteger.valueOf(2024L)
var cache = mutableMapOf<Pair<BigInteger, Int>, BigInteger>()

fun main() {

    val input = readInput()

    val blinkingStonesArray = iterateStones(input, 25)
    println(blinkingStonesArray.size)

    val iterateStonesPart2 = iterateStonesPart2(input, 75)
    println(iterateStonesPart2)

}

fun iterateStonesPart2(input: String, times: Int): BigInteger {
    // todo iterate element one by one instead of iterating list as a whole
    val singleStones = input.split(" ")
    val sum = singleStones
        .map { stone ->
            countContribution(stone.toBigInteger(), times)
    }.reduce(BigInteger::plus)
    return sum
}

fun countContribution(stone: BigInteger, times: Int): BigInteger {
    if (times == 0) {
        return BigInteger.ONE
    }

    val maybeCached = cache[stone to times]
    if (maybeCached != null) {
//        println("Found cache value: $maybeCached")
        return maybeCached
    }

    val result: BigInteger = when {
        stone == BigInteger.ZERO -> countContribution(BigInteger.ONE, times -1)
        stone.toString().length % 2 == 0 -> {
            val s = stone.toString()
            val mid = s.length / 2
            countContribution(s.substring(0, mid).toBigInteger(), times - 1) + countContribution(s.substring(mid).toBigInteger(), times - 1)
        }
        else -> countContribution(stone.times(currentYear), times - 1)
    }
    cache.putIfAbsent(stone to times, result)
    return result
}


fun iterateStones(input: String, times: Int): List<String> {
    var resultingArray: List<String> = input.split(" ")
    repeat(times) {
        resultingArray = resultingArray
            .map { stone ->
                if (stone.toBigInteger() == BigInteger.ZERO) listOf("1")
                else if (stone.length % 2 == 0) listOf(
                    stone.substring(0, stone.length / 2),
                    (stone.substring(stone.length / 2, stone.length)).toBigInteger().toString()
                )
                else listOf((stone.toBigInteger().times(BigInteger.valueOf(2024L))).toString())
            }
            .flatten()
    }
    return resultingArray
}


fun readInput(): String {
    val path: Path = File(object {}.javaClass.getResource("/Day11/input.txt")?.file ?: "").toPath()
    val lines = Files.readString(path).trimIndent()
    return lines
}
