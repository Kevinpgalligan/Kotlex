package com.tugboat.regex.parsing

import org.junit.Test
import kotlin.test.assertEquals

class TokenizerTest {

    private val tokenizer = Tokenizer()

    @Test
    fun testTokenizeEmptyString() {
        assertEquals(emptyList(), tokenizer.tokenize(""))
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
            tokenizer.tokenize("ah()*e"))
    }

    @Test
    fun testTokenizeEscapeCharactersTreatedAsNormalCharacters() {
        assertEquals(
            listOf(
                Token.RawCharacter('\\'),
                Token.LeftRoundBracket,
                Token.RawCharacter('\\'),
                Token.RawCharacter('d')),
            tokenizer.tokenize("\\(\\d"))
    }

    @Test
    fun testTokenizeSpecialTokenRepeatedMultipleTimes() {
        assertEquals(
            listOf(
                Token.LeftRoundBracket,
                Token.LeftRoundBracket,
                Token.LeftRoundBracket),
            tokenizer.tokenize("((("))
        println(tokenizer.tokenize("a()|.*"))
    }
}