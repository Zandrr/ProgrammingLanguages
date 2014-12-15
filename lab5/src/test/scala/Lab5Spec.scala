import org.scalatest._
import jsy.lab5.ast._
import Lab5._

class Lab5Spec extends FlatSpec {

"mapFirstDoWith" should "map the first element where f returns Some" in {
   val l1 = List(1, 2, -3, 4, -5)
   val gold1 = List(1, 2, 3, 4, -5)
   def dowith[W]: DoWith[W,List[Int]] = mapFirstWith[W,Int] { (i: Int) => if (i < 0) Some(doreturn(-i)) else None } (l1)
   assertResult((true,gold1)) { dowith(true) }
   assertResult((42,gold1)) { dowith(42) }
}

"DoNeg" should "negate values" in {
    val e = Unary(Neg, N(42))
    val (mp:Mem, ep: Expr) = step(e)(Mem.empty)
    assert(mp.isEmpty)
    assertResult(N(-42)) { ep }
  }
 
"DoSeq" should "produce second element in sequence" in {
    val e = Binary(Seq, N(1), Binary(Plus, N(2), N(3)))
    val (mp:Mem, ep: Expr) = step(e)(Mem.empty)
    assert(mp.isEmpty)
    assertResult(Binary(Plus, N(2), N(3))) { ep }
  }

  "DoArith" should "sum numbers" in {
    val e = Binary(Plus, N(2), N(3))
    val (mp:Mem, ep: Expr) = step(e)(Mem.empty)
    assert(mp.isEmpty)
    assertResult(N(5)) { ep }
  }
 
"DoPlusString" should "concat strings" in {
    val e = Binary(Plus, S("abc"), S("xyz"))
    val (mp:Mem, ep: Expr) = step(e)(Mem.empty)
    assert(mp.isEmpty)
    assertResult(S("abcxyz")) { ep }
  }


 "DoAndTrue" should "not shortcircuiting boolean and" in {
    val e = Binary(And, B(true), Decl(MVar, "x", B(true), Var("x")))
    val (mp:Mem, ep: Expr) = step(e)(Mem.empty)
    assert(mp.isEmpty)
    assertResult(Decl(MVar, "x", B(true), Var("x"))) { ep }
  }

  "DoObject" should "instantiate an object in memory" in {
    val e = Obj(Map("a" -> N(42), "b" -> N(47)))
    val (mp:Mem, a: A) = step(e)(Mem.empty)
    assertResult(e) { mp.get(a).get }
  }

  "DoGetField" should "access a field from an object in memory" in {
    val setup = Obj(Map("a" -> N(42), "b" -> N(47)))
    val (m:Mem, addr: A) = step(setup)(Mem.empty)
    val e = GetField(addr, "b")
    val (mp, ep: Expr) = step(e)(m)
    assert(m == mp)
    assertResult(N(47)) { ep }
  }
 
 
  "SearchDecl" should "step its declared value" in {
    val e = Decl(MConst,"x",Binary(Times,N(6),N(7)),Undefined)
    val (mp: Mem, ep: Expr) = step(e)(Mem.empty)
    assert(mp.isEmpty)
    assertResult(Decl(MConst,"x",N(42),Undefined)) { ep }
  }

  "DoAssignVar" should "assign the variable to a value" in {
    val e = Decl(MVar,"x",N(42),Assign(Var("x"),N(47)))
    val (mp: Mem, ep: Expr) = step(e)(Mem.empty)
    val aref = ep match {
      case Assign(Unary(Deref, a@A(_)), N(47)) => Some(a)
      case _ => None
    }
    val (mpp, epp: Expr) = step(ep)(mp)
    assertResult(N(47)) { mpp get(aref get) get}
    assertResult(N(47)) { epp }
  }

  "DoAssignField" should "access a field from an object in memory" in {
    val setup = Obj(Map("a" -> N(42), "b" -> N(47)))
    val (m:Mem, a: A) = step(setup)(Mem.empty)
    val e = Assign(GetField(a, "b"), N(99))
    val (mp, ep: Expr) = step(e)(m)
    assertResult(Obj(Map("a" -> N(42), "b" -> N(99)))) { mp get a get}
    assertResult(N(99)) { ep }
  }

  "SearchAssign1" should "step the lhs" in {
    //  { a: 42, b: 47 }.a = 99
    val e = Assign(GetField(Obj(Map("a" -> N(42.0), "b" -> N(47.0))),"a"),N(99.0))
    val (mp: Mem, ep: Expr) = step(e)(Mem.empty)

  }

  "SearchAssign2" should "step the rhs" in {
    val e = Decl(MVar,"x",N(42),Assign(Var("x"),N(47)))
    val (mp: Mem, ep: Expr) = step(e)(Mem.empty)
    assert(ep match {
      case Assign(Unary(Deref, a @ A(_)),N(47)) =>
        // Verify memory correctly references N(42)
        mp.get(a).get == N(42)
      case _ => false
    })
  }
  // Probably want to write some tests for castOk, typeInfer, substitute, and step.
}
