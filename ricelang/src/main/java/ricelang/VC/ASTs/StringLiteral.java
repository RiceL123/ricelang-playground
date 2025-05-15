/*
 * StringLiteral.java
 *
 *
 * See t24c.vc.
 *
 */

package ricelang.VC.ASTs;

import ricelang.VC.Scanner.SourcePosition;

public class StringLiteral extends Terminal {

  public StringLiteral (String value, SourcePosition position) {
    super (value, position);
  }

  public Object visit(Visitor v, Object o) {
    return v.visitStringLiteral(this, o);
  }

}
