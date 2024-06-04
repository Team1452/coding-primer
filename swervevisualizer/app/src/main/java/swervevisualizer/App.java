package swervevisualizer;

import static swervevisualizer.Constants.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import swervevisualizer.Constants.Drivebase;
import swervevisualizer.Constants.Field;
import swervevisualizer.asteroids.Asteroids;

public class App extends Application {

  private static final double secondsPerUpdate = 1 / 60;

  private Swerve swerve = new Swerve();

  // Asteroids are toggleable, so at each tick
  // we either have and work with an existing Asteroids object,
  // or we don't.
  private Optional<Asteroids> asteroids = Optional.of(new Asteroids());

  private GraphicsContext ctx;
  private CheckBox fieldOrientedCheckbox = new CheckBox("Field oriented?") {
    {
      setLayoutX(10);
      setLayoutY(70);
      setSelected(true);
    }
  };
  private CheckBox asteroidsCheckbox = new CheckBox("Asteroids!") {
    {
      setLayoutX(10);
      setLayoutY(90);
      setSelected(true);
    }
  };

  // Variables for tracking what keys are held
  private Set<KeyCode> heldKeys = new HashSet<KeyCode>();

  private boolean holding(KeyCode key) {
    return heldKeys.contains(key);
  }

  public void drive() {
    // Process inputs, then update swerve
    Vector2 targetVelocity = new Vector2(0, 0);

    if (holding(KeyCode.W)) targetVelocity.plusEquals(new Vector2(0, 1));
    if (holding(KeyCode.A)) targetVelocity.plusEquals(new Vector2(-1, 0));
    if (holding(KeyCode.S)) targetVelocity.plusEquals(new Vector2(0, -1));
    if (holding(KeyCode.D)) targetVelocity.plusEquals(new Vector2(1, 0));

    targetVelocity =
      targetVelocity.normalize().times(Drivebase.MAX_SPEED_METERS_PER_SECOND);

    double targetAngularVelocity = 0;
    if (holding(KeyCode.E)) targetAngularVelocity +=
      Drivebase.MAX_ANGULAR_SPEED_RADIANS_PER_SECOND;
    if (holding(KeyCode.Q)) targetAngularVelocity -=
      Drivebase.MAX_ANGULAR_SPEED_RADIANS_PER_SECOND;

    boolean fieldOriented = fieldOrientedCheckbox.selectedProperty().get();

    swerve.drive(targetVelocity, targetAngularVelocity, fieldOriented);
  }

  private void update(double dt) {
    drive();
    asteroidsShoot(dt);

    swerve.update(dt);

    boolean enableAsteroids = asteroidsCheckbox.selectedProperty().get();
    if (enableAsteroids) {
      if (asteroids.isEmpty()) {
        asteroids = Optional.of(new Asteroids());
      }
    } else {
      if (asteroids.isPresent()) {
        asteroids = Optional.empty();
      }
    }

    if (asteroids.isPresent()) {
      asteroids.get().update(dt);
    }
  }

  private void draw() {
    /** Draw field objects */
    ctx.save();

    // Here we construct our "world space".
    // When we draw the "robot", we want to think
    // in terms of "meters" or the dimensions of the
    // field, not the size of the window or screen in
    // pixels (which could be anything).
    ctx.scale(
      SCREEN_WIDTH / Field.WIDTH_METERS,
      -SCREEN_HEIGHT / Field.HEIGHT_METERS
    );
    ctx.translate(0, -Field.HEIGHT_METERS);

    ctx.clearRect(0, 0, Field.WIDTH_METERS, Field.HEIGHT_METERS);

    swerve.draw(ctx);

    if (asteroids.isPresent()) {
      asteroids.get().draw(ctx);
    }

    ctx.restore();

    // Draw text to display some relevant variables.
    // In this case it is more convenient to just work
    // in terms of pixels, so we do this in screen space.
    Rigidbody rigidbody = swerve.getRigidbody();
    Vector2 velocity = rigidbody.getVelocity();
    Vector2 position = rigidbody.getPosition();
    ctx.fillText(
      String.format("Velocity: %.2f, %.2f", velocity.x, velocity.y),
      10,
      20
    );
    ctx.fillText(
      String.format("Position: %.2f, %.2f", position.x, position.y),
      10,
      40
    );
    ctx.fillText(
      String.format("Heading: %.2f deg", rigidbody.getHeadingDegrees()),
      10,
      60
    );
  }

  private void tick(double dt) {
    update(dt);
    draw();
  }

  public void start(Stage stage) {
    // Set up window, canvas/drawing context
    stage.setTitle("Swerve Visualizer");

    Canvas canvas = new Canvas();

    canvas.setWidth(Constants.SCREEN_WIDTH);
    canvas.setHeight(Constants.SCREEN_HEIGHT);

    ctx = canvas.getGraphicsContext2D();

    Group group = new Group(canvas, fieldOrientedCheckbox, asteroidsCheckbox);
    Scene scene = new Scene(
      group,
      Constants.SCREEN_WIDTH,
      Constants.SCREEN_HEIGHT
    );

    stage.setScene(scene);

    stage.show();

    scene.addEventHandler(
      KeyEvent.KEY_PRESSED,
      keyEvent -> {
        handleKeyPressed(keyEvent);
      }
    );

    scene.addEventHandler(
      KeyEvent.KEY_RELEASED,
      keyEvent -> {
        handleKeyReleased(keyEvent);
      }
    );

    // Schedule game loop to run every frame
    AnimationTimer loop = new AnimationTimer() {
      boolean calledYet = false;
      double lastCallTime;

      public void handle(long now) {
        if (!calledYet) {
          calledYet = true;
          lastCallTime = now;
          return;
        }

        double secondsElapsed = (now - lastCallTime) / 1e9;
        if (secondsElapsed >= secondsPerUpdate) {
          tick(secondsElapsed);
          lastCallTime = now;
        }
      }
    };
    loop.start();
  }

  private static final double SHOOT_DEBOUNCE = 0.1;
  private double sinceShootTime = 0;

  private void asteroidsShoot(double dt) {
    /** "Early return" if the precondition for shooting,
     * whether the corresponding key (X) is held,
     * is not met.
     */
    if (!heldKeys.contains(KeyCode.X)) {
      return;
    }

    if (asteroids.isPresent()) {
      sinceShootTime += dt;

      if (sinceShootTime >= SHOOT_DEBOUNCE) {
        sinceShootTime = 0;

        Rigidbody rigidbody = swerve.getRigidbody();
        asteroids
          .get()
          .shoot(rigidbody, rigidbody.getHeadingVector().rescale(0.3));
      }
    }
  }

  private void handleKeyReleased(KeyEvent keyEvent) {
    heldKeys.remove(keyEvent.getCode());
  }

  private void handleKeyPressed(KeyEvent keyEvent) {
    heldKeys.add(keyEvent.getCode());
  }

  public static void main(String[] args) {
    launch(args);
  }
}
