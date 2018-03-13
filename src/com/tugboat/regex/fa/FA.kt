package com.tugboat.regex.fa

interface FA {

    fun matches(string: String): Boolean
}

data class State(val id: Int, val isAccepting: Boolean)

/**
 * Used to create states, each instance of the factory will keep a separate
 * ID counter (starting from 1) and use it to assign IDs to states.
 *
 * I'm not particularly happy with how states are distinguished by
 * an integer counter, but otherwise, it seems like we'd have to
 * perform fancy equivalence matching between 2 FAs if we wanted to
 * compare them. I'm open to a better solution.
 */
class StateFactory {

    private var id = 1

    fun create(isAccepting: Boolean): State = State(id++, isAccepting)
}
