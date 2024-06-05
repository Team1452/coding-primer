package swerve;

public class Constants {

  public static final double SCREEN_WIDTH = 400;
  public static final double SCREEN_HEIGHT = 400;

  public static class Field {

    public static final double WIDTH_METERS = 10;
    public static final double HEIGHT_METERS = 10;

    public static final Shape shape = new Shape.Rectangle(
      WIDTH_METERS,
      HEIGHT_METERS
    );
  }

  public static class Drivebase {

    public static final double MAX_SPEED_METERS_PER_SECOND = 20;
    public static final double MAX_ANGULAR_SPEED_RADIANS_PER_SECOND = Math.toRadians(
      1000
    );

    public static final double ALIGN_IMPORTANCE = 50;

    public static final double FRICTION = 0.8;
    public static final double MASS_KG = 2;
    public static final double TRACK_METERS = 0.7;
    public static final double WHEELBASE_METERS = 0.7;
    public static final double MOMENT_OF_INERTIA = 0.5;

    public static final Shape SHAPE = new Shape.Rectangle(
      TRACK_METERS,
      WHEELBASE_METERS
    );

    // Taken from https://www.swervedrivespecialties.com/products/mk4i-swerve-module?variant=39598777172081
    public static final double STEERING_GEAR_RATIO = 1.0 / (150.0 / 7.0);
    public static final double DRIVING_GEAR_RATIO = 1.0 / 6.75; // Ex. L2
    public static final double WHEEL_DIAMETER = 0.1; // Ex. Colson wheels are 4"
    public static final double WHEEL_CIRCUMFERENCE = WHEEL_DIAMETER * Math.PI;
  }

  public static class PID {

    public static final double DRIVE_P = 100;
    public static final double DRIVE_I = 0;
    public static final double DRIVE_D = 0;

    public static final double STEER_P = 500;
    public static final double STEER_I = 0;
    public static final double STEER_D = 0;
  }
}
