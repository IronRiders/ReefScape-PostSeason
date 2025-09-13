package org.ironriders.core;

import java.util.function.DoubleSupplier;

import org.ironriders.climb.ClimbCommands;
import org.ironriders.core.ElevatorWirstCTL.EWState;
import org.ironriders.drive.DriveCommands;
import org.ironriders.intake.CoralIntakeCommands;
import org.ironriders.intake.CoralIntakeConstants.CoralIntakeState;
import org.ironriders.targeting.TargetingCommands;

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
  private final CoralIntakeCommands coralIntakeCommands;
  private final ClimbCommands climbCommands;
  private final ElevatorWirstCTL EWCTLCommands;

  private final GenericHID controller;

  public RobotCommands(
      DriveCommands driveCommands,
      TargetingCommands targetingCommands,
      CoralIntakeCommands coralIntakeCommands,
      ElevatorWirstCTL EWCTLCommands,
      ClimbCommands climbCommands,
      GenericHID controller) {
    this.driveCommands = driveCommands;
    this.targetingCommands = targetingCommands;
    this.coralIntakeCommands = coralIntakeCommands;
    this.EWCTLCommands = EWCTLCommands;
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

    return EWCTLCommands.reset(); // moves everything to zero
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
    return Commands.parallel(EWCTLCommands.setEW(EWState.INTAKING), coralIntakeCommands.set(CoralIntakeState.GRAB));
  }

  public Command stopIntake() {
    return Commands.parallel(EWCTLCommands.setEW(EWState.STOW), coralIntakeCommands.set(CoralIntakeState.STOP));
  }


  public Command rumble() {
    return Commands.sequence(
        Commands.runOnce(() -> controller.setRumble(GenericHID.RumbleType.kBothRumble, 1)),
        Commands.waitSeconds(0.3),
        Commands.runOnce(() -> controller.setRumble(GenericHID.RumbleType.kBothRumble, 0)))
        .handleInterrupt(() -> controller.setRumble(GenericHID.RumbleType.kBothRumble, 0));
  }
}
