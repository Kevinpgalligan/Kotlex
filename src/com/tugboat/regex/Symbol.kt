package com.tugboat.regex

/**
 * Represents a class of characters and can match on those characters.
 */
abstract class Symbol {

    abstract fun matches(char: Char): Boolean

    data class RawCharacter(private val rawChar: Char): Symbol() {
        override fun matches(char: Char): Boolean = (rawChar == char)
    }

    object Dot: Symbol() {
        override fun matches(char: Char): Boolean = true
    }
}