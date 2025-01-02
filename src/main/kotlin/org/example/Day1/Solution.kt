package org.example.Day1

import java.nio.file.Files
import java.nio.file.Paths
import java.util.HashMap
import kotlin.math.abs

fun main() {
    val (offices, locations) = readInput()
//    officeLocationSimilarities(offices, locations)

    var locationsCounted = HashMap<Int, Int>()


    var similarityScore = 0
    locations.forEach { key ->
        val intKey = key.toInt()
        val maybeKey = locationsCounted[intKey]
        if (maybeKey != null) {
            locationsCounted[key] = maybeKey + 1
        } else {
            locationsCounted[key] = 1
        }
    }

    offices.forEach { officeValue ->
        if (locationsCounted.containsKey(officeValue)) {
            similarityScore += officeValue * locationsCounted[officeValue]!!
        }
    }

    println(similarityScore)


}

private fun officeLocationSimilarities(offices: IntArray, locations: IntArray) {
    offices.sort()
    locations.sort()

    var sum = 0

    offices.forEachIndexed { index, element ->
        sum += abs(element - locations[index]);
    }

    print(sum)
}

fun readInput() : Pair<IntArray, IntArray> {

    val lines = Files.readAllLines(Paths.get("src/main/kotlin/org/example/Day1/input.txt"))
    val offices = IntArray(lines.size)
    val locations = IntArray(lines.size)
    lines.forEachIndexed { index, s ->
        val split = s.split("   ")
        offices[index] = split[0].toInt()
        locations[index] = split[1].toInt()
    }
    return Pair(offices, locations)
}
