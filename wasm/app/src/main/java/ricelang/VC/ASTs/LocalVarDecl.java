/*
 * LocalVarDecl.java       
 *
 * See t6.vc -- t8.vc.
 */

package ricelang.VC.ASTs;

import ricelang.VC.Scanner.SourcePosition;

public class LocalVarDecl extends Decl {

  public Expr E;

  public LocalVarDecl(Type tAST, Ident iAST, Expr eAST, SourcePosition position) {
    super (position);
    T = tAST;
    I = iAST;
    E = eAST;
    T.parent = I.parent = E.parent = this;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitLocalVarDecl(this, o);
  }

}
