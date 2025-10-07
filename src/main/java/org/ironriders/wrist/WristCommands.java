package org.ironriders.wrist;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import org.ironriders.core.ElevatorWristCtl.WristRotation;

public class WristCommands {

  private final WristSubsystem wristSubsystem;

  /**
   * Initalizer.
   *
   * @param wrist wrist subsystem
   */
  public WristCommands(WristSubsystem wrist) {
    this.wristSubsystem = wrist;
  }

  /**
   * Set the goal of the wrist.
   *
   * @param rotation target rotation
   * @return Command to do that
   */
  public Command set(WristRotation rotation) {
    return new Command() {
      public void initialize() {
        wristSubsystem.setGoal(rotation);
      }

      public boolean isFinished() {
        return wristSubsystem.atGoal();
      }
    };
  }

  /**
   * Reset the wrist.
   *
   * @return Command to do that
   */
  public Command reset() {
    return wristSubsystem.runOnce(wristSubsystem::reset);
  }

  /**
   * Reset and then move the wrist all the way in.
   *
   * @return Command to do that
   */
  public Command stowReset() {
    return Commands.sequence(reset(), set(WristRotation.HOLD));
  }
}
