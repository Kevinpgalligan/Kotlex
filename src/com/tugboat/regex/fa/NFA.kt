package com.tugboat.regex.fa

import com.tugboat.regex.Symbol
import java.util.ArrayDeque

data class NFA(
        private val startState: State,
        private val transitionsByState: Map<State, Set<Pair<Symbol, State>>>,
        private val epsilonTransitions: Map<State, Set<State>>) : FA {

    companion object {
        fun builder(startState: State): NFABuilder = NFABuilder(startState)
    }

    override fun matches(string: String): Boolean {
        var currentStates = mutableSetOf(startState)
        for (c in string) {
            if (currentStates.isEmpty()) {
                return false
            }
            expandWithEpsilonTransitions(currentStates)
            currentStates = nextStates(currentStates, c)
        }
        expandWithEpsilonTransitions(currentStates)
        return currentStates.any { it.isAccepting }
    }

    private fun expandWithEpsilonTransitions(states: MutableSet<State>) {
        val unexpandedStates = ArrayDeque<State>()
        unexpandedStates.addAll(states)
        while (unexpandedStates.isNotEmpty()) {
            for (epsilonTransitionState in epsilonTransitions[unexpandedStates.pop()] ?: emptySet()) {
                if (epsilonTransitionState !in states) {
                    states.add(epsilonTransitionState)
                    unexpandedStates.add(epsilonTransitionState)
                }
            }
        }
    }

    private fun nextStates(states: Set<State>, c: Char): MutableSet<State> {
        val nextStates: MutableSet<State> = mutableSetOf()
        for (state in states) {
            val transitions: Set<Pair<Symbol, State>>? = transitionsByState[state]
            transitions ?: continue
            for ((symbol, to) in transitions) {
                if (symbol.matches(c)) {
                    nextStates.add(to)
                }
            }
        }
        return nextStates
    }
}

class NFABuilder(private val startState: State) {

    private val transitions: MutableMap<State, MutableSet<Pair<Symbol, State>>> = mutableMapOf()
    private val epsilonTransitions: MutableMap<State, MutableSet<State>> = mutableMapOf()

    fun withTransition(from: State, input: Symbol, to: State): NFABuilder {
        transitions
            .computeIfAbsent(from, { mutableSetOf() })
            .add(Pair(input, to))
        return this
    }

    fun withEpsilonTransition(from: State, to: State): NFABuilder {
        epsilonTransitions
            .computeIfAbsent(from, { mutableSetOf() })
            .add(to)
        return this
    }

    fun build(): NFA {
        return NFA(startState, transitions, epsilonTransitions)
    }
}