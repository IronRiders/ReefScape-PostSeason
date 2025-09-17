package org.ironriders.wrist;

public class WristConstants {
    public static final Integer PRIMARY_WRIST_MOTOR = 12;

    public static final Integer SECONDARY_WRIST_MOTOR = 13;

    // TODO: Need to tune
    public static final double P = 0.02; 
    public static final double I = 0.0;
    public static final double D = 0.0;

    public static final double T = 0.2;

    public static final double TOLERANCE = 0.05;

    public static final double MAX_ACC = 10; // After intial testing for each position velocity and acceleration should be increased a lot. 
    public static final double MAX_VEL = 20; // I would recomend bumping these up to 360 for acc and 180 for velocity 

    public static final double SPROCKET_RATIO = 1;
    public static final double GEAR_RATIO = SPROCKET_RATIO / 33.75;
    public static final double ENCODER_SCALE = GEAR_RATIO;
    public static final double CAD_POSITION_OFFSET = 45; // Adjustment for odd alignment in the CAD
    public static final double ENCODER_OFFSET = 0.935; // Rotations for the absolute encoder to get to zero when in FULLY stowed
    // The Arm should be inside the elevator at the start
}
