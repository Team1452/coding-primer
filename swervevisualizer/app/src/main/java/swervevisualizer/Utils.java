package swervevisualizer;

public class Utils {

  public static double modBetween(
    double input,
    double minimumInput,
    double maximumInput
  ) {
    double modulus = maximumInput - minimumInput;

    // Wrap input if it's above the maximum input
    int numMax = (int) ((input - minimumInput) / modulus);
    input -= numMax * modulus;

    // Wrap input if it's below the minimum input
    int numMin = (int) ((input - maximumInput) / modulus);
    input -= numMin * modulus;

    return input;
  }

  public static double angleModulusRadians(double angle) {
    return modBetween(angle, -Math.PI, Math.PI);
  }
}
