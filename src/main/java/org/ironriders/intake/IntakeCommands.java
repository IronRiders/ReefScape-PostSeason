package org.ironriders.intake;

import static org.ironriders.intake.IntakeConstants.DISCHARGE_TIMEOUT;
import static org.ironriders.intake.IntakeConstants.INTAKE_JOG_TIME;

import org.ironriders.intake.IntakeConstants.IntakeJogState;
import org.ironriders.intake.IntakeConstants.IntakeState;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

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
    intake.publish("Intake Jog Up", jog(IntakeJogState.UP));
    intake.publish("Intake Jog Down", jog(IntakeJogState.DOWN));
  }

  public Command set(IntakeConstants.IntakeState state) {
    Command command = intake.run(() -> intake.set(state));

    switch (state) {
      case GRAB:
        return command.until(() -> intake.atGoal()).finallyDo(() -> intake.set(IntakeState.STOP));

      case EJECT:
        return command.withTimeout(DISCHARGE_TIMEOUT).finallyDo(() -> intake.set(IntakeState.STOP));
      default:
        return command.finallyDo(() -> intake.set(IntakeState.STOP));
    }
  }

  public Command reset() {
    return intake.runOnce(() -> intake.set(IntakeState.STOP));
  }

  public Command jog(IntakeJogState state) {
    return Commands.sequence(
        intake.runOnce(() -> intake.setGoal(intake.getGoal() + state.increment)),
        Commands.waitSeconds(INTAKE_JOG_TIME), set(IntakeState.STOP));
  }

  public IntakeSubsystem getIntake() {
    return intake;
  }

  public void setOnSuccess(Runnable onSucess) {
    this.onSuccess = onSucess;
  }
}
