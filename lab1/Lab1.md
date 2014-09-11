# Lab 1



## Question 1

### Part (a)

```
1 val pi = 3.14
2	def circumference(r: Double): Double = {
3		val pi = 3.14159
4		2.0 * pi * r
5	}	
6   def area(r: Double): Double =
7 		pi * r * r

```
<p>
	The value <i>pi</i> on line 4 is bound to the declaration of <i>pi</i> on line 3 because it is in the circumference function's scope.
</p>

<p>
	The value <i>pi</i> on line 7 is bound to the declaration of <i>pi</i> on line 1.  The area function does not have a definition of pi within its scope, so it reverts to the 
	<i>pi</i> defined in the global scope.
</p>

### Part (b)

```
1 val x = 3
2 	def f(x: Int): Int =
3 		x match {
4 			case 0 => 0
5 			case x => {
6 				val y = x + 1
7 			({
8 				val x = y + 1
9 					y
10 				} * f(x - 1))
11 			}
12 		}
13	val y = x + f(x)



```

<p>
	The use of x on line 3 is bound at line 2 because it is in the scope of the <i>f</i> function.
	The use of x on line 6 is bound at line 5 because it is in the scope of the <i>x match</i>.
	The use of x on line 10 is bound at line 5 because it is in the scope of the <i>x match</i>.
	The use of x on line 13 is bound at line 1 because it falls in the global scope.
</p>



## Question 2

<p>
	Yes, the body expression of plus is well-typed with type ((Int, Int), Int)

</p>
```
(b, 1):((Int, Int), Int) beacuse
	b: (Int,Int)
	1: Int
OR

(b, a + 2):((Int, Int), Int) beacuse
	b: (Int,Int)
	a+2: Int
```







