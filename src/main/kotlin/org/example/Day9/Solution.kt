package org.example.Day9

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

fun main() {

    val input = readInput().map { it.digitToInt() }.toTypedArray()
//    val input: Array<Int> = "2333133121414131402".map { it.digitToInt() }.toTypedArray()


    val toTypedArray: Array<Int?> =
        "0099811188827773336446555566..............".map { if (it.isDigit()) it.digitToInt() else null }.toTypedArray()
    val convertedString = toTypedArray;

//    val customArray: Array<Int?> = arrayOf(0, null, 1, 2, 3)

//    println(convertedString.toString())
//
//    println((calculateSum(convertedString)))

    val blockToLine = blockToLine(input)

    val arrangedBlock = relocateFiles(blockToLine)
    println(calculateSum(arrangedBlock))

}

fun relocateFiles(block: Array<Int?>): Array<Int?> {
    var reassembledBlocks = block
    var lastForwardIndex = 0
    // do something
    backward@ for ((_, backwardIndex) in (reassembledBlocks.size - 1 downTo 0).withIndex()) {

        while (hasGaps(reassembledBlocks)) {
            val backwardElement = reassembledBlocks[backwardIndex]

//            println(backwardIndex)
            if (backwardElement == null) {
                // nothing to do, move to next element
                continue@backward
            } else {
                forward@ for (forwardIndex in lastForwardIndex..<reassembledBlocks.size) {
                    // find a place to position the element
                    val forwardElement = reassembledBlocks[forwardIndex]
                    if (forwardElement == null) {
                        reassembledBlocks[forwardIndex] = backwardElement
                        reassembledBlocks[backwardIndex] = null
                        lastForwardIndex = forwardIndex
//                        println(reassembledBlocks.toList())
                        break@forward
                    }
                }
            }
        }
    }
    return reassembledBlocks
}

fun hasGaps(block: Array<Int?>): Boolean {
    var hasGaps = false
    var firstNullElementIndex: Int? = null
    block.forEachIndexed { index, elem ->
        if (elem == null) {
            firstNullElementIndex = index
            if (firstNullElementIndex == index - 1) {
                // there is a gap
                hasGaps = true
                return@forEachIndexed
            }
        } else {
            if (firstNullElementIndex == index - 1) {
                // there is a gap too
                hasGaps = true
                return@forEachIndexed
            }
        }
    }
    return hasGaps
}

fun blockToLine(block: Array<Int>): Array<Int?> {
    var newArray = arrayOf<Int?>()
    var isBlock = true
    var fileId = 0
    block.forEachIndexed { index, i ->
        if (isBlock) {
            repeat(i) { newArray = newArray.plus(fileId) }
            isBlock = false
            fileId += 1
        } else {
            for (empty in 0..<i) {
                newArray = newArray.plus(null)
            }
            isBlock = true
        }
    }
    return newArray
}

fun calculateSum(line: Array<Int?>): Long {
    var sum: Long = 0L
    for (i in line.indices) {
        if (line[i] != null) {
            sum += (i * line[i]?.toLong()!!)
        }
    }
    return sum
}

fun readInput(): String {
    val path: Path = File(object{}.javaClass.getResource("/Day9/input.txt")?.file ?: "").toPath()
    val lines = Files.readString(path).trimIndent()
    return lines
}