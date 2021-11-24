package com.tugboat.regex

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertEquals

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

class AnyOfTest {
    private val anyOf = Symbol.AnyOf(listOf('c', 'b', 'a'))

    @Test
    fun testMatches() {
        assertTrue(anyOf.matches('a'))
        assertTrue(anyOf.matches('b'))
        assertTrue(anyOf.matches('c'))
    }

    @Test
    fun testDoesNotMatch() {
        assertFalse(anyOf.matches('A'))
        assertFalse(anyOf.matches('`'))
        assertFalse(anyOf.matches('d'))
    }

    @Test
    fun testStringInit() {
        assertEquals(Symbol.AnyOf("abc"), anyOf)
    }
}

class NoneOfTest {
    private val noneOf = Symbol.NoneOf(listOf('c', 'b', 'a'))
    private val stringInit = Symbol.NoneOf("cba")

    @Test
    fun testMatches() {
        assertTrue(noneOf.matches('A'))
        assertTrue(noneOf.matches('`'))
        assertTrue(noneOf.matches('d'))
    }

    @Test
    fun testDoesNotMatch() {
        assertFalse(noneOf.matches('a'))
        assertFalse(noneOf.matches('b'))
        assertFalse(noneOf.matches('c'))
    }

    @Test
    fun testStringInit() {
        assertEquals(Symbol.NoneOf("abc"), noneOf)
    }
}
