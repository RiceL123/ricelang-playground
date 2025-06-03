# About

## Contents

## Introduction
This page is a high level run down on how I turned this idea into a completed personal project.

This playground was conceived to provide an accessible way to try out the compiler for my programming language, RiceLang. Its creation can be categorised into 4 main sections.

- [Compiler](#compiler)
- [Backend](#backend)
- [Frontend](#frontend)
- [Wasm](#wasm)

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
```

### Scanner
The scanner takes the source code and return tokens. It will return tokens according to the [grammar](https://ricelang-playground.vercel.app/language-definition#grammar) and detect any syntactical errors. This means it will ignore comments, tokenize numbers according to the longest match, detect keywords and enforce rules like local variable declarations being at the top of a compound statement. Additionally, it will store information regarding the token's position (line and column number) to allow users to debug their RiceLang code. It does this one character at a time choosing between either looking ahead or consuming the character to add to the current token's spelling.

Rather than turning the entire source code into an array of tokens in one pass, it instead provides a `getToken` method which the parser can call as it builds the AST. In this way the code can be streamed and be more memory efficient.
### Parser
The parser is an [LL(1) recursive parser](https://wikipedia.org/wiki/LL_parser). It will take the tokens from the scanner and generate an Abstract Syntax Tree (AST). An AST is an immediate tree-like representation of the program that defines how the program is to be executed.

It checks that the tokens returned by the scanner are valid in the given context (like if a `while` keyword is returned, it will check that the next token is an open bracket followed by an expression and a closing bracket). While the brackets are required syntactically, they are not necessary in the AST as it encodes this information within its structure.

The parser will also encode the precedence of instructions of the program for various statements. It does this by recursively building the tree according to the grammar and putting operations with higher precedence closer to the leaf nodes.

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

As the AST is generated with all its nodes, a `Visitor` interface is produced to allow further modification and reading of this intermediate state.
### Type Checker
The type checker, as the name implies, validates the types in the AST generated by the parser. The AST can be accessed with the `Visitor` interface that provides a way to visit all the possible types of nodes. In one pass, the checker ensures that functions are called with the correct number and type of arguments, operations are applied to valid types, conditionals for `if` and `while` statements are booleans and the such are checked.

As it is checking, it will also do type coercions from `int` to `floats` for assignments, mixed binary expressions, function calls and return statements. This is done by passing around inherited and synthesised [attributes](https://wikipedia.org/wiki/Attribute_grammar) while implementing the `Visitor` interface.

```ricelang
int i = 2;
float f = i; // i is converted 2.0
int i_2 = f; // Error: cannot convert from float to int
```

Specifically, a float declaration with an int would change the AST by adding an unary expression with the `i2f` operation. Below shows two charts before and after type coercion.

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
	a[LocalVarDecl]
    a-->b[float]
    a-->c[my_float]
    a-->d[IntExpr]
    d-->e[5]
    
	z[LocalVarDecl]
    z-->y[float]
    z-->x[my_float]
    z-->w[FloatExpr]
    w-->v[UnaryExpr]
    v-->u[i2f]
    v-->t[IntExpr]
    t-->s[5]
```

As the coercion is done, changing the overloaded operators to their respective type is also done. This is because instructions like the addition of 2 integers verses 2 floats requires different Java byte code instructions. `1 + 2` and `1.0 + 2.0` both use the `+` operator. They would be changed to `1 i+ 2` and `1.0 f+ 2.0` respectively to disambiguate the operator.

The checker also ensures scope rules are adhered to while linking identifiers of functions and variables to their declarations by maintaining a [symbol table](https://wikipedia.org/wiki/Symbol_table) during the pass. This includes guaranteeing that there are no duplicate local variable declarations in the same scope and such. Furthermore, it will also do some miscellaneous decoration like when an array declaration has a empty subscript, it will use the length of the array initializer or if a `for` loop has an empty conditional it will insert a `true` literal.
### Code Generator
The Code Generator takes the decorated AST from the checker and assumes that it is fully valid so that Jasmin source code can be generated. [Jasmin](https://jasmin.sourceforge.net/) is an assembler for the Java virtual machine meaning that it can take the source code and turn it into a Java class file. It is written as a [stack machine](https://wikipedia.org/wiki/Stack_machine) where you can load things like array pointers, variables and constants onto the stack and operate on them.

The Code Generator also implements the `Visitor` class converts the meaning held in the AST (like the precedence and associativity) and generates the assembler code.

The following RiceLang code
```ricelang
while (true) putStringLn("hello");
```

Would translate to something along these lines
```jasmin
beginWhileLabel:
	iconst_1              ; true
	ifeq endWhileLabel    ; if false goto Label

	; load java's printStream and "hello" onto the stack in order to print
	getstatic java.lang.System.out Ljava.io.PrintStream;
	ldc "hello"

	; invoking println consumes the PrintStream and the "hello" so
	; that nothing is left on the stack afterwards
	invokevirtual java.io.PrintStream.println(Ljava.lang.String;)V
	
	goto beginWhileLabel  ; loop to start again
endWhileLabel:
```

The code generation process is where behaviour like short circuiting, cascading assignment is actually done. However, all these operations on the stack machine require declarations of the maximum stack height and number of local variables so that the amount of memory that needs to be used is known before running the code. This information is calculated by simulating pushes and pops on the stack with a `frame` object that gets pass around the `Visitor` class to eventually be used by the function declaration. 

The generator has been designed do produce a Java class file with an initialiser for doing some setup and a main method that maps to the RiceLang source code main function. In the initialiser, a global variables are declared as static variables of that class while also adding a global scanner for any `getInt` or `getFloat` class to read stdin. It will define the functions in the RiceLang program as methods in the class.

With this this Object class, it is ready to be turned into a Java class file by the Jasmin assembler.
### JVM
Once a Java class file is generated by the assembler, it is as simple as running `java file` in the terminal. This is because I specifically chose to only use built in Java libraries like `java.io` and `java.util` so that no meddling of environment classpaths need to be done. 

At this stage one would typically need to compile RiceLang code to Jasmin assembler code, compile that to a Java class file and then run that file on the JVM. So I migrated the project to [Gradle](https://gradle.org/) and used made some `build.gradle` tasks for running RiceLang code on the JVM in a single command.

Overall, the compiler was complete in the span of about 10 weeks! It worked perfectly fine on any JVM to produce cool programs like a gcd calculator, a Mandelbrot set and the such thanks to the '[Write once, run anywhere](https://wikipedia.org/wiki/Write_once,_run_anywhere)' philosophy of Java. Though complete for terminal use, it was time to make it more accessible by deploying it to the web.
## Backend
As the compiler was written in Java, it was only natural that Java based backend should be reached for. This lead me to the very popular [Spring Boot](https://docs.spring.io/spring-boot/) in order to create a simple [RESTful](https://wikipedia.org/wiki/REST) web service.

I simply made an application, chucked on some decorators to accept `POST` requests with the source code in a JSON body. The server would then compile the code to Java byte code and then run it on the JVM piping the output so that it could be returned as a response.

Making a `/run` route was so simple, I decided to also make routes like `/jasmin` for just returning the Jasmin assembler code  and `/ast` for returning the AST.

There were multiple considerations for how I could approach sending an AST response. In the end, I landed on generating [MermaidJS](https://mermaid.js.org/) due to its conciseness, clarity and popularity being used in software like GitHub and [Obsidian](https://obsidian.md). This meant that I could send less over the wire to improve performance and have the frontend render the visualisation. So I created a simple class that implemented the `Visitor` pattern which would generate the AST.

With the server working locally, I decided to add a [Dockerfile](https://www.docker.com/) to ensure that the server would be deployed with [jdk17](https://openjdk.org/projects/jdk/17/) so that it could run the compiled Java byte code and build the server to eventually expose the routes to the world. To complete it, I decided to use [render](https://render.com/) to host and deploy the server.
## Frontend
For the building the frontend, I decided to take it easy and stick with what I already knew; [React](https://react.dev/) and [Tailwind](https://tailwindcss.com/). So, I landed on the extremely popular [Nextjs](https://nextjs.org/) and [Shadcn](https://ui.shadcn.com/) to build out my frontend and provide a user facing website to showcase my compiler. What better way to do that than to make a playground.
### Playground
A code playground is a place to write and run code online without having to do any manual setup yourself. It consists of 2 main parts; the editor and the output. While I could have made something along the lines of existing playgrounds like the [rust one](https://play.rust-lang.org/), I wanted experience to feel cooler. So, taking inspiration from some [linux ricing](https://www.reddit.com/r/unixporn/), I decided to design the website to look like it was [tiling window manager](https://wikipedia.org/wiki/Tiling_window_manager).

As in typical with the design of [React](https://react.dev/) components, you have a '*smart*' component for maintaining the data and dumb components for simple rendering. The homepage would be the smart component and the editor and output would be the dumb components. It would then provide the child components with the data and methods to use and manipulate the data to allow for both modular code and sharing of state.

```mermaid
flowchart TD
a[Smart Homepage Component
contains srcCode, setData method, output and compile method]
a--data,setData-->b1[Dumb Editor Component
renders editor and updates srcCode using setSrcCode]
a-- output-->b2[Dumb Output Component
renders output]
```

The window with the editor would use [Monaco](https://microsoft.github.io/monaco-editor/) the editor that powers [VS Code](https://code.visualstudio.com/). This is because in addition to have syntax highlighting for custom languages, it also had [VS Code](https://code.visualstudio.com/) shortcuts like `Ctrl` + `D` for making multiple selections. Because [Monaco](https://microsoft.github.io/monaco-editor/) requires a definition for a custom language, regular expressions were used to match identifiers, keywords, string literals, numbers and the such. With a little bit of customisation and hooks for setting source code, the editor half was complete and able to switch between default [VS Code](https://code.visualstudio.com/) theme based on the theme context.

The output window was quite simple for text output; wait for the parent to make a fetch to the backend, and then render the output in monospaced font. As for the rendering of the [MermaidJS](https://mermaid.js.org) source code to an AST visualisation, just using the `render` function as provided by the [npm package](https://npmjs.com/package/mermaid) was all that was needed.
### Wasm
While the [Spring Boot](https://spring.io/projects/spring-boot) backend worked correctly, the idea of removing packet delay / latency altogether seemed extremely desirable especially with [render](https://render.com) services spinning down after periods of inactivity. So, I decided to turn my compiler into [Web Assembly](https://webassembly.org/) with the help of [TeaVM](https://teavm.org/) which is an ahead-of-time compiler that converts Java into Wasm (specifically Wasm Garbage Collection (WasmGC)).

```mermaid
sequenceDiagram
    participant Browser
    participant Server
    
    Browser->>Server: HTTP POST api/run (fetch)
    Server->>Browser: HTTP 200 OK + data
	
	Note left of Browser: Wasm eliminates the need <br/> to hit server

	Browser->>Browser: Wasm
```

Because [Wasm](https://webassembly.org/) runtime doesn't have access to I/O, it meant that the main `/run` route wouldn't be able to be converted directly. So, I wrote a JavaScript transpiler by implementing the `Visitor` class yet again to produce vanilla JS so that it could run in the browser. I used the `prompt` function for stdin and just saved stdout to an array to later be used to `console.log`.  [TeaVM](https://teavm.org/) made this quite lovely as all I had to do was change some logic to only use compatible Java libraries and then simply add some decorators to some exported binding functions. Then I would just copy the binaries over to the frontend and have them loaded once in a [context](https://react.dev/reference/react/createContext) to be used throughout the [React](https://react.dev/) app.

This worked beautifully with both [SpiderMonkey](https://spidermonkey.dev/) for Firefox and [V8](https://v8.dev/) for Chromium while having a minimal affect on TTI (Time to Interactive) as the binary was only `800kb`. This gave an average `30x` improvement in performance from `1500ms` to `30ms` for a typical `/run`. If the backend had spun down, performance increases upwards of `3000x` could be achieved (`100000ms` to `30ms`). With such an improvement, I made Wasm the default with an optional legacy route if true JVM compilation was desired.

However, [WebKit](https://webkit.org/) for Safari and all browsers in iOS was not able to properly load the WasmGC module (A [TeaVM issue](https://github.com/konsoletyper/teavm/issues/1028) seems to blame it on Apple). Because of this, I made a simple fallback to the [Spring Boot](https://spring.io/projects/spring-boot) backend if any errors were found in the [Wasm](https://webassembly.org/) initialisation.

Interestingly, I could have also compiled to [Wasm WAT](https://webassembly.github.io/spec/core/exec/index.html) for it also is a stack machine like [Jasmin](https://jasmin.sourceforge.net/) meaning that it wouldn't be totally foreign as a task to complete. However, I have left this as an exercise for the reader.
### Other pages
A good language definition / documentation is crucial for users to be able to understand a language. So, I decided to write one in [markdown](https://markdownguide.org/) and then use [remark](https://remark.js.org/) to turn it into html.

I wrote formal definitions of RiceLang syntax in [Latex](https://wikipedia.org/wiki/Latex) delimited by `$$` signs and had [Katex](https://katex.org/) convert it to html. As examples are also great in documentation, I had created a custom [highlight.js](https://highlightjs.org/) definition to incorporate syntax highlighting when showing RiceLang code snippets. It was also important that my documentation would purposefully incorporate redundancy. This would be so that readers would be able to read a single section and understand it without having to reference other parts of the documentation. While I did have to implement some custom logic for code styles and opening external links in new tabs, the [unified](https://unifiedjs.com/) interface was lovely to work with producing the following

```javascript
const html = await unified()
  .use(remarkParse)  // parse the markdown
  .use(remarkMath)   // identify latex blocks
  .use(remarkToc)    // populate table of contents
  .use(remarkRehype) // convert to html ast
  .use(rehypeSlug)   // add ids to headers
  .use(rehypeHighlight, { languages: { java, javascript, ricelang, jasmin }) 
  .use(rehypeKatex, { output: 'html', trust: true }) // render latex
  .use(addLanguageDataAttribute)   // add data-label for css ::before label
  .use(openExternalLinksInNewTabs) // add target="_blank" for links
  .use(rehypeStringify)
  .process(await fs.readFile('./about.md', 'utf8'););
```

With this, I was able to write the documentation in [markdown](https://markdownguide.org/) / [Obsidian](https://obsidian.md) and have it compiled to html at build time with static site generation automatically thanks to the [Nextjs app router](https://nextjs.org/docs/app).

Similar to the Language Definition page, I wrote this about page in [markdown](https://markdownguide.org/) / [Obsidian](https://obsidian.md) and then had it converted to html at build time (although the [Mermaid](https://mermaid.js.org/) diagrams are hydrated on client side). This made the writing process so much more enjoyable.
## Conclusion
The project took around 14 weeks to complete and was very rewarding. I learnt a lot about compilers, Wasm and everything else required to build the playground. Overall, a great project for the books.
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

All the code is available on my github at [https://github.com/RiceL123/ricelang-playground](https://github.com/RiceL123/ricelang-playground).

If you would like more of a personal take with my learnings and pain points while developing the project you can check [my blog](https://ricel123.vercel.app/)~!
