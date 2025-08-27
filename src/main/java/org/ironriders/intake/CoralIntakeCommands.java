package org.ironriders.intake;

import static org.ironriders.intake.CoralIntakeConstants.DISCHARGE_TIMEOUT;

import edu.wpi.first.wpilibj2.command.Command;
import org.ironriders.intake.CoralIntakeConstants.CoralIntakeState;

public class CoralIntakeCommands {

  private final CoralIntakeSubsystem intake;

  @SuppressWarnings("unused")
  private Runnable onSuccess;

  public CoralIntakeCommands(CoralIntakeSubsystem intake) {
    this.intake = intake;

    intake.publish("Coral Intake Grab", set(CoralIntakeState.GRAB));
    intake.publish("Coral Intake Eject", set(CoralIntakeState.EJECT));
    intake.publish("Coral Intake Stop", set(CoralIntakeState.STOP));
  }

  public Command set(CoralIntakeConstants.CoralIntakeState state) {
    Command command = intake.run(() -> intake.set(state));

    switch (state) {
      case GRAB:
        return command 
          .until(()->intake.hasGamePiece())
          .finallyDo(()-> intake.set(CoralIntakeState.STOP));

      case EJECT:
        return command
          .withTimeout(DISCHARGE_TIMEOUT)
          .finallyDo(() -> intake.set(CoralIntakeState.STOP));
      default:
        return command.finallyDo(() -> intake.set(CoralIntakeState.STOP));
    }
  }

  public Command reset() {
    return intake.runOnce(() -> intake.set(CoralIntakeState.STOP));
  }

  public CoralIntakeSubsystem getCoralIntake() {
    return intake;
  }

  public void setOnSuccess(Runnable onSucess) {
    this.onSuccess = onSucess;
  }
}
