# The RiceLang Language Definition

## Contents

## Introduction

RiceLang is a simple C/Java like programming language created by yours truly: RiceL123. From source code to an [AST](https://wikipedia.org/wiki/Abstract_syntax_tree), RiceLang programs can compile to Java byte code or transpile to JavaScript.

An example of a simple RiceLang program is shown below.
```ricelang
int main() {
	putStringLn("T-T");
	byebye 0;
}
```

## Grammar
The following conventions are adopted for defining grammar rules for syntax.
- Terminal symbols: $\textbf{bold}$
- Nonterminal symbols: $\textit{italics}$
- symbols can be grouped with brackets `(` `)` (e.g. $(~A~B~)$)
- $A*$ is a sequence of 0 or more iterations of $A$
- $A?$ is an optional occurrence of $A$
- $A|B$ represents two possible productions being $A$ or $B$
- Productions are written $A\rightarrow B_1~|~\text{...}~|~B_n$

$$
\begin{aligned}
\textit{program}\rightarrow&~(~\textit{func-decl}~|~\textit{var-decl}~)*\\
\href{#functions}{\textit{func-decl}}\rightarrow&~\textit{type}~\textit{identifier}~\textit{para-list}~\textit{compound-stmt}\\
\textit{para-list}\rightarrow&~\textbf{(}~\textit{proper-para-list}?~\textbf{)}\\
\textit{proper-para-list}\rightarrow&~\textit{para-decl}~(~\textbf{,}~\textit{para-decl}~)*\\
\textit{para-decl}\rightarrow&~\textit{type}~\textit{declarator}\\
\href{#variables}{\textit{var-decl}}\rightarrow&~\textit{type}~\textit{init-declarator-list}~\textbf{;}\\
\textit{init-declarator-list}\rightarrow&~ \textit{init-declarator}~(~\textbf{,}~\textit{init-declarator}~)*\\
\textit{init-declarator}\rightarrow&~\textit{declarator}~(~\textbf{=}~\textit{initialiser}~)?\\
\textit{declarator}\rightarrow&~\textit{identifier}\\
|&~\textit{identifier}~\textbf{[}~\textbf{INTLITERAL}?~\textbf{]}\\
\textit{initialiser}\rightarrow&~ \textit{expr}\\
|&~\textbf{\{}~\textit{expr}~(~\textbf{,}~\textit{expr}~)*~\textbf{\}}\\
\href{#basic-types}{\textit{type}}\rightarrow&~ \textbf{void}~|~\href{#boolean}{\textbf{boolean}}~|~\href{#int}{\textbf{int}}~|~\href{#float}{\textbf{float}}\\
\href{#identifiers}{\textit{identifier}}\rightarrow&~\textbf{ID}\\
\textit{compound-stmt}\rightarrow&~\textbf{\{}~\textit{var-decl}*~\textit{stmt}*~\textbf{\}}\\
\href{#statements}{\textit{stmt}}\rightarrow&~\textit{compound-stmt}\\
|&~\textit{if-stmt}\\
|&~\textit{for-stmt}\\
|&~\textit{while-stmt}\\
|&~\textit{break-stmt}\\
|&~\textit{continue-stmt}\\
|&~\textit{return-stmt}\\
\href{#if}{\textit{if-stmt}}\rightarrow&~\textbf{if}~\textbf{(}~\textit{expr}~\textbf{)}~\textit{stmt}~(~\textbf{else}~\textit{stmt}~)?\\
\href{#for}{\textit{for-stmt}}\rightarrow&~\textbf{for}~\textbf{(}~\textit{expr}?~\textbf{;}~\textit{expr}?~\textbf{;}~\textit{expr}?~\textbf{)}~\textit{stmt}\\
\href{#while}{\textit{while-stmt}}\rightarrow&~\textbf{while}~\textbf{(}~\textit{expr}~\textbf{)}~\textit{stmt}\\
\href{#break}{\textit{break-stmt}}\rightarrow&~\textbf{break}~\textbf{;}\\
\href{#continue}{\textit{continue-stmt}}\rightarrow&~\textbf{continue}~\textbf{;}\\
\href{#byebye}{\textit{return-stmt}}\rightarrow&~\textbf{byebye}~\textit{expr}?~\textbf{;}\\
\href{#expression-statements}{\textit{expr-stmt}}\rightarrow&~\textbf{expr}?~\textbf{;}\\
\textit{expr}\rightarrow&~\textit{assignment-expr}\\
\textit{assignment-expr}\rightarrow&~(~\textit{cond-or-expr}~\textbf{=}~)*\textit{cond-or-expr}\\
\textit{cond-or-expr}\rightarrow&~\textit{cond-or-expr}\\
|&~\textit{cond-or-expr}~\textbf{||}~\textit{cond-and-expr}\\
\textit{cond-and-expr}\rightarrow&~\textit{equality-expr}\\
|&~\textit{cond-and-expr}~\textbf{\&\&}~\textit{equality-expr}\\
\textit{equality-expr}\rightarrow&~\textit{rel-expr}\\
|&~\textit{equality-expr}~\textbf{==}~\textit{rel-expr}\\
|&~\textit{equality-expr}~\textbf{!=}~\textit{rel-expr}\\
\textit{rel-expr}\rightarrow&~\textit{additive-expr}\\
|&~\textit{rel-expr}~\textbf{<}~\textit{additive-expr}\\
|&~\textit{rel-expr}~\textbf{<=}~\textit{additive-expr}\\
|&~\textit{rel-expr}~\textbf{>}~\textit{additive-expr}\\
|&~\textit{rel-expr}~\textbf{>=}~\textit{additive-expr}\\
\textit{additive-expr}\rightarrow&~\textit{mutliplicative-expr}\\
|&~\textit{additive-expr}~\textbf{+}~\textit{mutliplicative-expr}\\
|&~\textit{additive-expr}~\textbf{-}~\textit{mutliplicative-expr}\\
\textit{multiplicative-expr}\rightarrow&~\textit{unary-expr}\\
|&~\textit{multiplicative-expr}~\textbf{*}~\textit{unary-expr}\\
|&~\textit{multiplicative-expr}~\textbf{/}~\textit{unary-expr}\\
\textit{unary-expr}\rightarrow&~\textit{primary-expr}\\
|&~\textbf{+}~\textit{unary-expr}\\
|&~\textbf{-}~\textit{unary-expr}\\
|&~\textbf{!}~\textit{unary-expr}\\
\textit{primary-expr}\rightarrow&~\textit{identifier}~\textit{arg-list}?\\
|&~\textit{identifier}~\textbf{[}~\textit{expr}~\textbf{]}\\
|&~\textbf{(}~\textit{expr}~\textbf{)}\\
|&~\textbf{INTLITERAL}\\
|&~\textbf{FLOATLITERAL}\\
|&~\textbf{BOOLLITERAL}\\
|&~\textbf{STINGLITERAL}\\
\textit{arg-list}\rightarrow&~\textbf{(}~\textit{proper-arg-list}?~\textbf{)}\\
\textit{proper-arg-list}\rightarrow&~\textit{arg}~(~\textbf{,}~\textit{arg}~)*\\
\textit{arg}\rightarrow&~\textit{expr}\\
\end{aligned}
$$

## Program Structure
A RiceLang program is a collection of function and variable declarations in a single file.

The entry point of the program is the `main` function which must have a return type of `int`. Due to scope rules, it will usually be the last function in a program. `main` cannot call itself recursively.

### Comments
RiceLang supports single line comments and multi-line comments. It does not support nesting of multi line comments.
```ricelang
// this is a single line comment
/*
this is a multi line comment
*/
```
All comments are ignored by the compiler.
### Separators
White space (like new lines, tabs or spaces) as well as the following can be used as separators
- `{`, `}`, `(`, `)`, `[`, `]`, `;`, `,`
When the AST is generated, all separator tokens and white space is omitted.
### Identifiers
Identifiers are used to define both variables and function names and must be 1 or more characters long. They start with a letter or underscore and end with a letter, number or underscore.

$$
\begin{aligned}
\textbf{ID}\rightarrow&~ \textit{start-char}~\textit{end-char}*\\
\textit{start-char}\rightarrow&~\textbf{A}~|~\textbf{...}~|~\textbf{Z}~|~\textbf{a}~|~\textbf{...}~|~\textbf{z}~|~\textbf{\_}\\
\textit{end-char}\rightarrow&~\textit{start-char}~|~\textbf{0}~|~\textbf{1}~|~\textbf{...}~|~\textbf{9}
\end{aligned}
$$

## Operators
There are 14 operators. Ordered from highest to lowest precedence with their associativity they are:
1. `+`, `-`, `!` (right-associative) // `+` and `-` as unary operators
2.  `*`, `/` (left-associative)
3. `+`, `-` (left-associative) // as binary operators
4. `<`, `<=`, `>`, `>=` (left-associative)
5. `==`, `!=` (left-associative)
6. `&&` (left-associative)
7. `||` (left-associative)
8. `=` (right-associative)

## Basic Types
RiceLang programs operate on 3 primitive data types with operators to form expressions.

### int
An $\textbf{INTLITERAL}$ is a decimal number of at least 1 digit. They are of type `int`.

$$
\begin{aligned}
\textbf{INTLITERAL}&\rightarrow (~ \textbf{0} ~|~ \textbf{1} ~|~ \text{...} ~|~ \textbf{9} ~)*
\end{aligned}
$$

The value of an `int` type is a 32-bit signed integer. They can be operated on by
- `+`, `-`, `*`, `/` to produce  `int` values
- `<`, `>`, `<=`, `>=`, `==`, `!=` to produce `boolean` values

```ricelang
int i = 3;
int j = -2;
int k = i / j; // integer division: -2
boolean b = i > j; // true
```

### float
A $\textbf{FLOATLITERAL}$ is made up of a whole-number, decimal point, a fractional part and an exponent. It is of type `float`.

$$
\begin{aligned}
\textbf{FLOATLITERAL}\rightarrow&~\textit{digit}*~\textit{fraction}~\textit{exponent}? \\
|&~\textit{digit*}~\textbf{.}\\
|&~\textit{digit*}~\textbf{.}?~\textit{exponent}\\
\textit{digit}\rightarrow&~\textbf{0} ~|~ \textbf{1} ~|~ \text{...} ~|~ \textbf{9}\\
\textit{fraction}\rightarrow&~\textbf{.}~\textit{digit}+\\
\textit{exponent}\rightarrow&~(~\textbf{E}~|~\textbf{e}~)~(~\textbf{+}~|~\textbf{-}~)?~\textit{digit}+
\end{aligned}
$$

The value of a `float` type is a single-precision 32-bit [IEEE 754](https://wikipedia.org/wiki/IEEE_754) floating point. They can be operated on by
- `+`, `-`, `*`, `/` to produce  `float` values
- `<`, `>`, `<=`, `>=`, `==`, `!=` to produce `boolean` values

Coercion on an `int` to a `float` will automatically occur for in expressions including for `byebye` statements, declarations and functions arguments.

```ricelang
float i = 3.;
int j = -2;
float k = i / j; // -1.5 (j converted to -2.0)
boolean b = i > j; // true (j is converted to -2.0)
```
### boolean
A $\textbf{BOOLEANLITERAL}$ is either true or false and is of type `boolean`.

$$
\textbf{BOOLEANLITERAL}\rightarrow \textbf{true} ~|~ \textbf{false}
$$

Although technically `boolean` only needs 1 bit, they will typically use a whole byte. They can be operated on by
- `!`, `!=`, `==`, `&&`, `||` to produce `boolean` values

When `&&` or `||` are used, they are evaluated left to right and will try to [short-circuit](https://wikipedia.org/wiki/Short-circuit_evaluation).

```ricelang
boolean i = true;
boolean j = !true;
boolean k = i && j; // false
boolean l = false && boolFunc(); // shortcircuit: boolFunc wont be called
```
### string literals
A $\textbf{STRINGLITERAL}$ is zero or more characters surrounded by quotation marks.

$$
\begin{aligned}
\textbf{STRINGLITERAL} \rightarrow&~ \textbf{``}~\textit{character}*~\textbf{''}\\
\end{aligned}
$$

$\textit{character}$ refers to [ASCII](https://wikipedia.org/wiki/ASCII) characters. If non-ASCII / [UTF-8](https://wikipedia.org/wiki/UTF-8) characters are used, they may be read as single bytes. Escape sequences like `\n` and `\"` are also supported. RiceLang has no `String` type to use; string literals can only be used in the built-in functions `putString` and `putStringLn`. Strings cannot span more than 1 line.

```ricelang
putString("Hewwo world\n");
putStringLn("Byebye world");
```
## Arrays
Ricelang only supports 1-dimensional arrays of type `int`, `float` and `boolean`. Arrays have a fixed size determined by an $\textbf{INTLITERAL}$ in the subscript or by the length of an array initialiser ($\textbf{\{}~\textit{expr}~(~\textbf{,}~\textit{expr}~)*~\textbf{\}}$). Arrays are filled with default values meaning `int` and `float` arrays will be filled with zeros and `boolean` arrays filled with `false`.

```ricelang
int a[2];                   // default array: [ 0, 0 ]
float a[2];                 // default array: [ 0.0, 0.0 ]
boolean a[2];               // default array: [ false, false ]
int b[5] = { 1, 2 };        // zeroed array: [ 1, 2, 0, 0, 0 ]
int c[] = { 1, 2 };         // empty subscript: [ 1, 2 ]
int d[];                    // Error: requires either size or initialiser
int d[1] = { 1, 2 };        // Error: initialiser > size
float e[] = { 1, 2, 3.14 }; // coercion to declared type: [ 1.0, 2.0, 3.14 ]
```

Arrays themselves can be passed as an argument to a function call (one will typically also pass in the array size). Arrays are passed to functions as pointers so modifications on them by the callee can be observed by the caller. Only element access with a subscript allows for valid manipulation of the array.

```ricelang
int increment_all(int x[], int size) {
	int i; // defaults to zero
	for(; i < size; i = i + 1) {
		x[i] = x[i] + 1;
	}
}

int main() {
	int x[] = { 1, 2 };
	increment_all(x, 2); // x is now [ 2, 3 ]
	putIntLn(x[1] + 1);  // 4
	x + 1;               // Error
}
```

## Variables
There are 3 types of variables being, global variables, local variables and function parameters. Global and local variables are similar in that they are both declared by a $\textit{type}$, $\textit{identifier}$ and optionally a list of declarations.

$$
\begin{aligned}\textit{var-decl}\rightarrow&~\textit{type}~\textit{init-declarator-list}~\textbf{;}\\
\textit{init-declarator-list}\rightarrow&~ \textit{init-declarator}~(~\textbf{,}~\textit{init-declarator}~)*\\
\textit{init-declarator}\rightarrow&~\textit{declarator}~(~\textbf{=}~\textit{initialiser}~)?\\
\textit{declarator}\rightarrow&~\textit{identifier}\end{aligned}
$$

Local variable declarations in compound statements must come before any statements.

Function parameters act like local variable declarations at the beginning of a function's compound statement.

```ricelang
int a = 1;       // global variable
int fun(int b) { // function parameter - same scope as c
	int c;       // local variable
	int d, e = 2, f[] = { 1, 2 }; // int d; int e = 2; int f[] = { 1, 2 };
}
```

Similar to arrays, variables are also initialised to default values if unspecified; `int` and `float` default to `0` and `booleans` default to `false`.

## Statements
Statements can either be just a single statement or a compound statement that contains zero or more variable declarations followed by zero or more statements.

$$
\begin{aligned}\textit{stmt}\rightarrow&~\textit{compound-stmt}\\
\textit{compound-stmt}\rightarrow&~\textbf{\{}~\textit{var-decl}*~\textit{stmt}*~\textbf{\}}
\end{aligned}
$$

Because there is no hoisting one may be inclined to add compound statements to have increased locality of variable declaration and use. (Or you could just make a deal with it)

```ricelang
int main() {
	int a = 1;
	// int b = 2; // decl is further away
	for (;a < 10; a = a + 1)
		putIntLn(a);

	{ // introduce a new compound statement
		// b decl is closer
		int b = 2; 
		for (; b < 10; b = b + 1)
			putIntLn(b);
	}
}
```

### If
If statements control the flow of a program based on the evaluation of its expression.

$$
\textit{if-stmt}\rightarrow\textbf{if}~\textbf{(}~\textit{expr}~\textbf{)}~\textit{stmt}~(~\textbf{else}~\textit{stmt}~)?
$$

When multiple if statements have a single `else` statement, the `else` is attached to the innermost if.
```ricelang
// the following nested if statements with a single else are equivalent
if (1 < 2) if (3 == 5) putString("nani"); else putString("hello");
if (1 < 2) {
	if (3 == 5) putString("nani");
	else putString("hello");
}
```

### While
If a while statement's $\textit{expr}$ is $\textbf{true}$, it will continuously execute its $\textit{stmt}$ and re-evaluate its $\textit{expr}$ until it is $\textbf{false}$.

$$
\textit{while-stmt}\rightarrow\textbf{while}~\textbf{(}~\textit{expr}~\textbf{)}~\textit{stmt}
$$

```ricelang
while (i < 5) {
	putIntLn(i);
	i = i + 1;
}
```

### For
For statements are equivalent to while statements with $\textit{expr1}$ executing once before entering and $\textit{expr3}$ executing every loop after the $\textit{stmt}$. There is an exception for the behaviour of $\textbf{continue}$; control passes to $\textit{expr3}$ instead of straight to the conditional.

$$
\textit{for-stmt}\rightarrow\textbf{for}~\textbf{(}~\textit{expr1}?~\textbf{;}~\textit{expr2}?~\textbf{;}~\textit{expr3}?~\textbf{)}~\textit{stmt}
$$

If $\textit{expr2}$ is omitted, it is decorated with $\textbf{true}$ resulting in an infinite loop.
```ricelang
for (i = 0; i < 5; i = i + 1) {
	putIntLn(i);
	if (i == 4) continue; // will not loop infinitely as expr3 is executed
}
for (;;) {} // infinite loop
```

### Break
$\textbf{break}$ statements exit the control of the current loop.

$$
\textit{break-stmt}\rightarrow\textbf{break}~\textbf{;}
$$

```ricelang
while (true) {
	break;
}
// control is now here
```
### Continue
$\textbf{continue}$ statements pass the control back to the start of the loop or to $\textit{expr3}$ in the case of for loops.

$$
\textit{continue-stmt}\rightarrow\textbf{continue}~\textbf{;}
$$

```ricelang
while (true) {
	continue; // pass control back to evaluate condition
	putStringLn("hello"); // will not execute
}
```
### Byebye
$\textbf{byebye}$ acts as a return statement which transfers control back to the caller of the function that contains it.

$$
\textit{return-stmt}\rightarrow\textbf{byebye}~\textit{expr}?~\textbf{;}
$$

$\textbf{byebye}$ without an $\textit{expr}$ must be in a void function. 
$\textbf{byebye}$ with an $\textit{expr}$ must have the $\textit{expr}$ assignable to the function type.

RiceLang does not do data-flow analysis and as such, puts the burden of ensuring all possible branches have a `byebye` onto the user. A simple solution would be to include a `byebye` at the end of the function.
```ricelang
int fun(boolean b) {
    if (b) {
        byebye 1; // no run time error
    } else {
        putStringLn("no byebye"); // run time error due to no byebye int;
    }
    // could just have a byebye here
}
```
### Expression Statements
An expression statement is just an expression followed by a semicolon. This will most typically be used for expressions that are assignments or function calls.

$$
\textit{expr-stmt}\rightarrow\textbf{expr}?~\textbf{;}
$$

```ricelang
myfunc();
i = 0;
```

## Scope rules
Scope rules govern declarations and their uses.
- No identifier can defined more than once in the same block (this means function parameters must not collide with the local variable declarations in a function's body)
- For every occurrence of an identifier, there must be some declaration in the same or an outer scope
- An occurrence of an identifier will use the declaration that is the inner most scope that is equal to greater than its own scope (this produces the possibility of scope holes)
- Every compound statement (and thus every function) forms a nested scope
- Functions (including the [built-ins](#built-in)) and global variables are all defined in the outermost scope

```ricelang
int main() {
	int main = 1;
	{
		int main = 2;
		putIntLn(main); // prints 2
	}
	putIntLn(main); // prints 1
}
```

## Functions
Functions in RiceLang require that they be declared before they are called. This means that one will typically find `main` at the bottom of a program. Formally, a declaration is as follows.

$$
\begin{aligned}
\textit{func-decl}\rightarrow&~\textit{type}~\textit{identifier}~\textit{para-list}~\textit{compound-stmt}\\
\textit{para-list}\rightarrow&~\textbf{(}~\textit{proper-para-list}?~\textbf{)}\\
\textit{proper-para-list}\rightarrow&~\textit{para-decl}~(~\textbf{,}~\textit{para-decl}~)*\\
\textit{para-decl}\rightarrow&~\textit{type}~\textit{declarator}\\
\end{aligned}
$$

A function call is an identifier followed by some brackets. Formally as follows.

$$
\begin{aligned}
\textit{function-call}\rightarrow&~\textit{identifier}~\textit{arg-list}\\
\textit{arg-list}\rightarrow&~\textbf{(}~\textit{proper-arg-list}?~\textbf{)}\\
\textit{proper-arg-list}\rightarrow&~\textit{arg}~(~\textbf{,}~\textit{arg}~)*\\
\textit{arg}\rightarrow&~\textit{expr}
\end{aligned}
$$

While functions support recursion, they do not support overloading.

Functions must return one of `int`, `float`, `boolean`, `void` and have a corresponding `byebye` statement. Functions cannot return arrays.

### Built-in
RiceLang consists of 11 built-in functions for I/O.

**Input functions** will block and parse a line from stdin and its value if valid. In the case of the RiceLang playground, any program that calls the built-in input functions that
- uses the legacy run command will timeout as stdin isn't available
- uses vanilla JavaScript transpilation will use the browser's `prompt()` function
```ricelang
int i = getInt(); // read and a parse a line of stdin to an int
float f = getFloat(); // as above but for float
```

**Output functions** will print a particular data type to stdout. These functions all return `void`.
```ricelang
putInt(int x);         // prints value of x to stdout
putIntLn(int x);       // prints value of x to stdout + "\n"
putFloat(float x);     // prints value of x to stdout
putFloatLn(float x);   // prints value of x to stdout + "\n"
putBool(boolean x);    // prints value of x to stdout
putBoolLn(boolean x);  // prints value of x to stdout + "\n"
putString("string literal");   // prints the string to stdout
putStringLn("string literal"); // prints the string + "\n" to stdout
```

All arguments are passed by value. This means they are copied into temporary variables and thus cannot modify the caller's arguments from within the function. As for arrays as arguments, an array pointer is passed and copied with a function call which allows for modification of the same array however doesn't allow changing of the caller's array pointer which is consistent with the pass by value behaviour.
