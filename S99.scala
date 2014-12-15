//S99 problem 1
 def last(x: List[Int]): Int =  x match{
  case h::Nil => h 
  case _::tail => last(tail)
  case _ => throw new NoSuchElementException
 }
val n = List(1,2,3,4)
// println(last(n))

//S99 problem 2
def secondLast(x:List[Int]): Int = x match {
  case h::_::Nil => h 
  case _::tail => secondLast(tail)
  case _ => throw new NoSuchElementException
}

//problem 3
def kth(x:Int, y:List[Int]):Int = (x,y) match {
  case (0, h::tail) => h
  case(_, h::tail) => kth(x-1, tail)

}
// println(kth(1, List(1,2,3,4)))

//problem 4
def numElements(x:List[Int]):Int = x match {
  case Nil => 0
  case h::tail => 1 + numElements(tail)
}
// println(numElements(List(1,2,3,4)))

//problem 5
def reverseList(x:List[Int]):List[Int] = {
  x.foldLeft(List[Int]())((a,b)=>b::a)
}

// println(reverseList(List(1,2,3)))

//problem 6
def isPalidrome(x:List[Int]):Boolean = {
  x == reverseList(x)
}

// println(isPalidrome(List(1,2,3,2,1)))
