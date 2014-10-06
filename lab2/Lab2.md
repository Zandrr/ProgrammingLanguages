## Grammars: Synthetic Examples.

### Question 1
#### (A)

```
  A:: = A1 & A2 | V

  A1 ∈ AObjects, A2 ∈ AObjects      V ∈ VObjects
  ----------------------------      -------------
  A1 & A2 <- AObjects               V ∈ AObjects

  V:: = a | b

  V ∈ VObjects  V ∈ VObjects
  -----------   ------------
  a ∈ VObjects  b ∈ VObjects

```

#### (B)

Given: a & b & b
```
   A            
  /|\           
 A & A          
/|\   \  
A & A   V         
|   |   |       
V   V   b        
|   |
a   b


   A            
  /|\           
 A & A  
/   /|\
V   A & A
|   |   |
a   V   V
   |   |
   b   b
```   

#### (C)

The language will yield strings with one or more a's or b's or c's

#### (D)

(1) S => AaBb => baBb => baab
(2) Not possible with given grammar
(3) Not possible with given grammar
(4) S => AaBb => AbaBb => bbaab

#### (E)

```
abcd is possible
     S
  / | | \
 a  S c  B
    |    |
    b    d

acccbd is not possible because the rightmost B cannot produce bd      
       S
    / | | \
   a  S c  B
     / \    
    c   A   
        |
        c

acccbcc is not possible because the rightmost B cannot produce bcc
       S
    / | | \
   a  S c  B
     / \    
    c   A   
        |
        c

acd is not possible because it is not possible to only produce 3 symbols
       S
    / | | \
   a  S c  B

accc is possible
       S
    / | | \
   a  S c  B
      |    |
      A    A
      |    |
      c    c
```

### Question 2

## Grammars: Understanding a Language


#### (A)

######i.


The first grammar contains two terminals which are operand and operator. It is left associative because it recurses  on the left side.


The second grammar contains three terminal symbols which are operand, operator, and ∈.  It is right associate because it recurses on the right side.


Therefore each expression contains one or more operands seperated by operators.


######ii.


These grammars generate the same expression.  This is because they both recurse.  The first one recurses on itself, and the second on recurses on esuffix


#### (B)
The - operator has precedence over the << operator, because of the following scala code:


scala> 5 << 3 - 1
res9: Int = 20

scala> 5 << 2
res10: Int = 20


scala> 5 - 3 << 6
res11: Int = 128

scala> 2 <<  6
res12: Int = 128


#### (C)


Float ::= Fractions | Exponents

Fractions ::= Sign | Sign n

Exponent ::= E Sign | ε

Sign ::= n | -n

D ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9




