/*
 * IntType.java               
 *
 * See, for example, t3.vc (displayed as "int").
 */

package ricelang.VC.ASTs;

import ricelang.VC.Scanner.SourcePosition;

public class IntType extends Type {

  public IntType (SourcePosition Position) {
    super (Position);
  }

  public Object visit (Visitor v, Object o) {
    return v.visitIntType(this, o);
  }

  public boolean equals(Object obj) {
    if (obj != null && obj instanceof ErrorType)
      return true;
    else    
      return (obj != null && obj instanceof IntType);
  }

  public boolean assignable(Object obj) {
    return equals(obj);
  }

  public String toString() {
    return "int";
  }

}
