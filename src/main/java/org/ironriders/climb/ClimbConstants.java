package org.ironriders.climb;

public class ClimbConstants {

  public static final int CLIMBER_MOTOR_ID = 17;
  public static final int CURRENT_LIMIT = 40;

  //public static double ROTATION_MAXUP = -150;
  //public static double ROTATION_MAXDOWN = 40;

  public static final double MAX_ACC = 60;
  public static final double MAX_VEL = 60;

  public static double P = 1.0;
  public static double I = 0;
  public static double D = 0;
  public static double T = 0.02;

  public static double TOLERANCE = 0.005;

  public enum Targets {
    MIN(0),
    MAX(-123.5), // max position upward
    CLIMBED(40); // TEST and figure out (40 should be safe and visible) (This is supposed to be
                 // the angle where the robot is off the ground but not touching the chain)

    public final double pos;

    Targets(double pos) {
      this.pos = pos;
    }
  }
}
