package com.tugboat.regex

import com.tugboat.regex.fa.FA
import com.tugboat.regex.fa.NFA
import com.tugboat.regex.fa.NFABuilder
import com.tugboat.regex.fa.StateFactory
import com.tugboat.regex.fa.State

/**
 * Transforms a Regexp into an equivalent Finite Automaton. Is thread-safe.
 */
class RegexFAConstructor(private val stateFactory: StateFactory) {

    fun constructFrom(expression: Regexp): FA {
        // Use Thompson's Construction, which allows us to build an NFA piece-by-piece from
        // subexpressions of the Regexp.
        val startState = stateFactory.create(false)
        val builder: NFABuilder = NFA.builder(startState)
        val lastStateBeforeFinal: State = withExpression(expression, startState, builder)
        return builder
            .withEpsilonTransition(lastStateBeforeFinal, stateFactory.create(true))
            .build()
    }

    /*
     * Implementation note: 'with*' methods return the last state that was added
     * to the NFA, we can use this to chain together the subexpressions.
     */
    private fun withExpression(expression: Regexp, previousState: State, builder: NFABuilder): State {
        return when(expression) {
            is Regexp.Concatenation -> withConcatenation(expression, previousState, builder)
            is Regexp.Or -> withOr(expression, previousState, builder)
            is Regexp.Group -> withExpression(expression.subexpression, previousState, builder)
            is Regexp.CharMatcher -> withCharMatcher(expression, previousState, builder)
            is Regexp.ZeroOrMoreTimes -> withZeroOrMoreTimes(expression, previousState, builder)
            is Regexp.OneOrMoreTimes -> withOneOrMoreTimes(expression, previousState, builder)
            is Regexp.ZeroOrOneTime -> withZeroOrOneTime(expression, previousState, builder)
        }
    }

    private fun withConcatenation(expression: Regexp.Concatenation, previousState: State, builder: NFABuilder): State {
        var lastState = previousState
        for (subexpression in expression.subexpressions) {
            lastState = withExpression(subexpression, lastState, builder)
        }
        return lastState
    }

    private fun withOr(expression: Regexp.Or, previousState: State, builder: NFABuilder): State {
        val leftBranchState = nextState()
        val rightBranchState = nextState()
        builder.withEpsilonTransition(previousState, leftBranchState)
        builder.withEpsilonTransition(previousState, rightBranchState)
        val lastState = nextState()
        builder.withEpsilonTransition(withExpression(expression.firstSubexpression, leftBranchState, builder), lastState)
        builder.withEpsilonTransition(withExpression(expression.secondSubexpression, rightBranchState, builder), lastState)
        return lastState
    }

    private fun withCharMatcher(expression: Regexp.CharMatcher, previousState: State, builder: NFABuilder): State {
        val nextState = nextState()
        builder.withTransition(previousState, expression.symbol, nextState)
        return nextState
    }

    private fun withZeroOrMoreTimes(expression: Regexp.ZeroOrMoreTimes, previousState: State, builder: NFABuilder): State {
        val firstStateOfSubexpression = nextState()
        val lastState = nextState()
        builder.withEpsilonTransition(previousState, firstStateOfSubexpression)
        builder.withEpsilonTransition(previousState, lastState)
        val lastStateOfSubexpression = withExpression(expression.subexpression, firstStateOfSubexpression, builder)
        builder.withEpsilonTransition(lastStateOfSubexpression, lastState)
        builder.withEpsilonTransition(lastStateOfSubexpression, firstStateOfSubexpression)
        return lastState
    }

    private fun withOneOrMoreTimes(expression: Regexp.OneOrMoreTimes, previousState: State, builder: NFABuilder): State {
        val lastStateOfSubexpression = withExpression(expression.subexpression, previousState, builder)
        val lastState = nextState()
        builder.withEpsilonTransition(lastStateOfSubexpression, lastState)
        builder.withEpsilonTransition(lastStateOfSubexpression, previousState)
        return lastState
    }

    private fun withZeroOrOneTime(expression: Regexp.ZeroOrOneTime, previousState: State, builder: NFABuilder): State {
        val firstStateOfSubexpression = nextState()
        val lastState = withExpression(expression.subexpression, firstStateOfSubexpression, builder)
        builder.withEpsilonTransition(previousState, firstStateOfSubexpression)
        builder.withEpsilonTransition(previousState, lastState)
        return lastState
    }

    private fun nextState(): State {
        return stateFactory.create(false)
    }
}