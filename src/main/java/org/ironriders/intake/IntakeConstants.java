package org.ironriders.intake;

import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class IntakeConstants {

  public static final int INTAKE_MOTOR_RIGHT = 14;
  public static final int INTAKE_MOTOR_LEFT = 15;
  public static final int INTAKE_MOTOR_TOP = 16;

  public static final InvertedValue INTAKE_MOTOR_LEFT_INVERSION = InvertedValue.CounterClockwise_Positive;
  public static final InvertedValue INTAKE_MOTOR_RIGHT_INVERSION = InvertedValue.Clockwise_Positive;
  public static final InvertedValue INTAKE_MOTOR_ROLLER_INVERSION = InvertedValue.CounterClockwise_Positive;

  public static final NeutralModeValue INTAKE_NEUTRAL_MODE = NeutralModeValue.Brake;

  public static final double LEFT_SPEED_MUL = 2;
  public static final double RIGHT_SPEED_MUL = 1;
  public static final double ROLLER_SPEED_MUL = 1;

  public static final double MAX_VEL = 1;
  public static final double MAX_ACC = 1;

  public static final double P = 0.1; // proportion
  public static final double I = 0.00; // integral
  public static final double D = 0.00; // derivative
  public static final double T = 0.02; // time to next step

  public static final double TOLERANCE = 0.05;

  public static final double INTAKE_JOG_TIME = 0.1;

  public static final double WHEEL_COMPLIANCE = 0.2; // Adjust for the wheels being able to squish
                                                     // in.

  public static final double WHEEL_DIAMETER = 4 - WHEEL_COMPLIANCE;
  public static final double WHEEL_CIRCUMFERENCE = (2 * Math.PI) * (WHEEL_DIAMETER / 2);

  public static final double TARGET_SETPOINT = WHEEL_CIRCUMFERENCE / 2f; // In inches, about half a
                                                                         // rotation seems good

  // TODO Tune These
  public static final int INTAKE_STATOR_CURRENT = 30; // Stator Current Torque and Acceleration
  public static final int INTAKE_SUPPLY_CURRENT = 40; // Supply Current Speed + (a little Torque).
                                                      // If Supply
                                                      // Current Lower Time is <= 0 then this will
                                                      // be the Supply
                                                      // Current
  public static final int INTAKE_SUPPLY_CURRENT_LOWER_LIMIT = 30; // Supply Current if
                                                                  // SupplyCurrentLowerTime is
                                                                  // >0 and applys after
  public static final int INTAKE_SUPPLY_CURRENT_LOWER_TIME = 1; // In Seconds. This determines how
                                                                // long Supply
                                                                // Current is applied and then limit
                                                                // is lowered to
                                                                // SupplyCurrentLowerLimit

  public static final int INTAKE_BEAMBREAK = 0;

  public static final double DISCHARGE_TIMEOUT = 2;

  public enum IntakeState {
    GRAB(.25), SCORE(.30), EJECT(-.10), STOP(0.00);

    public final double speed;

    IntakeState(double speed) {
      this.speed = speed;
    }
  }

  public enum IntakeJogState {
    UP(0.5), DOWN(-0.5);

    public final double increment;

    IntakeJogState(double increment) {
      this.increment = increment;
    }
  }
}
