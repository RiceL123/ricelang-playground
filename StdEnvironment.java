/*
 * StdEnvironment.java
 *
 * Most programming languages provide a standard collection of
 * pre-defined constants, variables, types, and functions that
 * programmers can use without needing to define them explicitly.
 * Examples include the `java.lang` package in Java and the standard
 * prelude in Haskell. Such a collection is referred to as the
 * standard environment.
 *
 * In our programming language, VC, the standard environment includes five
 * built-in primitive types and 11 built-in I/O functions. Additionally,
 * there is an `errorType`, which is assigned to expressions when a
 * type error is detected. This `errorType` helps reduce the number of
 * spurious errors reported during compilation. For more details, refer
 * to `VC.ASTs.IntType.java` and `VC.ASTs.FloatType.java`.
 *
 * In the current implementation of the symbol table, the attribute of
 * an identifier is represented by a pointer to its corresponding
 * declaration. However, for built-in functions, the programmer does
 * not provide a declaration. Instead, the compiler must explicitly
 * construct a "declaration" for each built-in function and insert its
 * name into the symbol table. This task is handled by the
 * `establishStdEnvironment` method in the `Checker` class, located in
 * `Checker.java`.
 *
 */

package VC;

import VC.ASTs.*;

public final class StdEnvironment {

  public static Type booleanType, intType, floatType, stringType, voidType, errorType;

  // Small ASTs representing "declarations" of nine built-in functions

  public static FuncDecl
    putBoolDecl, putBoolLnDecl, 
    getIntDecl, putIntDecl, putIntLnDecl, 
    getFloatDecl, putFloatDecl, putFloatLnDecl, 
    putStringDecl, putStringLnDecl, putLnDecl;

}
