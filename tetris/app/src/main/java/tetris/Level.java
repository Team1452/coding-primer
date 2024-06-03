package tetris;

/** Handles calculating what level the player
 * is on. It takes 10 * n lines to be cleared to
 * progress to the next level, where n is the current level.
 */
public class Level {

  private int level = 1;
  private int linesToNextLevel = 10;

  public void clear(int lines) {
    linesToNextLevel -= lines;
    if (linesToNextLevel <= 0) {
      level += 1;
      linesToNextLevel += level * 10;
    }
  }

  public int get() {
    return level;
  }
}
