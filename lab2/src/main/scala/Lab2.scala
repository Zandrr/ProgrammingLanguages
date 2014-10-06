object Lab2 extends jsy.util.JsyApplication {
  import jsy.lab2.Parser
  import jsy.lab2.ast._
  
  /*
   * CSCI 3155: Lab 2
   */

  /*
   * Replace the 'throw new UnsupportedOperationException' expression with
   * your code in each function.
   * 
   * Do not make other modifications to this template, such as
   * - adding "extends App" or "extends Application" to your Lab object,
   * - adding a "main" method, and
   * - leaving any failing asserts.
   * 
   * Your lab will not be graded if it does not compile.
   * 
   * This template compiles without error. Before you submit comment out any
   * code that does not compile or causes a failing assert.  Simply put in a
   * 'throws new UnsupportedOperationException' as needed to get something
   * that compiles without error.
   */
  
  /* We represent a variable environment is as a map from a string of the
   * variable name to the value to which it is bound.
   * 
   * You may use the following provided helper functions to manipulate
   * environments, which are just thin wrappers around the Map type
   * in the Scala standard library.  You can use the Scala standard
   * library directly, but these are the only interfaces that you
   * need.
   */
  
  type Env = Map[String, Expr]
  val emp: Env = Map()
  def get(env: Env, x: String): Expr = env(x)
  def extend(env: Env, x: String, v: Expr): Env = {
    require(isValue(v))
    env + (x -> v)
  }
  
  /* Some useful Scala methods for working with Scala values include:
   * - Double.NaN
   * - s.toDouble (for s: String)
   * - n.isNaN (for n: Double)
   * - n.isWhole (for n: Double)
   * - s (for n: Double)
   * - s format n (for s: String [a format string like for printf], n: Double)
   */

  def toNumber(v: Expr): Double = {
    require(isValue(v))
    (v: @unchecked) match {
      case N(n) => n
      case S(s) => s.toDouble
      case B(true) => 1
      case B(false) => 0
      case Undefined => Double.NaN
    }
  }
  
  def toBoolean(v: Expr): Boolean = {
    require(isValue(v))
    (v: @unchecked) match {
      case B(b) => b
      case S("") => false
      case S(_) => true
      case N(0) => false 
      case N(_) => true
      case Undefined => false
      case N(Double.NaN) => false
    }
  }
  
  def toStr(v: Expr): String = {
    require(isValue(v))
    (v: @unchecked) match {
      case S(s) => s
      case Undefined => "undefined"
      case B(b) => b.toString
      case N(n) => n.toString
    }
  }
  
  def eval(env: Env, e: Expr): Expr = {
    /* Some helper functions for convenience. */
    def eToVal(e: Expr): Expr = eval(env, e)
    def eToB(e: Expr): Boolean = toBoolean(eval(env,e))
    def eToN(e: Expr): Double = toNumber(eval(env,e))

      e match {
      /* Base Cases */
      case _ if isValue(e) => e
      // Var
      case Var(x) => get(env, x)
      
      /* Inductive Cases */
      case Print(e1) => println(pretty(eval(env, e1))); Undefined
      
      // Neg
      case Unary(Neg, e1) => 
    N(-eToN(e1))
        
      // Not
      case Unary(Not, e1) => 
        B(!eToB(e1))
      
      // Plus
      case Binary(Plus, e1, e2) => (eToVal(e1), eToVal(e2)) match {
        case (S(s1), S(s2)) => S(s1 + s2)
        case (S(s1), v2) => S(s1 + toStr(v2))
        case (v1, S(s2)) => S(toStr(v1) + s2)
        case (v1, v2) => N(toNumber(v1) + toNumber(v2))
        case _ => N(eToN(e1) + eToN(e2))
      }
      
      // Minus
      case Binary(Minus, e1, e2) => 
        N(eToN(e1) - eToN(e2))
        
      // Times
      case Binary(Times, e1, e2) => 
        N(eToN(e1) * eToN(e2))
        
      // Div
      case Binary(Div, e1, e2) => 
        N(eToN(e1) / eToN(e2))
      
      // Eq  
      case Binary(Eq, e1, e2) => (eToVal(e1), eToVal(e2)) match {
    case (S(v1), S(v2)) => B(v1 == v2)
    case (v1, v2) => B(toNumber(v1) == toNumber(v2))
        }
      
      // Ne
      case Binary(Ne, e1, e2) => (eToVal(e1), eToVal(e2)) match {
    case (S(v1), S(v2)) => B(v1 != v2)
    case (v1, v2) => B(toNumber(v1) != toNumber(v2))
      }
        
      
      // Lt
      case Binary(Lt, e1, e2) => (eToVal(e1), eToVal(e2)) match{
    case (S(v1), S(v2)) => B(v1 < v2)
    case (v1, v2) => B(toNumber(v1) < toNumber(v2))
      }
      
      // Le
      case Binary(Le, e1, e2) => (eToVal(e1), eToVal(e2)) match{
    case (S(v1), S(v2)) => B(v1 <= v2)
    case (v1, v2) => B(toNumber(v1) <= toNumber(v2))
      }
      
      // Gt
      case Binary(Gt, e1, e2) => (eToVal(e1), eToVal(e2)) match{
        case (S(v1), S(v2)) => B(v1 > v2)
    case (v1, v2) => B(toNumber(v1) > toNumber(v2))
      }
      
      // Ge
      case Binary(Ge, e1, e2) => (eToVal(e1), eToVal(e2)) match{
        case (S(v1), S(v2)) => B(v1 >= v2)
    case (v1, v2) => B(toNumber(v1) >= toNumber(v2))
      }
      
      // And
      case Binary(And, e1, e2) =>
        if (eToB(eToVal(e1))) eToVal(e2) else eToVal(e1)
      
      // Or
      case Binary(Or, e1, e2) =>
        if (eToB(eToVal(e1))) eToVal(e1) else eToVal(e2)
      
      // Seq ,
      case Binary(Seq, e1, e2) => 
        eToVal(e1); eToVal(e2)
      
      // If
      case If(e1, e2, e3) => 
        if (eToB(e1)) eToVal(e2) else eToVal(e3)
      
      // Const
      // const x = e1; e2
      case ConstDecl(x, e1, e2) => 
        eval(extend(env, x, eToVal(e1)), e2)
      
      case _ => throw new UnsupportedOperationException
    }
  }
    
  // Interface to run your interpreter starting from an empty environment.
  def eval(e: Expr): Expr = eval(emp, e)

  // Interface to run your interpreter from a string.  This is convenient
  // for unit testing.
  def eval(s: String): Expr = eval(Parser.parse(s))

 /* Interface to run your interpreter from the command-line.  You can ignore what's below. */ 
 def processFile(file: java.io.File) {
    if (debug) { println("Parsing ...") }
    
    val expr = Parser.parseFile(file)
    
    if (debug) {
      println("\nExpression AST:\n  " + expr)
      println("------------------------------------------------------------")
    }
    
    if (debug) { println("Evaluating ...") }
    
    val v = eval(expr)
    
    println(pretty(v))
  }

}