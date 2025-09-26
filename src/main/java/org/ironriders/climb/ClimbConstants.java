package org.ironriders.climb;

public class ClimbConstants {

  public static final int CLIMBER_MOTOR_ID = 17;
  public static final int CURRENT_LIMIT = 40;

  public static final double MAX_ACC = 200;
  public static final double MAX_VEL = 200;

  public static final double ENCODER_SCALE = (1f/100f);

  public static double P = 0.05;  // proportion
  public static double I = 0;    // integral
  public static double D = 0;    // derivative
  public static double T = 0.02; // time to next step

  public static double TOLERANCE = 0.005;

  public enum Targets {
    MIN(0),
    MAX(-580), // max position upward
    CLIMBED(-123.5); // TEST and figure out (40 should be safe and visible) (This is supposed to be
                 // the angle where the robot is off the ground but not touching the chain)

    public final double pos;

    Targets(double pos) {
      this.pos = pos;
    }
  }
}
