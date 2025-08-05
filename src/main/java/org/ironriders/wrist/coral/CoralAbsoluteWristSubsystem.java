package org.ironriders.wrist.coral;

import static org.ironriders.wrist.coral.CoralWristConstants.WRIST_CURRENT_STALL_LIMIT;
import static org.ironriders.wrist.coral.CoralWristConstants.WRIST_MOTOR;
import static org.ironriders.wrist.coral.CoralWristConstants.WRIST_TOLERANCE;
import static org.ironriders.wrist.coral.CoralWristConstants.D;
import static org.ironriders.wrist.coral.CoralWristConstants.ENCODER_OFFSET;
import static org.ironriders.wrist.coral.CoralWristConstants.FORWARD_LIMIT;
import static org.ironriders.wrist.coral.CoralWristConstants.GEAR_RATIO;
import static org.ironriders.wrist.coral.CoralWristConstants.I;
import static org.ironriders.wrist.coral.CoralWristConstants.MAX_ACC;
import static org.ironriders.wrist.coral.CoralWristConstants.MAX_VEL;
import static org.ironriders.wrist.coral.CoralWristConstants.P;
import static org.ironriders.wrist.coral.CoralWristConstants.REVERSE_LIMIT;
import static org.ironriders.wrist.coral.CoralWristConstants.SECONDARYWRISTMOTOR;
import static org.ironriders.wrist.coral.CoralWristConstants.SPROCKET_RATIO;

import org.ironriders.lib.data.PID;
import org.ironriders.wrist.AbsoluteWristSubsystem;

import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class CoralAbsoluteWristSubsystem extends AbsoluteWristSubsystem{
    private final CoralWristCommands commands;

    public CoralAbsoluteWristSubsystem() {
        super(
                WRIST_MOTOR,
                GEAR_RATIO,
                SPROCKET_RATIO,
                ENCODER_OFFSET,
                REVERSE_LIMIT,
                FORWARD_LIMIT,
                false,
                new PID(P, I, D),
                new TrapezoidProfile.Constraints(MAX_VEL, MAX_ACC),
                WRIST_CURRENT_STALL_LIMIT,
                false,
                SECONDARYWRISTMOTOR);

        pid.setTolerance(WRIST_TOLERANCE);

        commands = new CoralWristCommands(this);
    }

    public CoralWristCommands getCommands() {
        return commands;
    }
}
