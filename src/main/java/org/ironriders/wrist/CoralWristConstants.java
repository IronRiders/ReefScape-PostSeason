package org.ironriders.wrist;

import org.ironriders.lib.data.MotorSetup;

import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;

public class CoralWristConstants {

  public static final MotorSetup PRIMARY_WRIST_MOTOR = new MotorSetup(
    -1,
    false
  ); // TODO

  public static final MotorSetup SECONDARY_WRIST_MOTOR = new MotorSetup(
    -1,
    false
  ); // TODO

  // Need to tune
  public static final double P = 0.01; //TODO
  public static final double I = 0.0;
  public static final double D = 0.0;

    public static final double SPROCKET_RATIO = 1; 
    public static final double GEAR_RATIO = 1.0 / 33.75;
    public static final double ENCODER_SCALE = GEAR_RATIO;

  public static final Angle ENCODER_OFFSET = Units.Degrees.of(0); //TODO TUNE
  public static final Angle REVERSE_LIMIT = Units.Degrees.of(-35); // TODO: TUNE
  public static final Angle FORWARD_LIMIT = Units.Degrees.of(55); // TODO: TUNE

  public static final double MAX_ACC = 90; //TODO
  public static final double MAX_VEL = 45;

  public static final int WRIST_CURRENT_STALL_LIMIT = 10; // lower for testing
    public static final double WRIST_TOLERANCE = 1; // tune me please

    public static final MotorSetup SECONDARYWRISTMOTOR = new MotorSetup(1, false); // TODO Check inversion status

    public enum WristState {
        Intaking(-100),//TODO
        STOWED(30), // TODO
        L1(40), //TODO
        L2toL3(40), //TODO
        L4(-10); //TODO

    final Angle angle;

    WristState(double degrees) {
      this.angle = Units.Degrees.of(degrees);
    }

    public Angle getAngle() {
      return angle;
    }
  }
}
