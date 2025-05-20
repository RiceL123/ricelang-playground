/*
 * SourceFile.java                   
 */

package ricelang.VC.Scanner;

public class SourceFile {

  static final char eof = '\u0000';
  private static String content;
  private int index = 0;

  public SourceFile(String sourceCode) {
    content = sourceCode;
  }

  char getNextChar() {
    if (index >= content.length()) return eof;
    return content.charAt(index++);
  }

  char inspectChar(int nthChar) {
    int targetIndex = index + nthChar - 1;
    if (targetIndex >= content.length()) return eof;
    return content.charAt(targetIndex);
  }
}
