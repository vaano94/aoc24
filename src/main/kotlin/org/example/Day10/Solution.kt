package org.example.Day10

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

fun main() {

    val input = readInput()

//    val input = """
//        89010123
//        78121874
//        87430965
//        96549874
//        45678903
//        32019012
//        01329801
//        10456732
//    """.trimIndent()

    val inputAsArray = inputStringToArray(input)
    println(inputAsArray.printArray())

//    val trailHeadScore = findPaths(inputAsArray)
//    println("Trailhead score for the map: $trailHeadScore")
//    println("Scores of all trainHeads: ${trailHeadScore.values.sum()}")

    val trailHeadScore2 = findPathsPart2(inputAsArray)
    println("Trailhead score for the map: $trailHeadScore2")
    println("Scores of all trainHeads: ${trailHeadScore2.values.sum()}")

}

fun findPaths(routeMap: Array<Array<Int?>>): MutableMap<MapPoint, Int> {
    val trailHeadToScore = mutableMapOf<MapPoint, Int>()
    for (i in routeMap.indices) {
        for (j in routeMap[i].indices) {
            val point = routeMap[i][j]
            if (point == 0) {
                // start calculating the path
                val startingMapPoint = MapPoint(i, j)
                val foundPath = findPath(startingMapPoint, routeMap)
                if (foundPath != 0) {
                    trailHeadToScore[startingMapPoint] = foundPath
                }
            }
        }
    }
    return trailHeadToScore
}

fun findPathsPart2(routeMap: Array<Array<Int?>>): MutableMap<MapPoint, Int> {
    val trailHeadToScore = mutableMapOf<MapPoint, Int>()
    for (i in routeMap.indices) {
        for (j in routeMap[i].indices) {
            val point = routeMap[i][j]
            if (point == 0) {
                // start calculating the path
                val startingMapPoint = MapPoint(i, j)
                val foundPath = findPathPart2(startingMapPoint, routeMap)
                if (foundPath != 0) {
                    trailHeadToScore[startingMapPoint] = foundPath
                }
            }
        }
    }
    return trailHeadToScore
}

fun findPathPart2(point: MapPoint, routeMap: Array<Array<Int?>>): Int {
    var neighboursList = findNeighbours(point, routeMap).toList()

    while (neighboursList.isNotEmpty()) {
        val currentElement = routeMap[neighboursList.first().x][neighboursList.first().y]
        if (currentElement == 9) {
            // search completed
            return neighboursList.size
        }

        neighboursList = neighboursList.map { p -> findNeighbours(p, routeMap) }.toList().flatten().toList()
    }
    return 0
}

fun findPath(point: MapPoint, routeMap: Array<Array<Int?>>): Int {
    var neighboursList = findNeighbours(point, routeMap).toSet()

    while (neighboursList.isNotEmpty()) {
        val currentElement = routeMap[neighboursList.first().x][neighboursList.first().y]
        if (currentElement == 9) {
            // search completed
            return neighboursList.size
        }

        neighboursList = neighboursList.map { p -> findNeighbours(p, routeMap) }.toSet().flatten().toSet()
    }
    return 0
}

fun findNeighbours(point: MapPoint, routeMap: Array<Array<Int?>>): List<MapPoint> {
    return Direction.entries.mapNotNull { direction ->
        val directionX = direction.x + point.x
        val directionY = direction.y + point.y
        if (directionX < 0 || directionY < 0 || directionX >= routeMap.size || directionY >= routeMap.first().size) {
            // out of bounds
            null
        } else {
            val startingPoint = routeMap[point.x][point.y]
            if (startingPoint != null && routeMap[directionX][directionY] != null) {
                if ((routeMap[directionX][directionY]?.minus(startingPoint) ?: 0) == 1) {
                    MapPoint(directionX, directionY)
                } else null
            } else null
        }
    }
}

data class MapPoint(var x: Int, var y: Int)

enum class Direction(val x: Int, val y: Int) {
    UP(-1, 0),
    LEFT(0, -1),
    DOWN(1, 0),
    RIGHT(0, 1),
}

fun inputStringToArray(input: String): Array<Array<Int?>> {
    return input.lines().map { line -> line.toCharArray().map { if (it == '.') null else it.digitToInt() }.toTypedArray() }.toTypedArray()
}

fun Array<Array<Int?>>.printArray() {
    this.forEach { a -> print(a.map { it ?: "." }); println() }
    println("------------")
}

fun readInput(): String {
    val path: Path = File(object{}.javaClass.getResource("/Day10/input.txt")?.file ?: "").toPath()
    val lines = Files.readString(path).trimIndent()
    return lines
}
