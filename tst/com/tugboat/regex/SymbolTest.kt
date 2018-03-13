package com.tugboat.regex

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RawCharacterTest {

    private val rawChar = Symbol.RawCharacter('a')

    @Test
    fun testMatchesSameCharacter() {
        assertTrue(rawChar.matches('a'))
    }

    @Test
    fun testDoesNotMatchDifferentCharacter() {
        assertFalse(rawChar.matches('b'))
    }

    @Test
    fun testDoesNotMatchUpperCaseEquivalent() {
        assertFalse(rawChar.matches('A'))
    }
}

class DotTest {

    private val dot = Symbol.Dot

    @Test
    fun testMatchesActualDot() {
        assertTrue(dot.matches('.'))
    }

    @Test
    fun testMatchesSomeCharacter() {
        assertTrue(dot.matches('a'))
    }
}
