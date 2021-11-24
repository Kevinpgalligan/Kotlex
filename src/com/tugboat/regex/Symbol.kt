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

    class AnyOf(unsortedCharacters: Iterable<Char>): Symbol() {
        val characters = unsortedCharacters.sorted()

        public constructor(string: String): this(string.toList())

        override fun matches(char: Char) = characters.binarySearch(char) >= 0
        override fun equals(other: Any?) = other is AnyOf && other.characters == this.characters
    }

    class NoneOf(unsortedCharacters: Iterable<Char>): Symbol() {
        val characters = unsortedCharacters.sorted()

        public constructor(string: String): this(string.toList())

        override fun matches(char: Char) = characters.binarySearch(char) < 0
        override fun equals(other: Any?) = other is NoneOf && other.characters == this.characters
    }
}
