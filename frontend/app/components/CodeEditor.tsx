import React, { useEffect } from "react";
import { Editor, OnChange, OnMount, useMonaco } from "@monaco-editor/react";
import { useTheme } from 'next-themes'

export default function CodeEditor({ setSourceCode, sourceCode }: { setSourceCode: (newSourceCode: string) => void, sourceCode: string }) {
  const { resolvedTheme } = useTheme();
  const monaco = useMonaco();

  const handleEditorDidMount: OnMount = (_editor, monaco) => {

    // Define the language
    monaco.languages.register({ id: "ricelang" });

    // Define syntax highlighting rules
    monaco.languages.setMonarchTokensProvider("ricelang", {
      keywords: [
        "boolean",
        "break",
        "continue",
        "else",
        "for",
        "float",
        "if",
        "int",
        "byebye",
        "void",
        "while",
      ],

      typeKeywords: ["boolean", "float", "int", "void"],

      operators: [
        "=",
        ">",
        "<",
        "!",
        "==",
        "<=",
        ">=",
        "!=",
        "&&",
        "||",
        "+",
        "-",
        "*",
        "/",
      ],

      // we include these common regular expressions
      symbols: /[=><!~?:&|+\-*\/\^%]+/,

      // The main tokenizer for our languages
      tokenizer: {
        root: [
          [/\b(true|false)\b/, "keyword.constant"],

          // identifiers and keywords [A-Za-z_][A-Za-z_0-9]*[\w]
          [
            /[A-Za-z_][A-Za-z_0-9]*/,
            {
              cases: {
                "@typeKeywords": "type",
                "@keywords": "keyword",
                "@default": "identifier",
              },
            },
          ],

          // whitespace
          { include: "@whitespace" },

          // delimiters and operators
          [/[{}()\[\]]/, "@brackets"],
          [/[<>](?!@symbols)/, "@brackets"],
          [
            /@symbols/,
            {
              cases: {
                "@operators": "operator",
                "@default": "",
              },
            },
          ],

          // numbers
          [/\d*\.\d+([eE][\-+]?\d+)?/, "number.float"],
          [/\d+/, "number"],

          // delimiter: after number because of .\d floats
          [/[;,.]/, "delimiter"],

          // strings
          [/"([^"\\]|\\.)*$/, "string.invalid"], // non-terminated string
          [/"/, { token: "string.quote", bracket: "@open", next: "@string" }],
        ],

        comment: [
          [/[^\/*]+/, "comment"],
          [/\/\*/, "comment", "@push"], // nested comment
          ["\\*/", "comment", "@pop"],
          [/[\/*]/, "comment"],
        ],

        string: [
          [/[^\\"]+/, "string"],
          [/\\./, "string.escape.invalid"],
          [/"/, { token: "string.quote", bracket: "@close", next: "@pop" }],
        ],

        whitespace: [
          [/[ \t\r\n]+/, "white"],
          [/\/\*/, "comment", "@comment"],
          [/\/\/.*$/, "comment"],
        ],
      },
    });

    monaco.editor.defineTheme("transparent-theme", {
      base: resolvedTheme === "light" ? "vs" : "vs-dark",
      inherit: true,
      rules: [],
      colors: {
        "editor.background": "#00000000",
        "minimap.background": "#00000000",
        "scrollbarSlider.background": "#ffffff00",
      },
    });

    monaco.editor.setTheme("transparent-theme");
  }

  const handleEditorChange: OnChange = value => {
    if (value != undefined) setSourceCode(value);
  }

  useEffect(() => {
    if (monaco) {
      monaco.editor.defineTheme("transparent-theme", {
        base: resolvedTheme === "light" ? "vs" : "vs-dark",
        inherit: true,
        rules: [],
        colors: {
          "editor.background": "#00000000",
          "minimap.background": "#00000000",
          "scrollbarSlider.background": "#ffffff00",
        },
      });

      monaco.editor.setTheme("transparent-theme");
    }
  }, [monaco, resolvedTheme]);

  return (
    <div className="h-full w-full flex overflow-hidden bg-primary-foreground/20 backdrop-blur-sm border border-2 border-accent-foreground rounded-xl shadow-sm hover:bg-primary-foreground/30 transition" >
      <Editor
        defaultLanguage="ricelang"
        defaultValue="// some comment"
        onMount={handleEditorDidMount}
        onChange={handleEditorChange}
        options={{
          padding: { top: 16 },
          lineNumbersMinChars: 3,
        }}
        value={sourceCode}

      />
      <style>{`.monaco-editor { outline: 0; }`}</style>
    </div>
  );
}
