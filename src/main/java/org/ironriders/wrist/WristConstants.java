package org.ironriders.wrist;

/** Class to store the wrist constants. */
public class WristConstants {
  public static final Integer PRIMARY_WRIST_MOTOR = 12;

  public static final Integer SECONDARY_WRIST_MOTOR = 13;

  // TODO: Need to tune
  public static final double P = 0.022; // proportion
  public static final double I = 0.0; // integral
  public static final double D = 0.0; // derivative
  public static final double T = 0.02; // time to next step

  public static final double TOLERANCE = 1;

  public static final double MAX_ACC = 180; // After intial testing for each position velocity and
  // acceleration should
  // be increased a lot.
  public static final double MAX_VEL = 360; // I would recomend bumping these up to 360 for acc
  // and 180 for velocity

  public static final double ENCODER_SCALE = 1;
  public static final double CAD_POSITION_OFFSET = 50; // Adjustment for odd alignment in the CAD
  public static final double ENCODER_OFFSET = 0.935;

  private WristConstants() {} // Rotations for the absolute encoder to get
  // to zero when in
  // FULLY stowed
  // The Arm should be inside the elevator at the start
}
