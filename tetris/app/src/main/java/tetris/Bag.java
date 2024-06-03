package tetris;

import java.util.Random;

/** In Tetris, pieces aren't just
 * sampled randomly, but taken from
 * a pregenerated "bag" that iterates
 * through all 7 available pieces.
 */
public class Bag {

  /** Here we use the Prototype pattern.
   * This means we don't construct a new Piece from
   * scratch each time, but "clone" an existing one
   * we specify here.
   */
  private static final Piece[] prototypes = new Piece[] {
    new Piece(2, "...."), // O
    new Piece(3, "-.-...---"), // T
    new Piece(3, "..--..---"), // Z
    new Piece(3, "-....----"), // S
    new Piece(3, "-..-.--.-"), // J
    new Piece(3, "-.--.--.."), // L
    new Piece(4, "-.---.---.---.--"), // I
  };
  public static final int NUM_PIECES = prototypes.length;

  private static Random random = new Random();

  private static void shuffle(int[] array) {
    for (int i = 1; i < array.length; i++) {
      array[random.nextInt(i)] = array[i];
    }
  }

  private int[] bag = new int[NUM_PIECES];
  private int[] nextBag = new int[NUM_PIECES];
  private int grabIndex = 0;

  private int[] generatePieceIndices() {
    int[] indices = new int[NUM_PIECES];
    for (int i = 0; i < NUM_PIECES; i++) indices[i] = i;
    shuffle(indices);
    return indices;
  }

  public Piece nextPiece() {
    int prototypeIndex = bag[grabIndex];
    Piece piece = prototypes[prototypeIndex].clone();
    grabIndex++;
    if (grabIndex == NUM_PIECES) {
      bag = nextBag;
      nextBag = generatePieceIndices();
      grabIndex = 0;
    }
    return piece;
  }

  public Piece peekPiecePrototype(int n) {
    int index = grabIndex + n;
    if (index > NUM_PIECES * 2 - 1) {
      throw new IllegalArgumentException(
        "Don't have the " + index + "th next piece to preview."
      );
    }
    int prototypeIndex = index < NUM_PIECES
      ? bag[index]
      : nextBag[index - NUM_PIECES];
    return prototypes[prototypeIndex];
  }

  public void reset() {
    bag = generatePieceIndices();
    nextBag = generatePieceIndices();
  }

  public Bag() {
    reset();
  }
}
