package com.skopenweb.exchange.conversion

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 * Given an array nums of n integers, are there elements a, b, c in nums such that a + b + c = 0?
 *
 * Find all unique triplets in the array which gives the sum of zero.
 *
 * Input: [-1,0,1,2,-1,-4]
 * Output: [[-1,-1,2],[-1,0,1]]
 *
 * If input empty array, output will return empty array.
 *
 * Input: []
 * Output: []
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    fun triplets(input: IntArray): Array<IntArray> {
        if (input.size < 3) return emptyArray()
        val result = mutableListOf<IntArray>()
        val ans = IntArray(3)

        for (i in input.indices) {
            for (j in i + 1..<input.size) {
                for (k in j + 1..<input.size) {
                    if (input[i] + input[j] + input[k] == 0) {
                        ans[0] = input[i]
                        ans[1] = input[j]
                        ans[2] = input[k]

                        ans.sort()
                        result.checkAndAdd(ans.clone())
//                        result.add(ans.clone())
                    }
                }
            }
        }
        return Array(result.size) {
            result[it]
        }
    }

    fun MutableList<IntArray>.checkAndAdd(a: IntArray) {
        for (e in this) {
            if (a.contentEquals(e)) return
        }
        add(a)
    }

    fun MutableList<IntArray>.contains(a: IntArray): Boolean {
        for (e in this) {
            if (a.contentEquals(e)) return true
        }
        return false
    }

    @Test
    fun testEmptyWorksFine() {
//        val input = intArrayOf(-1, 0, 1, 2, -1, -4)

        val result = triplets(intArrayOf())

        assert(result.isEmpty())
    }

    @Test
    fun testNonEmptyWorksFine1() {
        val input = intArrayOf(-1, 0, 1, 2, -1, -4)

        val result = triplets(input)

        assert(result.isNotEmpty())
        println(result.size)
        assert(result.size == 2)

        val actualFirstElement = result[1]
        val actualSecondElement = result[0]

        val expectedFirstElement = intArrayOf(-1, -1, 2)
        val expectedSecondElement = intArrayOf(-1, 0, 1)

        assert(actualFirstElement.contentEquals(expectedFirstElement))
        assert(actualSecondElement.contentEquals(expectedSecondElement))
    }

}