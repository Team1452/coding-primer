package tetris;

/** Represents a piece, or "Tetromino".
 * Each piece is a square grid where each slot
 * is filled in or not (i.e. a boolean) that
 * can be rotated or flipped (rotated 180 degrees).
 */
public class Piece {

  private int size;

  /** We also use row-major ordering
   * here to be consistent.
   */
  private boolean[][] slots;

  public Piece(int size, boolean[][] slots) {
    this.size = size;
    this.slots = slots;
  }

  /** Convenience function for specifying a piece via a string. */
  public Piece(int size, String layout) {
    if (size * size != layout.length()) {
      throw new IllegalArgumentException(
        "Tried to define piece with layout length (" +
        layout.length() +
        ") but size " +
        size
      );
    }
    this.size = size;
    slots = new boolean[size][size];
    for (int i = 0; i < layout.length(); i++) {
      slots[i / size][i % size] = layout.charAt(i) == '.';
    }
  }

  public Piece clone() {
    return new Piece(size, slots);
  }

  public int getSize() {
    return size;
  }

  public boolean get(int x, int y) {
    // Row major
    return slots[y][x];
  }

  public void flip() {
    boolean[][] flippedSlots = new boolean[size][size];
    for (int y = 0; y < size; y++) {
      for (int x = 0; x < size; x++) {
        flippedSlots[y][x] = slots[size - 1 - y][x];
      }
    }
    slots = flippedSlots;
  }

  /**
   * [ -- row 1 -- ]         [   |      |      |   ]
   * [ -- row 2 -- ]   -->   [ row 3  row 2  row 1 ]
   * [ -- row 3 -- ]         [   |      |      |   ]
   */
  public void rotateRight() {
    boolean[][] rotatedSlots = new boolean[size][size];
    for (int y = 0; y < size; y++) {
      for (int x = 0; x < size; x++) {
        rotatedSlots[y][x] = slots[size - 1 - x][y];
      }
    }
    slots = rotatedSlots;
  }

  /**
   * [ 11 12 13 ]         [ 13 23 33 ]
   * [ 21 22 23 ]   -->   [ 12 22 32 ]
   * [ 31 32 33 ]         [ 11 21 31 ]
   */
  public void rotateLeft() {
    boolean[][] rotatedSlots = new boolean[size][size];
    for (int y = 0; y < size; y++) {
      for (int x = 0; x < size; x++) {
        rotatedSlots[y][x] = slots[x][size - 1 - y];
      }
    }
    slots = rotatedSlots;
  }
}
