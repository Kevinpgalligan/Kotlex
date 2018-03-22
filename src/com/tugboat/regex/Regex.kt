package com.tugboat.regex

import com.tugboat.regex.fa.FA
import com.tugboat.regex.fa.StateFactory
import com.tugboat.regex.parsing.parse
import com.tugboat.regex.parsing.tokenize

// This is thread-safe, so it's okay to use a static instance. Although,
// it does make unit testing impossible.
private val faConstructor = RegexFAConstructor(StateFactory())

fun compileRegex(string: String): CompiledRegex {
    val tokens = tokenize(string)
    val expression = parse(tokens)
    return CompiledRegex(faConstructor.constructFrom(expression))
}

class CompiledRegex (private val fa: FA) {

    fun matches(string: String): Boolean {
        return fa.matches(string)
    }
}