package com.elpassion.myapplication

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class BowlingScoreCalculatorTest {

    private val game = BowlingGame()

    @Test fun shouldCalculateScoreForWorstGameEver               () = checkGame(                   listOf( 0) * 20,   0)
    @Test fun shouldCalculateScoreWhenOnePinWasHitDuringWholeGame() = checkGame(listOf(       1) + listOf( 0) * 19,   1)
    @Test fun shouldCountCorreclyIfSpareIsPresent                () = checkGame(listOf( 5, 5, 2) + listOf( 0) * 17,  14)
    @Test fun shouldCalculateScoreForStrike                      () = checkGame(listOf(10, 2, 1) + listOf( 0) * 16,  16)
    @Test fun shouldHavePerfectScoreWhenRollStrike12             () = checkGame(                   listOf(10) * 12, 300)

    fun checkGame(rolls: List<Int>, result: Int) {
        rolls.forEach { game.roll(it) }
        assertEquals(result, game.score())
    }

    operator fun List<Int>.times(n: Int) = (1..n).fold(emptyList<Int>()) { list, n -> list + this }

}

class BowlingGame() {

    private val rolls: MutableList<Int> = ArrayList()

    fun roll(pins: Int) {
        rolls.add(pins)
    }

    fun score(): Int {
        var score = 0
        var firstInFrame = 0
        for (i in 0 until 10) {
            if (isStrike(firstInFrame)) {
                score += scoreForStrike(firstInFrame)
                firstInFrame += 1
            } else if (isSpare(firstInFrame)) {
                score += scoreForSpare(firstInFrame)
                firstInFrame += 2
            } else {
                score += scoreForFrame(firstInFrame)
                firstInFrame += 2
            }
        }
        return score
    }

    private fun scoreForStrike(firstInFrame: Int) = rolls[firstInFrame] + rolls[firstInFrame + 1] + rolls[firstInFrame + 2]

    private fun scoreForSpare(firstInFrame: Int) = rolls[firstInFrame] + rolls[firstInFrame + 1] + rolls[firstInFrame + 2]

    private fun isSpare(firstInFrame: Int) = rolls[firstInFrame] + rolls[firstInFrame + 1] == 10

    private fun scoreForFrame(firstInFrame: Int) = rolls[firstInFrame] + rolls[firstInFrame + 1]

    private fun isStrike(firstInFrame: Int) = rolls[firstInFrame] == 10
}
