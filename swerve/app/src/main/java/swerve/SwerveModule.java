package swerve;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import swerve.Constants.Drivebase;
import swerve.Constants.PID;

public class SwerveModule {

  private final PIDController steerPid = new PIDController(
    PID.STEER_P,
    PID.STEER_I,
    PID.STEER_D
  );

  private final PIDController drivePid = new PIDController(
    PID.DRIVE_P,
    PID.DRIVE_I,
    PID.DRIVE_D
  );

  private Vector2 velocity = new Vector2(0, 0);
  private Vector2 targetVelocity = new Vector2(0, 0);

  private double headingAngle = 0;
  private double speedRotations = 0;

  public SwerveModule() {
    steerPid.setContinuous(-Math.PI, Math.PI);
  }

  public void drive(Vector2 targetVelocity) {
    this.targetVelocity = targetVelocity;
  }

  private static double metersToRotationsPerSecond(double meters) {
    return meters / Drivebase.WHEEL_CIRCUMFERENCE;
  }

  private static double rotationsPerSecondToMeters(double rotations) {
    return rotations * Drivebase.WHEEL_CIRCUMFERENCE;
  }

  public void update(double dt) {
    Vector2 heading = Vector2.unitCircle(headingAngle);
    double targetSpeed = targetVelocity.dot(heading);
    Vector2 currentTargetVelocity = targetVelocity.times(targetSpeed);

    double targetHeadingAngle = currentTargetVelocity.magnitude() < 1e-3
      ? targetVelocity.angle()
      : currentTargetVelocity.angle();
    double steering = steerPid.calculate(headingAngle, targetHeadingAngle);
    headingAngle =
      Utils.angleModulusRadians(
        headingAngle + Drivebase.STEERING_GEAR_RATIO * steering * dt
      );

    double targetSpeedRotations = metersToRotationsPerSecond(targetSpeed);
    double driving = drivePid.calculate(speedRotations, targetSpeedRotations);
    speedRotations += Drivebase.DRIVING_GEAR_RATIO * driving * dt;

    velocity =
      Vector2.fromPolar(
        rotationsPerSecondToMeters(speedRotations),
        headingAngle
      );
  }

  public void applyPhysics(
    Rigidbody rigidbody,
    Vector2 modulePosition,
    double dt
  ) {
    // Applied force (for simplicity the same as velocity)
    // is in *world* space, so we transform the velocity from
    // module space.
    Vector2 absoluteCurrentVelocity = velocity.rotate(
      -rigidbody.getHeadingRadians()
    );
    // But torque is applied "locally" to an object relative to
    // its center of mass (in this case the center of the drivebase),
    // so we don't rotate it here.
    double torque = modulePosition.crossZ(velocity); // Torque = r x F
    rigidbody.applyImpulse(absoluteCurrentVelocity, torque, dt);
    rigidbody.applyFriction(Drivebase.FRICTION, dt);
  }

  public void draw(GraphicsContext ctx, Vector2 modulePosition) {
    // Note that SwerveModule expects to be drawn
    // in *chassis space*. So (0, 0) is *not* the bottom left
    // of the screen like it is in "world space", but is the
    // center of the chassis.

    ctx.save();

    ctx.translate(modulePosition.x, modulePosition.y);

    velocity.draw(ctx, 0, 0, Color.GRAY);
    targetVelocity.draw(ctx, 0, 0, Color.GREEN);

    double moduleSizeMeters = 0.1;

    ctx.strokeRect(
      -moduleSizeMeters / 2,
      -moduleSizeMeters / 2,
      moduleSizeMeters,
      moduleSizeMeters
    );

    ctx.restore();
  }
}
