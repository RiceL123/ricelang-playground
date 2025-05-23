/*
 * BreakStmt.java    
 */

package ricelang.VC.ASTs;

import ricelang.VC.Scanner.SourcePosition;

public class BreakStmt extends Stmt {

  public BreakStmt(SourcePosition Position) {
    super (Position);
  }

  public Object visit(Visitor v, Object o) {
    return v.visitBreakStmt(this, o);
  }

}
