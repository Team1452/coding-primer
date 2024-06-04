package swervevisualizer.asteroids;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import swervevisualizer.Constants.Field;
import swervevisualizer.Rigidbody;
import swervevisualizer.Shape;
import swervevisualizer.Shape.Edges;
import swervevisualizer.Shape.Polygon;
import swervevisualizer.Shape.Rectangle;
import swervevisualizer.Vector2;

/** Encapsulates the (extraneous) "Asteroids" logic!
 * Manages both an array of "Asteroids"
 * and pellets, that can be shot from anywhere on the field.
 */
public class Asteroids {

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

      boolean intersects = false;

      for (Asteroid other : asteroids) {
        if (other == this) {
          continue;
        }

        Edges selfEdges = polygon.edges();
        Edges otherEdges = other.polygon.edges();

        Vector2 otherLocalPosition = other.rigidbody
          .getPosition()
          .minus(rigidbody.getPosition());

        intersects =
          selfEdges.intersects(
            otherEdges,
            otherLocalPosition.x,
            otherLocalPosition.y
          );

        if (intersects) {
          break;
        }
      }

      ctx.setStroke(intersects ? Color.RED : Color.BLACK);

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
  private List<Pellet> pellets = new ArrayList<>();

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

    double mass = 1, momentOfInertia = 1, heading = 0;

    Rigidbody rigidbody = new Rigidbody(
      position,
      heading,
      mass,
      momentOfInertia
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
          pellets.remove(pelletI);
          asteroids.remove(asteroidI);
          nAsteroidsRemoved += 1;
          break;
        }
      }
    }

    for (int i = 0; i < nAsteroidsRemoved; i++) {
      asteroids.add(spawn());
    }
  }

  /** This function is very well unnecessary
   * (it's better to copy and paste than to cram
   * in an awkward abstraction) but I wanted to show
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

    int nAsteroidsRemoved = removeOutOfBoundsObjects(asteroids);

    // Add back any destroyed
    for (int i = 0; i < nAsteroidsRemoved; i++) {
      asteroids.add(spawn());
    }

    for (Asteroid asteroid : asteroids) {
      asteroid.update(dt);
    }

    for (Pellet pellet : pellets) {
      pellet.update(dt);
    }
  }

  public void draw(GraphicsContext ctx) {
    for (Asteroid asteroid : asteroids) {
      asteroid.draw(ctx);
    }

    for (Pellet pellet : pellets) {
      pellet.draw(ctx);
    }
  }
}
