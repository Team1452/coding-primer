package tetris;

import java.util.Arrays;

/** Handles the Tetris board, where
 * the board is a grid of slots.
 * (A multidimensional array of booleans.)
 *
 * Something to note if you're not familiar
 * is that contrary to how graphing works in math,
 * in programming a grid or a screen the point (0, 0)
 * is usually by default the top-left corner.
 * Positive x still means going to the right,
 * but positive y means going down from the top.
 * We use that same convention here in the Board grid,
 * and also in the Piece grid in Piece.java.
 */
public class Board {

  private boolean[][] board;

  private int height;
  private int width;

  public Board(int width, int height) {
    this.width = width;
    this.height = height;

    // We use "row-major" ordering, meaning that
    // the first index is the row (y coordinate)
    // and then the second is the column (x coordinate).
    this.board = new boolean[height][width];
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public boolean get(int x, int y) {
    // Again row-major ordering
    return board[y][x];
  }

  public void set(int x, int y, boolean filled) {
    board[y][x] = filled;
  }

  private boolean tryToClearRow(int y) {
    boolean cleared = true;
    for (int x = 0; x < width; x++) {
      if (!get(x, y)) {
        cleared = false;
        break;
      }
    }

    if (cleared) {
      // If the row is cleared, move all above rows down.
      // A nice thing about having the board be row major
      // is that we can just move the arrays around, which
      // saves some steps.
      for (int row = y; row >= 1; row--) {
        board[row] = board[row - 1];
      }

      // Top-most row is emptied out.
      Arrays.fill(board[0], false);
    }

    return cleared;
  }

  /** Places a piece at a given spot. Checks if
   * any changed rows/lines have been filled out ("cleared"),
   * then clears those rows and returns the number of lines cleared.
   */
  public int place(Piece piece, int x, int y) {
    if (collides(piece, x, y)) {
      throw new IllegalArgumentException(
        "Tried to place a piece at an invalid position!"
      );
    }

    int rowsCleared = 0;

    for (int pieceY = 0; pieceY < piece.getSize(); pieceY++) {
      int row = y + pieceY;
      boolean modifiedRow = false;
      for (int pieceX = 0; pieceX < piece.getSize(); pieceX++) {
        if (piece.get(pieceX, pieceY)) {
          set(x + pieceX, row, true);
          modifiedRow = true;
        }
      }

      if (modifiedRow) {
        if (tryToClearRow(row)) {
          rowsCleared += 1;
        }
      }
    }

    return rowsCleared;
  }

  /** Checks whether a piece, at a given
   * position (where the top-left corner would be),
   * would either hit a wall or a previously placed piece.
   */
  public boolean collides(Piece piece, int x, int y) {
    for (int pieceY = 0; pieceY < piece.getSize(); pieceY++) {
      for (int pieceX = 0; pieceX < piece.getSize(); pieceX++) {
        if (piece.get(pieceX, pieceY)) {
          int boardX = x + pieceX, boardY = y + pieceY;

          boolean hitsEdge =
            boardX < 0 || boardX >= width || boardY < 0 || boardY >= height;
          if (hitsEdge) return true;

          boolean collides = get(boardX, boardY);
          if (collides) return true;
        }
      }
    }
    return false;
  }

  /** Provides a valid starting position for a new piece.
   * In this case, just the middle of the top row of the board,
   * but you could get more creative (e.g. moving where the piece
   * starts up as the player gradually tops out).
   */
  public Position nextPiecePosition(Piece piece) {
    return new Position(width / 2 - piece.getSize() / 2, 0);
  }
}
