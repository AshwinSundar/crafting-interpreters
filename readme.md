# Crafting Interpreters - Notes

## How to run

1. Navigate to `src/`
2. Build with `javac com/craftinginterpreters/lox/*.java`
3. Run REPL with `java com.craftinginterpreters.lox.Lox`

## Chapter 1 

## Chapter 2 

## Chapter 3 - The Lox Language

### First Class Functions

- Here is an example of a first-class function:

```
fun addPair(a, b) {
  return a + b;
}

fun identity(a) {
  return a;
}

print identity(addPair)(1, 2);
```

- `addPair` is a first class function. It can be passed as an argument to `identity`, to which the arguments for `addPair` can be passed directly to.

### Object-Oriented Programming

3.9.5 - The idea behind object-Oriented programming is encapsulating behavior _and_ state together.


### Challenges

2. Open Questions about Lox:

- Is variable shadowing allowed? I think it should be, and I think Lox would allow it based on what I know so far.

- Are imports of other Lox files (and their contents) allowed? I.e. do their contents get appended to the abstract syntax theory? I think the answer is yes as well.

3. What features do you think Lox is missing?

- It doesn't seem to support pattern-matching, which is not surprising given that it's not a functional-first language. Although it's a nice feature to have, because it allows for exhaustive matching, which makes it easier to cover all potential conditions a particular code block may enter.

## Chapter 4 - Scanning

- "scanning" aka "lexing", which is short for "lexical analysis"
- **lexeme** - a special group of characters that we want our language to recognize as important. Analogous to a human-language "word"
- **token** - A lexeme, bundled with some other useful contextual information

## Chapter 5 - Representing Code

[Copied from textbook]

| Terminology | Lexical grammar | Syntactic grammar |
| - | - | - |
| The "alphabet" is... | Characters | Tokens |
| A "string" is... | Lexeme or token | Expression |
| It's implemented by the... | Scanner | Parser | 

| Term | Definition |
| - | - |
| production | A grammar rule (i.e. a rule that _produces_ strings in the grammar) |
| derivation | A string that is generated based on a production |
| production head | The name of the production |
| production body | Describes what the production generates |
| terminal | A term with no more production rules to resolve |
| nonterminal | reference to another production |

## Chapter 6 - Parsing Expressions 

- *Operator Precedence* - determines which operator is evaluated first in an expression containing a mixture of different operators. e.g. 6 / 3 - 1 resolves to (6 / 3) - 1

- *Operator Associativity* - determines which operator is evaluated first in a series containing the same operators. e.g.  a = b = c resolves a = (b = c)

- *Recursive Descent* - Top-down parser. Starts with the "outermost" grammar rule and works its way into the nested subexpressions before finally reaching the leaves of the syntax tree. Contrast with bottom-up parsers (e.g. LR) which start with primary expressions and compose them into larger and larger chunks of syntax

| Expression Grammar for Lox parser| |
| - | - |
| expression | -> equality |
| equality | -> comparison ( ( "!=" \| "==" ) comparison )* ; |
| comparison | -> term ( ( ">" \| ">=" \| "<" \| "<=" ) term )* ; |
| term | -> factor ( ( "-" \| "+" ) factor )* ; |
| factor | -> unary ( ( "/" \| "\*" ) unary )* ; |
| unary  | -> ("!" \| "-" ) unary \| primary ; |
| primary | -> NUMBER \| STRING \| "true" \| "false" \| "nil" \| "(" expression ")" ; |

- *Error recovery* - the way a parser responds to an error, and then continues to look for later errors (as opposed to giving up after the first error it finds, or declaring the remainder of the file to be an error as well)
- *Synchronization* - one way a parser can recover from an error. Involves discarding tokens until it reaches a token that is allowed to appear in the current rule

## Chapter 7 - Evaluating Expressions

- "In Lox, values are created by **literals**, computed by **expressions**, and stored in **variables**.

| Lox types | Java representation |
| - | - |
| Any Lox value | Object |
| `nil` | `null` |
| Boolean | Boolean |
| number | Double |
| string | String |

- **Literals** - "the leaves of an expression tree, the atomic bits of syntax that all other expressions are composed of...a _bit of syntax_ that produces a value

### Challenges

1. Allowing comparisons on types other than numbers could be useful. The operators might have a reasonable interpretation for strings. Even comparisons among mixed types, like 3 < "pancake" could be handy to enable things like ordered collections of heterogeneous types. Or it could simply lead to bugs and confusion.

- I think I would only overload the `==` and `!=` operators for other object types, because one can figure out if strings are equal, for example. Javascript automatically casts types, and I think allows comparison of strings using `<` and `>`, which is not intuitive. I think it creates a lot of confusion. On the other hand, Rust makes it difficult to compare anything other than primitives. One needs to manually define traits for any custom types, for example. I kind of like that approach, but it can get tedious.

2. Extend `visitBinaryExpr()` to allow strings and numbers to be combined

- Done :)

3.  What happens right now if you divide a number by zero? What do you think should happen? Justify your choice. How do other languages you know handle division by zero, and why do they make the choices they do? Change the implementation in visitBinaryExpr() to detect and report a runtime error for this case.

- Dividing by zero is a mathematical error. The value should be a RuntimeError, with information about what exactly the error is.
- Done :)