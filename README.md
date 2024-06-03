# Learning by exploration

_Or, A Young Omnicat's Illustrated Primer_

I've been asked a couple times on how to get started with learning to code. Or, more specifically, what _language_ to get started with. But this isn't the right question—programming isn't about languages or tools; it's about solving problems and building things that _work_. The means you use to that end of course matter. (It was [using Lisp over C++](https://flownet.com/gat/jpl-lisp.html) that enabled JPL engineers to fix a critical bug on Deep Space 1, by then hundreds of millions of miles away, in almost _real time!_) But what's most important as a programmer is that you know the right tool for the job, and know how to use it well. Every general purpose language in use today is enormously powerful and equipped with a vast suite of libraries that will do most of the hard work for you. So just pick something, and start making things. We use Java for the robot (or at least when I was on the team) so I recommend you start with that.

So how should you learn how to code? Read and write lots of code! You can start with existing code (this repo!) but you'll _really_ learn by writing your own stuff. The idea with this repo is to get up and running by running some existing code, seeing how that code works, and then using what you learned to write your own projects. The important thing isn't so much that you learn Java, but that you learn how to gradually translate an idea into working software.

If you run into any issues, see something that's wrong or should be improved, have any questions, or anything else, I'm Kai on the Omnicats Discord. And of course  feel free to change anything with a commit or [pull request](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/about-pull-requests). (If the Discord no longer exists, or you just feel like it, feel free to email me.)

## Overview

This repository is supposed to be a jumping off point for getting started with programming, namely in Java. There are a few projects of increasing complexity:

1. [Tic-Tac-Toe](/tictactoe)
2. [Tetris](/tetris)
3. A basic [swerve drive visualizer](/swervevisualizer) (with an Asteroids mode!)

where the third is specifically geared toward FRC.

There's also a brief intro to [what Java looks like](/hello/) that demonstrates some Java features. [**For completion (is this complete?) I've also included the `crescendo-2024` repo here as a relatively well-documented example of a basic but used FRC codebase.**](/crescendo-2024/)

There are (free!) resources abound on the internet for learning Java: Codecademy, FreeCodeCamp, [books](https://www.rcsdk12.org/cms/lib/NY01001156/Centricity/Domain/4951/Head_First_Java_Second_Edition.pdf), [YouTube tutorials](https://www.youtube.com/watch?v=eIrMbAQSU34), etc. The purpose of this repo is to be a compliment to those. The projects are supposed to off the bat give you a lot of experience working with code. If you can follow the code, which will come with spending enough time, and especially if you follow it well enough to add features, then you have a firm enough grasp of programming to start writing your own projects. And writing your own stuff is, of course, how you'll become a good programmer. (Or if you already are one, feel free to butcher my coding skills in a PR.) That of course isn't to say the code in this repo is particularly *good* or elegant or something you should try to emulate, but it does what it's supposed to do and is interesting enough to try to understand. In fact, any criticisms you may eventually notice will stick with you when you do write your own projects.

An asterisk is that I can only attempt to (poorly) explain so much of how the code works, and I am, uh, also not very good at explaining things—but this could actually be a blessing in disguise! You can read as much of my choppy prose as you want, but you'll really understand the code by messing around with it yourself and seeing what happens. And then once you get an idea of what the lines of code mean, you can try adding features. If you're newer to programming, this will be a struggle—especially if you're not intimately familiar with the ins and outs of your tool (the trusty Java Development Kit!). But once you do originally figure it out (even if you have to resort to a lot of Googling) you'll really understand what you had to do to get there. You'll learn more about Java, you'll learn more about programming, you'll become a better problem solver—until you're writing code that's more refactorable and elegant than what's in this repo!

The way to do this is probably step-by-step: look through the code for a project, read the corresponding notes on it, read through the code and how it flows until you understand what's going on—you can try doing little experiments to see what happens when you change, remove or add certain things—and then try adding features. (Can you change the size of the board in Tic-Tac-Toe? Add colors to the pieces in Tetris? Make the "robot" in the swerve visualizer a pentagon?)

Then, whenever you feel like it, get started on writing your own project. Can you write Snake? Chess? A calculator? A visualizer for differential drive? Or try writing an original game or app. An exercise that might be useful could be rewriting a project in this repo from scratch: can you write your own Tetris implementation? Or do you have the intuition for writing out the logic for swerve from scratch? It really is simpler than it might seem! If your understanding of how some code works is shaky, rewriting either parts or all of it from scratch would mean you really understand it. 

The last thing I'll note is that this repo is in Java, and has nothing to directly do with FRC robot programming or WPILib. But if you know how to think like a computer, how to solve problems, and how to practically break up the complexity of a project, then learning [another language](https://www.learncpp.com/) or [how to write an FRC codebase](https://docs.wpilib.org/en/stable/index.html) becomes relatively straightforward. Problem solving and software design are a universal skill and the basis of software engineering.

