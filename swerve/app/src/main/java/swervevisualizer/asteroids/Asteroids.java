package swerve.asteroids;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.canvas.GraphicsContext;
import swerve.Constants.Field;
import swerve.Rigidbody;
import swerve.Shape;
import swerve.Shape.Edges;
import swerve.Shape.Line;
import swerve.Shape.Polygon;
import swerve.Vector2;

/** Encapsulates the (extraneous) "Asteroids" logic!
 * Manages both an array of "Asteroids"
 * and pellets, that can be shot from anywhere on the field.
 */
public class Asteroids {

  private static final int SCORE_PER_ASTEROID = 100;

  /** The difference between a base/abstract class and an interface
   * is that a class can extend ("be") only one base class, but can
   * implement any number of interfaces.
   */
  interface HasShape {
    public abstract Shape getShape();
  }

  interface HasPosition {
    public abstract Vector2 getPosition();
  }

  class DestroyedSegment implements HasPosition, HasShape {

    private Line line;
    private Vector2 position, velocity;

    private static double BLAST_SPEED = 10;

    @Override
    public Vector2 getPosition() {
      return position;
    }

    @Override
    public Shape getShape() {
      return line;
    }

    public DestroyedSegment(Line line, Vector2 position, Vector2 velocity) {
      Vector2 middle = line.getStart().plus(line.getEnd()).divide(2);
      Vector2 blast = middle.rescale(BLAST_SPEED);

      this.position = position.plus(middle);
      this.velocity = velocity.plus(blast);

      /** Center the given line so that (0, 0) is the middle of line,
       * rather than the center of the entity that line came from,
       * so .getPosition() returns the center of the line in world space.
       */
      Line centeredLine = new Line(
        line.getStart().minus(middle),
        line.getEnd().minus(middle)
      );
      this.line = centeredLine;
    }

    public void update(double dt) {
      position = position.plus(velocity.times(dt));
    }

    public void draw(GraphicsContext ctx) {
      ctx.save();

      ctx.translate(position.x, position.y);

      ctx.setLineWidth(0.03);
      ctx.strokeLine(
        line.getStart().x,
        line.getStart().y,
        line.getEnd().x,
        line.getEnd().y
      );

      ctx.restore();
    }
  }

  class Asteroid implements HasShape, HasPosition {

    private Polygon polygon;
    private Rigidbody rigidbody;

    @Override
    public Shape getShape() {
      return polygon;
    }

    @Override
    public Vector2 getPosition() {
      return rigidbody.getPosition();
    }

    Asteroid(
      Rigidbody rigidbody,
      double maxAngleStep,
      double minRadius,
      double maxRadius
    ) {
      this.rigidbody = rigidbody;

      List<Vector2> points = new ArrayList<>();

      for (double theta = 0; theta < 2 * Math.PI - 1e-6;) {
        theta = Math.min(theta + random.nextDouble(maxAngleStep), 2 * Math.PI);
        double r = random.nextDouble(minRadius, maxRadius);
        Vector2 point = Vector2.fromPolar(r, theta);
        points.add(point);
      }

      Edges edges = new Edges(points);
      polygon = new Polygon(edges);
    }

    public void draw(GraphicsContext ctx) {
      ctx.save();

      rigidbody.translate(ctx);
      rigidbody.rotate(ctx);

      ctx.setLineWidth(0.03);

      Edges edges = polygon.edges();
      edges.stroke(ctx);

      ctx.restore();
    }

    public void update(double dt) {
      // Rigidbody updates position
      rigidbody.update(dt);
    }
  }

  class Pellet implements HasShape, HasPosition {

    private final Vector2 position, velocity;
    private static final double PELLET_LENGTH = 0.1;
    private Edges line;

    @Override
    public Shape getShape() {
      return line;
    }

    @Override
    public Vector2 getPosition() {
      return position;
    }

    public Pellet(Vector2 position, Vector2 velocity) {
      this.position = position;
      this.velocity = velocity;
      this.line =
        new Edges(List.of(new Vector2(0, 0), velocity.rescale(PELLET_LENGTH)));
    }

    public void update(double dt) {
      this.position.plusEquals(velocity.times(dt));
    }

    public void draw(GraphicsContext ctx) {
      ctx.save();

      ctx.translate(position.x, position.y);

      ctx.setLineWidth(0.03);
      line.stroke(ctx);

      ctx.restore();
    }
  }

  private int nAsteroids = 5;
  private List<Asteroid> asteroids = new ArrayList<>();
  private List<DestroyedSegment> destroyedSegments = new ArrayList<>();
  private List<Pellet> pellets = new ArrayList<>();

  private int score = 0;
  private boolean ticking = true;

