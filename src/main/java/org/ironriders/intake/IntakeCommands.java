package org.ironriders.intake;

import static org.ironriders.intake.IntakeConstants.DISCHARGE_TIMEOUT;

import org.ironriders.intake.IntakeConstants.IntakeState;

import edu.wpi.first.wpilibj2.command.Command;

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
            .until(() -> intake.hasHighCurrent()).until(null)
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
