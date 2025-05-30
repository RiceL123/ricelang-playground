/*
 * GlobalVarDecl.java       
 *
 *
 * See t2.vc and t3.vc.
 */

package ricelang.VC.ASTs;

import ricelang.VC.Scanner.SourcePosition;

public class GlobalVarDecl extends Decl {

  public Expr E;

  public GlobalVarDecl(Type tAST, Ident iAST, Expr eAST, SourcePosition position) {
    super (position);
    T = tAST;
    I = iAST;
    E = eAST;
    T.parent = I.parent = E.parent = this;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitGlobalVarDecl(this, o);
  }

}
