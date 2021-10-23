package com.tugboat.regex.parsing

import com.tugboat.regex.Regexp
import com.tugboat.regex.Symbol

/**
 * Parses a list of regex tokens using recursive descent.
 * See: the grammar.
 *
 * @throws RegexParsingException if a syntax error is detected.
 */
fun parse(tokens: List<Token>): Regexp {
    return StatefulParser(tokens).parse()
}

class RegexParsingException(message: String): Exception(message)

/**
 * A class that uses its state to track the parsing, hence it's not
 * thread-safe.
 */
private class StatefulParser(tokens: List<Token>) {

    var tokensIt = tokens.listIterator()

    fun parse(): Regexp {
        val parsedExpression = regexp()
        if (hasNext()) {
            throw RegexParsingException("Tokens left over after attempted parse.")
        }
        return parsedExpression
    }

    private fun regexp(): Regexp {
        // This is called inside a group, it can be "empty" but with characters
        // still remaining. Would possibly be neater to allow or() to return
        // an empty concatenation.
        if (!hasNext() || nextMatches(TokenType.RIGHT_ROUND_BRACKET)) {
            return Regexp.Concatenation()
        }
        return or()
    }

    private fun or(): Regexp {
        return orOptional(concatenation())
    }

    private fun orOptional(leftPart: Regexp): Regexp {
        if (nextMatches(TokenType.OR)) {
            skip()
            return orOptional(Regexp.Or(leftPart, concatenation()))
        }
        return leftPart
    }

    private fun skip() {
        tokensIt.next()
    }

    private fun hasNext(): Boolean {
        return tokensIt.hasNext()
    }

    private fun nextMatches(vararg matchTypes: TokenType): Boolean {
        if (!hasNext()) {
            return false
        }
        val next: Token = getNext()
        rewind()
        return matchTypes.any { it == next.type }
    }

    private fun nextType(): TokenType? {
        if (!hasNext()) {
            return null
        }

        val type = getNext().type
        rewind()
        return type
    }

    private fun getNext(): Token {
        return tokensIt.next()
    }

    private fun rewind() {
        tokensIt.previous()
    }

    private fun concatenation(): Regexp {
        val subexpressions: MutableList<Regexp> = mutableListOf()
        while (nextMatches(TokenType.LEFT_ROUND_BRACKET, TokenType.DOT, TokenType.CHARACTER)) {
            subexpressions.add(unit())
        }
        if (subexpressions.isEmpty()) {
            throw RegexParsingException("Expected symbols but found none.")
        } else if (subexpressions.size == 1) {
            return subexpressions.first()
        }
        return Regexp.Concatenation(subexpressions)
    }

    private fun unit(): Regexp {
        return modifier(unmodifiedUnit())
    }

    private fun unmodifiedUnit(): Regexp {
        val next: Token = getNext()
        return when(next.type) {
            TokenType.CHARACTER -> Regexp.CharMatcher(Symbol.RawCharacter(next.raw))
            TokenType.DOT -> Regexp.CharMatcher(Symbol.Dot)
            TokenType.LEFT_ROUND_BRACKET -> group()
            else -> throw RegexParsingException("Failed to parse $next!")
        }
    }

    private fun modifier(expressionToModify: Regexp): Regexp {
        if (!hasNext()) {
            return expressionToModify
        }

        val type = nextType()
        skip()
        return when (type) {
            TokenType.STAR -> Regexp.ZeroOrMoreTimes(expressionToModify)
            TokenType.PLUS -> Regexp.OneOrMoreTimes(expressionToModify)
            else -> {
                rewind()
                expressionToModify
            }
        }
    }

    private fun group(): Regexp {
        val subexpression = regexp()
        if (!nextMatches(TokenType.RIGHT_ROUND_BRACKET)) {
            throw RegexParsingException("Unclosed group!")
        }
        skip()
        return Regexp.Group(subexpression)
    }
}
