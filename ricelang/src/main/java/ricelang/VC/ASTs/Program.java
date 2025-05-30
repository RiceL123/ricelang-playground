/*
 * Program.java
 */

package ricelang.VC.ASTs;

import ricelang.VC.Scanner.SourcePosition;

public class Program extends AST {

  public List FL;

  public Program (List dlAST, SourcePosition position) {
    super (position);
    FL = dlAST;
    FL.parent = this;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitProgram(this, o);
  }

}
