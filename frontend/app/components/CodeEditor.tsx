import React, { useEffect, useState } from "react";
import { Editor, OnChange, OnMount, useMonaco } from "@monaco-editor/react";
import { useTheme } from "next-themes";
import { useAtom } from "jotai";
import { sourceCodeAtom } from "@/lib/jotai";

const ricelang = "ricelang";
let hasMounted = false;

export default function CodeEditor() {
  const [sourceCode, setSourceCode] = useAtom(sourceCodeAtom);
  const { resolvedTheme } = useTheme();
  const monaco = useMonaco();
  const [fontSize, setFontSize] = useState(14);

  useEffect(() => {
    if (typeof window !== "undefined") {
      setFontSize(window.innerWidth < 640 ? 11 : 16);
    }
  }, []);

  const handleEditorDidMount: OnMount = (_editor, monaco) => {
    if (hasMounted) return;
    hasMounted = true;

    // Define the language
    monaco.languages.register({ id: ricelang });

    // Define syntax highlighting rules
    monaco.languages.setMonarchTokensProvider(ricelang, {
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

      brackets: [
        { open: "[", close: "]", token: "@brackets" },
        { open: "{", close: "}", token: "@brackets" },
        { open: "(", close: ")", token: "@brackets" },
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

          // identifiers and keywords
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

          // brackets
          [/[{}()\[\]]/, "@brackets"],

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

          // delimiter
          [/[;,]/, "delimiter"],

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

    monaco.languages.setLanguageConfiguration(ricelang, {
      comments: {
        lineComment: "//",
        blockComment: ["/*", "*/"],
      },
      autoClosingPairs: [
        { open: "{", close: "}" },
        { open: "[", close: "]" },
        { open: "(", close: ")" },
        { open: '"', close: '"', notIn: ["string"] },
        { open: "'", close: "'", notIn: ["string", "comment"] },
      ],
    });

    monaco.languages.registerCompletionItemProvider(ricelang, {
      provideCompletionItems: (model, position) => {
        const word = model.getWordUntilPosition(position);
        const range = {
          startLineNumber: position.lineNumber,
          endLineNumber: position.lineNumber,
          startColumn: word.startColumn,
          endColumn: word.endColumn,
        };
        const suggestions = [
          {
            label: 'putStringLn("")',
            insertText: 'putStringLn("$0");',
            detail: "Put a String + \\n to stdout",
          },
          {
            label: 'putString("")',
            insertText: 'putString("$0");',
            detail: "Put a String to stdout",
          },
          {
            label: "putInt()",
            insertText: "putInt($0);",
            detail: "Put an Int to stdout",
          },
          {
            label: "putIntLn()",
            insertText: "putIntLn($0);",
            detail: "Put an Int + \\n to stdout",
          },
          {
            label: "putFloat()",
            insertText: "putFloat($0);",
            detail: "Put a Float to stdout",
          },
          {
            label: "putFloatLn()",
            insertText: "putFloatLn($0);",
            detail: "Put a Float + \\n to stdout",
          },
          {
            label: "putBool()",
            insertText: "putBool($0);",
            detail: "Put a Bool to stdout",
          },
          {
            label: "putBoolLn()",
            insertText: "putBoolLn($0);",
            detail: "Put a Bool + \\n to stdout",
          },
        ].map((item) => ({
          ...item,
          kind: monaco.languages.CompletionItemKind.Function,
          insertTextRules:
            monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
          range,
        }));
        return { suggestions };
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
  };

  const handleEditorChange: OnChange = (value) => {
    if (value != undefined) setSourceCode(value);
  };

  useEffect(() => {
    if (monaco) {
      monaco.editor.defineTheme("transparent-theme", {
        base: resolvedTheme === "light" ? "vs" : "vs-dark",
        inherit: true,
        rules: [
          { token: "brackets", foreground: "A0A0A0" }, // Choose a color you like
        ],
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
    <div className="h-full w-full flex overflow-hidden bg-primary-foreground/20 backdrop-blur-sm border border-2 border-accent-foreground rounded-xl shadow-sm hover:bg-primary-foreground/30 transition">
      <Editor
        defaultLanguage={ricelang}
        defaultValue="// some comment"
        onMount={handleEditorDidMount}
        onChange={handleEditorChange}
        options={{
          autoClosingBrackets: "always",
          fontSize: fontSize,
          padding: { top: 16 },
          lineNumbersMinChars: 3,
        }}
        theme="transparent-theme"
        value={sourceCode}
      />
      <style>{`.monaco-editor { outline: 0; }`}</style>
    </div>
  );
}
