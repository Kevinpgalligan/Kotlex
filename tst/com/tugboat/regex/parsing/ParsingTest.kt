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
    fun testParseEscapedCharacter() {
        testParse(
            Regexp.Concatenation(
                Regexp.CharMatcher(Symbol.RawCharacter('(')),
                Regexp.CharMatcher(Symbol.RawCharacter('|')),
                Regexp.CharMatcher(Symbol.RawCharacter('\\'))),
            listOf(
                Token.Backslash,
                Token.LeftRoundBracket,
                Token.Backslash,
                Token.Or,
                Token.Backslash,
                Token.Backslash))
    }

    @Test
    fun testParseCharacterClass() {
        testParse(
            Regexp.Concatenation(
                Regexp.CharMatcher(Symbol.NoneOf("0123456789")),
                Regexp.CharMatcher(Symbol.AnyOf("0123456789abcdefABCDEF"))),
            listOf(
                Token.Backslash,
                Token.RawCharacter('D'),
                Token.Backslash,
                Token.RawCharacter('x')))
    }

    @Test
    fun testParseCharacterRange() {
        testParse(
            Regexp.CharMatcher(Symbol.AnyOf("abc")),
            listOf(
                Token.LeftSquareBracket,
                Token.RawCharacter('a'),
                Token.RawCharacter('b'),
                Token.RawCharacter('c'),
                Token.RightSquareBracket))
    }

    @Test
    fun testParseInvertedCharacterRange() {
        testParse(
            Regexp.CharMatcher(Symbol.NoneOf("abc")),
            listOf(
                Token.LeftSquareBracket,
                Token.RawCharacter('^'),
                Token.RawCharacter('a'),
                Token.RawCharacter('b'),
                Token.RawCharacter('c'),
                Token.RightSquareBracket))
    }

    @Test
    fun testParseEscapedCharInCharacterRange() {
        testParse(
            Regexp.CharMatcher(Symbol.AnyOf("^]")),
            listOf(
                Token.LeftSquareBracket,
                Token.Backslash,
                Token.RawCharacter('^'),
                Token.Backslash,
                Token.RightSquareBracket,
                Token.RightSquareBracket))
    }

    @Test
    fun testParseComprehensionInCharacterRange() {
        for (range in listOf("a0-9b", "ab0-9", "b0-9a", "0-9ab")) {
            testParse(
                Regexp.CharMatcher(Symbol.AnyOf("ab0123456789")),
                listOf(
                    Token.LeftSquareBracket,
                    *rawCharacters(range),
                    Token.RightSquareBracket))
        }
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

    @Test
    fun testParseStrayBackslash() {
        testFailedParse(listOf(Token.Backslash))
    }

    @Test
    fun testParseBadCharacterAfterBackslash() {
        testFailedParse(listOf(Token.Backslash, Token.RawCharacter('a')))
    }

    @Test
    fun testParseUnclosedCharacterRange() {
        testFailedParse(listOf(Token.LeftSquareBracket))
    }

    @Test
    fun testParseStrayBackslashInCharacterRange() {
        testFailedParse(listOf(Token.LeftSquareBracket, Token.Backslash))
    }

    @Test
    fun testParseBadCharacterAfterBackslashInCharacterRange() {
        testFailedParse(listOf(Token.LeftSquareBracket, Token.Backslash, Token.RawCharacter('d'), Token.RightSquareBracket))
    }

    @Test
    fun testParseTwoHyphensInCharacterRange() {
        testFailedParse(listOf(Token.LeftSquareBracket, Token.RawCharacter('-'), Token.RawCharacter('-'), Token.RightSquareBracket))
    }

    @Test
    fun testParseStrayHyphenInCharacterRange() {
        testFailedParse(listOf(Token.LeftSquareBracket, Token.RawCharacter('-'), Token.RightSquareBracket))
    }

    @Test
    fun testParseInvertedComprehensionInCharacterRange() {
        testFailedParse(listOf(Token.LeftSquareBracket, *rawCharacters("z-a"), Token.RightSquareBracket))
    }

    @Test
    fun testParseIncompleteComprehensionInCharacterRange() {
        testFailedParse(listOf(Token.LeftSquareBracket, *rawCharacters("a-"), Token.RightSquareBracket))
        testFailedParse(listOf(Token.LeftSquareBracket, *rawCharacters("-z"), Token.RightSquareBracket))
    }

    private fun rawCharacters(str: String): Array<Token.RawCharacter> {
        return str.map { Token.RawCharacter(it) }.toTypedArray()
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
