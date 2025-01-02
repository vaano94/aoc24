package org.examplei.Day3

import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val input = readInput()
    val mulGroups: List<MultiplicationParams> = getMulGroups(input)
    println(mulGroups)
    val result = mulGroups.fold(0) { acc, param -> acc + (param.x * param.y) }
    println(result)

    getMulGroups2(input)
}

val regex: Regex = Regex("mul\\(([0-9]{1,3}),([0-9]{1,3})\\)")

fun getMulGroups2(input: String) {
    val rangeParams = Regex("mul\\(([0-9]{1,3}),([0-9]{1,3})\\)")
        .findAll(input)
        .map { Pair(it.range, MultiplicationParams(it.groupValues.get(1).toInt(), it.groupValues.get(2).toInt())) }
        .toList()

    var subStringRangesToParams = mutableListOf<Pair<String, MultiplicationParams>>()

    for (i in 0.. rangeParams.size - 1) {
        if (i == 0) {
            subStringRangesToParams.add(Pair(input.substring(IntRange(0, rangeParams[i].first.first - 1)), rangeParams[i].second))
            continue
        }

        val inBetweenString = input.substring(IntRange(rangeParams[i-1].first.last + 1, rangeParams[i].first.first - 1))

        subStringRangesToParams.add(Pair(inBetweenString, rangeParams[i].second))

    }

    var enabledCalculation = true
    var sum = 0
    subStringRangesToParams.forEach { (str, params) ->
        val dosAndDontsList = Regex("(don't|do)").findAll(str).toList()
        if (dosAndDontsList.isNotEmpty()) {
            val lastVerb: String = dosAndDontsList.last().value
            if (lastVerb == "don't") {
                enabledCalculation = false
            } else if (lastVerb == "do") {
                enabledCalculation = true
            }
        }
        if (enabledCalculation) {
            sum += params.x * params.y
        }

    }

    println(sum)

}

fun getMulGroups(input: String): List<MultiplicationParams> {
    if (regex.containsMatchIn(input)) {
        return regex.findAll(input)
            .map { MultiplicationParams(it.groupValues.get(1).toInt(), it.groupValues.get(2).toInt()) }
            .toList()
    } else return emptyList()
}

data class MultiplicationParams(val x:Int, val y: Int)

fun readInput() : String {

    val lines = Files.readString(Paths.get("src/main/kotlin/org/example/Day3/input.txt"))
    return lines
}
