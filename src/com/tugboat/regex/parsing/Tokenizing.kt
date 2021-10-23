package com.tugboat.regex.parsing

sealed class Token(val type: TokenType, val raw: Char) {
    data class RawCharacter(private val char: Char): Token(TokenType.CHARACTER, char)
    object LeftRoundBracket: Token(TokenType.LEFT_ROUND_BRACKET, '(')
    object RightRoundBracket: Token(TokenType.RIGHT_ROUND_BRACKET, ')')
    object Star: Token(TokenType.STAR, '*')
    object Plus: Token(TokenType.PLUS, '+')
    object QuestionMark: Token(TokenType.QUESTION_MARK, '?')
    object Or: Token(TokenType.OR, '|')
    object Dot: Token(TokenType.DOT, '.')
}

enum class TokenType {
    CHARACTER,
    LEFT_ROUND_BRACKET,
    RIGHT_ROUND_BRACKET,
    STAR,
    PLUS,
    QUESTION_MARK,
    OR,
    DOT
}

private val specialTokens = listOf(
    Token.LeftRoundBracket,
    Token.RightRoundBracket,
    Token.Star,
    Token.Plus,
    Token.QuestionMark,
    Token.Or,
    Token.Dot)

private val charToSpecialToken: Map<Char, Token> = specialTokens.associateBy { it.raw }

/**
 * Takes a string and converts it into a list of Regex-specific tokens. Is thread-safe.
 */
fun tokenize(string: String): List<Token> {
    return string.map(::toToken)
}

private fun toToken(char: Char): Token {
    return charToSpecialToken[char] ?: Token.RawCharacter(char)
}
