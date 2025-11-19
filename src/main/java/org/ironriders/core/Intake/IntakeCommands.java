package org.ironriders.core.Intake;

import edu.wpi.first.wpilibj2.command.Command;


public class IntakeCommands {
    private final IntakeSubsystem intake;

    public IntakeCommands(IntakeSubsystem intakeSubsystem) {
        this.intake = intakeSubsystem;
        intake.debugPublish("Intake Grab", set(IntakeConstants.IntakeState.GRAB));
        intake.debugPublish("Intake Score", set(IntakeConstants.IntakeState.SCORE));
        intake.debugPublish("Intake Stop", set(IntakeConstants.IntakeState.STOP));
        
    }
    public Command set(IntakeConstants.IntakeState state) {
        return intake.runOnce(() -> intake.set(state));
    }

}
