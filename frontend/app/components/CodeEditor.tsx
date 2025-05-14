import React, { useEffect } from "react";
import { Editor, OnChange, OnMount, useMonaco } from "@monaco-editor/react";
import { useTheme } from 'next-themes'

export const defaultSourceCode = `// Mendlebrot in ricelang

int MAX_DEPTH = 100;
float LIMIT = 8.0;
int WIDTH = 150;
int HEIGHT = 50;
float REAL_MIN = -2.5;
float REAL_MAX = 1.0;
float IMAG_MIN = -1.5;
float IMAG_MAX = 1.5;
int COLOUR_OFFSET = 2;

int mod(int a, int b) {
    return a - (a / b * b);
}

int mandelbrot(float real, float imag) {
    float z_real = 0.0;
    float z_imag = 0.0;
    float z_real2 = 0.0;
    float z_imag2 = 0.0;
    int depth = 0;

    while (depth < MAX_DEPTH && z_real2 + z_imag2 < LIMIT) {
        z_imag = 2.0 * z_real * z_imag + imag;
        z_real = z_real2 - z_imag2 + real;
        z_real2 = z_real * z_real;
        z_imag2 = z_imag * z_imag;
        depth = depth + 1;
    }

    return depth;
}

int main() {
    int x, y;
    float real, imag;
    int depth;

    for (y = 0; y < HEIGHT; y = y + 1) {
        for (x = 0; x < WIDTH; x = x + 1) {
            real = REAL_MIN + (REAL_MAX - REAL_MIN) * x / WIDTH;
            imag = IMAG_MIN + (IMAG_MAX - IMAG_MIN) * y / HEIGHT;
            depth = mandelbrot(real, imag);
            if (depth == MAX_DEPTH) {
                putString("#");
            } else {
              putString(" ");
            }
        }
        putLn();
    }
}

`
export default function CodeEditor({ setSourceCode, sourceCode }: { setSourceCode: React.Dispatch<React.SetStateAction<string>>, sourceCode: string }) {
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
        "return",
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

  const handleEditorChange: OnChange = (value, _event) => {
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
