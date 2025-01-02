package org.examplei.Day6

import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val inputArea = readInput()
    val map = MovementMap(inputArea)
    map.moveGuard()
    map.printMap()
    map.countPath()

    val countingTimeParadoxes = countingTimeParadoxes(inputArea)
    println("Found loops: $countingTimeParadoxes")

}

fun countingTimeParadoxes(inputArea: String): Int {
    var loopsFound = 0
    for (c in 0..inputArea.length - 1) {
        val charArray = inputArea.toCharArray()

        val currentElement = charArray[c]
//        println("Element $currentElement")
        if (currentElement == '\n' || currentElement == '#') {
            continue
        }
        val findByChar = GuardDirection.findByChar(currentElement)
        if (findByChar == null) {
             charArray[c] = 'O'
        }
        if (findByChar != null) {
            continue
        }
        val map = MovementMap(charArray.concatToString())
        map.moveGuard()
        if (map.isLooped) {
            println("Loop found!")
            loopsFound += 1
        }
//        map.printMap()
    }
    return loopsFound

}

class MovementMap(i: String) {

    val paths: Array<Array<Char>> = i.lines().map { l -> l.toCharArray().toTypedArray() }.toTypedArray()

    val guardLocation: GuardLocation

    val traversedPath: MutableList<GuardLocation>
    var isLooped: Boolean

    init {
        guardLocation = findGuardLocation()
        traversedPath = mutableListOf()
        isLooped = false
    }

    fun findGuardLocation(): GuardLocation {
        var guardLocation: GuardLocation? = null
        paths.forEachIndexed { x, row ->
            row.forEachIndexed { y, symbol ->
                GuardDirection.entries.forEach { enum ->
                    val maybeGuard = GuardDirection.findByChar(paths[x][y])
                    if (maybeGuard != null) {
                        guardLocation = GuardLocation(x, y, maybeGuard)
                        return@forEachIndexed
                    }
                }
            }
        }
        if (guardLocation != null) return guardLocation!!
        else throw Exception("Could not find guard")
    }

    fun moveGuard() {

        while (canMove(guardLocation.x + guardLocation.direction.dr.x, guardLocation.y + guardLocation.direction.dr.y)) {

            val newX = guardLocation.x + guardLocation.direction.dr.x
            val newY = guardLocation.y + guardLocation.direction.dr.y

            if (paths[newX][newY] == '#' || paths[newX][newY] == 'O') {
                guardLocation.direction = GuardDirection.next(guardLocation.direction)
                continue
            }

            paths[guardLocation.x][guardLocation.y] = 'X'

            val currentLocation = GuardLocation(guardLocation.x, guardLocation.y, guardLocation.direction)
            if (!traversedPath.contains(currentLocation)) {
                traversedPath.add(currentLocation)
            } else {
                // guardian is looped
                println("Found a loop with coordinates: $currentLocation")
                isLooped = true
//                printMap()
                break
            }

            guardLocation.x = newX
            guardLocation.y = newY
            paths[newX][newY] = guardLocation.direction.charValue
//            printMap()

        }
        // count the last step
        paths[guardLocation.x][guardLocation.y] = 'X'

    }

    fun canMove(newX: Int, newY: Int): Boolean {
        if (newX > paths.size - 1 || newX < 0) return false
        if (newY > paths[paths.size - 1].size - 1 || newY < 0) return false
        return true
    }


    fun printMap() {
        paths.forEach { e ->
            for (c in e) {
                print(c)
            }
            println()
        }
        println("----------------------")
    }

    fun countPath(): Int {
        val fold = paths.fold(0) { acc, chars -> acc + chars.count { c -> c == 'X' } }
        println(fold)
        return fold
    }

    fun printTraversePath() {
        println(traversedPath)
    }

}

enum class Direction(val x: Int, val y: Int) {
    UP(-1, 0),
    LEFT(0, -1),
    DOWN(1, 0),
    RIGHT(0, 1),
}

class GuardLocation(var x: Int, var y: Int, var direction: GuardDirection) {
    override fun toString(): String {
        return "GuardLocation(x=$x, y=$y), direction direction=$direction"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GuardLocation

        if (x != other.x) return false
        if (y != other.y) return false
        if (direction != other.direction) return false

        return true
    }


}

enum class GuardDirection(val charValue: Char, val dr: Direction) {
    UP('^', Direction.UP),
    RIGHT('>', Direction.RIGHT),
    DOWN('v', Direction.DOWN),
    LEFT('<', Direction.LEFT);

    companion object {
        fun findByChar(charToFind: Char): GuardDirection? {
            return entries.find { it.charValue == charToFind }
        }
        fun next(dr: GuardDirection): GuardDirection {
            return when(dr) {
                UP -> RIGHT
                LEFT -> UP
                DOWN -> LEFT
                RIGHT -> DOWN
            }
        }
    }
}

val inputArea =
    """
   ....#.....
   .........#
   ..........
   ..#.......
   .......#..
   ..........
   .#..^.....
   ........#.
   #.........
   ......#...
""".trimIndent()

fun readInput(): String {
    val lines = Files.readString(Paths.get(object{}.javaClass.getResource("/Day6/input.txt").path)).trimIndent()
    return lines
}
