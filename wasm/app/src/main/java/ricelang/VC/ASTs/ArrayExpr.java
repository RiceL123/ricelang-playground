/*
 * ArrayExpr.java       
 *
 * Used for representing an array expression.
 *
 * See t46.vc and its corresponding AST given in the spec of Assignment 3.
 */

package ricelang.VC.ASTs;

import ricelang.VC.Scanner.SourcePosition;

public class ArrayExpr extends Expr {

  public Var V;
  public Expr E; // index of array var

  public ArrayExpr (Var idAST, Expr indexAST, SourcePosition position) {
    super (position);
    V = idAST;
    E = indexAST;
    V.parent = E.parent = this;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitArrayExpr(this, o);
  }

}
