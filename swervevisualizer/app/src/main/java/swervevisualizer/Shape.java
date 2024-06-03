package swervevisualizer;

/**
 * A shape has a boundary defined by an Edges object,
 * and a hit() method that returns whether a point is inside it.
 */
public abstract class Shape {

  public static class Edges extends Shape {

    private final double[] xPoints, yPoints;
    private final int nPoints;

    public Edges(double[] xPoints, double[] yPoints, int nPoints) {
      this.xPoints = xPoints;
      this.yPoints = yPoints;
      this.nPoints = nPoints;
    }

    public double[] xPoints() {
      return xPoints;
    }

    public double[] yPoints() {
      return yPoints;
    }

    public int n() {
      return nPoints;
    }

    public double x(int i) {
      return xPoints[i % nPoints];
    }

    public double y(int i) {
      return yPoints[i % nPoints];
    }

    public boolean intersects(Edges other) {
      // Naively iterates through all combinations of line-line pairs
      // to see if any intersect each other.
      for (int i = 0; i < nPoints; i++) {
        for (int j = 0; j < other.nPoints; j++) {
          double a = x(i), b = y(i), c = x(i + 1), d = y(i + 1);
          double p = other.x(j), q = other.y(j), r = other.x(
            j + 1
          ), s = other.y(j + 1);
          if (linesIntersect(a, b, c, d, p, q, r, s)) {
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

  /** Returns whether given x, y coordinates
   * (implicitly relative or "local" to the shape's *center*) are inside.
   */
  public abstract boolean hit(double localX, double localY);

  /** Returns the edges that define the shape. */
  public abstract Edges edges();

  public boolean intersects(
    Shape other,
    double selfX,
    double selfY,
    double otherX,
    double otherY
  ) {
    // If either the edges of the two shapes intersect,
    // or the center of the first shape is inside the other,
    // the shapes intersect. (I think this is true?)
    // We find the latter by transforming the center of the first shape
    // to be relative to the center of the second and calling .hit().
    return (
      edges().intersects(other.edges()) ||
      other.hit(selfX - otherX, selfY - otherY)
    );
  }

  /** Returns whether two lines, (a, b) -> (c, d) and (p, q) -> (r, q), intersect.  */
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
    double det = (a - c) * (q - s) - (b - d) * (p - r);
    if (det == 0) {
      return false;
    } else {
      double lambda = (-q * r + b * (r - p) + a * (q - s) + p * s) / det;
      return 0 <= lambda && lambda <= 1;
    }
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
            new double[] { -width / 2, width / 2, width / 2, -width / 2 },
            new double[] { height / 2, height / 2, -height / 2, -height / 2 },
            4
          );
      }
      return edges;
    }

    @Override
    public boolean hit(double localX, double localY) {
      return Math.abs(localX) / 2 <= width && Math.abs(localY) / 2 <= height;
    }
  }

  public static class Polygon extends Shape {

    private final Edges edges;

    public Polygon(double[] xPoints, double[] yPoints, int nPoints) {
      edges = new Edges(xPoints, yPoints, nPoints);
    }

    @Override
    public Edges edges() {
      return edges;
    }

    @Override
    public boolean hit(double localX, double localY) {
      // One way of checking whether a point is inside a polygon
      // is to take a line which starts anywhere outside of the polygon
      // and ends at the point, and count how many edges the line intersects.
      // If the line intersects an even number of edges, the point is outside,
      // otherwise is inside.
      double a = localX - 1000, b = localY - 1000, c = localX, d = localY;

      int hits = 0;

      for (int i = 0; i < edges.nPoints; i++) {
        double p = edges.x(i), q = edges.y(i), r = edges.x(i + 1), s = edges.y(
          i + 1
        );
        boolean intersect = linesIntersect(a, b, c, d, p, q, r, s);
        if (intersect) {
          hits += 1;
        }
      }

      boolean evenNumberOfHits = hits % 2 == 0;
      return evenNumberOfHits;
    }
  }
}
