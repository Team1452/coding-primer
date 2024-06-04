package swervevisualizer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import swervevisualizer.Constants.Drivebase;
import swervevisualizer.Constants.Field;
import swervevisualizer.asteroids.Asteroids;

public class Swerve {

  private final double track = Drivebase.trackMeters;
  private final double wheelbase = Drivebase.wheelbaseMeters;

  private Vector2 centerOfRotation = new Vector2(0, 0);

  private final SwerveModule[] modules = new SwerveModule[] {
    new SwerveModule(), // Top right
    new SwerveModule(), // Top left
    new SwerveModule(), // Bottom right
    new SwerveModule(), // Bottom left
  };
  private final Vector2[] modulePositions = new Vector2[] {
    new Vector2(track / 2, wheelbase / 2),
    new Vector2(-track / 2, wheelbase / 2),
    new Vector2(track / 2, -wheelbase / 2),
    new Vector2(-track / 2, -wheelbase / 2),
  };

  private final Rigidbody rigidbody = new Rigidbody(
    // Starts in the center of the field
    new Vector2(Field.WIDTH_METERS / 2, Field.HEIGHT_METERS / 2),
    // Faces 0 degrees
    Math.toRadians(0),
    // Some made up physics constants
    Drivebase.massKg,
    Drivebase.momentOfInertia
  );

  public Rigidbody getRigidbody() {
    return rigidbody;
  }

  public void draw(GraphicsContext ctx) {
    ctx.save();

    // Draw with swerve at (0, 0)
    ctx.translate(rigidbody.getPosition().x, rigidbody.getPosition().y);

    Vector2 velocity = rigidbody.getVelocity();
    Vector2 velocityDirection = velocity.normalize();
    velocity.draw(ctx, velocityDirection.x, velocityDirection.y, Color.ORANGE);

    // Then rotate so 0 degrees is the front of the robot.
    ctx.rotate(Math.toDegrees(rigidbody.getHeadingRadians()));

    ctx.setLineWidth(0.03);

    // Body
    Shape shape = new Shape.Rectangle(wheelbase, track);

    if (Asteroids.instance != null) {
      if (
        Asteroids.instance.collidesWithAsteroids(shape, rigidbody.getPosition())
      ) {
        ctx.setStroke(Color.RED);
      }
    }

    ctx.strokeRect(-track / 2, -wheelbase / 2, track, wheelbase);

    // Arrow to indicate heading
    double arrowLength = wheelbase * 0.3;
    new Vector2(arrowLength, 0)
      .draw(ctx, -arrowLength / 2 - 0.02, 0, Color.RED);

    // Draw each module.
    for (int i = 0; i < modules.length; i++) {
      SwerveModule module = modules[i];
      Vector2 modulePosition = modulePositions[i];
      module.draw(ctx, modulePosition);
    }

    ctx.restore();
  }

  public void setCenterOfRotation(Vector2 centerOfRotation) {
    this.centerOfRotation = centerOfRotation;
  }

  public void drive(
    Vector2 targetTransVelocityMetersPerSecond,
    double targetAngularVelocityRadiansPerSecond,
    boolean fieldOriented
  ) {
    if (fieldOriented) {
      targetTransVelocityMetersPerSecond =
        targetTransVelocityMetersPerSecond.rotate(
          rigidbody.getHeadingRadians()
        );
    }

    Vector2[] moduleVelocities = new Vector2[modules.length];
    double maxSpeed = 0;

    for (int i = 0; i < modules.length; i++) {
      Vector2 modulePosition = modulePositions[i];
      Vector2 offset = modulePosition.minus(centerOfRotation);
      Vector2 perp = offset.ninetyCounterClockwise();
      Vector2 moduleVelocity = targetTransVelocityMetersPerSecond.plus(
        perp.times(targetAngularVelocityRadiansPerSecond)
      );
      moduleVelocities[i] = moduleVelocity;
      maxSpeed = Math.max(maxSpeed, moduleVelocity.magnitude());
    }

    if (maxSpeed > Drivebase.MAX_SPEED_METERS_PER_SECOND) {
      double scalingFactor = Drivebase.MAX_SPEED_METERS_PER_SECOND / maxSpeed;
      for (int i = 0; i < moduleVelocities.length; i++) {
        moduleVelocities[i] = moduleVelocities[i].times(scalingFactor);
      }
    }

    for (int i = 0; i < modules.length; i++) {
      SwerveModule module = modules[i];
      Vector2 moduleVelocity = moduleVelocities[i];
      module.drive(moduleVelocity);
    }
  }

  public void update(double dt) {
    for (int i = 0; i < modules.length; i++) {
      Vector2 position = modulePositions[i];
      SwerveModule module = modules[i];

      module.update(dt);
      module.applyPhysics(rigidbody, position, dt);
    }

    rigidbody.update(dt);
  }
}
