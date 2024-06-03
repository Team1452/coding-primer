package swervevisualizer;

import static swervevisualizer.Constants.SCREEN_HEIGHT;
import static swervevisualizer.Constants.SCREEN_WIDTH;

import java.util.HashSet;
import java.util.Set;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import swervevisualizer.Constants.Drivebase;
import swervevisualizer.Constants.Field;
import swervevisualizer.asteroids.Asteroids;

public class World extends Entity {

  private GraphicsContext ctx;
  private CheckBox fieldOrientedCheckbox = new CheckBox("Field oriented?") {
    {
      setLayoutX(10);
      setLayoutY(70);
      selectedProperty().set(true);
    }
  };

  private Swerve swerve = new Swerve(this);
  private Asteroids asteroids = new Asteroids(this);

  private Set<KeyCode> heldKeys = new HashSet<>();

  private boolean holding(KeyCode key) {
    return heldKeys.contains(key);
  }

  public World() {
    super(null);
    asteroids.setActive(false);
  }

  private void tick(double dt) {
    processInputs();
    update(dt);
    draw(ctx);
  }

  private void processInputs() {
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

  private void drawText() {
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

  @Override
  public void draw(GraphicsContext ctx) {
    super.draw(ctx);
    drawText();
  }

  @Override
  protected void updateLocal(double dt) {}

  @Override
  protected void drawLocal(GraphicsContext ctx) {
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

    ctx.restore();
  }

  public void start(Stage stage) {
    // Set up window, canvas/drawing context
    stage.setTitle("Swerve Visualizer");

    Canvas canvas = new Canvas();

    canvas.setWidth(Constants.SCREEN_WIDTH);
    canvas.setHeight(Constants.SCREEN_HEIGHT);

    ctx = canvas.getGraphicsContext2D();

    Group group = new Group(canvas, fieldOrientedCheckbox);
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
      double lastCallTime = 0;

      public void handle(long now) {
        double secondsElapsed = (now - lastCallTime) / 1e9;
        if (secondsElapsed >= secondsPerUpdate) {
          tick(secondsElapsed);
          lastCallTime = now;
        }
      }
    };
    loop.start();
  }
}
