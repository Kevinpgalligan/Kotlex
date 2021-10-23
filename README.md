## About
A simple regex implementation in Kotlin.

```
> val compiledRegex = compileRegex("h*(ello)* world|(here (be|are) dragons)")
> compiledRegex.matches("here be dragons")
true
> compiledRegex.matches("bello dorld")
false
```

Currently supports the following parts of regex: `*`, `+`, `()`, `.`, `|`. There is a wrapper around this that allows true/false text-matching. The matches are all-or-nothing.

## How to Build
The project is built using the Maven build system. Install Maven, clone the repository, then run `mvn package` from the top-level directory. That's it.

## How it works
1. Tokenizes the regex string.
2. Parses the tokens into a syntax tree.
3. From the syntax tree, builds a Nondeterministic Finite Automaton (using Thompson's Construction) that can be used to match strings.

Here's the grammar:

```
REGEXP                  -> OR | epsilon
OR                      -> CONCATENATION OR_OPTIONAL
CONCATENATION           -> EXPRESSION | EXPRESSION CONCATENATION
OR_OPTIONAL             -> "|" CONCATENATION OR_OPTIONAL | epsilon
UNIT                    -> UNMODIFIED_UNIT MODIFIER
UNMODIFIED_UNIT         -> GROUP | CHAR_MATCHER
MODIFIER                -> "*" | "+" | epsilon
GROUP                   -> "(" REGEXP ")"
CHAR_MATCHER            -> DOT | NON_SPECIAL_CHARACTER
NON_SPECIAL_CHARACTER   -> "a" | "b" | ...
DOT                     -> "."
```

Notes on the grammar:
* It's unambiguous (this is a requirement).
* It avoids left-recursion (i.e. where a non-terminal decomposes into
a string where that same non-terminal is the left-most symbol). Left-recursion
can be problematic for parsers.
* The precedence goes, from highest to lowest: 'modifiers', 'concatenation', 'or'.
* 'Or' is left-associative.

## License
MIT.
