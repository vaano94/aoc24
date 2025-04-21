package org.example.Day11

import java.io.File
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path

val currentYear = BigInteger.valueOf(2024L)

fun main() {

//    val input = readInput()
    val input = "125 17"

//    val blinkingStonesArray = iterateStones(input, 25)
//    println(blinkingStonesArray.size)

    // todo would make sense to split it into equal chunks or smth, and use arrays
    // todo instead of joining lists
    val blinkingStonesArrayPartTwo = iterateStonesPart2("125", 75)
    // todo wrong 1036288 0 72 20 24 4048 1 4048 8096 28 67 60 32 13

//    val blinkingStonesArrayPartTwo = iterateStonesPart2("512 72 2024 2 0 2 4 2867 6032 9", 4)
//    println(blinkingStonesArrayPartTwo)

//    val processSingleStoneElement = processSingleStoneElement("125 17", 1)
//    println("Final")
//    println(processSingleStoneElement)

}

fun iterateStonesPart2(input: String, times: Int): BigInteger {
    // todo iterate element one by one instead of iterating list as a whole
    val singleStones = input.split(" ")
    val sum = singleStones.parallelStream().map { stone ->
        processSingleStoneElement(stone, times)
    }
        .peek(::print)
        .map { it.trim().split(" ").size }
//        .peek(::println)
        .toList()
        .sum()
//        .reduce((0))
//    return sum.get()
    return sum.toBigInteger()
}

fun processSingleStoneElement(stone: String, times: Int): String {
    // 10
    // 1 0
    // 2024 1
    // 20 24 2024
    // 2 0 2 4 20 24

    var acc: String = ""
    if (times == 0) {
//        println(stone)
        return BigInteger.valueOf(stone.toLong()).toString() + " "
    }

    repeat(times) {
//        println("Times ${times}")
        if (stone.toBigInteger() == BigInteger.ZERO) {
            return processSingleStoneElement("1", times - 1)
        } else if (BigInteger(stone).toString().length % 2 == 0)
            return processSingleStoneElement(
                stone.substring(0, stone.length / 2),
                times - 1
            ) + processSingleStoneElement(stone.substring(stone.length / 2, stone.length), times - 1)
        else {
            return processSingleStoneElement(stone.toBigInteger().times(currentYear).toString(), times - 1)
        }
    }
    return stone
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

//        println(resultingArray)

    }
    return resultingArray
}


fun readInput(): String {
    val path: Path = File(object {}.javaClass.getResource("/Day11/input.txt")?.file ?: "").toPath()
    val lines = Files.readString(path).trimIndent()
    return lines
}
