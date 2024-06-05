# Writing a swerve drive visualizer from scratch

1. How Swerve works
2. PID Controllers
3. Code overview
4. Asteroids!

## How Swerve works

If you have [intuition for how vectors work](#a-intro-to-vectors), then the basic logic for swerve is really quite straightforward. I like this explanation: https://dominik.win/blog/programming-swerve-drive/, but I'll summarize it here. (I also have a little [Desmos for this](https://www.desmos.com/calculator/ax0rju65fk).)

TODO

## Code overview

I'll summarize by class:
- `PIDController`: a PID controller.
- `Swerve`: a swerve drivebase composed of an assortment of modules, each specified by some offset. Represents the "robot".
- `SwerveModule`: a swerve module, i.e. a motorized wheel you can steer.
- `Rigidbody`: handles a crude approximation of the basic physics that affects a rigid, 2d object that rotates or moves. This lets us apply an impulse (push and turn) to an object, or also apply friction (or really just slow it down).
- `Vector2`: a basic implementation of a [2d vector](#a-intro-to-vectors) in code.
- `Constants`: a bag of constants that we use throughout the project.

## Asteroids!

We have a metal box adrift in an open space, free to move and spin around at will—maybe [this is familiar](https://images.squarespace-cdn.com/content/v1/50189cc3e4b0807297e80058/1595786628808-VEKI6XEPS0JB1HQCO1X2/Asteroids.jpg)?

If you haven't [played it before](https://www.echalk.co.uk/amusements/Games/asteroidsClassic/ateroids.html), _Asteroids_ is a classic arcade game that's also beautifully simple. You fly a little spaceship around and shoot pellets at asteroids (weird looking polygons) while trying to not be crushed by one.

To add that to our swerve visualizer, we're basically already there. We just have to add some logic for spawning asteroids, shooting pellets, and then handling when they intersect.

I just cordoned all of this off into its own package/file, `asteroids/Asteroids.java`. I, uh, also did kind of lie about this being all we needed—to add some more learning fodder I also added a `Shape` class with implementations `Polygon`, `Edges` and `Rectangle`. I think JavaFX already has shape classes, so this was redundant, but it might be interesting to read through that file. There's also some more linear algebra (!) used, specifically to find the intersection of two line segments. There's a [Desmos](https://www.desmos.com/calculator/8em6l1t2qo) demo for how the `linesIntersect` function works, but if you're really interested in how you derive the algebra (you can either slog through the systems of equations, or use linear algebra to do it quickly) feel free to ask on the Discord.

## Things to try

The swerve drive visualizer and asteroids parts are pretty isolated, so I'd recommend mainly focusing on the first for now (and for FRC anyway it's more useful you understand the basic logic behind swerve drive, even if you're using a library) and then playing around with the asteroids part later if you're interested.

- Mess around with different PID gains, and see how they affect the fidelity of the (crudely simulated) drivebase. You might notice that with less aggressive PID gains, the wheels not only take longer to converge on the "optimal" direction, but in the process had some unwanted drift that slightly moves or spins the robot in entirely unwanted directions. In an actual robot, this tends to be less of an issue based on inertia/friction, but this effect tends to be noticeable if unimportant. To somewhat address this, you could exponentiate the dot product of the swerve module's heading with its target heading, to control how much the module spins when it's facing the wrong direction. Or, you could also think of alternative approaches. (If you really wanted to get fancy, you could think of aligning the modules as some sort of search problem where the "optimal" paths that each module takes to align balances out the incorrect components of each module's velocity. But again, this is overkill for an actual robot.)

- Can you make the "robot" a pentagon? Or whatever weird shape you come up with, with however many modules? (Note: for accurate collision detection with the asteroids, you'd also have to change the shape used to define the drivebase in `Constants`. Track/wheelbase might no longer be meaningful.)

- Try adding trajectory tracking/path planning to the visualizer, so you can draw out a path, and then either hit a key or click a button to have the robot follow the path to the best of its ability, perhaps using some PID controllers for position/rotation.

- In the original game, upon being shot an asteroid would break up into smaller asteroids rather than just being blown up. Then only once a small enough asteroid was shot would it evaporate. Could you try messing around with that?

- Also in the original game, the colors were inverted: the background was black (space) and the sprites (in this case, the drivebase and the asteroids) were white. But in the visualizer, as a default, the colors are the other way around, and this stays the same even with the asteroids mode enabled. Try changing this so the colors are still the same, black on white, with asteroids disabled, but the colors invert to white on black as soon as the asteroids are enabled.

## Appendix

### A. Intro to vectors

### B. The unit circle

### C. PID controllers