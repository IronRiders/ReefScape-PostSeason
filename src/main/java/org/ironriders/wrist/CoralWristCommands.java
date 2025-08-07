package org.ironriders.wrist;

import org.ironriders.wrist.CoralWristConstants.WristState;

import edu.wpi.first.wpilibj2.command.Command;

public class CoralWristCommands {
    private final CoralAbsoluteWristSubsystem coralWrist;

    public CoralWristCommands(CoralAbsoluteWristSubsystem wrist) {
        this.coralWrist = wrist;

        wrist.publish("Home", home());
        wrist.publish("Wrist Intaking", set(WristState.Intaking));
        wrist.publish("Wrist Stowed", set(WristState.STOWED));
        wrist.publish("Wrist L1", set(WristState.L1));
        wrist.publish("Wrist L2-L3", set(WristState.L2toL3));
        wrist.publish("Wrist L4", set(WristState.L4));
    }

    public Command set(WristState state) {
        return coralWrist.moveToCmd(state.getAngle());
    }

    public Command reset() {
        return coralWrist.runOnce(coralWrist::reset);
    }

    public Command home() {
        return coralWrist.homeCmd();
    }

    public CoralAbsoluteWristSubsystem getCoralWrist() {
        return coralWrist;
    }
}
