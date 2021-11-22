package com.tugboat.regex.parsing

import org.junit.Test
import kotlin.test.assertEquals

class TokenizingTest {

    @Test
    fun testTokenizeEmptyString() {
        assertEquals(emptyList(), tokenize(""))
    }

    @Test
    fun testTokenizeAllTokenTypes() {
        assertEquals(
            listOf(
                Token.RawCharacter('a'),
                Token.RawCharacter('h'),
                Token.LeftRoundBracket,
                Token.RightRoundBracket,
                Token.Star,
                Token.RawCharacter('e')),
            tokenize("ah()*e"))
    }

    @Test
    fun testTokenizeEscapeCharactersTreatedAsNormalCharacters() {
        assertEquals(
            listOf(
                Token.Backslash,
                Token.LeftRoundBracket,
                Token.Backslash,
                Token.RawCharacter('d')),
            tokenize("\\(\\d"))
    }

    @Test
    fun testTokenizeSpecialTokenRepeatedMultipleTimes() {
        assertEquals(
            listOf(
                Token.LeftRoundBracket,
                Token.LeftRoundBracket,
                Token.LeftRoundBracket),
            tokenize("((("))
    }
}