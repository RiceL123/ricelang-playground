/*
 * StringExpr.java       
 *
 */

package ricelang.VC.ASTs;

import ricelang.VC.Scanner.SourcePosition;

public class StringExpr extends Expr {

  public StringLiteral SL;

  public StringExpr(StringLiteral slAST, SourcePosition position) {
    super (position);
    SL = slAST;
    SL.parent = this;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitStringExpr(this, o);
  }

}
