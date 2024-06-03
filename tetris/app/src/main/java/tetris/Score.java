package tetris;

/** Handles calculating the score of the game
 * whenever the player clears lines.
 */
public class Score {

  private int score = 0;

  private int calculateScore(int lines) {
    /** Note that this function is hardcoded
     * to use the same max piece size that
     * happens to be in Bag.java. E.g., if you
     * wanted to add a 5x5 piece that could
     * clear 5 lines at once, you'd have to
     * update this function.
     * (That's what the exception is for: it'll tell you!)
     */
    switch (lines) {
      case 0:
        return 0;
      case 1:
        return 40;
      case 2:
        return 100;
      case 3:
        return 300;
      case 4:
        return 1200;
      default:
        throw new IllegalArgumentException(
          "Tried to score more than 4 lines (" + lines + ") being cleared."
        );
    }
  }

  public void clear(int lines) {
    score += calculateScore(lines);
  }

  public int get() {
    return score;
  }
}
