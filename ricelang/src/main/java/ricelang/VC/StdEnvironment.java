package ricelang.VC;

import ricelang.VC.ASTs.*;

public final class StdEnvironment {

  public static Type booleanType, intType, floatType, stringType, voidType, errorType;

  // Small ASTs representing "declarations" of nine built-in functions

  public static FuncDecl
    putBoolDecl, putBoolLnDecl, 
    getIntDecl, putIntDecl, putIntLnDecl, 
    getFloatDecl, putFloatDecl, putFloatLnDecl, 
    putStringDecl, putStringLnDecl, putLnDecl;

}
