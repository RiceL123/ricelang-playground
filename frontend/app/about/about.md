# About

## Contents

## Introduction
This page is a high level run down on how I turned this idea into a completed personal project.

This playground was conceived to provide an accessible way to try out the compiler for my programming language, RiceLang. Its creation can be categorised into 4 main sections.

- [Compiler](#Compiler)
- [Backend](#Backend)
- [Frontend](#Frontend)
- [Wasm](#Wasm)

```mermaid
gitGraph:
	commit id:" "
	branch compiler
    checkout compiler
    commit id:"lexer"
    commit id:"parser"
    commit id:"type checker"
    commit id:"code gen"
    checkout main
    merge compiler
    commit id:"  "
    branch backend
    checkout backend
    commit id:"spring boot"
    checkout main
    merge backend
	commit id:"   "
	branch frontend
	checkout frontend
	commit id:"playground"
	checkout compiler
	branch wasm
	commit id:"TeaVM"
	checkout frontend
	merge wasm
	commit id:"language def"
	commit id:"about"
	checkout main
	merge frontend
```
## Compiler
The compiler turns RiceLang source code into Java byte code much like other popular JVM languages including [Scala](https://scala-lang.org/), [Groovy](https://groovy-lang.org/) and [Kotlin](https://kotlinlang.org/). It does this ahead-of-time in 4 steps to eventually be run on the JVM.

```mermaid
graph TD
	0[RiceLang Source Code]-->A
	A[Scanner]--Tokenize-->B
	B[Parser]--Generate AST-->C
	C[Type Checker]--Validate and Decorate-->D
	D[Code Generator]--Jasmin source code-->E
	E[run on JVM]
	click A "#Scanner"
	click B "#Scanner"
	click C "#Type Checker"
	click D "#Code Generator"
	click E "#JVM"
```

### Scanner
The scanner takes the source code and return tokens. It will return tokens according to the [grammar](https://ricelang-playground.vercel.app/language-definition#grammar) and detect any syntactical errors. This means it will ignore comments, tokenize numbers according to the longest match, detect keywords and enforce rules like local variable declarations being at the top of a compound statement. Additionally, it will store information regarding the token's position (line and column number) to allow users to debug their RiceLang code.

Rather than turning the entire source code into an array of tokens in one pass, it instead provides a `getToken` method which the parser can call as it builds the AST. In this way the code can be streamed and be more memory efficient.
### Parser
The parser is an LL(1) recursive parser. It will take the tokens from the scanner and generate an Abstract Syntax Tree (AST). An AST is an immediate representation of the program that defines how the program is to be executed.

It checks that the tokens returned by the scanner are valid in the given context (like if a `while` keyword is returned, it will check that the next token is an open bracket followed by an expression and a closing bracket). While the brackets are required syntactically, they are not necessary as the AST encodes this information within its structure.

The parser will also encode the precedence of instructions of the program for various statements. 

As an example, the code below

```ricelang
int main() {
	int i = 0;
	while (i < 5) {
		putIntLn(i);
		i = i + 1;
	}
	byebye 0;
}
```

will generate the following AST

```mermaid
%%{ init: {
  "theme": "default",
  "flowchart": {
    "nodeSpacing": 10,
    "rankSpacing": 30
  },
  "width": "100%"
} }%%
flowchart TD
    classDef small font-size:10px,padding:0px;

    1[Program]:::small
    1-->2[DeclList]:::small
    2-->3[FuncDecl]:::small
    3-->4[int]:::small
    3-->5[main]:::small
    3-->6[EmptyParaList]:::small
    3-->7[CompoundStmt]:::small
    7-->8[DeclList]:::small
    8-->9[LocalVarDecl]:::small
    9-->10[int]:::small
    9-->11[i]:::small
    9-->12[IntExpr]:::small
    12-->13[0]:::small
    8-->14[EmptyDeclList]:::small
    7-->15[StmtList]:::small
    15-->16[WhileStmt]:::small
    16-->17[BinaryExpr]:::small
    17-->18[VarExpr]:::small
    18-->19[SimpleVar]:::small
    19-->20[i]:::small
    17-->21["<"]:::small
    17-->22[IntExpr]:::small
    22-->23[5]:::small
    16-->24[CompoundStmt]:::small
    24-->25[EmptyDeclList]:::small
    24-->26[StmtList]:::small
    26-->27[ExprStmt]:::small
    27-->28[CallExpr]:::small
    28-->29[putIntLn]:::small
    28-->30[ArgList]:::small
    30-->31[Arg]:::small
    31-->32[VarExpr]:::small
    32-->33[SimpleVar]:::small
    33-->34[i]:::small
    30-->35[EmptyArgList]:::small
    26-->36[StmtList]:::small
    36-->37[ExprStmt]:::small
    37-->38[AssignExpr]:::small
    38-->39[VarExpr]:::small
    39-->40[SimpleVar]:::small
    40-->41[i]:::small
    38-->42[BinaryExpr]:::small
    42-->43[VarExpr]:::small
    43-->44[SimpleVar]:::small
    44-->45[i]:::small
    42-->46["\+"]:::small
    42-->47[IntExpr]:::small
    47-->48[1]:::small
    36-->49[EmptyStmtList]:::small
    15-->50[StmtList]:::small
    50-->51[byebye]:::small
    51-->52[IntExpr]:::small
    52-->53[0]:::small
    50-->54[EmptyStmtList]:::small
    2-->55[EmptyDeclList]:::small
```

### Type Checker
The type checker, as the name implies, validates the types in 1 pass of the AST generated by the parser. This means it ensures that functions are called with the correct number and type of arguments, the correct operations are used to for the specific types, conditionals for statements are booleans and the such are checked.
It will also ensure scope rules are adhered to and link variable and function uses to their declarations
### Code Generator

### JVM

## Backend

## Frontend

### Playground

### Wasm

### Language Def

### About

## Conclusion
Overall, there were a multitude of technologies used to get this working including

### Technologies
- [Java](https://www.java.com/) + [Gradle](https://gradle.org/)
- [Jasmin](https://jasmin.sourceforge.net/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Nextjs](https://nextjs.org/) + [React](https://react.dev/)
- [Shadcn](https://ui.shadcn.com/) + [Tailwind](https://tailwindcss.com/)
- [Monaco](https://microsoft.github.io/monaco-editor/)
- [Mermaid](https://mermaid.js.org/)
- [Markdown](https://markdownguide.org/) + [remark](https://remark.js.org/)
- [TeaVM](https://teavm.org/)

All the code is available on my github at https://github.com/RiceL123/ricelang-playground.

If you like more of a personal take on the project you can check it out on [my blog](https://ricel123.vercel.app/)~!
