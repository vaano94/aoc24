package org.examplei.day4

import java.nio.file.Files
import java.nio.file.Paths
import java.util.LinkedList

fun main() {

    var searchWord = LinkedList<String>()
    searchWord.addAll(listOf("X", "M", "A", "S"))

    val arr = readInput()

    val totalCombinations = mutableListOf<List<NextLetterCords>>()

    // iterate over every element
    for (i in arr.indices) {
        for (j in arr[i].indices) {
            val foundCombinations = traverseAround(arr, i, j, searchWord)
            totalCombinations += foundCombinations
        }
    }

    // count list entries
    val wordsInMatrix = totalCombinations.count { c -> c.size == searchWord.size }
    println("Words in Matrix: $wordsInMatrix")

}


fun traverseAround(array: Array<Array<String>>, startingX: Int, startingY: Int, searchWord: LinkedList<String>): MutableList<List<NextLetterCords>> {
    val desiredLength = searchWord.size
    val foundCombinations = mutableListOf<List<NextLetterCords>>()

    // check all directions for starting letter
    Direction.entries.forEach { d ->
        val beginningElement: String = array[startingX][startingY]
        val copySearchWordForDirection = LinkedList(searchWord)

        val letterCords = mutableListOf<NextLetterCords>()
        if (beginningElement == copySearchWordForDirection.poll()) {
            letterCords.add(NextLetterCords(beginningElement, startingX, startingY))
            // save entering index
            // begin word search in given direction
            var newStartingx = startingX
            var newStartingy = startingY


            // for each starting letter, continue in same direction
            while (copySearchWordForDirection.isNotEmpty()) {
                val expectedLetter = copySearchWordForDirection.poll()
                val newCoordinates = moveToDirection(array, d, newStartingx, newStartingy)
                if (newCoordinates != null && newCoordinates.nextLetter == expectedLetter) {
                    letterCords.add(NextLetterCords(expectedLetter, newCoordinates.newX, newCoordinates.newY))
                    newStartingx = newCoordinates.newX
                    newStartingy = newCoordinates.newY
                } else {
                    break
                }
            }
        }
        if (letterCords.size == desiredLength) {
            // we found a matching word
            foundCombinations.add(letterCords)
        }
    }
    return foundCombinations
}

data class NextLetterCords(val nextLetter: String, val newX: Int, val newY: Int)

fun moveToDirection(array: Array<Array<String>>, d: Direction, currentX: Int, currentY: Int): NextLetterCords? {
    val newX = currentX + d.x
    val newY = currentY + d.y
    if (newX > array.size - 1 || newX < 0) return null
    if (newY > array[array.size-1].size - 1 || newY < 0) return null

    return NextLetterCords(array[newX][newY], newX, newY)
}


enum class Direction(val x: Int, val y: Int) {
    UP(-1, 0),
    UPLEFT(-1, -1),
    LEFT(0, -1),
    DOWNLEFT(1, -1),
    DOWN(1, 0),
    DOWNRIGHT(1, 1),
    RIGHT(0, 1),
    UPRIGHT(-1, 1),
}

fun readInput(): Array<Array<String>> {
    val lines: MutableList<String> = Files.readAllLines(Paths.get("src/main/kotlin/org/example/day4/input.txt"))
    return lines.map {
        it.map { c -> c.toString() }.toTypedArray()
    }.toTypedArray()
}
