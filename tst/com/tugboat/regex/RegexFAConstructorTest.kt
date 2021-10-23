package com.tugboat.regex

import com.tugboat.regex.fa.NFA
import com.tugboat.regex.fa.State
import com.tugboat.regex.fa.StateFactory
import org.junit.Test
import kotlin.test.assertEquals

class RegexFAConstructorTest {

    private val firstState = State(1, false)
    private val secondState = State(2, false)
    private val thirdState = State(3, false)
    private val fourthState = State(4, false)
    private val fifthState = State(5, false)
    private val sixthState = State(6, false)

    private val symbol = Symbol.Dot
    private val anotherSymbol = Symbol.RawCharacter('a')

    private val constructor = RegexFAConstructor(StateFactory())

    @Test
    fun testConstructEmptyConcatenation() {
        testConstruct(
            emptyMap(),
            mapOf(firstState to setOf(State(2, true))),
            Regexp.Concatenation())
    }

    @Test
    fun testConstructConcatenationWithSomethingInIt() {
        testConstruct(
            mapOf(
                firstState to setOf(Pair(symbol, secondState)),
                secondState to setOf(Pair(anotherSymbol, thirdState))),
            mapOf(thirdState to setOf(State(4, true))),
            Regexp.Concatenation(
                Regexp.CharMatcher(symbol),
                Regexp.CharMatcher(anotherSymbol)))
    }

    @Test
    fun testConstructCharMatcher() {
        testConstruct(
            mapOf(firstState to setOf(Pair(symbol, secondState))),
            mapOf(secondState to setOf(State(3, true))),
            Regexp.CharMatcher(symbol))
    }

    @Test
    fun testConstructOr() {
        testConstruct(
            mapOf(
                secondState to setOf(Pair(symbol, fifthState)),
                thirdState to setOf(Pair(anotherSymbol, sixthState))),
            mapOf(
                firstState to setOf(secondState, thirdState),
                fifthState to setOf(fourthState),
                sixthState to setOf(fourthState),
                fourthState to setOf(State(7, true))),
            Regexp.Or(
                Regexp.CharMatcher(symbol),
                Regexp.CharMatcher(anotherSymbol)))
    }

    @Test
    fun testConstructGroupIgnoresGroup() {
        testConstruct(
            mapOf(firstState to setOf(Pair(symbol, secondState))),
            mapOf(secondState to setOf(State(3, true))),
            Regexp.Group(
                Regexp.Group(
                    Regexp.Group(
                        Regexp.CharMatcher(symbol)))))
    }

    @Test
    fun testConstructZeroOrMoreTimes() {
        testConstruct(
            mapOf(
                secondState to setOf(Pair(symbol, fourthState))),
            mapOf(
                firstState to setOf(secondState, thirdState),
                fourthState to setOf(secondState, thirdState),
                thirdState to setOf(State(5, true))),
            Regexp.ZeroOrMoreTimes(
                Regexp.CharMatcher(symbol)))
    }

    @Test
    fun testConstructOneOrMoreTimes() {
        testConstruct(
            mapOf(
                firstState to setOf(Pair(symbol, secondState))),
            mapOf(
                secondState to setOf(firstState, thirdState),
                thirdState to setOf(State(4, true))),
            Regexp.OneOrMoreTimes(
                Regexp.CharMatcher(symbol)))
    }

    private fun testConstruct(
            expectedTransitions: Map<State, Set<Pair<Symbol, State>>>,
            expectedEpsilonTransitions: Map<State, Set<State>>,
            regexp: Regexp) {
        assertEquals(
            NFA(firstState, expectedTransitions, expectedEpsilonTransitions),
            constructor.constructFrom(regexp))
    }
}

