package org.ironriders.wrist;

import org.ironriders.wrist.WristConstants.WristState;

import edu.wpi.first.wpilibj2.command.Command;

public class WristCommand {
    WristSubsystem wristSubsystem;

    WristCommand(WristSubsystem wristSubsystem){
        this.wristSubsystem = wristSubsystem;
        wristSubsystem.debugPublish("Set STOWED", set(WristState.STOWED));
        wristSubsystem.debugPublish("Set INTAKING", set(WristState.INTAKING));
    }

    public Command set(WristState state){
        return new Command() {
            public void initialize() {
                wristSubsystem.setGoal(state);
            }
            public boolean isFinished(){
                return wristSubsystem.atGoal();
            }
        };
    }
}
