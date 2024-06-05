package swerve;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public class Vector2 {

  public double x, y;

  private boolean angleDirty;
  private double angle;

  public void update(double x, double y) {
    this.x = x;
    this.y = y;
    angleDirty = true;
  }

  public Vector2(double x, double y) {
    update(x, y);
  }

  public static Vector2 fromPolar(double radius, double theta) {
    return new Vector2(radius * Math.cos(theta), radius * Math.sin(theta));
  }

  public static Vector2 unitCircle(double theta) {
    return new Vector2(Math.cos(theta), Math.sin(theta));
  }

  public Vector2 unaryMinus() {
    return new Vector2(-x, -y);
  }

  public double dot(Vector2 other) {
    return x * other.x + y * other.y;
  }

  public Vector2 plus(Vector2 other) {
    return new Vector2(x + other.x, y + other.y);
  }

  public Vector2 minus(Vector2 other) {
    return new Vector2(x - other.x, y - other.y);
  }

  public Vector2 plusEquals(Vector2 other) {
    update(x + other.x, y + other.y);
    return this;
  }

  public Vector2 times(double scalar) {
    return new Vector2(x * scalar, y * scalar);
  }

  public Vector2 divide(double scalar) {
    return new Vector2(x / scalar, y / scalar);
  }

  public double crossZ(Vector2 other) {
    return x * other.y - y * other.x;
  }

  public double magnitude() {
    return Math.sqrt(x * x + y * y);
  }

  public Vector2 normalize() {
    double magnitude = magnitude();
    return magnitude < 1e-3 ? this : times(1 / magnitude());
  }

  public Vector2 rescale(double newMagnitude) {
    return normalize().times(newMagnitude);
  }

  public double angle() {
    // Only calculate the angle
    // if we have to (if x and y have
    // changed since last call.)
    if (angleDirty) {
      angle = Math.atan2(y, x);
    }
    return angle;
  }

  public Vector2 ninetyCounterClockwise() {
    return new Vector2(y, -x);
  }

  public Vector2 rotate(double radians) {
    double cos = Math.cos(radians), sin = Math.sin(radians);
    double rotatedX = cos * x + sin * y;
    double rotatedY = -sin * x + cos * y;
    return new Vector2(rotatedX, rotatedY);
  }

  @Override
  public String toString() {
    return String.format("Vector2(%.2f, %.2f)", x, y);
  }

  /** Visualize the direction and magnitude of this vector
   * by drawing it somewhere in the world.
   */
  public void draw(
    GraphicsContext ctx,
    double originX,
    double originY,
    Paint color
  ) {
    ctx.save();

    ctx.setLineWidth(0.03);
    ctx.setStroke(color);
    ctx.setFill(color);

    double endX = originX + x, endY = originY + y;
    ctx.strokeLine(originX, originY, endX, endY);

    Vector2 norm = normalize();
    Vector2 perp = norm.ninetyCounterClockwise();

    double headWidth = 0.1;
    double headLength = 0.1;

    double[] headPointsX = new double[] {
      endX + perp.x * headWidth,
      endX - perp.x * headWidth,
      endX + norm.x * headLength,
    };

    double[] headPointsY = new double[] {
      endY + perp.y * headWidth,
      endY - perp.y * headWidth,
      endY + norm.y * headLength,
    };

    ctx.fillPolygon(headPointsX, headPointsY, 3);

    ctx.restore();
  }
}
