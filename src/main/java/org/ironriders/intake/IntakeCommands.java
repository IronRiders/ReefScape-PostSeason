package org.ironriders.intake;

import static org.ironriders.intake.IntakeConstants.DISCHARGE_TIMEOUT;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

import org.ironriders.intake.IntakeConstants.IntakeState;

public class IntakeCommands {

  private final IntakeSubsystem intake;

  @SuppressWarnings("unused")
  private Runnable onSuccess;

  public IntakeCommands(IntakeSubsystem intake) {
    this.intake = intake;

    intake.publish("Intake Grab", set(IntakeState.GRAB));
    intake.publish("Intake Score", set(IntakeState.SCORE));
    intake.publish("Intake Eject", set(IntakeState.EJECT));
    intake.publish("Intake Stop", set(IntakeState.STOP));
  }

  public Command set(IntakeConstants.IntakeState state) {
    Command command = intake.run(() -> intake.set(state));

    switch (state) {
      case GRAB:
        return command
            .until(() -> intake.hasHighCurrent())
            .withTimeout(IntakeConstants.INTAKE_GIVE_UP_TIME)
            .finallyDo(() -> intake.set(IntakeState.STOP));

      case EJECT:
        return command
            .withTimeout(DISCHARGE_TIMEOUT)
            .finallyDo(() -> intake.set(IntakeState.STOP));
      default:
        return command.finallyDo(() -> intake.set(IntakeState.STOP));
    }
  }

  public Command reset() {
    return intake.runOnce(() -> intake.set(IntakeState.STOP));
  }

  public IntakeSubsystem getIntake() {
    return intake;
  }

  public void setOnSuccess(Runnable onSucess) {
    this.onSuccess = onSucess;
  }
}
