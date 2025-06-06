/*
 * StringType.java               
 *
 * Used in Assignment 4 for performing type checking.
 *
 * In VC, no variables can be declared to have a string type, but
 * a string will be deduced to have a string type in Assignment 4.
 */

package ricelang.VC.ASTs;

import ricelang.VC.Scanner.SourcePosition;

public class StringType extends Type {

  public StringType (SourcePosition Position) {
    super (Position);
  }

  public Object visit (Visitor v, Object o) {
    return v.visitStringType(this, o);
  }

  public boolean equals(Object obj) {
    if (obj != null && obj instanceof ErrorType)
      return true;
    else    
      return (obj != null && obj instanceof StringType);
  }

  // not used this year
  public boolean assignable(Object obj) {
    if (obj != null && obj instanceof ErrorType)
      return true;
    else    
      return (obj != null && obj instanceof StringType);
  }

  public String toString() {
    return "string";
  }

}
