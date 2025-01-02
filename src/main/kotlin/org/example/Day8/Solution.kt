package org.example.Day8

import java.nio.file.Files
import java.nio.file.Paths

fun main() {
//    val input = """
//        ............
//        ........0...
//        .....0......
//        .......0....
//        ....0.......
//        ......A.....
//        ............
//        ............
//        ........A...
//        .........A..
//        ............
//        ............
//    """.trimIndent()
//    println(input)
    val input = readInput()

    val modifiableArray: Array<CharArray> = input.lines().map { it.toCharArray() }.toTypedArray()

    placeAntinodes(modifiableArray)
    printMap(modifiableArray)

}

fun placeAntinodes(antennaArray: Array<CharArray>) {
    val antinodeSet = mutableSetOf<AntinodeLocation>()
    val uniqueAntennaNodes = mutableListOf<AntennaLocation>()

    // collect all the possible combinations of letters
    for (x in antennaArray.indices) {
        for (y in 0..< antennaArray[x].size) {
            val inputSymbol = antennaArray[x][y]
            if (inputSymbol != '#' && inputSymbol != '.') {
              // it's a new letter
                uniqueAntennaNodes.add(AntennaLocation(inputSymbol, x, y))
            }
        }
    }

    // group nodes by letter
    val groupedNodes = uniqueAntennaNodes.groupBy { it.name }
//    println(groupedNodes)

    // for each combination, place node on the map
    groupedNodes.forEach {
        val nodeList = it.value
        while (nodeList.isNotEmpty()) {
            val firstNode = nodeList.removeFirst()
            // iterate one more over all left nodes
            nodeList.forEach { pairedNode ->
                // we have two points all the time, for each point direction difference, try to draw antinode until cant

                // first pair
                val newXa = firstNode.x - pairedNode.x
                val newYa = firstNode.y - pairedNode.y
                drawAntinode(antennaArray, firstNode.x, firstNode.y, NodeDifference(newXa, newYa), antinodeSet)

                // second pair, other direction
                val newXb = pairedNode.x - firstNode.x
                val newYb = pairedNode.y - firstNode.y
                drawAntinode(antennaArray, pairedNode.x, pairedNode.y, NodeDifference(newXb, newYb), antinodeSet)

            }
        }
    }


    // we want to have UNIQUE results. Even if potential antinode location combine with results of MULTIPLE other Antennas
    println("Antinodes counted: ${antinodeSet.count()}")

}

fun drawAntinode(antennaArray: Array<CharArray>, startX: Int, startY: Int, diff: NodeDifference, antinodeSet: MutableSet<AntinodeLocation>) {
    // get initial cords
    var continuingX = startX + diff.difX
    var continuingY = startY + diff.difY

    // when can draw, draw and update cords with the diff
    if ( continuingX >= 0 && continuingX < antennaArray.size && continuingY >= 0 && continuingY < antennaArray[0].size) {
        if (antennaArray[continuingX][continuingY] != '#' && antennaArray[continuingX][continuingY] != '.') {
            antinodeSet.add(AntinodeLocation(continuingX, continuingY))
            return
        }
        if (antennaArray[continuingX][continuingY] == '.') { // not busy and can draw
            antennaArray[continuingX][continuingY] = '#'
            antinodeSet.add(AntinodeLocation(continuingX, continuingY))
            continuingX += diff.difX
            continuingY += diff.difY
//            printMap(antennaArray)
        }
    }
    return

}

fun printMap(antennaArray: Array<CharArray>) {
    antennaArray.forEach { e ->
        for (c in e) {
            print(c)
        }
        println()
    }
    println("----------------------")
}


data class NodeDifference(val difX: Int, val difY: Int)

data class AntennaLocation(val name: Char, val x: Int, val y: Int)

data class AntinodeLocation(val x: Int, val y: Int)

fun readInput(): String {
    val lines = Files.readString(Paths.get(object{}.javaClass.getResource("/Day8/input.txt")?.path ?: "")).trimIndent()
    return lines
}


