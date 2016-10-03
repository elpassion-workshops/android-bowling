package com.elpassion.myapplication

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.util.*

class BowlingScoreCalculatorTest {

    private val game = BowlingGame()

    @Test fun shouldCalculateScoreForOneRollOnly                 () = checkGame(listOf( 7      )                  ,   7)
    @Test fun shouldCalculateScoreForOneFrameOnly                () = checkGame(listOf( 7, 2   )                  ,   9)
    @Test fun shouldCalculateScoreForOneSpareFrameOnly           () = checkGame(listOf( 7, 3   )                  ,  10)
    @Test fun shouldCalculateScoreForOneSpareAndThreeInNextRoll  () = checkGame(listOf( 7, 3, 3)                  ,  16)
    @Test fun shouldCalculateScoreForSpareAndStrike              () = checkGame(listOf( 7, 3,10)                  ,  30)
    @Test fun shouldCalculateScoreForSpareAndStrikeAnd2And1      () = checkGame(listOf( 7, 3,10, 2, 1)            ,  36)
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

@RunWith(JUnitPlatform::class)
class BowlingSpec : Spek({
    given("a bowling game") {

        val game = BowlingGame()

        var expectedScore = 0

        fun rollAndExpectIncrease(roll: Int, increase: Int) {
            game.roll(roll)
            expectedScore += increase
            assertEquals(expectedScore, game.score())
        }

        it("should start with score 0")                        { assertEquals(0, game.score())                   }
        it("should return score 3 when rolling 3 pins")        { rollAndExpectIncrease(roll =  3, increase =  3) }
        it("should return score 10 when rolling 7 pins")       { rollAndExpectIncrease(roll =  7, increase =  7) }
        it("should count first roll twice (we had spare)")     { rollAndExpectIncrease(roll =  5, increase = 10) }
        it("should count second roll in second frame once")    { rollAndExpectIncrease(roll =  1, increase =  1) }
        it("should add 10 when strike in third frame")         { rollAndExpectIncrease(roll = 10, increase = 10) }
        it("should count first roll twice  (we had strike)")   { rollAndExpectIncrease(roll =  4, increase =  8) }
        it("should count second roll twice (we had strike)")   { rollAndExpectIncrease(roll =  5, increase = 10) }

        for(frame in 5..10) {
            for(roll in listOf("first", "second")) {
                it("should increase score by 3 when rolling 3 pins in $frame'th frame in $roll roll") { rollAndExpectIncrease(3, 3) }
            }
        }
    }
})

class BowlingGame() {

    private val rolls = object : ArrayList<Int>() {
        override fun get(index: Int) = if(index < size) super.get(index) else 0
    }

    fun roll(pins: Int) {
        rolls.add(pins)
    }

    fun score(): Int {
        var score = 0
        var firstInFrame = 0
        for (i in 1..10) {
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
