object Lab4 extends jsy.util.JsyApplication {
  import jsy.lab4.ast._
  import jsy.lab4.Parser
  
  /*
   * CSCI 3155: Lab 4
   * Alex Campbell
   * 
   * Partner: Edward Zhu
   * 
   */

  /*
   * Fill in the appropriate portions above by replacing things delimited
   * by '<'... '>'.
   * 
   * Replace 'YourIdentiKey' in the object name above with your IdentiKey.
   * 
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
  
  /* Collections and Higher-Order Functions */
  
  /* Lists */
  // eliminates consecutive duplicates of list elements
  // implement with direct recursion
  def compressRec[A](l: List[A]): List[A] = l match {
    case Nil | _ :: Nil => l
    // If head of current list and next element are equal, then call compressRec on tail
    // else keep original list.
    case h1 :: (t1 @ (h2 :: _)) => if (h1==h2) compressRec(t1) else h1 :: compressRec(t1)
  }
  //fold right 
  def compressFold[A](l: List[A]): List[A] = l.foldRight(Nil: List[A]){
    (h, acc) => acc match { 
      case (h1 :: h2) if (h == h1) => acc 
      case _ => h :: acc
    }
  }
  
  def mapFirst[A](f: A => Option[A])(l: List[A]): List[A] = l match {
    case Nil => l
    case h :: t => f(h) match {
      case Some(x) => x :: t // if function finds something, return the operator as tail
      case _ => h :: mapFirst(f)(t) // else call mapfirst with function and call it on the tail
    }
  }
  
  /* Search Trees */
  
  sealed abstract class Tree {
    def insert(n: Int): Tree = this match {
      case Empty => Node(Empty, n, Empty)
      case Node(l, d, r) => if (n < d) Node(l insert n, d, r) else Node(l, d, r insert n)
    } 
    //fold left
    def foldLeft[A](z: A)(f: (A, Int) => A): A = {
      def loop(acc: A, t: Tree): A = t match {
        case Empty => acc
        case Node(l, d, r) => loop(f(loop(acc,l),d), r)
      }
      loop(z, this)
    }
    
    def pretty: String = {
      def p(acc: String, t: Tree, indent: Int): String = t match {
        case Empty => acc
        case Node(l, d, r) =>
          val spacer = " " * indent
          p("%s%d%n".format(spacer, d) + p(acc, l, indent + 2), r, indent + 2)
      } 
      p("", this, 0)
    }
  }
  case object Empty extends Tree
  case class Node(l: Tree, d: Int, r: Tree) extends Tree
  
  def treeFromList(l: List[Int]): Tree =
    l.foldLeft(Empty: Tree){ (acc, i) => acc insert i }
  
  def sum(t: Tree): Int = t.foldLeft(0){ (acc, d) => acc + d }
  
  def strictlyOrdered(t: Tree): Boolean = {
    val (b, _) = t.foldLeft((true, None: Option[Int])){
      (acc, h) => acc match {
        case (b1, None) => (true, Some(h))
        case (b1, a) => (((a.get < h) && b1), Some(h)) 
      }
    }
    b
  }
  

  /* Type Inference */
  
  // A helper function to check whether a jsy type has a function type in it.
  // While this is completely given, this function is worth studying to see
  // how library functions are used.
  def hasFunctionTyp(t: Typ): Boolean = t match {
    case TFunction(_, _) => true
    case TObj(fields) if (fields exists { case (_, t) => hasFunctionTyp(t) }) => true
    case _ => false
  }
  
