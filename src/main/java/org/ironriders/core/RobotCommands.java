package org.ironriders.core;

import static org.ironriders.elevator.ElevatorConstants.L4_HEIGHT;

import java.util.function.DoubleSupplier;

import org.ironriders.climb.ClimbCommands;
import org.ironriders.drive.DriveCommands;
import org.ironriders.elevator.ElevatorCommands;
import org.ironriders.elevator.ElevatorConstants;
import org.ironriders.intake.CoralIntakeCommands;
import org.ironriders.intake.CoralIntakeConstants;
import org.ironriders.targeting.TargetingCommands;
import org.ironriders.wrist.CoralWristCommands;
import org.ironriders.wrist.CoralWristConstants;
import org.ironriders.wrist.CoralWristConstants.WristState;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

/**
 * These commands require more complex logic and are not directly tied to a
 * subsystem.
 * They generally interface w/ multiple subsystems via their commands and are
 * higher-level.
 *
 * These commands are those which the driver controls call.
 */
public class RobotCommands {

  private final DriveCommands driveCommands;
  private final TargetingCommands targetingCommands;
  private final ElevatorCommands elevatorCommands;
  private final CoralWristCommands coralWristCommands;
  private final CoralIntakeCommands coralIntakeCommands;
  private final ClimbCommands climbCommands;

  private final GenericHID controller;

  public RobotCommands(
    DriveCommands driveCommands,
    TargetingCommands targetingCommands,
    ElevatorCommands elevatorCommands,
    CoralWristCommands coralWristCommands,
    CoralIntakeCommands coralIntakeCommands,
    ClimbCommands climbCommands,
    GenericHID controller
  ) {
    this.driveCommands = driveCommands;
    this.targetingCommands = targetingCommands;
    this.elevatorCommands = elevatorCommands;
    this.coralWristCommands = coralWristCommands;
    this.coralIntakeCommands = coralIntakeCommands;
    this.climbCommands = climbCommands;
    this.controller = controller;
    // TODO: Named commands, implement along w/ on-the-fly autos
  }

  /**
   * Initialize all subsystems when first enabled.
   *
   * This primarily involves homing. We need to home sequentially coral -> algae
   * -> elevator due to physical
   * limitations.
   */
  public Command startup() {
    coralIntakeCommands.setOnSuccess(() -> rumble());
    return Commands.sequence(
      coralWristCommands.set(WristState.STOWED),
      elevatorCommands.home()
    );
  }

  /**
   * Command to drive the robot given controller input.
   *
   * @param inputTranslationX DoubleSupplier, value from 0-1.
   * @param inputTranslationY DoubleSupplier, value from 0-1.
   * @param inputRotation     DoubleSupplier, value from 0-1.
   */
  public Command driveTeleop(
    DoubleSupplier inputTranslationX,
    DoubleSupplier inputTranslationY,
    DoubleSupplier inputRotation
  ) {
    return driveCommands.driveTeleop(
      inputTranslationX,
      inputTranslationY,
      inputRotation,
      true
    );
  }

  public Command jog(double robotRelativeAngleDegrees) {
    return driveCommands.jog(robotRelativeAngleDegrees);
  }

  public Command rumble() {
    return Commands.sequence(
      Commands.runOnce(() ->
        controller.setRumble(GenericHID.RumbleType.kBothRumble, 1)
      ),
      Commands.waitSeconds(0.3),
      Commands.runOnce(() ->
        controller.setRumble(GenericHID.RumbleType.kBothRumble, 0)
      )
    ).handleInterrupt(() ->
      controller.setRumble(GenericHID.RumbleType.kBothRumble, 0)
    );
  }

  public Command moveElevatorAndWrist(ElevatorConstants.Level level) {
    if(level.equals(ElevatorConstants.Level.Intaking)){
    return Commands.sequence(
        elevatorCommands.set(level),
        coralWristCommands.set(
          switch (level) {
            case L1, L2, L3 -> CoralWristConstants.WristState.L2toL3;
            case L4 -> CoralWristConstants.WristState.L4;
            case Intaking -> CoralWristConstants.WristState.Intaking;
            case Down -> CoralWristConstants.WristState.STOWED;
            case HighAlgae -> CoralWristConstants.WristState.STOWED;
            default -> {
              throw new IllegalArgumentException(
                "Cannot score coral to level: " + level
              );
            }
          }
        )
      );
      }
    else {
    return Commands.sequence(
        coralWristCommands.set(CoralWristConstants.WristState.STOWED),
        elevatorCommands.set(level),
        coralWristCommands.set(
          switch (level) {
            case L1, L2, L3 -> CoralWristConstants.WristState.L2toL3;
            case L4 -> CoralWristConstants.WristState.L4;
            case Intaking -> CoralWristConstants.WristState.Intaking;
            case Down -> CoralWristConstants.WristState.STOWED;
            case HighAlgae -> CoralWristConstants.WristState.STOWED;
            default -> {
              throw new IllegalArgumentException(
                "Cannot score coral to level: " + level
              );
            }
          }
        )
      );
      }    
    };

  public Command scoreCoral() {
    return Commands.sequence(
      coralIntakeCommands.set(CoralIntakeConstants.CoralIntakeState.EJECT),
      Commands.parallel(
        coralWristCommands.set(CoralWristConstants.WristState.STOWED),
        elevatorCommands.set(ElevatorConstants.Level.Down)
      )
    );
  }

  public Command prepareToGrabCoral() {
    return Commands.parallel(
      coralWristCommands.set(CoralWristConstants.WristState.Intaking),
      elevatorCommands.set(ElevatorConstants.Level.Intaking)
    );
  }

  public Command grabCoral() {
    return Commands.sequence(
      coralIntakeCommands.set(CoralIntakeConstants.CoralIntakeState.GRAB),
      coralWristCommands.set(CoralWristConstants.WristState.STOWED),
      elevatorCommands.set(ElevatorConstants.Level.Down)
    );
  }
}
