package org.ironriders.wrist;

import static org.ironriders.wrist.CoralWristConstants.D;
import static org.ironriders.wrist.CoralWristConstants.ENCODER_OFFSET;
import static org.ironriders.wrist.CoralWristConstants.FORWARD_LIMIT;
import static org.ironriders.wrist.CoralWristConstants.GEAR_RATIO;
import static org.ironriders.wrist.CoralWristConstants.I;
import static org.ironriders.wrist.CoralWristConstants.MAX_ACC;
import static org.ironriders.wrist.CoralWristConstants.MAX_VEL;
import static org.ironriders.wrist.CoralWristConstants.P;
import static org.ironriders.wrist.CoralWristConstants.REVERSE_LIMIT;
import static org.ironriders.wrist.CoralWristConstants.SECONDARY_WRIST_MOTOR;
import static org.ironriders.wrist.CoralWristConstants.ENCODER_SCALE;
import static org.ironriders.wrist.CoralWristConstants.WRIST_CURRENT_STALL_LIMIT;
import static org.ironriders.wrist.CoralWristConstants.PRIMARY_WRIST_MOTOR;
import static org.ironriders.wrist.CoralWristConstants.WRIST_TOLERANCE;

import org.ironriders.lib.data.PID;

import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class CoralAbsoluteWristSubsystem extends AbsoluteWristSubsystem {

  private final CoralWristCommands commands;

  public CoralAbsoluteWristSubsystem() {
    super(
      PRIMARY_WRIST_MOTOR,
      GEAR_RATIO,
      ENCODER_SCALE,
      ENCODER_OFFSET,
      REVERSE_LIMIT,
      FORWARD_LIMIT,
      false,
      new PID(P, I, D),
      new TrapezoidProfile.Constraints(MAX_VEL, MAX_ACC),
      WRIST_CURRENT_STALL_LIMIT,
      SECONDARY_WRIST_MOTOR
    );
    pid.setTolerance(WRIST_TOLERANCE);

    commands = new CoralWristCommands(this);
  }

  public CoralWristCommands getCommands() {
    return commands;
  }
}
