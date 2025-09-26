package org.ironriders.elevator;

import org.ironriders.core.ElevatorWristCTL.ElevatorLevel;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class ElevatorCommands {

  private ElevatorSubsystem elevatorSubsystem;

  public ElevatorCommands(ElevatorSubsystem elevator) {
    this.elevatorSubsystem = elevator;

    elevator.publish("Rehome", home());
  }

  /**
   * Command to set the elevator's target position to one of several predefined
   * levels.
   * 
   * @return a Command to change target, finishes when the elevator has reached
   *         it.
   */
  public Command set(ElevatorLevel level) {
    return new Command() {
      public void initialize() {
        elevatorSubsystem.setGoal(level);
        
      }

      public boolean isFinished() {
        return elevatorSubsystem.isAtPosition();
      }
    };
  }

  /**
   * Command to home the elevator, finding the bottom pos and remembering it.
   * 
   * @return a Command that finishes when the bottom limit switch is pressed.
   */
  public Command home() {
    return Commands.runOnce(() -> elevatorSubsystem.setNotHomed());
  }

  public Command downRehomeReset() {
    return Commands.sequence(set(ElevatorLevel.DOWN), home(), elevatorSubsystem.runOnce(elevatorSubsystem::reset));
  }
}
