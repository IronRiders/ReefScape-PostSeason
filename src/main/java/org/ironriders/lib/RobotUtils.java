package org.ironriders.lib;

/** Utility class to encourage the robot's dangerous math addiction. */
public class RobotUtils {

  /**
   * Applies a control curve (currently just an exponential function) Only works with input values
   * from 0 to 1 because 1^x = 1.
   *
   * @param input The value to put into the curve (0.0 - 1.0 ONLY)
   * @param exponent The exponent value.
   * @return The end result of the curve.
   */
  public static double controlCurve(double input, double exponent, double deadband) {
    return Math.pow(input, exponent);
  }

  /**
   * Normalizes a rotational input value to the range [0, 360) degrees.
   *
   * @param input The input rotational value.
   * @return The normalized rotational value within the range [0, 360) degrees.
   */
  public static double absoluteRotation(double input) {
    return ((input % 360) + 360) % 360;
  }

  /**
   * if in > max: in = max; if in < min: in = min.
   *
   * @param min min
   * @param max max
   * @param in value
   * @return output
   */
  public static double clamp(double min, double max, double in) {
    if (in > max) {
      in = max;
    }
    if (in < min) {
      in = min;
    }
    return in;
  }

  /*
   * Checks if @param current is within @param tolerance of @param goal. Will return false if @param tolerance is less than zero.
   */
  public static boolean tolerance(double current, double goal, double tolerance) {
    if (tolerance < 0) {
      return false;
    }

    return Math.abs(current - goal) < tolerance;
  }
}
