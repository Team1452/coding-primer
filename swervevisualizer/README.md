# Writing a swerve drive visualizer from scratch

1. How Swerve works
2. PID Controllers
3. Code overview
4. Asteroids!

## How Swerve works

If you have [intuition for how vectors work](#a-intro-to-vectors), then the basic logic for Swerve is really quite straightforward. I like this explanation: https://dominik.win/blog/programming-swerve-drive/, but I'll summarize it here.

## Code overview

I'll summarize by class:
- `Entity`: a base class for anything that participates in the tick loop. This class just takes the same tick loop methods (`update` and `draw`) used in [Tetris](/articles/tetris.md), and makes it its own class. The benefit of doing this is that you don't have to worry about calling `draw` and `tick` for every object, this is done automatically by the `parent`. We have `World` as our "root" entity, where everything else is a child of `World`.
- `World`
- `PIDController`
- `Swerve`
- `SwerveModule`
- `Vector2`
- `Constants`: bag of constants that we use throughout the project.

## Asteroids!

We have a metal box adrift in an open space, free to move and spin around at will—maybe [this is familiar](https://images.squarespace-cdn.com/content/v1/50189cc3e4b0807297e80058/1595786628808-VEKI6XEPS0JB1HQCO1X2/Asteroids.jpg)?

If you haven't [played it before](https://www.echalk.co.uk/amusements/Games/asteroidsClassic/ateroids.html), _Asteroids_ is a classic arcade game that's also beautifully simple. You fly a little spaceship around and shoot pellets at asteroids (weird looking polygonal circles) while trying to not be crushed by one.

To add that to our swerve visualizer, we're basically already there. We just have to add some logic for spawning asteroids, shooting pellets, and then handling when they intersect.

I just cordoned all of this off into its own package/file, `asteroids/Asteroids.java`. I, uh, also did kind of lie about this being all we needed—to add some more learning fodder I also added a `Shape` class with implementations `Polygon`, `Edges` and `Rectangle`. I think JavaFX already has shape classes, so this was redundant, but it might be interesting to read through that file. There's also some more linear algebra (!) used, specifically to find the intersection of two line segments. There's a [Desmos]() demo for how the `linesIntersect` function works, but if you're really interested in how you derive the algebra feel free to ask on the Discord.

## Appendix

### A. Intro to vectors

### B. The unit circle

### C. PID controllers