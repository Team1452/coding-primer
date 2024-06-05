# Tic-Tac-Toe

Let's make the best game ever made: Tic-Tac-Toe.

I, uh, don't think that's true, but it's a good "first program" after "Hello World". If you're feeling up to it, I highly highly recommend you spend some time figuring out how to code a basic Tic-Tac-Toe implementation on your own, ideally with nothing but your editor, Java and Google (but no looking up tutorials!). If I haven't labored it enough already, my point with how you learn things is that _struggling_ is how you learn. Perhaps you know about variables, control flow and `System.out.println`—can you think of how you'd represent a board? How you'd print it? How you'd read input from the console (hint: look up then use the `Scanner` class) so the current player can make a move? Or how to check if the current player, after having moved, has just won? You are smarter and more capable than you know, if you put in the effort. By stretching yourself _just enough_, you will most deeply develop as both a programmer and a learner.

So if I ever personally quested you to go and do this (you know who you are) that's why.

I wrote up two different approaches to writing Tic-Tac-Toe. One does everything in `main()` in one file, and the other (`tictactoe2`) splits the work up into `App.java` and `Board.java`. I'll go over both. (If you're not sure how to actually run this on your computer, I discuss it a bit at the end.)

(Again, these notes are brief, so I recommend you open up the respective files in their own tab/window and walk through them as you read this as I won't be copy-pasting code.)

`TicTacToe.java` does everything in the most direct, laborious way possible, step-by-step. We first define a board through defining a multidimensional char array, that is "row major". This meaning that in memory, it looks like this:
```
        [['X', ' ', 'O'],
board =  ['O', 'X', 'O'],
         ['X', 'O', 'X']]
```
Where we don't index the board like you would on a graph, where the first coordinate ("x") means left-right and the second coordinate ("y") means bottom-up. Instead, we index it through first a row coordinate (the first, 0, second, 1, or third, 2) and then a column (in this case also 0/1/2). This is just a convention—if you wanted to, you could have each sub-array be a _column_ rather than a row. And there's nothing fundamental in the code about ordering it in a "row major" or "column major" way, it's just in how you use them. (Note we use `board[row][col]`, not `board[col][row]`. But it's fine as long as you _always_ use it the same way.)

Next, we set up the player, and a helper `Scanner` object.

We then have the game loop! Where we run a block of code (`while (true)`) again and again until we ultimately call `break` for whatever reason. In the loop, we print the board (through two for loops), read the player's move (in another, nested while-true loop, which exits when the input row/col are valid and we don't need to ask again), make the corresponding move, and then check whether the current player just won or tied. If either of those happen, we print the corresponding message, and exit the loop through `break`. This would otherwise cause the program to reach the end of the `main` function, which would then end the Java process, but before that happens we call `scanner.close()` to clean up (this is, as the comment says, unnecessary but still "best practice").

That's it! I glossed over the actual code, but if you're familiar with Java syntax it's as simple as you'd expect. (Or maybe it's not? But with some time it will be.)

But there's also `tictactoe2` as a (very mild) A/B comparison. Sure, the first implementation _works_, but it's highly verbose, and every detail is crammed in at every part of the program. Maybe the most important concept (in my limited experience, anyway) in programming is the *separation of concerns* of your program. Your program may have to do a bunch of different things (its concerns) but it's your choice on how it organizes doing all those things. As simple as Tic-Tac-Toe is, on one level it has to deal with the high level of what the game looks like (the gist of the game loop) and on another the lower level details of manipulating a "board" or evaluating if a player won or not. You can split things up however you want—this analysis (if you can call it that) is just one way of looking at it. But at least in some sense it's useful to think of the game in that way, as it means you don't have to keep everything in your head at once. So if I'm working on `tictactoe2` and, say I want to print the state of the final board when the player won or tied, I can add that easily or see where they're missing. Or if I play test the game, and see that the game isn't checking if the player won correctly, I know to check the internals of `Board`, as that's the concern of that system. Either way, however you want to do things is your choice, but I wanted to add a little bit of variety to show that there are, as always, many ways to skin the (Omni-)cat. But the idea of abstraction, that you can build your own custom language for your application (here we think not in terms of for loops or boolean variables, but a board) is fundamental to programming, and is what lets you build arbitrarily complicated and intricate systems by defining these different layers of abstraction (game details, board details) and going between them. The crucial part being that at each layer, you can naively assume that the other layers all do their job (you can focus on the board and think just about it and not the game, or focus just on the game and not the board).

Anyway, go forth and play around with this code a little bit. Here are some suggestions for things to try, if any of these sound interesting:
- Can you make the game 4x4 instead of 3x3? 5x5? How about making it a rectangle and not a square?
- Can you add a third player? Any number of players, set by the user?
- Try to make Connect Four. This means the player selects not a cell, but a slot.
- Can you make a bot/computer player? Can you make it beat you every time?

# Running

If you're using an editor like IntelliJ, there might be an option to run an individual file (`TicTacToe.java`) or multiple files (`Board.java`/`App.java` where `public static void main(String[] args)` is in `App.java`) if you click around the editor, and it'll generate and then run the corresponding `javac` and `java` commands.

But, the more general way to use Java (if you have the JDK installed on your computer) is to use the `java` and `javac` commands directly. I'm referring to commands you run on your terminal/command prompt/command line—if you're not familiar with the command line (which is a useful thing to know, though for the purposes of learning Java you could figure out how to have IntelliJ do all the building/running stuff for you) I recommend looking up some videos and articles. (I don't know any good resources in particular, but it's not too hard to find decent ones and you can figure this out by messing around in your terminal.)

You can just run `TicTacToe.java` by running `javac TicTacToe.java`, and then `java TicTacToe`, or just `java TicTacToe`. This works because it's just one class—however it gets slightly trickier if you have multiple files in a package. For running `tictactoe2`, you'd have to be in the directory above it (so `ls` lists `tictactoe2`), then run `javac tictactoe2/Board.java tictactoe2/App.java` or just `javac tictactoe2/*.java`. Then you can run `java tictactoe2/App` as it's the entrypoint class (the class with the `main` method). The reason you have to run this outside the directory is because of how Java packages work and how the JVM tries to find the main class, which is less intuitive than it should be. This is also partially why tools like Gradle are nice, because they handle the build/run step for you, which you just have to specify in the configuration (`build.gradle` or `build.gradle.kts`) file.