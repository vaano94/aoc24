package org.example.Day9

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

fun main() {

    val input = readInput().map { it.digitToInt() }.toTypedArray()
//    val input: Array<Int> = "2333133121414131402".map { it.digitToInt() }.toTypedArray()

    val blockToLine = blockToLine(input)

//    val arrangedBlock = relocateFiles(blockToLine)
//    println(calculateSum(arrangedBlock))

    val arrangedBlockSecondPart = relocateFilesPartTwo(blockToLine)
    println(calculateSum(arrangedBlockSecondPart))
    println(arrangedBlockSecondPart)

}

data class MovingBlock(val element:Int?, var endIndex: Int, val startIndex: Int)

fun relocateFilesPartTwo(block: Array<Int?>):Array<Int?> {
    var reassembledBlocks = block

    var movingBlock: MovingBlock? = null
    var lastMovedBlockElement: Int? = null

    for (backwardIndex in block.lastIndex downTo 0) {
//        println(reassembledBlocks.toList())
        // Starting from beginning, find an EMPTY block of size equals or less
        val backwardElement = block[backwardIndex]
        if (backwardElement != null) {
            // we met an element, continue gathering until meet next null
            if (movingBlock != null && backwardElement == movingBlock.element) {
                // we found the same element , add it as well
                movingBlock.endIndex -= 1
                continue
            } else if (movingBlock != null && movingBlock.element != null) {
                // it's a new element, we need to put the old one
                if (lastMovedBlockElement != movingBlock.element) {
                    forwardPlaceBlock(block, movingBlock)
                }
                lastMovedBlockElement = movingBlock.element
                movingBlock = MovingBlock(element = backwardElement, startIndex = backwardIndex, endIndex = backwardIndex)
                continue
            } else if (movingBlock == null) {
                // create a new one
                movingBlock = MovingBlock(element = backwardElement, startIndex = backwardIndex, endIndex = backwardIndex)
            }
        } else {
            if (movingBlock != null) {
                if (lastMovedBlockElement != movingBlock.element) {
                    forwardPlaceBlock(block, movingBlock)
                }
                lastMovedBlockElement = movingBlock.element
            }
        }
    }
    return reassembledBlocks
}

fun forwardPlaceBlock(block: Array<Int?>, movingBlock: MovingBlock) {
//    println("Moving element: ${movingBlock}")
    // find the first block that satisfies this size and that is not later that the lower bound of the movingBlock
    val blockSize = if (movingBlock.endIndex == movingBlock.startIndex)
        1
        else (movingBlock.startIndex - movingBlock.endIndex) + 1
    var nullFilledMovingBlock: MovingBlock? = null
    for (forwardIndex in 0 ..< movingBlock.endIndex) {
        if (block[forwardIndex] == null) {
            // potentially found a candidate to replace, continue
            if (nullFilledMovingBlock != null) {
                if (forwardIndex - nullFilledMovingBlock.endIndex == 1) {
                    nullFilledMovingBlock.endIndex += 1
                } else {
                    continue
                }
            } else {
                nullFilledMovingBlock = MovingBlock(element = null, startIndex = forwardIndex, endIndex = forwardIndex)
            }

            // if length is OK, replace the element
            val nullableBlockLength = (nullFilledMovingBlock.endIndex - nullFilledMovingBlock.startIndex + 1)
            if (blockSize == nullableBlockLength) {
                for (i in movingBlock.endIndex..movingBlock.startIndex) {
                    block[i] = null
//                    println(block.toList())
                }
                for (j in nullFilledMovingBlock.startIndex .. nullFilledMovingBlock.endIndex) {
                    block[j] = movingBlock.element
//                    println(block.toList())
                }
                return
            }

        } else {
            nullFilledMovingBlock = null
//            println(block.toList())
            continue
        }
    }
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
