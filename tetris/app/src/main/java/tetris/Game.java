package tetris;

import static tetris.Game.DrawingConstants.*;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/** This is the central class of our application,
 * as it stores and handles the actual game logic.
 *
 * I admit this is a bit messy (and perhaps someone
 * "better" at OOP/Java would break up the separable
 * concerns handled by this class into smaller classes)
 * but I think with enough time reading and messing around
 * --change the code and see what happens!--the gist
 * of what goes on isn't too hard to grasp.
 */
public class Game {

  private static int BOARD_WIDTH = 10;
  private static int BOARD_HEIGHT = 20;

  private double ticksPerStep;
  private double ticksSinceLastStep;

  private Board board;
  private Bag bag;

  /** Note that we have the piece (its data)
   * and its position on the board as separate
   * objects. Similar to how the JavaFX DrawingContext
   * has different "spaces" (through calling its scale, rotate
   * and translate functions), we maintain a "board space"
   * and a local "piece space", where we have to be explicit
   * about the difference (e.g. the latter looks like pieceX/pieceY).
   */
  private Piece piece;
  private Position piecePosition;

  private int lines;
  private Score score;
  private Level level;

  private boolean gameOver = false;

  private static int calculateTicksPerUpdate(int level) {
    return Math.max(1, 20 - level);
  }

  public Game() {
    reset();
  }

  /** The following public methods are the interface to Game.java.
   * Namely, they're how App.java, where `public static void main(String[] args)` is,
   * bridges the gap between the user and the state of our game.
   */

  /** Resets/initializes our game state. */
  public void reset() {
    ticksPerStep = calculateTicksPerUpdate(1);
    ticksSinceLastStep = 0;

    board = new Board(10, 20);
    bag = new Bag();
    piece = bag.nextPiece();
    piecePosition = board.nextPiecePosition(piece);

    lines = 0;
    score = new Score();
    level = new Level();
  }

  /** Called every tick. Drops the piece (step()) at a rate
   * based on how advanced the current level is.
   */
  public void update() {
    if (ticksSinceLastStep == ticksPerStep) {
      step();
      ticksSinceLastStep = 0;
    } else {
      ticksSinceLastStep += 1;
    }
  }

  /** The controls to the game. */
  public void rotateRight() {
    tryToRotate(RotationDirection.RIGHT);
  }

  public void rotateLeft() {
    tryToRotate(RotationDirection.LEFT);
  }

  public void moveRight() {
    tryToMove(1, 0);
  }

  public void moveLeft() {
    tryToMove(-1, 0);
  }

  public void moveDown() {
    tryToMove(0, 1);
  }

  public void flip() {
    tryToFlip();
  }

  public void drop() {
    if (board.collides(piece, piecePosition.getX(), piecePosition.getY() + 1)) {
      step();
      return;
    }

    for (int dy = 1; dy < board.getHeight(); dy++) {
      if (
        board.collides(piece, piecePosition.getX(), piecePosition.getY() + dy)
      ) {
        piecePosition.add(0, dy - 1);
        step();
        return;
      }
    }
  }

  /** Core game logic. Drops the piece.
   * If it falls onto (collides with) something,
   * place it, collect score, reset the piece, and
   * then check if the game is over or not.
   */
  private void step() {
    if (gameOver) {
      return;
    }

    if (board.collides(piece, piecePosition.getX(), piecePosition.getY() + 1)) {
      int linesJustCleared = board.place(
        piece,
        piecePosition.getX(),
        piecePosition.getY()
      );

      lines += linesJustCleared;
      score.clear(linesJustCleared);
      level.clear(linesJustCleared);
      ticksPerStep = calculateTicksPerUpdate(level.get());

      piece = bag.nextPiece();
      piecePosition = board.nextPiecePosition(piece);

      // The "lose condition" is when a new piece
      // can't be spawned in without colliding
      // with the existing grid.
      // In Tetris, this is called "topping out".
      if (board.collides(piece, piecePosition.getX(), piecePosition.getY())) {
        gameOver = true;
        return;
      }
    } else {
      // Drop a row
      piecePosition.setY(piecePosition.getY() + 1);
    }
  }

  private void tryToMove(int deltaX, int deltaY) {
    boolean validMove = !board.collides(
      piece,
      piecePosition.getX() + deltaX,
      piecePosition.getY() + deltaY
    );

    if (validMove) {
      piecePosition.add(deltaX, deltaY);
    }
  }

  private void tryToFlip() {
    piece.flip();

    boolean shouldReset = board.collides(
      piece,
      piecePosition.getX(),
      piecePosition.getY()
    );

    if (shouldReset) {
      piece.flip();
    }
  }

  /** Another helper for rotating the piece.
   *
   * The same logic is used for rotating both left
   * and right, so the direction is passed as an argument.
   * You *could* use a boolean, but I redundantly define
   * and use an enum to be explicit. (And it's obvious what
   * the caller of the function is trying to do, whereas the
   * meaning of true/false needs more context.)
   */

  enum RotationDirection {
    LEFT,
    RIGHT,
  }

