package org.ironriders.wrist;

import org.ironriders.core.ElevatorWristCTL.WristRotation;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class WristCommands {

  private WristSubsystem wristSubsystem;

  public WristCommands(WristSubsystem wrist) {
    this.wristSubsystem = wrist;
  }

  public Command set(WristRotation rotation) {
    return new Command() {
        public void execute() {
          wristSubsystem.setGoal(rotation);
        }
  
        public boolean isFinished() {
          return wristSubsystem.atGoal;
        }
      };
    }

  public Command reset() {
    return wristSubsystem.runOnce(wristSubsystem::reset);
  }

  public Command stowReset() {
    return Commands.sequence(set(WristRotation.STOW), reset());
  }
}
