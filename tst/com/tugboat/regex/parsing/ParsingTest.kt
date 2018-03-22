package com.tugboat.regex.parsing

import com.tugboat.regex.Regexp
import com.tugboat.regex.Symbol
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ParsingTest {

    @Test
    fun testParseEmptyExpression() {
        testParse(
            Regexp.Concatenation(),
            emptyList())
    }

    @Test
    fun testParseSingleCharacter() {
        testParse(
            Regexp.CharMatcher(Symbol.RawCharacter('a')),
            listOf(Token.RawCharacter('a')))
    }

    @Test
    fun testParseDot() {
        testParse(
            Regexp.CharMatcher(Symbol.Dot),
            listOf(Token.Dot))
    }

    @Test
    fun testParseMultipleCharacters() {
        testParse(
            Regexp.Concatenation(
                Regexp.CharMatcher(Symbol.RawCharacter('a')),
                Regexp.CharMatcher(Symbol.RawCharacter('b'))),
            listOf(
                Token.RawCharacter('a'),
                Token.RawCharacter('b')))
    }

    @Test
    fun testParseEmptyGroup() {
        testParse(
            Regexp.Group(
                Regexp.Concatenation()),
            listOf(
                Token.LeftRoundBracket,
                Token.RightRoundBracket))
    }

    @Test
    fun testParseSimpleOr() {
        testParse(
            Regexp.Or(
                Regexp.CharMatcher(Symbol.RawCharacter('b')),
                Regexp.CharMatcher(Symbol.RawCharacter('a'))),
            listOf(
                Token.RawCharacter('b'),
                Token.Or,
                Token.RawCharacter('a')))
    }

    @Test
    fun testParseOrIsLeftAssociative() {
        testParse(
            Regexp.Or(
                Regexp.Or(
                    Regexp.CharMatcher(Symbol.RawCharacter('a')),
                    Regexp.CharMatcher(Symbol.RawCharacter('b'))),
                Regexp.CharMatcher(Symbol.RawCharacter('c'))),
            listOf(
                Token.RawCharacter('a'),
                Token.Or,
                Token.RawCharacter('b'),
                Token.Or,
                Token.RawCharacter('c')))
    }

    @Test
    fun testParseOrWhenAssociativityBlockedByGroup() {
        testParse(
            Regexp.Or(
                Regexp.CharMatcher(Symbol.RawCharacter('a')),
                Regexp.Group(
                    Regexp.Or(
                        Regexp.CharMatcher(Symbol.RawCharacter('b')),
                        Regexp.CharMatcher(Symbol.RawCharacter('c'))))),
            listOf(
                Token.RawCharacter('a'),
                Token.Or,
                Token.LeftRoundBracket,
                Token.RawCharacter('b'),
                Token.Or,
                Token.RawCharacter('c'),
                Token.RightRoundBracket))
    }

    @Test
    fun testParsePrecedenceOfConcatenationOverOr() {
        testParse(
            Regexp.Or(
                Regexp.Concatenation(
                    Regexp.CharMatcher(Symbol.RawCharacter('a')),
                    Regexp.CharMatcher(Symbol.RawCharacter('a'))),
                Regexp.Concatenation(
                    Regexp.CharMatcher(Symbol.RawCharacter('b')),
                    Regexp.CharMatcher(Symbol.RawCharacter('b')))),
            listOf(
                Token.RawCharacter('a'),
                Token.RawCharacter('a'),
                Token.Or,
                Token.RawCharacter('b'),
                Token.RawCharacter('b')))
    }

    @Test
    fun testParseStarAfterCharacter() {
        testParse(
            Regexp.ZeroOrMoreTimes(
                Regexp.CharMatcher(Symbol.RawCharacter('a'))),
            listOf(
                Token.RawCharacter('a'),
                Token.Star))
    }

    @Test
    fun testParseStarAfterDot() {
        testParse(
            Regexp.ZeroOrMoreTimes(
                Regexp.CharMatcher(Symbol.Dot)),
            listOf(
                Token.Dot,
                Token.Star))
    }

    @Test
    fun testParseStarAfterGroup() {
        testParse(
            Regexp.ZeroOrMoreTimes(
                Regexp.Group(
                    Regexp.CharMatcher(Symbol.RawCharacter('a')))),
            listOf(
                Token.LeftRoundBracket,
                Token.RawCharacter('a'),
                Token.RightRoundBracket,
                Token.Star))
    }

    @Test
    fun testParseOrWithNothingOnEitherSide() {
        testFailedParse(listOf(Token.Or))
    }

    @Test
    fun testParseOrWithSymbolOnLeftButNotRight() {
        testFailedParse(listOf(Token.RawCharacter('a'), Token.Or))
    }

    @Test
    fun testParseOrWithNoSymbolOnLeftButSymbolOnRight() {
        testFailedParse(listOf(Token.Or, Token.RawCharacter('a')))
    }

    @Test
    fun testParseStarAppliedToOr() {
        testFailedParse(listOf(Token.Or, Token.Star))
    }

    @Test
    fun testParseStarAppliedToLeftBracket() {
        testFailedParse(listOf(Token.LeftRoundBracket, Token.Star, Token.RightRoundBracket))
    }

    @Test
    fun testParseTwoStarsConsecutively() {
        testFailedParse(listOf(Token.RawCharacter('a'), Token.Star, Token.Star))
    }

    @Test
    fun testParseStarByItself() {
        testFailedParse(listOf(Token.Star))
    }

    @Test
    fun testParseUnclosedGroup() {
        testFailedParse(listOf(Token.LeftRoundBracket))
    }

    @Test
    fun testParseUnopenedGroup() {
        testFailedParse(listOf(Token.RightRoundBracket))
    }

    private fun testParse(expectedExpression: Regexp, tokens: List<Token>) {
        assertEquals(
            expectedExpression,
            parse(tokens))
    }

    private fun testFailedParse(tokens: List<Token>) {
        assertFailsWith(RegexParsingException::class) {
            parse(tokens)
        }
    }
}
