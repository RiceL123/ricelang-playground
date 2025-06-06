/*
 * WhileStmt.java
 *
 *
 * See t33.vc and t34.vc.
 *
 */

package ricelang.VC.ASTs;

import ricelang.VC.Scanner.SourcePosition;

public class WhileStmt extends Stmt {

  public Expr E;
  public Stmt S;

  // ********* NOTE *********
  // The two fields below are not used for this year's assignments
  public AST trueSuccessor, falseSuccessor;

  public WhileStmt (Expr eAST, Stmt sAST, SourcePosition Position) {
    super (Position);
    E = eAST;
    S = sAST;
    E.parent = S.parent = this;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitWhileStmt(this, o);
  }

}
