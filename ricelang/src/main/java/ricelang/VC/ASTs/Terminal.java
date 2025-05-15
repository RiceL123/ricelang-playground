/*
 * Terminal.java    
 */

package ricelang.VC.ASTs;

import ricelang.VC.Scanner.SourcePosition;

abstract public class Terminal extends AST {

  public String spelling;

  public Terminal (String value, SourcePosition Position) {
    super (Position);
    spelling = value;
  }

}
