package com.tugboat.regex.fa

import com.tugboat.regex.Symbol
import org.junit.Test
import kotlin.test.assertEquals

class NFATest {

    private val symbolMatchingLowercaseA: Symbol = Symbol.RawCharacter('a')
    private val symbolMatchingLowercaseB: Symbol = Symbol.RawCharacter('b')
    private val acceptingState = State(1, true)
    private val anotherAcceptingState = State(2, true)
    private val rejectingState = State(3, false)
    private val anotherRejectingState = State(4, false)
    private val andAnotherRejectingState = State(5, false)

    @Test
    fun testNFAWithJustStartingStateThatAccepts() {
        testMatches(
            NFA.builder(acceptingState)
                .build(),
            listOf(
                Pair(true, ""),
                Pair(false, "h")))
    }

    @Test
    fun testNFAWithJustStartingStateThatRejects() {
        testMatches(
            NFA.builder(rejectingState)
                .build(),
            listOf(
                Pair(false, ""),
                Pair(false, "b")))
    }

    @Test
    fun testNFAWithSingleEpsilonTransitionToAcceptingState() {
        testMatches(
            NFA.builder(rejectingState)
                .withEpsilonTransition(rejectingState, acceptingState)
                .build(),
            listOf(
                Pair(true, ""),
                Pair(false, "a")))
    }

    @Test
    fun testNFAWithSingleTransitionToAcceptingState() {
        testMatches(
            NFA.builder(rejectingState)
                .withTransition(rejectingState, symbolMatchingLowercaseA, acceptingState)
                .build(),
            listOf(
                Pair(false, ""),
                Pair(true, "a"),
                Pair(false, "b"),
                Pair(false, "aa")))
    }

    @Test
    fun testNFAWithSingleTransitionAwayFromAcceptingState() {
        testMatches(
            NFA.builder(acceptingState)
                .withTransition(acceptingState, symbolMatchingLowercaseA, rejectingState)
                .build(),
            listOf(
                Pair(false, "a"),
                Pair(true, "")))
    }

    @Test
    fun testNFAWithMultipleConsecutiveEpsilonTransitions() {
        testMatches(
            NFA.builder(rejectingState)
                .withEpsilonTransition(rejectingState, anotherRejectingState)
                .withEpsilonTransition(anotherRejectingState, acceptingState)
                .build(),
            listOf(
                Pair(true, ""),
                Pair(false, "a")))
    }

    @Test
    fun testNFAWithMultipleConsecutiveRegularTransitions() {
        testMatches(
            NFA.builder(rejectingState)
                .withTransition(rejectingState, symbolMatchingLowercaseA, anotherRejectingState)
                .withTransition(anotherRejectingState, symbolMatchingLowercaseB, acceptingState)
                .build(),
            listOf(
                Pair(false, ""),
                Pair(false, "a"),
                Pair(false, "aa"),
                Pair(true, "ab"),
                Pair(false, "aba"),
                Pair(false, "abb")))
    }

    @Test
    fun testNFAWithEpsilonTransitionFollowedByRegularTransition() {
        testMatches(
            NFA.builder(rejectingState)
                .withEpsilonTransition(rejectingState, anotherRejectingState)
                .withTransition(anotherRejectingState, symbolMatchingLowercaseA, acceptingState)
                .build(),
            listOf(
                Pair(false, ""),
                Pair(true, "a"),
                Pair(false, "b"),
                Pair(false, "aa")))
    }

    @Test
    fun testNFAWithRegularTransitionFollowedByEpsilonTransition() {
        testMatches(
            NFA.builder(rejectingState)
                .withTransition(rejectingState, symbolMatchingLowercaseA, anotherRejectingState)
                .withEpsilonTransition(anotherRejectingState, acceptingState)
                .build(),
            listOf(
                Pair(false, ""),
                Pair(true, "a"),
                Pair(false, "b"),
                Pair(false, "aa")))
    }

    @Test
    fun testNFAWithMultipleTransitionsFromSameState() {
        testMatches(
            NFA.builder(rejectingState)
                .withTransition(rejectingState, symbolMatchingLowercaseA, anotherRejectingState)
                .withTransition(rejectingState, symbolMatchingLowercaseB, acceptingState)
                .build(),
            listOf(
                Pair(false, ""),
                Pair(true, "b"),
                Pair(false, "a"),
                Pair(false, "ba"),
                Pair(false, "ab")))
    }

    @Test
    fun testNFAWithMultipleTransitionsOfSameTypeFromSameState() {
        testMatches(
            NFA.builder(rejectingState)
                .withTransition(rejectingState, symbolMatchingLowercaseA, anotherRejectingState)
                .withTransition(rejectingState, symbolMatchingLowercaseA, acceptingState)
                .build(),
            listOf(
                Pair(false, ""),
                Pair(false, "b"),
                Pair(true, "a"),
                Pair(false, "aa")))
    }