  public void setTicking(boolean ticking) {
    this.ticking = ticking;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getScore() {
    return score;
  }

  private void updateArrays(double dt) {
    for (Asteroid asteroid : asteroids) {
      asteroid.update(dt);
    }

    for (DestroyedSegment segment : destroyedSegments) {
      segment.update(dt);
    }

    for (Pellet pellet : pellets) {
      pellet.update(dt);
    }
  }

  public void drawArrays(GraphicsContext ctx) {
    for (Asteroid asteroid : asteroids) {
      asteroid.draw(ctx);
    }

    for (DestroyedSegment segment : destroyedSegments) {
      segment.draw(ctx);
    }

    for (Pellet pellet : pellets) {
      pellet.draw(ctx);
    }
  }

  private final Random random = new Random();

  public static Asteroids instance = null;

  public Asteroids() {
    instance = this;
    for (int i = 0; i < nAsteroids; i++) {
      asteroids.add(spawn());
    }
  }

  public void shoot(Rigidbody shooter, Vector2 offset) {
    double pelletSpeed = 10;
    Vector2 velocity = offset.rescale(pelletSpeed);
    Pellet pellet = new Pellet(shooter.getPosition().plus(offset), velocity);
    pellets.add(pellet);
  }

  private Asteroid spawn() {
    double xSign = random.nextBoolean() ? 1 : -1;
    double ySign = random.nextBoolean() ? 1 : -1;

    double xRange = 3;
    double yRange = 3;

    double x =
      (random.nextDouble(xRange) + Field.WIDTH_METERS / 2) *
      xSign +
      Field.WIDTH_METERS /
      2;
    double y =
      (random.nextDouble(yRange) + Field.HEIGHT_METERS / 2) *
      ySign +
      Field.HEIGHT_METERS /
      2;

    Vector2 position = new Vector2(x, y);

    double mass = 1, MOMENT_OF_INERTIA = 1, heading = 0;

    Rigidbody rigidbody = new Rigidbody(
      position,
      heading,
      mass,
      MOMENT_OF_INERTIA
    );

    // Fling the asteroid toward some point on the field with some rotation
    double fieldX = random.nextDouble(
      Field.WIDTH_METERS
    ), fieldY = random.nextDouble(Field.HEIGHT_METERS);
    Vector2 fieldPosition = new Vector2(fieldX, fieldY);

    double minSpeed = 2;
    double maxSpeed = 5;

    double speed = random.nextDouble(minSpeed, maxSpeed);

    rigidbody.setVelocity(fieldPosition.minus(position).rescale(speed));

    // rigidbody.applyImpulse(fieldPosition.minus(position), torque, 1);

    double step = Math.toRadians(random.nextDouble(20, 50));

    double minRadius = 0.3;
    double maxRadius = 1.0;

    Asteroid asteroid = new Asteroid(rigidbody, step, minRadius, maxRadius);

    return asteroid;
  }

  public boolean collidesWithAsteroids(Shape shape, Vector2 position) {
    for (Asteroid asteroid : asteroids) {
      Vector2 asteroidPosition = asteroid.getPosition();
      boolean collides = asteroid.polygon.intersects(
        shape,
        position.x - asteroidPosition.x,
        position.y - asteroidPosition.y
      );
      if (collides) {
        return true;
      }
    }
    return false;
  }

  public void addDestroyedSegments(
    Edges edges,
    Vector2 position,
    Vector2 velocity,
    double rotationRadians
  ) {
    for (int i = 0; i < edges.n(); i++) {
      Vector2 start = edges.point(i), end = edges.point(i + 1);

      start = start.rotate(rotationRadians);
      end = end.rotate(rotationRadians);

      Line line = new Line(start, end);
      DestroyedSegment segment = new DestroyedSegment(line, position, velocity);
      destroyedSegments.add(segment);
    }
  }

  private void collidePelletsAndAsteroids() {
    // You have to be careful about removing elements from an array
    // while iterating through it. If you go from the end to the start,
    // then you can do it safely.
    int nAsteroidsRemoved = 0;

    for (int pelletI = pellets.size() - 1; pelletI >= 0; pelletI--) {
      for (int asteroidI = asteroids.size() - 1; asteroidI >= 0; asteroidI--) {
        Asteroid asteroid = asteroids.get(asteroidI);
        Pellet pellet = pellets.get(pelletI);

        Vector2 asteroidPosition = asteroid.rigidbody.getPosition();
        Vector2 pelletPosition = pellet.getPosition();

        boolean intersect = asteroid.polygon.intersects(
          pellet.line,
          pelletPosition.x - asteroidPosition.x,
          pelletPosition.y - asteroidPosition.y
        );

        if (intersect) {
          // Hit!
          addDestroyedSegments(
            asteroid.polygon.edges(),
            asteroid.getPosition(),
            asteroid.rigidbody.getVelocity(),
            asteroid.rigidbody.getHeadingRadians()
          );
          pellets.remove(pelletI);
          asteroids.remove(asteroidI);
          nAsteroidsRemoved += 1;
          score += SCORE_PER_ASTEROID;
          break;
        }
      }
    }

    for (int i = 0; i < nAsteroidsRemoved; i++) {
      asteroids.add(spawn());
    }
  }

  /** This function is very well unnecessary
   * —it's better to copy and paste than to cram
   * in an awkward abstraction—but I wanted to show
   * some Java type programming. (Which, compared to a
   * fancier language like Haskell, is extremely limited.)
   */
  private <T extends HasShape & HasPosition> int removeOutOfBoundsObjects(
    List<T> objects
  ) {
    int nRemoved = 0;

    for (int i = objects.size() - 1; i >= 0; i--) {
      T object = objects.get(i);
      Vector2 position = object.getPosition();
      Shape shape = object.getShape();

      boolean inField = Field.shape.intersects(
        shape,
        position.x - Field.WIDTH_METERS / 2,
        position.y - Field.HEIGHT_METERS / 2
      );

      if (!inField) {
        objects.remove(i);
        nRemoved += 1;
      }
    }

    return nRemoved;
  }

  public void update(double dt) {
    collidePelletsAndAsteroids();
    removeOutOfBoundsObjects(pellets);
    removeOutOfBoundsObjects(destroyedSegments);

    // For each asteroid destroyed, spawn a new one.
    int nAsteroidsRemoved = removeOutOfBoundsObjects(asteroids);
    for (int i = 0; i < nAsteroidsRemoved; i++) {
      asteroids.add(spawn());
    }

    if (ticking) {
      score += 1;
    }

    updateArrays(dt);
  }

  public void draw(GraphicsContext ctx) {
    drawArrays(ctx);
  }
}
