/*
 * IntLiteral.java
 *
 *
 * See, for example, t20.vc.
 *
 */

package ricelang.VC.ASTs;

import ricelang.VC.Scanner.SourcePosition;

public class IntLiteral extends Terminal {

  public IntLiteral (String value, SourcePosition position) {
    super (value, position);
  }

  public Object visit(Visitor v, Object o) {
    return v.visitIntLiteral(this, o);
  }

}
