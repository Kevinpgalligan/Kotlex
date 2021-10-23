package com.tugboat.regex

import org.junit.Test
import kotlin.test.assertEquals

class RegexIntegrationTest {

    @Test
    fun testMatchesPlainString() {
        testMatches(
            "hello world",
            listOf(
                "hello world"),
            listOf(
                "",
                "h",
                "hello worl",
                "world",
                "helloworld"))
    }

    @Test
    fun testMatchesAnyString() {
        testMatches(
            ".*",
            listOf(
                "ioiahsoeahswe",
                "",
                "hello world",
                "89acjoizcmn';#';e2-103"),
            emptyList())
    }

    @Test
    fun testMatchesStringWithDots() {
        testMatches(
            "h.llo .orld",
            listOf(
                "hello world",
                "hallo morld",
                "h3llo .orld"),
            listOf(
                "ehllo world",
                "hllo world",
                "hello orld"))
    }

    @Test
    fun testMatchesOr() {
        testMatches(
            "hello|world",
            listOf(
                "hello",
                "world"),
            listOf(
                "",
                "helloworld",
                "hell",
                "o",
                "w",
                "hello|world",
                "hellooworld"))
    }

    @Test
    fun testMatchesSomeStringPrecededAndFollowedByAnything() {
        testMatches(
            ".*hello world.*",
            listOf(
                "hello world",
                " hello world ",
                "hellohellohello worldworldworld",
                "oiniqnweoqinwehello world",
                "hello worldaojsdia0sdias",
                "hhello worldd"),
            listOf(
                "hello somethingithemiddleworld",
                "helloworld"))
    }

    @Test
    fun testMatchesPlus() {
        testMatches(
            "hello+",
            listOf(
                "hello",
                "helloo",
                "helloooooooo"),
            listOf("hell"))
    }

    @Test
    fun testMatchesOptionalCharacter() {
        testMatches(
            "hell?o",
            listOf(
                "hello",
                "helo"),
            listOf(
                "helllo",
                "heo"))
    }

    @Test
    fun testMatchesComplexExpressionWithGroups() {
        testMatches(
            "h*(ello)* world|(here (be|are) dragons)",
            listOf(
                "here be dragons",
                "here are dragons",
                " world",
                "ello world",
                "h world",
                "hello world",
                "helloelloello world",
                "hhhhhello world",
                "hhhhhelloelloello world"),
            listOf(
                "here  dragons",
                "here beare dragons",
                "here",
                "dragons",
                "elloh world",
                "a world",
                "llo world",
                "ahello world",
                "helloa world"))
    }

    private fun testMatches(pattern: String, expectedMatches: List<String>, expectedNonMatches: List<String>) {
        val compiled = compileRegex(pattern)
        expectedMatches.forEach { testMatch(compiled, it, true) }
        expectedNonMatches.forEach { testMatch(compiled, it, false) }
    }

    private fun testMatch(compiled: CompiledRegex, string: String, expectedResult: Boolean) {
        assertEquals(expectedResult, compiled.matches(string), "matching $string")
    }
}
