package com.tugboat.regex.parsing

sealed class Token(val type: TokenType, val raw: Char) {
    data class RawCharacter(private val char: Char): Token(TokenType.CHARACTER, char)
    object LeftRoundBracket: Token(TokenType.LEFT_ROUND_BRACKET, '(')
    object RightRoundBracket: Token(TokenType.RIGHT_ROUND_BRACKET, ')')
    object Star: Token(TokenType.STAR, '*')
    object Or: Token(TokenType.OR, '|')
    object Dot: Token(TokenType.DOT, '.')
    object Backslash: Token(TokenType.BACKSLASH, '\\')
}

enum class TokenType {
    CHARACTER,
    LEFT_ROUND_BRACKET,
    RIGHT_ROUND_BRACKET,
    STAR,
    OR,
    DOT,
    BACKSLASH,
}

private val specialTokens = listOf(
    Token.LeftRoundBracket,
    Token.RightRoundBracket,
    Token.Star,
    Token.Or,
    Token.Dot,
    Token.Backslash)

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
