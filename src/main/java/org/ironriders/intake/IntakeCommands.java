package org.ironriders.intake;

import static org.ironriders.intake.IntakeConstants.DISCHARGE_TIMEOUT;

import org.ironriders.intake.IntakeConstants.IntakeSpeeds;

import edu.wpi.first.wpilibj2.command.Command;

public class IntakeCommands {

  private final IntakeSubsystem intake;

  @SuppressWarnings("unused")
  private Runnable onSuccess;

  public IntakeCommands(IntakeSubsystem intake) {
    this.intake = intake;

    intake.publish("Intake Grab", set(IntakeSpeeds.GRAB));
    intake.publish("Intake Score", set(IntakeSpeeds.SCORE));
    intake.publish("Intake Eject", set(IntakeSpeeds.EJECT));
    intake.publish("Intake Stop", set(IntakeSpeeds.STOP));
  }

  public Command set(IntakeConstants.IntakeSpeeds state) {
    Command command = intake.run(() -> intake.set(state));

    switch (state) {
      case GRAB:
        return command
            .until(() -> intake.hasHighCurrent())
            .finallyDo(() -> intake.set(IntakeSpeeds.STOP));

      case EJECT:
        return command
            .withTimeout(DISCHARGE_TIMEOUT)
            .finallyDo(() -> intake.set(IntakeSpeeds.STOP));
      default:
        return command.finallyDo(() -> intake.set(IntakeSpeeds.STOP));
    }
  }

  public Command reset() {
    return intake.runOnce(() -> intake.set(IntakeSpeeds.STOP));
  }

  public IntakeSubsystem getIntake() {
    return intake;
  }

  public void setOnSuccess(Runnable onSucess) {
    this.onSuccess = onSucess;
  }
}
