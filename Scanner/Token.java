/*
 * Token.java   
 */

// ====== PLEASE DO NOT MODIFY THIS FILE =====

package VC.Scanner;

public final class Token {

  public int kind;
  public String spelling; // lexeme 
  public SourcePosition position;

  public Token(int kind, String spelling, SourcePosition position) {
    if (kind == Token.ID) {
      this.kind = determineKeywordKind(spelling);
    } else {
      this.kind = kind;
    }
    this.spelling = spelling;
    this.position = position;
  }

  // Determines the keyword kind for the given spelling, if it matches a reserved word.
  private int determineKeywordKind(String spelling) {
    for (int i = firstReservedWord; i <= lastReservedWord; i++) {
      if (keywords[i].equals(spelling)) {
        return i;
      }
    }
    return Token.ID; // If not a reserved keyword, return as ID.
  }

  // Returns the keyword string for a given token kind.
  public static String spell(int kind) {
    return keywords[kind];
  }

  @Override
  public String toString() {
    return String.format("Kind = %d [%s], spelling = \"%s\", position = %s", 
                          kind, spell(kind), spelling, position);
  }

  // Token kinds...

  public static final int

    // reserved words - must be in alphabetical order...
    BOOLEAN     = 0,
    BREAK       = 1,
    CONTINUE    = 2,
    ELSE        = 3,
    FLOAT       = 4,
    FOR         = 5,
    IF          = 6,
    INT         = 7,
    RETURN      = 8,
    VOID        = 9,
    WHILE       = 10,

    // operators
    PLUS        = 11,
    MINUS       = 12,
    MULT        = 13,
    DIV         = 14,
    NOT         = 15,
    NOTEQ       = 16,
    EQ          = 17,
    EQEQ        = 18,
    LT          = 19,
    LTEQ        = 20,
    GT          = 21,
    GTEQ        = 22,
    ANDAND      = 23,
    OROR        = 24,

    // separators
    LCURLY      = 25,
    RCURLY      = 26,
    LPAREN      = 27,
    RPAREN      = 28,
    LBRACKET    = 29,
    RBRACKET    = 30,
    SEMICOLON   = 31,
    COMMA       = 32,

    // identifiers
    ID          = 33,

    // literals
    INTLITERAL  = 34,
    FLOATLITERAL= 35,
    BOOLEANLITERAL = 36,
    STRINGLITERAL = 37,

    // special tokens...
    ERROR       = 38,
    EOF         = 39;

  private static final String[] keywords = new String[] {
    "boolean",
    "break",
    "continue",
    "else",
    "float",
    "for",
    "if",
    "int",
    "return",
    "void",
    "while",
    "+",
    "-",
    "*",
    "/",
    "!",
    "!=",
    "=",
    "==",
    "<",
    "<=",
    ">",
    ">=",
    "&&",
    "||",
    "{",
    "}",
    "(",
    ")",
    "[",
    "]",
    ";",
    ",",
    "<id>",
    "<int-literal>",
    "<float-literal>",
    "<boolean-literal>",
    "<string-literal>",
    "<error>",
    "$"
  };

  // Defining the range for the reserved words (keywords).
  private static final int firstReservedWord = Token.BOOLEAN;
  private static final int lastReservedWord = Token.WHILE;
}
