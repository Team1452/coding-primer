package tetris;

/** Wraps x/y coordinates. */
public class Position {

  /** These could just be public,
   * and you'd save some code, but
   * the convention is to explicitly
   * provide an interface to them through
   * "getters" and "setters".
   *
   * Though admittedly this is a pretty useless
   * abstraction (e.g. the role of a "Position" isn't
   * that well defined, in contrast to a vector),
   * so it really doesn't matter either way.
   */
  private int x, y;

  public Position(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public void add(int deltaX, int deltaY) {
    x += deltaX;
    y += deltaY;
  }
}