  def typeInfer(env: Map[String,Typ], e: Expr): Typ = {
    // Some shortcuts for convenience
    def typ(e1: Expr) = typeInfer(env, e1)
    def err[T](tgot: Typ, e1: Expr): T = throw StaticTypeError(tgot, e1, e)

    e match {
      case Print(e1) => typ(e1); TUndefined
      case N(_) => TNumber
      case B(_) => TBool
      case Undefined => TUndefined
      case S(_) => TString
      case Var(x) => env(x)
      case ConstDecl(x, e1, e2) => typeInfer(env + (x -> typ(e1)), e2)
      case Unary(Neg, e1) => typ(e1) match {
        case TNumber => TNumber
        case tgot => err(tgot, e1)
      }
      case Unary(Not, e1) => typ(e1) match {
        case TBool => TBool
        case tgot => err(tgot,e1)
      }
      
      //TypeArith(Plus) & TypePlusString
      case Binary(Plus, e1, e2) => (typ(e1),typ(e2)) match{
        case (TString,TString)=> TString
        case (TNumber,TNumber)=>TNumber
        case _ => err(typ(e1),e1)
      }
      
      //TypeArith
      case Binary(Minus|Times|Div, e1, e2) => (typ(e1),typ(e2)) match{
        case (TNumber,TNumber)=>TNumber
        case (_) => err(typ(e1),e1)
      }
      
      //TypeEquality
      case Binary(Eq|Ne, e1, e2) => (typ(e1),typ(e2)) match{
        case (TFunction(a, b), _) => err(TFunction(a, b), e1)
        case (_, TFunction(a, b)) => err(TFunction(a, b), e1)
        case (e1type,e2type) if(e1type==e2type)=>TBool
        case _ => err(typ(e1),e1)
      }
      
      //TypeInequalityNumber & TypeInequalityString
      case Binary(Lt|Le|Gt|Ge, e1, e2) => (typ(e1),typ(e2)) match{
        case(TNumber,TNumber) => TBool
        case(TString,TString)=> TBool
        case _=>err(typ(e1),e1)
      }
      
      //TypeAndOr
      case Binary(And|Or, e1, e2) => (typ(e1),typ(e2)) match{
        case(TBool,TBool)=> TBool
        case _=>err(typ(e1),e1)
      }
      
      //TypeSeq
      case Binary(Seq, e1, e2) => typ(e2)
      
      //TypeIf
      case If(e1, e2, e3) => (typ(e1), typ(e2), typ(e3)) match{
        case (TBool, e2type, e3type) => if (e2type == e3type) e2type else err(e3type, e3)
        case (e1type, _, _) => err(e1type, e1)
      }

      //TypeFunction & TypeFunctionAnn
      case Function(p, params, tann, e1) => {
        // Bind to env1 an environment that extends env with an appropriate binding if
        // the function is potentially recursive.
        val env1 = (p, tann) match {
          case (Some(f), Some(tret)) =>
            val tprime = TFunction(params, tret)
            env + (f -> tprime)
          case (None, _) => env
          case _ => err(TUndefined, e1)
        }
        // Bind to env2 an environment that extends env1 with bindings for params and arguments.
        val env2 = params.foldLeft(env1) {
          case(acc, (xName, xValue)) => acc + (xName -> xValue)
        }
        // Match on whether the return type is specified.
        tann match {
          case None => 
            //tfunction will return a type function with the params originally passed in and the type of the body. 
            val tBody = typeInfer(env2, e1)
            TFunction(params, tBody)
          case Some(tret) => TFunction(params, tret)
        }
      }

      //TypeCall
      case Call(e1, args) => typ(e1) match {
        case TFunction(params, tret) => {
          //parameters are like function(a:string, b:int) and the arguments are passed in, the arguments must match the parameter type.
          (params, args).zipped.foreach {
            //for every (params, args), the t is not the type of e of n, then return error, else tret
            case ((x, t), en) => if (t != typ(en)) err(t, en)
          };
          tret
        }
        case tgot => err(tgot, e1)
      }

      //TypeObject
      case Obj(fields) =>
        TObj(fields.mapValues((exp: Expr) => typ(exp)))

      //TypeGetField
      case GetField(e1, f) => typ(e1) match {
        case TObj(field) => field.get(f) match {
          case Some(f) => f
          case None => err(typ(e1),e1)
        }
        case _ => err(typ(e1),e1)
      }
    }
  }
  
  
  /* Small-Step Interpreter */
  
  def inequalityVal(bop: Bop, v1: Expr, v2: Expr): Boolean = {
    require(bop == Lt || bop == Le || bop == Gt || bop == Ge)
    ((v1, v2): @unchecked) match {
      case (S(s1), S(s2)) =>
        (bop: @unchecked) match {
          case Lt => s1 < s2
          case Le => s1 <= s2
          case Gt => s1 > s2
          case Ge => s1 >= s2
        }
      case (N(n1), N(n2)) =>
        (bop: @unchecked) match {
          case Lt => n1 < n2
          case Le => n1 <= n2
          case Gt => n1 > n2
          case Ge => n1 >= n2
        }
    }
  }
  
  def substitute(e: Expr, v: Expr, x: String): Expr = {
    require(isValue(v))
    
    def subst(e: Expr): Expr = substitute(e, v, x)
    
    e match {
      case N(_) | B(_) | Undefined | S(_) => e
      case Print(e1) => Print(subst(e1))
      case Unary(uop, e1) => Unary(uop, subst(e1))
      case Binary(bop, e1, e2) => Binary(bop, subst(e1), subst(e2))
      case If(e1, e2, e3) => If(subst(e1), subst(e2), subst(e3))
      case Var(y) => if (x == y) v else e
      case ConstDecl(y, e1, e2) => ConstDecl(y, subst(e1), if (x == y) e2 else subst(e2))
      // Did these functions below
      // If free variable, stubstatute, otherwise you dont have to
      case Function(p, params, tann, e1) => p match{
          case(None)=> 
            if (params.forall{case(n,_) => (x != n)}){
              Function(p, params, tann, subst(e1))
            } else {
              Function(p, params, tann, e1)
            }
          case Some(a) => 
            if (params.forall{case(n,_) => (x != n)} && a!=x){
              Function(p, params, tann, subst(e1))
            } else {
              Function(p, params, tann, e1)
            }           
        }
      case Call(e1, args) =>
        Call(subst(e1), args map subst)
      case Obj(fields) =>
        Obj(fields.mapValues((exp: Expr) => subst(exp)))
      //if a free variable, subst
      case GetField(e1, f) =>
        GetField(subst(e1), f)
    }
  }
  
