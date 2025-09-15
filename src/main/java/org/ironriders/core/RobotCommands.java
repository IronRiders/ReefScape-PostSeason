package org.ironriders.core;

import java.util.function.DoubleSupplier;

import org.ironriders.climb.ClimbCommands;
import org.ironriders.core.ElevatorWristCTL.ElevatorWristState;
import org.ironriders.drive.DriveCommands;
import org.ironriders.intake.IntakeCommands;
import org.ironriders.intake.IntakeConstants.IntakeState;
import org.ironriders.targeting.TargetingCommands;

import com.pathplanner.lib.auto.NamedCommands;

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

@SuppressWarnings("unused")
public class RobotCommands {

  private final DriveCommands driveCommands;
  private final TargetingCommands targetingCommands;
  private final IntakeCommands intakeCommands;
  private final ClimbCommands climbCommands;
  private final ElevatorWristCTL elevatorWristCommands;

  private final GenericHID controller;

  public RobotCommands(
      DriveCommands driveCommands,
      TargetingCommands targetingCommands,
      IntakeCommands intakeCommands,
      ElevatorWristCTL elevatorWristCommands,
      ClimbCommands climbCommands,
      GenericHID controller) {
    this.driveCommands = driveCommands;
    this.targetingCommands = targetingCommands;
    this.intakeCommands = intakeCommands;
    this.elevatorWristCommands = elevatorWristCommands;
    this.climbCommands = climbCommands;
    this.controller = controller;
    // TODO: More named commands, implement good autos

    NamedCommands.registerCommand("ElevatorWrist L2", elevatorWristCommands.setElevatorWrist(ElevatorWristState.L2));
    NamedCommands.registerCommand("ElevatorWrist L3", elevatorWristCommands.setElevatorWrist(ElevatorWristState.L3));
    NamedCommands.registerCommand("ElevatorWrist L4", elevatorWristCommands.setElevatorWrist(ElevatorWristState.L4));

    NamedCommands.registerCommand("Intake Eject", eject());
    NamedCommands.registerCommand("Intake", intake());
  }

  /**
   * Initialize all subsystems when first enabled.
   */
  public Command startup() {
    intakeCommands.setOnSuccess(() -> rumble());

    return elevatorWristCommands.reset(); // moves everything to zero
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
      DoubleSupplier inputRotation) {
    return driveCommands.driveTeleop(
        inputTranslationX,
        inputTranslationY,
        inputRotation,
        true);
  }

  public Command jog(double robotRelativeAngleDegrees) {
    return driveCommands.jog(robotRelativeAngleDegrees);
  }

  public Command intake() {
    return Commands.parallel(elevatorWristCommands.setElevatorWrist(ElevatorWristState.INTAKING), intakeCommands.set(IntakeState.GRAB));
  }

  public Command eject() {
    return intakeCommands.set(IntakeState.EJECT);
  }

  public Command stopIntake() {
    return Commands.parallel(elevatorWristCommands.setElevatorWrist(ElevatorWristState.STOW), intakeCommands.set(IntakeState.STOP));
  }


  public Command rumble() {
    return Commands.sequence(
        Commands.runOnce(() -> controller.setRumble(GenericHID.RumbleType.kBothRumble, 1)),
        Commands.waitSeconds(0.3),
        Commands.runOnce(() -> controller.setRumble(GenericHID.RumbleType.kBothRumble, 0)))
        .handleInterrupt(() -> controller.setRumble(GenericHID.RumbleType.kBothRumble, 0));
  }
}
