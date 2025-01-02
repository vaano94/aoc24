package org.examplei.Day5

import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

fun main() {

//    val rulesString = """
//        47|53
//        97|13
//        97|61
//        97|47
//        75|29
//        61|13
//        75|53
//        29|13
//        97|29
//        53|29
//        61|53
//        97|53
//        61|29
//        47|13
//        75|47
//        97|75
//        47|61
//        75|61
//        47|29
//        75|13
//        53|13
//        38|66
//    """.trimIndent()

    val rulesString: String = readInputRules()
//
//    val updates = """
//        75,47,61,53,29
//        97,61,53,29,13
//        75,29,13
//        75,97,47,61,53
//        61,13,29
//        97,13,75,29,47
//    """.trimIndent()

    val updates: String = readJournals()

    val updatesList: List<List<Int>> = updates.lines().map { it.split(",").map(String::toInt) }

//    val singleUpdate = mutableListOf("75","47","61","53","29")
//    val updatesList = listOf(mutableListOf("38","62","52","66","49"))
//    val updatesList = listOf(mutableListOf(97,13,75,29,47))

    val allInputRulesParsed = rulesString.trimIndent().lines().map { it.split("|") }.map { it[0].toInt() to it[1].toInt() }

    val canComeBefore: Map<Int, List<Int>> = allInputRulesParsed.groupBy( { it.second } , { v -> v.first })
    val canNotComeBefore = allInputRulesParsed.groupBy( { it.first } , { v -> v.second })

    val map = updatesList.map { update ->
        val queue: MutableList<Int> = update.toMutableList()
//        println(queue)
        while (queue.isNotEmpty()) {
            val lastElement = queue.removeLast()
            val notAllowedBefore = canNotComeBefore[lastElement]
            if (notAllowedBefore != null) {
                val intersect: Set<Int> = notAllowedBefore.intersect(queue.toSet())
                if (intersect.isNotEmpty()) {
                    return@map Pair(update, false)
                }
            }
        }
        Pair(update, true)
    }
    val filteredGoodResults = map.filter { it.second }.sumOf { pair -> pair.first[pair.first.size / 2] }

    val filteredAndCorrectedBadResults = map.filter { !it.second }.map { it -> it.first }
        .map { wrongList ->
            val queue: MutableList<Int> = wrongList.toMutableList()
            val resList: List<Int> = arrangeInQueue(queue, canComeBefore, mutableListOf(), Stack<Int>())
            resList
        }
        .map { println(it); it }
        .sumOf { l -> l[l.size / 2] }

    println(filteredGoodResults)
    println(filteredAndCorrectedBadResults)

}

fun arrangeInQueue(
    queue: MutableList<Int>,
    canComeBefore: Map<Int, List<Int>>,
    resultList: MutableList<Int>,
    stack: Stack<Int>
): List<Int> {
    while (queue.isNotEmpty() || stack.isNotEmpty()) {
        val firstElement = if (stack.isNotEmpty()) stack.pop() else queue.removeFirst()
        val elementsNotAllowedToBeBefore = canComeBefore[firstElement]
        if (elementsNotAllowedToBeBefore != null) {
            val intersect = queue.intersect(elementsNotAllowedToBeBefore)
            if (intersect.isNotEmpty()) {
                stack.push(firstElement)
                stack.push(intersect.first())
                queue.remove(intersect.first())
                queue.remove(firstElement)
                arrangeInQueue(queue, canComeBefore, resultList, stack)
            }
        }
        if (!resultList.contains(firstElement)) {
            resultList.add(firstElement)
        }
    }
    return resultList
}

fun readInputRules(): String {
    return Files.readString(Paths.get(object{}.javaClass.getResource("/Day5/input.txt").path))
}

fun readJournals(): String {
    return Files.readString(Paths.get(object{}.javaClass.getResource("/Day5/journals.txt").path)).trim()
}


