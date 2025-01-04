package org.example.Day8

import java.io.File
import java.nio.file.Files
import java.nio.file.Path


fun main() {
//    val input = """
//        ...................
//        ...................
//        ...................
//        .......YyYyY.......
//        ...................
//        .......R...S.......
//        ......r.....s......
//        ......R.....S......
//        .....ra.....ts.....
//        .......a...t.......
//        ...................
//        ...................
//        ...................
//        ...................
//        ...................
//        ...................
//        ...................
//        .................B.
//        ................B..
//    """.trimIndent()
//    val input = """
//        ..........T..T.......
//    """.trimIndent()
//    println(input)
    val input = readInput()

    val modifiableArray: Array<CharArray> = input.lines().map { it.toCharArray() }.toTypedArray()

    placeAntinodes(modifiableArray)
    printMap(modifiableArray)

    countAntinodesOnEveryAntenna(modifiableArray)

}

fun countAntinodesOnEveryAntenna(antennaArray: Array<CharArray>) {
    // find first # index
    val uniqueAntennaNodes = mutableListOf<AntinodeLocation>()

    // collect all the possible combinations of letters
    for (x in antennaArray.indices) {
        for (y in 0..< antennaArray[x].size) {
                // for every next # found
                if (antennaArray[x][y] != '.') {
                    uniqueAntennaNodes.add(AntinodeLocation(x, y))
            }
        }
    }
    println(uniqueAntennaNodes)

    val uniqueAntinodesOverall = mutableSetOf<AntinodeLocation>()

    while (uniqueAntennaNodes.isNotEmpty()) {
        val firstAntenna = uniqueAntennaNodes.removeFirst()
        uniqueAntinodesOverall.add(AntinodeLocation(firstAntenna.x, firstAntenna.y))
        // go backward and forward until end of array for the distance
        uniqueAntennaNodes.forEach { nextAntinode ->
            countAntennasOnPath(antennaArray, firstAntenna, nextAntinode, uniqueAntinodesOverall)
        }
    }
    println("Second solution")
    println(uniqueAntinodesOverall.size)
}

fun countAntennasOnPath(antennaArray: Array<CharArray>, firstAntenna: AntinodeLocation, nextAntinode: AntinodeLocation, uniqueAntinodesOverall: MutableSet<AntinodeLocation>) {
    // first pair
    // println("Coorinates # are $firstAntenna")
    val newXa = firstAntenna.x - nextAntinode.x
    val newYa = firstAntenna.y - nextAntinode.y

    countOnPathFor(antennaArray, firstAntenna, newXa, newYa, uniqueAntinodesOverall)

    // second pair, other direction
    val newXb = nextAntinode.x - firstAntenna.x
    val newYb = nextAntinode.y - firstAntenna.y

    countOnPathFor(antennaArray, firstAntenna, newXb, newYb, uniqueAntinodesOverall)
}

fun countOnPathFor(
    antennaArray: Array<CharArray>,
    antinode: AntinodeLocation,
    diffX: Int,
    diffY: Int,
    uniqueAntinodesOverall: MutableSet<AntinodeLocation>
) {
    var continuingX = antinode.x + diffX
    var continuingY = antinode.y + diffY
    while (continuingX >= 0 && continuingX < antennaArray.size && continuingY >= 0 && continuingY < antennaArray[0].size) {
        if (antennaArray[continuingX][continuingY] != '.') {
            // it's a an antenna, and should count
            uniqueAntinodesOverall.add(AntinodeLocation(continuingX, continuingY))
        }
        continuingX += diffX
        continuingY += diffY
    }
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
    val groupedNodes = uniqueAntennaNodes.groupBy { it.name }.filter { it.value.size > 1 }
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
                val wasSetFirstNode =
                    drawAntinode(antennaArray, firstNode.x, firstNode.y, NodeDifference(newXa, newYa), antinodeSet)

                // second pair, other direction
                val newXb = pairedNode.x - firstNode.x
                val newYb = pairedNode.y - firstNode.y
                val wasSetSecondNode =
                    drawAntinode(antennaArray, pairedNode.x, pairedNode.y, NodeDifference(newXb, newYb), antinodeSet)

                if (wasSetFirstNode || wasSetSecondNode) {
                    // if at least one of the antinodes was set, both antennas counts as antinodes
                    antinodeSet.add(AntinodeLocation(firstNode.x, firstNode.y))
                    antinodeSet.add(AntinodeLocation(pairedNode.x, pairedNode.y))
                }

            }
        }
    }

    // we want to have UNIQUE results. Even if potential antinode location combine with results of MULTIPLE other Antennas
    println("Antinodes counted: ${antinodeSet.count()}")

}

fun drawAntinode(
    antennaArray: Array<CharArray>,
    startX: Int,
    startY: Int,
    diff: NodeDifference,
    antinodeSet: MutableSet<AntinodeLocation>
): Boolean {
    // get initial cords
    var continuingX = startX + diff.difX
    var continuingY = startY + diff.difY

    var antinodeWasSet = false

    // when can draw, draw and update cords with the diff
    while (continuingX >= 0 && continuingX < antennaArray.size && continuingY >= 0 && continuingY < antennaArray[0].size) {
        if (antennaArray[continuingX][continuingY] != '#' && antennaArray[continuingX][continuingY] != '.') {
            antinodeSet.add(AntinodeLocation(continuingX, continuingY))
            continuingX += diff.difX
            continuingY += diff.difY
            continue
        }
        if (antennaArray[continuingX][continuingY] == '#') {
            // antinode was set and counted, skipping computation
            continuingX += diff.difX
            continuingY += diff.difY
            continue
        }
        if (antennaArray[continuingX][continuingY] == '.') { // not busy and can draw
            antennaArray[continuingX][continuingY] = '#'
            antinodeSet.add(AntinodeLocation(continuingX, continuingY))
            continuingX += diff.difX
            continuingY += diff.difY
            antinodeWasSet = true

        }
    }
    return antinodeWasSet
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
    val path: Path = File(object{}.javaClass.getResource("/Day8/input.txt")?.file ?: "").toPath()
    val lines = Files.readString(path).trimIndent()
    return lines
}


