package com.tugboat.regex

/**
 * The various types of expressions that make up regex.
 */
sealed class Regexp {
    data class Concatenation(val subexpressions: List<Regexp>): Regexp() {
        constructor(vararg subexpressions: Regexp): this(subexpressions.toList())
    }
    data class Or(val firstSubexpression: Regexp, val secondSubexpression: Regexp): Regexp()
    data class Group(val subexpression: Regexp) : Regexp()
    data class CharMatcher(val symbol: Symbol) : Regexp()
    data class ZeroOrMoreTimes(val subexpression: Regexp) : Regexp()
    data class OneOrMoreTimes(val subexpression: Regexp) : Regexp()
    data class ZeroOrOneTime(val subexpression: Regexp) : Regexp()
}

