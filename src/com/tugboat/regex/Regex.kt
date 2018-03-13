package com.tugboat.regex

import com.tugboat.regex.fa.FA
import com.tugboat.regex.fa.StateFactory
import com.tugboat.regex.parsing.Parser
import com.tugboat.regex.parsing.Tokenizer

// These are thread-safe, so it's okay to use static instances. Although,
// it does make unit testing impossible.
private val tokenizer = Tokenizer()
private val parser = Parser()
private val faConstructor = RegexFAConstructor(StateFactory())

fun compileRegex(string: String): CompiledRegex {
    val tokens = tokenizer.tokenize(string)
    val expression = parser.parse(tokens)
    return CompiledRegex(faConstructor.constructFrom(expression))
}

class CompiledRegex (private val fa: FA) {

    fun matches(string: String): Boolean {
        return fa.matches(string)
    }
}