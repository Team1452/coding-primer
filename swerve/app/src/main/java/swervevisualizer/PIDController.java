package swerve;

public class PIDController {

  private double kP, kI, kD;

  private double totalError = 0;
  private double previousError = 0;

  private boolean continuous = false;
  private double minValue, maxValue;

  public PIDController(double kP, double kI, double kD) {
    this.kP = kP;
    this.kI = kI;
    this.kD = kD;
  }

  public void setPID(double kP, double kI, double kD) {
    this.kP = kP;
    this.kI = kI;
    this.kD = kD;
  }

  public void setContinuous(double minValue, double maxValue) {
    continuous = true;
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  public void reset() {
    totalError = 0;
    previousError = 0;
  }

  public double calculate(double measurement, double reference) {
    double error = reference - measurement;

    if (continuous) {
      if (Math.abs(error) > (maxValue - minValue) / 2) {
        if (error > 0) {
          error = error - maxValue + minValue;
        } else {
          error = error + maxValue - minValue;
        }
      }
    }

    totalError += error;
    double output = kP * error + kI * totalError + kD * (error - previousError);
    previousError = error;
    return output;
  }
}