  private void tryToRotate(RotationDirection direction) {
    if (
      direction == RotationDirection.RIGHT
    ) piece.rotateRight(); else piece.rotateLeft();

    boolean invalid = board.collides(
      piece,
      piecePosition.getX(),
      piecePosition.getY()
    );
    if (invalid) {
      // If the piece can't rotate without hitting the wall,
      // try seeing if we can fit it in by moving it left or right.
      // (Another term: in Tetris, this is a "wall kick".)
      for (int d = 1; d < piece.getSize(); d++) {
        for (int sign = -1; sign <= 1; sign++) {
          if (sign == 0) continue;
          int dx = sign * d;
          boolean valid = !board.collides(
            piece,
            piecePosition.getX() + dx,
            piecePosition.getY()
          );
          if (valid) {
            piecePosition.add(dx, 0);
            return;
          }
        }
      }

      // Can't rotate to a valid state, so undo rotation.
      if (
        direction == RotationDirection.RIGHT
      ) piece.rotateLeft(); else piece.rotateRight();
    }

    return;
  }

  /** Drawing logic. */
  public void draw(GraphicsContext ctx) {
    drawBoard(ctx);
    drawNextPieces(ctx);
    drawText(ctx);
  }

  /** A bag of helper constants for drawing.
   *
   * Again, drawing the game (or the "View" in the case
   * of an app or document) is typically one of those
   * concerns that is separated from the logic of how
   * the state changes. (Where the state is handled by "Model"s.)
   * A benefit of this being that you can treat the logic of the game/app,
   * and then how it looks, as different worlds.
   * But this game is simple enough to do everything in one file.
   */
  static class DrawingConstants {

    public static final int nPiecesToPreview = 4;

    public static final double textRightX = 60;

    public static final double slotSize = 10;
    public static final double gridGap = 1;
    public static final double screenStartX = textRightX + 10;
    public static final double screenStartY = 10;
    public static final double boardScreenWidth =
      BOARD_WIDTH * (slotSize + gridGap);
    public static final double boardScreenHeight =
      BOARD_HEIGHT * (slotSize + gridGap);

    public static final double boardBagGap = 20;
    public static final double piecePreviewGap = 10;
    public static final int maxSize = 4;

    public static final double textGap = 15;
    public static final double rowGap = 50;
  }

  private void drawText(GraphicsContext ctx) {
    ctx.setTextAlign(TextAlignment.RIGHT);
    ctx.setTextBaseline(VPos.TOP);

    ctx.fillText("Score", textRightX, screenStartY);
    ctx.fillText(
      String.valueOf(score.get()),
      textRightX,
      screenStartY + textGap
    );

    ctx.fillText("Lines", textRightX, screenStartY + rowGap);
    ctx.fillText(
      String.valueOf(lines),
      textRightX,
      screenStartY + rowGap + textGap
    );

    ctx.fillText("Level", textRightX, screenStartY + 2 * rowGap);
    ctx.fillText(
      String.valueOf(level.get()),
      textRightX,
      screenStartY + 2 * rowGap + textGap
    );

    if (gameOver) {
      ctx.save();

      ctx.setTextAlign(TextAlignment.LEFT);
      ctx.setFill(Color.RED);
      ctx.fillText("Game Over!", screenStartX, screenStartY);

      ctx.restore();
    }
  }

  private void drawNextPieces(GraphicsContext ctx) {
    for (int i = 0; i < nPiecesToPreview; i++) {
      Piece prototype = bag.peekPiecePrototype(i);

      double startX = screenStartX + boardScreenWidth + boardBagGap;
      double startY = screenStartY + i * (maxSize * slotSize + piecePreviewGap);

      for (int pieceX = 0; pieceX < prototype.getSize(); pieceX++) {
        for (int pieceY = 0; pieceY < prototype.getSize(); pieceY++) {
          if (prototype.get(pieceX, pieceY)) {
            double slotScreenX = startX + pieceX * (slotSize + gridGap);
            double slotScreenY = startY + pieceY * (slotSize + gridGap);

            ctx.fillRect(slotScreenX, slotScreenY, slotSize, slotSize);
          }
        }
      }
    }
  }

  private void drawBoard(GraphicsContext ctx) {
    ctx.strokeRect(
      screenStartX,
      screenStartY,
      boardScreenWidth,
      boardScreenHeight
    );

    for (int y = 0; y < board.getHeight(); y++) {
      for (int x = 0; x < board.getWidth(); x++) {
        int pieceX = x - piecePosition.getX(), pieceY =
          y - piecePosition.getY();

        boolean validPieceCoordinates =
          pieceX >= 0 &&
          pieceX < piece.getSize() &&
          pieceY >= 0 &&
          pieceY < piece.getSize();
        boolean pieceSlot = validPieceCoordinates && piece.get(pieceX, pieceY);

        // If coordinate is either at a slot "locked" in
        // on the board, or at where the piece is in "board coordinates",
        // then draw a grid cell.
        if (board.get(x, y) || pieceSlot) {
          double screenX = screenStartX + (slotSize + gridGap) * x;
          double screenY = screenStartY + (slotSize + gridGap) * y;
          ctx.fillRect(screenX, screenY, slotSize, slotSize);
        }
      }
    }
  }
}
