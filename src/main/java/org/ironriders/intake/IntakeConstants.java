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
  public static final double RIGHT_SPEED_MUL = .5;
  public static final double ROLLER_SPEED_MUL = 1;

  // TODO Tune These
  public static final int INTAKE_STATOR_CURRENT = 30; // Stator Current ∝ Torque and Acceleration
  public static final int INTAKE_SUPPLY_CURRENT = 40; // Supply Current ∝ Speed + (a little Torque). If Supply
                                                            // Current Lower Time is <= 0 then this will be the Supply
                                                            // Current
  public static final int INTAKE_SUPPLY_CURRENT_LOWER_LIMIT = 30; // Supply Current if SupplyCurrentLowerTime is
                                                                        // >0 and applys after
  public static final int INTAKE_SUPPLY_CURRENT_LOWER_TIME = 1; // In Second. This determines how long Supply
                                                                      // Current is applied and then limit is lowered to
                                                                      // SupplyCurrentLowerLimit

  public static final int INTAKE_BEAMBREAK = 1; // TODO does not exist

  public static final double DISCHARGE_TIMEOUT = 7.5; // these are both in SECONDS
  public static final double INTAKE_IMPATIENCE = 0.0; // how much time to wait for the limit switch before stopping the
                                                      // motor anyway

  public static final double MAX_ACC = .1;
  public static final double MAX_VEL = .1;

  public enum IntakeState {
    GRAB(.25, 1),
    SCORE(.30, 1),
    EJECT(-.25, 0),
    STOP(0.00, 0),
    HOLD(.01, 1);

    public final double speed;
    public final double offset;

    IntakeState(double speed, double offset) {
      this.speed = speed;
      this.offset = offset;
    }

    public double getSpeed() {
      return speed;
    }

    public double getBool() {
      return offset;
    }
  }
}
