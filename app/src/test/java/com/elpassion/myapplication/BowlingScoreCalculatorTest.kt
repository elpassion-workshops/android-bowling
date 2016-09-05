package com.elpassion.myapplication

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class BowlingScoreCalculatorTest {

    private val game = BowlingGame()

    @Test
    fun shouldCalculateScoreForWorstGameEver() {
        rollZeroTimes(20)
        assertEquals(0, game.score())
    }

    @Test
    fun shouldCalculateScoreWhenOnePinWasHitDuringWholeGame() {
        game.roll(1)
        rollZeroTimes(19)
        assertEquals(1, game.score())
    }

    @Test
    fun shouldCountCorreclyIfSpareIsPresent() {
        rollSpare()
        game.roll(2)
        rollZeroTimes(17)
        assertEquals(14, game.score())
    }

    @Test
    fun shouldCalculateScoreForStrike() {
        rollStrike()
        game.roll(2)
        game.roll(1)
        rollZeroTimes(16)
        assertEquals(16, game.score())
    }

    @Test
    fun shouldHavePerfectScoreWhenRollStrike12() {
        (0..11).forEach {
            rollStrike()
        }

        assertEquals(300, game.score())
    }

    private fun rollStrike() {
        game.roll(10)
    }

    private fun rollSpare() {
        game.roll(5)
        game.roll(5)
    }

    private fun rollZeroTimes(times: Int) {
        (0 until times).forEach {
            game.roll(0)
        }
    }
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
