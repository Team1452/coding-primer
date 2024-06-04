package swervevisualizer;

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
    public static final double massKg = 5;
    public static final double trackMeters = 0.7;
    public static final double wheelbaseMeters = 0.7;
    public static final double momentOfInertia = 2;

    // Taken from https://www.swervedrivespecialties.com/products/mk4i-swerve-module?variant=39598777172081
    public static final double steeringGearRatio = 1.0 / (150.0 / 7.0);
    public static final double drivingGearRatio = 1.0 / 6.75; // Ex. L2
    public static final double wheelDiameter = 0.1; // Ex. Colson wheels are 4"
    public static final double wheelCircumference = wheelDiameter * Math.PI;
  }

  public static class PID {

    public static final double driveP = 100;
    public static final double driveI = 0;
    public static final double driveD = 0;

    public static final double steerP = 500;
    public static final double steerI = 0;
    public static final double steerD = 0;
  }
}
