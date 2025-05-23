/*
 * List.java      
 *
 */

package ricelang.VC.ASTs;

import ricelang.VC.Scanner.SourcePosition;

public abstract class List extends AST {

  public List (SourcePosition position) {
    super (position);
  }


  // The following methods will be used in Assignments 4 and 5.

  public boolean isEmptyArgList() {
    return this instanceof EmptyArgList;
  }

  public boolean isEmptyParaList() {
    return this instanceof EmptyParaList;
  }

  public boolean isEmptyStmtList() {
    return this instanceof EmptyStmtList;
  }

  public boolean isEmptyArrayExprList() {
    return this instanceof EmptyArrayExprList;
  }

  public boolean isEmptyDeclList() {
    return this instanceof EmptyDeclList;
  }

  public boolean isEmpty() {
    return isEmptyArgList() || isEmptyParaList() || isEmptyStmtList() 
           || isEmptyArrayExprList() || isEmptyDeclList();
  }

}
