package org.ironriders.intake;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class CoralIntakeConstants {

    // public static final int CORAL_INTAKE_MOTOR = 12;
    public static final int CORAL_INTAKE_MOTOR_LEFT = -1; //TODO
    public static final int CORAL_INTAKE_MOTOR_RIGHT = -1; //TODO
    public static final int CORAL_INTAKE_MOTOR_TOP = -1; //TODO

    public static final InvertedValue CORAL_INTAKE_MOTOR_LEFT_INVERSION = InvertedValue.CounterClockwise_Positive; //TODO
    public static final InvertedValue CORAL_INTAKE_MOTOR_RIGHT_INVERSION = InvertedValue.CounterClockwise_Positive; //TODO
    public static final InvertedValue CORAL_INTAKE_MOTOR_TOP_INVERSION = InvertedValue.CounterClockwise_Positive; //TODO

    public static final NeutralModeValue CORAL_INTAK_NEUTRAL_MODE = NeutralModeValue.Brake; //TODO

    public static final int CORAL_INTAKE_BEAMBREAK = -1; //TODO plugged into di on rio

    public static final double DISCHARGE_TIMEOUT = 7.5; // these are both in SECONDS
    public static final double INTAKE_IMPATIENCE = 0.0; // how much time to wait for the limit switch before stopping
                                                        // the motor anyway

    public static final double MAX_ACC = .1;
    public static final double MAX_VEL = .1;

    public enum CoralIntakeState {
        GRAB(.25),
        EJECT(-.25),
        STOP(0.00),
        HOLD(.01);

        private final double speed;

        CoralIntakeState(double speed) {
            this.speed = speed;
        }

        public double getSpeed() {
            return speed;
        }
    }
}
