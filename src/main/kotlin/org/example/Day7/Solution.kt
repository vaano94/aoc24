package org.examplei.Day7

import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.pow

fun main() {

    val input = readInput()

    val preparedInput: List<Pair<Long, MutableList<Long>>> = input.lines().map {
        val split = it.split(":")
        val ints = split[1].trim().split(" ").map { it.toLong() }.toMutableList()
        split[0].toLong() to ints
    }.toList()

    // part 1
//    partOne(preparedInput)
    // part 2
    partTwo(preparedInput)

//    println(computeExpression(mutableListOf(17, 8, 14, 15), mutableListOf(0, 1, 0))) // 17 * 8 + 14 * 15
//    println(computeExpression(mutableListOf(17, 8, 14), mutableListOf(2, 1))) // 17 || 8 + 14.
//    println(computeExpression(mutableListOf(6, 8, 6, 15), mutableListOf(0, 2, 0))) // 7290: 6 8 6 15 can be made true using 6 * 8 || 6 * 15.
}

private fun partOne(preparedInput: List<Pair<Long, MutableList<Long>>>) {
    val sumOfCombinations = preparedInput.sumOf {
        val matrix = generateSignCombinations(2.0, it.second.size - 1)
        val combinationCount = findCombination(it.first, matrix, it.second)
        if (combinationCount > 0) {
            it.first
        } else {
            0
        }
    }

    println(sumOfCombinations)
}

private fun partTwo(preparedInput: List<Pair<Long, MutableList<Long>>>) {
    val sumOfCombinations = preparedInput.sumOf {
        val matrix = generateSignCombinations(3.0, it.second.size - 1)
        val combinationCount = findCombinationPart2(it.first, matrix, it.second)
        if (combinationCount > 0) {
            it.first
        } else {
            0
        }
    }

    println(sumOfCombinations)
}

fun computeExpression(numbers: MutableList<Long>, operators: MutableList<Int>): Long {

    val firstNum = numbers.removeFirst()
    var combineList = mutableListOf<Any>()
    combineList.add(firstNum)
    while (numbers.isNotEmpty() || operators.isNotEmpty()) {
        val nextOperator = operators.removeFirst()
        combineList.add(operatorToString(nextOperator))
        combineList.add(numbers.removeFirst())
    }
    // high precedence operators
    while (combineList.size != 1) {
        // reduce computation
        val operatorIndex = findHighestPrecedenceOperatorIndex(combineList)
        val one = combineList[operatorIndex - 1]
        val operator = combineList[operatorIndex]
        val two = combineList[operatorIndex + 1]

        val value = compute(one.toString().toLong(), two.toString().toLong(), operator.toString())
        combineList.removeAt(operatorIndex - 1)
        combineList.removeAt(operatorIndex - 1)
        combineList.removeAt(operatorIndex - 1)

        combineList.add(operatorIndex - 1, value)

    }
    return combineList.first().toString().toLong()
}

fun findHighestPrecedenceOperatorIndex(list: MutableList<Any>): Int {
    val operatorIndexes = mutableListOf<Pair<Int, Operator>>()
    list.forEachIndexed { index, operator ->
        val maybeOperator = Operator.fromString(str = operator.toString())
        if (maybeOperator != null) {
            operatorIndexes.add(index to maybeOperator)
        }
    }
    return operatorIndexes.first().first
}

enum class Operator(val s: String, val p: Int) {
    MULTIPLY("*", 0),
    PLUS("+", 1),
    CONCAT("||", 2);
    companion object {
        fun fromString(str: String) = entries.find { it.s == str }
    }
}

fun compute(a: Long, b: Long, operator: String): Long {
    val result = when (operator) {
        "*" -> a * b
        "+" -> a + b
        "||" -> (a.toString() + b.toString()).toLong()
        else -> throw IllegalArgumentException("Invalid operator: $operator")
    }
    return result
}

fun operatorToString(operator: Int): String {
    return when(operator) {
        0 -> "*"
        1 -> "+"
        2 -> "||"
        else -> throw Exception("dont know operator")
    }
}

fun findCombinationPart2(sum: Long, matrix: Array<IntArray>, numbers: MutableList<Long>): Int {
    var positives = 0
    matrix.forEach { row: IntArray ->
        val computeExpression = computeExpression(numbers.toMutableList(), row.toMutableList())
        if (computeExpression == sum) {
            positives ++
        }
    }
    return positives
}

fun findCombination(sum: Long, matrix: Array<IntArray>, numbers: MutableList<Long>): Int {
    var positives = 0

    matrix.forEach { row ->
        val newNumbers = numbers.toMutableList()
        var sss = newNumbers.removeFirst()
        val rows = row.toMutableList()
        while (newNumbers.isNotEmpty()) {
            val sign = rows.removeFirst()
            if (sign == 0) {
                sss += newNumbers.removeFirst()
            }
            if (sign == 1) {
                sss *= newNumbers.removeFirst()
            }
        }
        if (sum == sss) positives++
    }
    return positives
}

val input = """
    190: 10 19
    3267: 81 40 27
    83: 17 5
    156: 15 6
    7290: 6 8 6 15
    161011: 16 10 13
    192: 17 8 14
    21037: 9 7 18 13
    292: 11 6 16 20
""".trimIndent()


fun generateSignCombinations(alphabetStrength: Double, inputArraySize: Int): Array<IntArray> {
    val rows = alphabetStrength.pow(inputArraySize).toInt()
    val matrix = Array(rows) { IntArray(inputArraySize) }

    for (i in 0 until rows) {
        var temp = i
        for (j in inputArraySize - 1 downTo 0) {
            matrix[i][j] = temp % alphabetStrength.toInt()
            temp /= alphabetStrength.toInt()
        }
    }
    return matrix
}


fun printMatrix(matrix: Array<IntArray>) {
    for (row in matrix) {
        println(row.contentToString())
    }
}

fun readInput(): String {
    val lines = Files.readString(Paths.get(object{}.javaClass.getResource("/Day7/input.txt").path)).trimIndent()
    return lines
}
