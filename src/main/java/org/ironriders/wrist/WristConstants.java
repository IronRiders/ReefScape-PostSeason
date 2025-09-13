package org.ironriders.wrist;

public class WristConstants {
    public static final Integer PRIMARY_WRIST_MOTOR = 12;

    public static final Integer SECONDARY_WRIST_MOTOR = 13;

    // Need to tune
    public static final double P = 0.02; // TODO
    public static final double I = 0.0;
    public static final double D = 0.0;

    public static final double T = 0.2;

    public static final double TOLERANCE = 0.05;

    public static final double SPROCKET_RATIO = 1;
    public static final double GEAR_RATIO = SPROCKET_RATIO / 33.75;
    public static final double ENCODER_SCALE = GEAR_RATIO;
}