    @Test
    fun testNFAWithTransitionFromStartStateToItself() {
        testMatches(
            NFA.builder(acceptingState)
                .withTransition(acceptingState, symbolMatchingLowercaseA, acceptingState)
                .build(),
            listOf(
                Pair(true, ""),
                Pair(true, "a"),
                Pair(true, "aa"),
                Pair(true, "aaaaaaaaaaaaa"),
                Pair(false, "b"),
                Pair(false, "ab")))
    }

    @Test
    fun testNFAThatLoopsWithEpsilonTransition() {
        testMatches(
            NFA.builder(acceptingState)
                .withEpsilonTransition(acceptingState, acceptingState)
                .build(),
            listOf(
                Pair(true, ""),
                Pair(false, "a")))
    }

    @Test
    fun testNFAWithOneTransitionToAcceptingStateAndThenAnotherTransitionAwayFromAcceptingState() {
        testMatches(
            NFA.builder(rejectingState)
                .withTransition(rejectingState, symbolMatchingLowercaseA, acceptingState)
                .withTransition(acceptingState, symbolMatchingLowercaseB, anotherRejectingState)
                .build(),
            listOf(
                Pair(false, ""),
                Pair(true, "a"),
                Pair(false, "b"),
                Pair(false, "ab"),
                Pair(false, "aa")))
    }

    @Test
    fun testNFAWithRegularTransitionAndEpsilonTransitionFromSameState() {
        testMatches(
            NFA.builder(rejectingState)
                .withTransition(rejectingState, symbolMatchingLowercaseA, anotherRejectingState)
                .withEpsilonTransition(rejectingState, andAnotherRejectingState)
                .withTransition(andAnotherRejectingState, symbolMatchingLowercaseB, acceptingState)
                .build(),
            listOf(
                Pair(false, ""),
                Pair(false, "a"),
                Pair(true, "b"),
                Pair(false, "ab"),
                Pair(false, "ba")))
    }

    @Test
    fun testNFAWithMultipleAcceptingStates() {
        testMatches(
            NFA.builder(acceptingState)
                .withTransition(acceptingState, symbolMatchingLowercaseB, anotherAcceptingState)
                .withTransition(acceptingState, symbolMatchingLowercaseA, anotherAcceptingState)
                .build(),
            listOf(
                Pair(true, ""),
                Pair(true, "a"),
                Pair(true, "b"),
                Pair(false, "c"),
                Pair(false, "aa"),
                Pair(false, "ab"),
                Pair(false, "ca")))
    }

    @Test
    fun testNFAWithMultipleSimultaneousTransitionsFromMultipleStates() {
        testMatches(
            NFA.builder(rejectingState)
                .withEpsilonTransition(rejectingState, anotherRejectingState)
                .withTransition(anotherRejectingState, symbolMatchingLowercaseA, acceptingState)
                .withTransition(anotherRejectingState, symbolMatchingLowercaseB, rejectingState)
                .withTransition(rejectingState, symbolMatchingLowercaseB, acceptingState)
                .withTransition(rejectingState, symbolMatchingLowercaseA, anotherRejectingState)
                .build(),
            listOf(
                Pair(false, ""),
                Pair(true, "b"),
                Pair(true, "a"),
                Pair(false, "c"),
                Pair(true, "aa"),
                Pair(false, "aaa"),
                Pair(true, "aba"),
                Pair(true, "abaa"),
                Pair(false, "abab"),
                Pair(true, "ababb"),
                Pair(true, "bb"),
                Pair(true, "bbbbbbbb"),
                Pair(false, "bbbbab"),
                Pair(true, "bbbbabb"),
                Pair(true, "bbabbabbabb")))
    }

    @Test
    fun testNFAWithMultipleTransitionsToSameState() {
        testMatches(
            NFA.builder(rejectingState)
                .withEpsilonTransition(rejectingState, anotherRejectingState)
                .withTransition(rejectingState, symbolMatchingLowercaseA, acceptingState)
                .withTransition(anotherRejectingState, symbolMatchingLowercaseA, acceptingState)
                .build(),
            listOf(
                Pair(false, ""),
                Pair(true, "a"),
                Pair(false, "b"),
                Pair(false, "aa")))
    }

    private fun testMatches(nfa: NFA, tests: List<Pair<Boolean, String>>) {
        for ((expectedResult, input) in tests) {
            assertEquals(expectedResult, nfa.matches(input), "matching input '$input'")
        }
    }
}
