# "Hello World"—Getting started with Java

If the projects are a microcosm of learning Java by doing (where you can see how Java is "actually" used for something like Tetris) the complimentary way to learn is "top-down" by learning about the various concepts explicitly. This file is a (*very*) brief intro to what Java looks like and how you use it. As I mentioned, there are much better resources online for learning all of this, but I thought I'd include a little explainer of my own here.

(If you've programmed before but just want to get caught up with Java, Learn X in Y minutes has a [good page](https://learnxinyminutes.com/docs/java/) that concisely walks through all the syntax you need to know.)

First, what is a program? Well, it's some instructions that run on a computer. What's a computer? There are many different, but all formally equivalent, ways of answering that question (lambda calculus, the sum of simple electrical circuits—NAND gates and D-Flip-Flops, even [dominos](https://en.wikipedia.org/wiki/Wang_tile), etc.) but they're all the same as a basic idea: a machine that can compute all "computable" things is equivalent to a head that moves around on an infinitely long piece of tape, that can read or write to that tape at will. This is Alan Turing's [Universal Turing Machine](https://en.wikipedia.org/wiki/Universal_Turing_machine), which actually wasn't really motivated by computers but by the [most important (pure) math problem of the early 20th century](https://en.wikipedia.org/wiki/Entscheidungsproblem). That was a brief tangent (though I recommend learning about this stuff too, if you're interested!) but I mention the Turing Machine because _that's_ what a program on a computer is: something that moves around and manipulates data in a useful way.

So a program is about representing some state, manipulating that state, and doing useful things with that state (printing the result of a computation to the user, or displaying a game to a screen). This takes the form of _variables_ in your program. 

TODO:
- Variables: data types
- Math
- Control flow
 - if/else, while, for, switch
- Functions
- Object-oriented programming. Big idea of object-oriented languages was to tie together variables and functions: a better way to think about a program is not to focus on the low level details of what the computer is literally doing, but to break up what it does into separable, high level systems that interact with each other. The biology analogy being autonomous "cells" that communicate with each other.
- Class
- Polymorphism
  - Composition and inheritance
  - Abstract classes, interfaces
- Qualifiers
- Using Java
  - The Java compiler, Java Virtual Machine (JVM), and the Java Development Kit (JDK)
  - Packages
  - Gradle
- Miscellaneous things
  - Enums
  - Data structures