# Getting started with Java

If the projects are a microcosm of learning Java by doing, by seeing what Java you actually use in writing something like Tetris or swerve drive, a complimentary learn is "top-down" by learning about the various concepts explicitly. This file is a (*very*) brief intro to what Java looks like and how you use it. As I mentioned, there are much better resources online for learning all of this, but I thought I'd include a little explainer of my own here.

1. What is a program? How should we think about programs? -> practically, representing and manipulating data. Perhaps a better way is as high level, self-contained systems that communicate with each other by passing messages.[FOOTNOTE] In Java, these are called *objects*, and every Java program has to be composed of objects whether you like it or not. 

- Variables: data types

So a program is about representing some state, manipulating that state, and doing useful things with that state (printing the result of a computation to the user, or displaying a game to a screen). This takes the form of _variables_ in your program. 

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

## Footnotes


ultimately, your goal shouldn't be to keep all the low level details of what the program is doing in your head at once, but should instead be to think about your program almost like biological "cells" that interact with each other. This makes your programs, ideally speaking, anyway, more robust and easier to reason about.

Originally, this idea came from Smalltalk in the 70's, and the cool kids would tell you Java is just a watered down, corporatized version of it. They are right. (In fact, that's what the original designer of Smalltalk says.) But the general principle of thinking about programs as objects that communicate still mostly holds.