  def step(e: Expr): Expr = {
    require(!isValue(e))
    
    def stepIfNotValue(e: Expr): Option[Expr] = if (isValue(e)) None else Some(step(e))
    
    e match {
      /* Base Cases: Do Rules */
      case Print(v1) if isValue(v1) => println(pretty(v1)); Undefined
      case Unary(Neg, N(n1)) => N(- n1)
      case Unary(Not, B(b1)) => B(! b1)
      case Binary(Seq, v1, e2) if isValue(v1) => e2

      //DoArith
      case Binary(Plus, S(s1), S(s2)) => S(s1 + s2)
      case Binary(Plus, N(n1), N(n2)) => N(n1 + n2)
      //Minus, Times, and Div on Numbers
      case Binary(Minus, N(n1), N(n2)) => N(n1 - n2)
      case Binary(Times, N(n1), N(n2)) => N(n1 * n2)
      case Binary(Div, N(n1), N(n2)) => N(n1 / n2)

      case Binary(bop @ (Lt|Le|Gt|Ge), v1, v2) if isValue(v1) && isValue(v2) => B(inequalityVal(bop, v1, v2))
      case Binary(Eq, v1, v2) if isValue(v1) && isValue(v2) => B(v1 == v2)
      case Binary(Ne, v1, v2) if isValue(v1) && isValue(v2) => B(v1 != v2)
      case Binary(And, B(b1), e2) => if (b1) e2 else B(false)
      case Binary(Or, B(b1), e2) => if (b1) B(true) else e2
      case ConstDecl(x, v1, e2) if isValue(v1) => substitute(e2, v1, x)
      
      // Filled in call Function
      // DoCall
      // v1 is a function
      case Call(v1, args) if isValue(v1) && (args forall isValue) =>
        v1 match {
          case Function(p, params, _, e1) => {
            //substutiting args for params
            val e1p = (params, args).zipped.foldRight(e1){
              (vars,acc)=> vars match{
                case((name,_),value) => substitute(acc,value,name)
              }
            }
            p match {

              case None => e1p
              //value is a function, and we are substatuting the actual function for that name
              case Some(x1) => substitute(e1p,v1,x1)
            }
          }
          case _ => throw new StuckError(e)
        }
      /*** Fill-in more cases here. ***/

        // DoIfTrue & DoIfFalse
        case If(B(b1), e2, e3) => if (b1) e2 else e3

        // DoGetField && SearchGetField
        //get returns value of it
        case GetField(Obj(fields), f) => fields.get(f) match {
          case Some(e1) => e1 
          case None => throw new StuckError(e)
        }
        case GetField(e1, f) => GetField(step(e1), f)

      /* Inductive Cases: Search Rules */
      case Print(e1) => Print(step(e1))
      case Unary(uop, e1) => Unary(uop, step(e1))
      case Binary(bop, v1, e2) if isValue(v1) => Binary(bop, v1, step(e2))
      case Binary(bop, e1, e2) => Binary(bop, step(e1), e2)
      case If(e1, e2, e3) => If(step(e1), e2, e3)
      case ConstDecl(x, e1, e2) => ConstDecl(x, step(e1), e2)
      /*** Fill-in more cases here. ***/
      //SearchCall 1 and 2
      case Call(v1,args) if isValue(v1)=> Call(v1, mapFirst(stepIfNotValue)(args))  
      case Call(e1,e2)=> Call(step(e1),e2)
      
      /* Everything else is a stuck error. Should not happen if e is well-typed. */
      case _ => throw StuckError(e)
    }
  }
  
  
  /* External Interfaces */
  
  this.debug = true // comment this out or set to false if you don't want print debugging information
  
  def inferType(e: Expr): Typ = {
    if (debug) {
      println("------------------------------------------------------------")
      println("Type checking: %s ...".format(e))
    } 
    val t = typeInfer(Map.empty, e)
    if (debug) {
      println("Type: " + pretty(t))
    }
    t
  }
  
  // Interface to run your small-step interpreter and print out the steps of evaluation if debugging. 
  def iterateStep(e: Expr): Expr = {
    require(closed(e))
    def loop(e: Expr, n: Int): Expr = {
      if (debug) { println("Step %s: %s".format(n, e)) }
      if (isValue(e)) e else loop(step(e), n + 1)
    }
    if (debug) {
      println("------------------------------------------------------------")
      println("Evaluating with step ...")
    }
    val v = loop(e, 0)
    if (debug) {
      println("Value: " + v)
    }
    v
  }

  // Convenience to pass in a jsy expression as a string.
  def iterateStep(s: String): Expr = iterateStep(Parser.parse(s))
  
  // Interface for main
  def processFile(file: java.io.File) {
    if (debug) {
      println("============================================================")
      println("File: " + file.getName)
      println("Parsing ...")
    }
    
    val expr =
      handle(None: Option[Expr]) {Some{
        Parser.parseFile(file)
      }} getOrElse {
        return
      }
    
    handle() {
      val t = inferType(expr)
    }
    
    handle() {
      val v1 = iterateStep(expr)
      println(pretty(v1))
    }
  }

}