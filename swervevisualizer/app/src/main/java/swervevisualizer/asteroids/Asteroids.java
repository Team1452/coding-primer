package swervevisualizer.asteroids;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import swervevisualizer.Constants.Field;
import swervevisualizer.Entity;
import swervevisualizer.Rigidbody;
import swervevisualizer.Shape;
import swervevisualizer.Shape.Polygon;
import swervevisualizer.Vector2;

/** Encapsulates the (extraneous) "Asteroids" logic!
 * Manages both an array of "Asteroids"
 * and pellets, that can be shot from anywhere on the field.
 */
public class Asteroids extends Entity {

  /** The difference between a base/abstract class and an interface
   * is that a class can extend ("be") only one base class, but can
   * implement any number of interfaces.
   */
  interface HasShape {
    public abstract Shape getShape();
  }

  interface HasRigidbody {
    public abstract Rigidbody getRigidbody();
  }

  class Asteroid extends Entity implements HasShape, HasRigidbody {

    private Polygon polygon;
    private Rigidbody rigidbody;

    @Override
    public Shape getShape() {
      return polygon;
    }

    @Override
    public Rigidbody getRigidbody() {
      return rigidbody;
    }

    Asteroid(
      Entity parent,
      Rigidbody rigidbody,
      int nPoints,
      double minRadius,
      double maxRadius
    ) {
      super(parent);
      addChild(rigidbody);

      double[] xPoints = new double[nPoints], yPoints = new double[nPoints];

      double theta = 0;

      for (int i = 0; i < nPoints; i++) {
        double r = Math.random() * (maxRadius - minRadius) + minRadius;
        xPoints[i] = r * Math.cos(theta);
        yPoints[i] = r * Math.sin(theta);
        theta += Math.random() * (2 * Math.PI - theta);
      }

      polygon = new Shape.Polygon(xPoints, yPoints, nPoints);
    }

    @Override
    protected void drawLocal(GraphicsContext ctx) {
      ctx.save();

      rigidbody.translate(ctx);
      rigidbody.rotate(ctx);

      Shape.Edges edges = polygon.edges();
      ctx.strokePolygon(edges.xPoints(), edges.yPoints(), edges.n());

      ctx.restore();
    }

    @Override
    protected void updateLocal(double dt) {
      // Rigidbody updates position
    }
  }

  class Pellet extends Entity implements HasShape, HasRigidbody {

    private Rigidbody rigidbody;

    private static final double BULLET_LENGTH = 1;

    private Shape.Edges edges = new Shape.Edges(
      new double[] { 0, BULLET_LENGTH },
      new double[] { 0, 0 },
      2
    );

    @Override
    public Shape getShape() {
      return edges;
    }

    @Override
    public Rigidbody getRigidbody() {
      return rigidbody;
    }

    public Pellet(Entity parent, Rigidbody rigidbody) {
      super(parent);
      addChild(rigidbody);
    }

    @Override
    protected void updateLocal(double dt) {}

    @Override
    protected void drawLocal(GraphicsContext ctx) {
      ctx.save();

      rigidbody.translate(ctx);
      rigidbody.rotate(ctx);

      ctx.setStroke(Color.BLACK);
      ctx.strokePolyline(edges.xPoints(), edges.yPoints(), edges.n());

      ctx.restore();
    }
  }

  private int nAsteroids = 10;
  private List<Asteroid> asteroids = new ArrayList<>();
  private List<Pellet> pellets = new ArrayList<>();

  private final Random random = new Random();

  public Asteroids(Entity parent) {
    super(parent);
    for (int i = 0; i < nAsteroids; i++) {
      asteroids.add(spawn());
    }
  }

  public void shoot(Rigidbody shooter, Vector2 offset) {
    double pelletSpeed = 10;
    Rigidbody rigidbody = new Rigidbody(
      null,
      shooter.getPosition().plus(offset),
      offset.angle(),
      1,
      1
    );
    rigidbody.setVelocity(
      shooter.getVelocity().plus(Vector2.fromPolar(pelletSpeed, offset.angle()))
    );
    Pellet pellet = new Pellet(this, rigidbody);
    pellets.add(pellet);
  }

  private Asteroid spawn() {
    double xSign = random.nextBoolean() ? 1 : -1;
    double ySign = random.nextBoolean() ? 1 : -1;

    double xRange = 10;
    double yRange = 10;

    double x = (random.nextDouble(xRange) + Field.WIDTH_METERS) * xSign;
    double y = (random.nextDouble(yRange) + Field.HEIGHT_METERS) * ySign;

    Vector2 position = new Vector2(x, y);

    double mass = 10, momentOfInertia = 10, heading = 0;

    Rigidbody rigidbody = new Rigidbody(
      null,
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
    double torque = 10;

    rigidbody.applyImpulse(fieldPosition.minus(position), torque, 1);

    int nPoints = random.nextInt(4, 8);

    double minRadius = 3;
    double maxRadius = 10;

    Asteroid asteroid = new Asteroid(
      this,
      rigidbody,
      nPoints,
      minRadius,
      maxRadius
    );

    return asteroid;
  }

  private void collidePelletsAndAsteroids() {
    // You have to be careful about removing elements from an array
    // while iterating through it. If you go from the end to the start,
    // then you can do it safely.
    for (int pelletI = pellets.size() - 1; pelletI >= 0; pelletI--) {
      for (int asteroidI = asteroids.size() - 1; asteroidI >= 0; asteroidI--) {
        Asteroid asteroid = asteroids.get(asteroidI);
        Pellet pellet = pellets.get(pelletI);

        Vector2 asteroidPosition = asteroid.rigidbody.getPosition();
        Vector2 pelletPosition = pellet.rigidbody.getPosition();

        boolean intersect = asteroid.polygon.intersects(
          pellet.edges,
          asteroidPosition.x,
          asteroidPosition.y,
          pelletPosition.x,
          pelletPosition.y
        );

        if (intersect) {
          // Hit!
          pellet.destroy();
          asteroid.destroy();

          pellets.remove(pelletI);
          asteroids.remove(asteroidI);
        }
      }
    }
  }

  /** This function is very well unnecessary
   * (it's better to copy and paste than to cram
   * in an awkward abstraction) but I wanted to show
   * some Java type programming. (Which, compared to a
   * fancier language like Haskell, is extremely limited.)
   */
  private <
    T extends Entity & HasShape & HasRigidbody
  > int removeOutOfBoundsObjects(List<T> objects) {
    int nRemoved = 0;

    for (int i = objects.size() - 1; i >= 0; i--) {
      T object = objects.get(i);
      Vector2 position = object.getRigidbody().getPosition();
      Shape shape = object.getShape();

      boolean inField = Field.shape.intersects(
        shape,
        Field.WIDTH_METERS / 2,
        Field.HEIGHT_METERS / 2,
        position.x,
        position.y
      );

      if (!inField) {
        objects.remove(i);
        removeChild(object);
        nRemoved += 1;
      }
    }

    return nRemoved;
  }

  @Override
  protected void updateLocal(double dt) {
    collidePelletsAndAsteroids();

    removeOutOfBoundsObjects(pellets);

    int nAsteroidsRemove = removeOutOfBoundsObjects(asteroids);

    // Add back any destroyed
    for (int i = 0; i < nAsteroidsRemove; i++) {
      asteroids.add(spawn());
    }
  }

  @Override
  protected void drawLocal(GraphicsContext ctx) {}
}
