package swerve;

import java.util.List;
import javafx.scene.canvas.GraphicsContext;

/**
 * A shape has a boundary defined by an Edges object,
 * and a hit() method that returns whether a point is inside it.
 */
public abstract class Shape {

  /** List of points defining a closed surface. */
  public static class Edges extends Shape {

    private final List<Vector2> points;
    private final int n;

    public Edges(List<Vector2> points) {
      this.points = points;
      this.n = points.size();
    }

    public int n() {
      return n;
    }

    public Vector2 point(int i) {
      return points.get(i % n);
    }

    public void stroke(GraphicsContext ctx) {
      ctx.beginPath();
      for (int i = 0; i < n + 1; i++) {
        Vector2 point = point(i);
        if (i == 0) ctx.moveTo(point.x, point.y); else ctx.lineTo(
          point.x,
          point.y
        );
      }
      ctx.stroke();
    }

    public boolean intersects(Edges other, double otherX, double otherY) {
      Vector2 localPosition = new Vector2(otherX, otherY);

      // Naively iterates through all combinations of line-line pairs
      // to see if any intersect each other.
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < other.n; j++) {
          Vector2 selfStart = point(i);
          Vector2 selfEnd = point(i + 1);

          Vector2 otherStart = other.point(j).plus(localPosition);
          Vector2 otherEnd = other.point(j + 1).plus(localPosition);

          if (linesIntersect(selfStart, selfEnd, otherStart, otherEnd)) {
            return true;
          }
        }
      }

      return false;
    }

    @Override
    public boolean hit(double localX, double localY) {
      // A polyline is one-dimensional, so nothing can "hit" it
      return false;
    }

    @Override
    public Edges edges() {
      return this;
    }
  }

  public static class Line extends Shape {

    private Vector2 start, end;

    public Line(Vector2 start, Vector2 end) {
      this.start = start;
      this.end = end;
    }

    public Vector2 getStart() {
      return start;
    }

    public Vector2 getEnd() {
      return end;
    }

    public void setStart(Vector2 start) {
      this.start = start;
    }

    public void setEnd(Vector2 end) {
      this.end = end;
    }

    @Override
    public boolean hit(double localX, double localY) {
      return false;
    }

    @Override
    public Edges edges() {
      return new Edges(List.of(start, end));
    }
  }

  /** Returns whether given x, y coordinates
   * (implicitly relative or "local" to the shape's *center*) are inside.
   */
  public abstract boolean hit(double localX, double localY);

  /** Returns the edges that define the shape. */
  public abstract Edges edges();

  public boolean intersects(Shape other, double localX, double localY) {
    // If either the edges of the two shapes intersect,
    // or the center of the first shape is inside the other,
    // the shapes intersect. (I think this is true?)
    // We find the latter by transforming the center of the first shape
    // to be relative to the center of the second and calling .hit().
    boolean edgesIntersect = edges().intersects(other.edges(), localX, localY);
    boolean centerInside = hit(localX, localY);
    return edgesIntersect || centerInside;
  }

  /** Returns whether two lines, (a, b) -> (c, d) and (p, q) -> (r, q), intersect.
   * Where does it come from? Linear algebra! You can define a matrix that maps
   * "line space" to "world space", then apply it to the offset of (p, q) from (a, b)
   * to get the intersection point (if it exists) in "line space". If its two new
   * line coordinates, lambda and gamma, are actually on both line segments
   * (both are between 0 and 1) then the intersection point is on both line segments.
   * I have a little Desmos for this: https://www.desmos.com/calculator/8em6l1t2qo.
   */
  private static boolean linesIntersect(
    double a,
    double b,
    double c,
    double d,
    double p,
    double q,
    double r,
    double s
  ) {
    double det = (c - a) * (s - q) - (r - p) * (d - b);
    if (det == 0) {
      return false;
    } else {
      double lambda = ((s - q) * (r - a) + (p - r) * (s - b)) / det;
      double gamma = ((b - d) * (r - a) + (c - a) * (s - b)) / det;
      return (0 < lambda && lambda < 1) && (0 < gamma && gamma < 1);
    }
  }

  private static boolean linesIntersect(
    Vector2 start0,
    Vector2 end0,
    Vector2 start1,
    Vector2 end1
  ) {
    return linesIntersect(
      start0.x,
      start0.y,
      end0.x,
      end0.y,
      start1.x,
      start1.y,
      end1.x,
      end1.y
    );
  }

  public static class Rectangle extends Shape {

    private Edges edges = null;
    private final double width, height;

    public Rectangle(double width, double height) {
      this.width = width;
      this.height = height;
    }

    @Override
    public Edges edges() {
      if (edges == null) {
        edges =
          new Edges(
            List.of(
              new Vector2(-width / 2, height / 2),
              new Vector2(width / 2, height / 2),
              new Vector2(width / 2, -height / 2),
              new Vector2(-width / 2, -height / 2)
            )
          );
      }
      return edges;
    }

    @Override
    public boolean hit(double localX, double localY) {
      return (Math.abs(localX) <= width / 2 && Math.abs(localY) <= height / 2);
    }
  }

  public static class Polygon extends Shape {

    private final Edges edges;

    public Polygon(Edges edges) {
      this.edges = edges;
    }

    @Override
    public Edges edges() {
      return edges;
    }

    @Override
    public boolean hit(double localX, double localY) {
      /** One way of checking whether a point is inside a polygon
       * is to take a line (a "ray", like a ray of light) which starts
       * anywhere outside of the polygon and ends at that point, and count
       * how many edges the line intersects. If it intersects an even number
       * of edges, it has moved-in-then-moved-out some number of times.
       * If odd, then eventually it moved in but never moved out, and the
       * point must be inside the polygon.
       * https://en.wikipedia.org/wiki/Point_in_polygon
       */
      Vector2 rayStart = new Vector2(localX - 10_000, localY - 10_000);
      Vector2 rayEnd = new Vector2(localX, localY);

      int hits = 0;

      for (int i = 0; i < edges.n; i++) {
        Vector2 start = edges.point(i), end = edges.point(i + 1);
        boolean intersect = linesIntersect(rayStart, rayEnd, start, end);

        if (intersect) {
          hits += 1;
        }
      }

      boolean oddNumberOfHits = hits % 2 == 1;
      return oddNumberOfHits;
    }
  }
}
