package tetris;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * The entrypoint of our application.
 * Takes care of setting up JavaFX and running
 * the game loop (i.e. calling tick 60 times a second).
 */
public class App extends Application {

  private static final double screenWidth = 350;
  private static final double screenHeight = 350;

  private static final double ticksPerSecond = 60;

  /** Our JavaFX variables. Stage represents the
   * root level container, and GraphicsContext is
   * what lets us draw to the Canvas, which we set up
   * in initStage().
   */
  private Stage stage;
  private GraphicsContext ctx;

  /** The actual game logic I moved to its own
   * class to separate it from the highest level
   * details of the app (configuring JavaFX, handling
   * keyboard input, the game loop).
   */
  private Game game = new Game();

  private void draw() {
    ctx.clearRect(0, 0, screenWidth, screenHeight);
    game.draw(ctx);
  }

  private void update() {
    game.update();
  }

  /** The heart of our game: the game or "tick" loop.
   * However many times a second (currently 60), we update
   * the state of our game (spawn a new piece, let it fall,
   * increase the score, etc.) and then draw it to the screen.
   */
  private void tick() {
    update();
    draw();
  }

  /** This is where we handle the user hitting the key,
   * which is the only user input we currently use.
   */
  private void handleKeyPressed(KeyEvent keyEvent) {
    switch (keyEvent.getCode()) {
      case ESCAPE:
        {
          stage.close();
          break;
        }
      case RIGHT:
        {
          game.moveRight();
          break;
        }
      case LEFT:
        {
          game.moveLeft();
          break;
        }
      case DOWN:
        {
          game.moveDown();
          break;
        }
      case X:
      case UP:
        {
          game.rotateRight();
          break;
        }
      case Z:
        {
          game.rotateLeft();
          break;
        }
      case A:
        {
          game.flip();
          break;
        }
      case R:
        {
          game.reset();
          break;
        }
      case SPACE:
        {
          game.drop();
          break;
        }
      default:
        {
          // We don't care about any other keys.
          break;
        }
    }
  }

  private void initStage() {
    stage.setTitle("Tetris");

    Canvas canvas = new Canvas();

    canvas.setWidth(screenWidth);
    canvas.setHeight(screenHeight);

    ctx = canvas.getGraphicsContext2D();

    Group group = new Group(canvas);
    Scene scene = new Scene(group, screenWidth, screenHeight);

    stage.setScene(scene);

    stage.show();

    scene.addEventHandler(
      KeyEvent.KEY_PRESSED,
      // You could also pass the method directly as this::handleKeyPressed.
      keyEvent -> {
        handleKeyPressed(keyEvent);
      }
    );
  }

  private void initGameLoop() {
    double secondsPerTick = 1 / ticksPerSecond;

    AnimationTimer loop = new AnimationTimer() {
      double lastCallTime = 0;

      public void handle(long now) {
        /** By default the JavaFX AnimationTimer will
         * call handle() as many times a second as it can,
         * which is usually a lot over 60. We can make
         * the framerate more consistent by "debouncing",
         * where we limit the rate at which tick() is called
         * by first checking if enough time has passed.
         * (`now` is in nanoseconds, so we divide by 10^9 to get seconds.)
         */
        double secondsElapsed = (now - lastCallTime) / 1e9;
        if (secondsElapsed >= secondsPerTick) {
          tick();
          lastCallTime = now;
        }
      }
    };

    loop.start();
  }

  public void start(Stage stage) {
    this.stage = stage;

    initStage();
    initGameLoop();
  }

  public static void main(String[] args) {
    try {
      launch(args);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
