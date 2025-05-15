/*
 * AST.java    
 *
 * The top-level abstract class for representing an AST node.
 */

package ricelang.VC.ASTs;

import ricelang.VC.Scanner.SourcePosition;

public abstract class AST {

  public SourcePosition	position;
  public AST parent;
 
  public AST (SourcePosition Position) {
    position = Position;
  }

  public SourcePosition getPosition() {
    return position;
  }

  public abstract Object visit(Visitor v, Object o);

}
