package org.examplei.Day2

import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.abs

fun main() {
    val reportLines: List<List<Int>> = readInput()

    val validReports = reportLines.map { ints ->
        isReportValid(ints)
    }.filter { it -> it }.count()
    println(validReports)

}

fun isReportValid(report: List<Int>): Boolean {
    var previousElement: Int = 0
    var hasSignConsistency: ConsistencySign? = null
    report.forEachIndexed { index, e ->
        if (index == 0) {
            previousElement = e
            return@forEachIndexed
        } else {
            if (index == 1) {
                // 8 5 3
                // 3 4 5
                if (previousElement - e > 0) {
                    hasSignConsistency = ConsistencySign.LT
                    if (abs(previousElement - e) > 3) return false
                    previousElement = e
                    return@forEachIndexed
                } else if (previousElement - e < 0) {
                    hasSignConsistency = ConsistencySign.GT
                    if (abs(previousElement - e) > 3) return false
                    previousElement = e
                    return@forEachIndexed
                } else if (previousElement - e == 0) {
                    return false
                }
            }

            if (hasSignConsistency != null) {
                val difference = previousElement - e

                if (difference > 0 && hasSignConsistency != ConsistencySign.LT) return false
                if (difference < 0 && hasSignConsistency != ConsistencySign.GT) return false

                val distance = Math.abs(difference)
                if (distance == 0 || distance > 3) return false
            }
            previousElement = e
        }

    }
    return true
}

enum class ConsistencySign {
    GT,
    LT
}


fun readInput() : List<List<Int>> {
    val lines = Files.readAllLines(Paths.get("src/main/kotlin/org/example/Day2/input.txt"))

    return lines.map { s ->
        val split = s.split(" ")
        split.map{e -> e.toInt()}
    }
}
