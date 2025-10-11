package org.ironriders.core;

import java.util.function.DoubleSupplier;

import org.ironriders.climb.ClimbCommands;
import org.ironriders.core.ElevatorWristCTL.ElevatorWristState;
import org.ironriders.drive.DriveCommands;
import org.ironriders.intake.IntakeCommands;
import org.ironriders.intake.IntakeSubsystem;
import org.ironriders.intake.IntakeConstants.IntakeState;
import org.ironriders.targeting.TargetingCommands;
import org.ironriders.wrist.WristSubsystem;

import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

/**
 * These commands require more complex logic and are not directly tied to a
 * subsystem. They generally interface w/ multiple subsystems via their commands
 * and are higher-level.
 *
 * These commands are those which the driver controls call.
 */

@SuppressWarnings("unused") // Targeting and climb are unused by high-level commands
public class RobotCommands {
  private ElevatorWristState lastState = ElevatorWristState.HOLD;
  private final DriveCommands driveCommands;
  private final TargetingCommands targetingCommands;
  private final IntakeCommands intakeCommands;
  private final ClimbCommands climbCommands;
  private final ElevatorWristCTL elevatorWristCommands;

  private final GenericHID controller;

  /**
   * Creates final variables for all command classes.
   *
   * @param driveCommands       DriveCommands instance
   * @param targetingCommands   TargetingCommands instance
   * @param elevatorCommands    ElevatorCommands instance
   * @param coralWristCommands  CoralWristCommands instance
   * @param coralIntakeCommands CoralIntakeCommands instance
   * @param climbCommands       ClimbCommands instance
   * @param controller          GenericHID controller (joystick/gamepad) instance
   */
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

    NamedCommands.registerCommand("ElevatorWrist L2", elevatorWristSet(ElevatorWristState.L2));
    NamedCommands.registerCommand("ElevatorWrist L3", elevatorWristSet(ElevatorWristState.L3));
    NamedCommands.registerCommand("ElevatorWrist L4", elevatorWristSet(ElevatorWristState.L4));

    NamedCommands.registerCommand("Prepare Score L4", prepareScoreLevel(ElevatorWristState.L4));
    NamedCommands.registerCommand("Prepare Score L3", prepareScoreLevel(ElevatorWristState.L3));
    NamedCommands.registerCommand("Prepare Score L2", prepareScoreLevel(ElevatorWristState.L2));

    NamedCommands.registerCommand("Intake Eject", eject());
    NamedCommands.registerCommand("Intake", intake());
    NamedCommands.registerCommand("Score", scoreAndDown());

  }

  /**
   * Initialize all subsystems when first enabled.
   */
  public Command startup() {
    intakeCommands.setOnSuccess(() -> rumbleController());

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

  public Command prepareScoreLevel(ElevatorWristState level) {
    return Commands.sequence(elevatorWristSet(level));
  }

  private Command scoreAndDown() {
    return Commands.sequence(intakeCommands.set(IntakeState.SCORE),
        elevatorWristSet(ElevatorWristState.HOLD));
  }

  /**
   * Small translation that is robot-centered rather than field-centered.
   * For example, moving a little 30 degrees will move 30 degrees relative
   * to the front of the robot, rather than relative to the field.
   * 
   * @param robotRelativeAngleDegrees The angle to move, in degrees relative to
   *                                  where the robot is facing
   * @return Returns command object that calls the
   *         {@link DriveCommands#jog(double)} method
   */
  public Command jog(double robotRelativeAngleDegrees) {
    return driveCommands.jog(robotRelativeAngleDegrees);
  }

  /**
   * <p>
   * Command to make the robot intake. Runs two commands in parallel:
   * <ul>
   * <li>Sets the {@link ElevatorWristCTL#setElevatorWrist(ElevatorWristState)
   * elevator wrist state} to {@link ElevatorWristState#INTAKING "INTAKING"}.</li>
   * <li>Sets the {@link IntakeCommands#set(IntakeState) intake state} to
   * {@link IntakeState#GRAB "GRAB"}.</li>
   * </ul>
   * <br>
   * 
   * </p>
   * 
   * @return returns the command described above
   */
  public Command intake() {
    return Commands.parallel(
        elevatorWristSet(ElevatorWristState.INTAKING),
        intakeCommands.set(IntakeState.GRAB)).unless(() -> intakeCommands.getIntake().beamBreakTriggered());
  }

  /**
   * Command to make the robot eject. Simply sets the
   * {@link IntakeCommands#set(IntakeState) intake state} to
   * {@link IntakeState#EJECT "eject"}.
   * 
   * @return returns the command described above
   */
  public Command eject() {
    return intakeCommands.set(IntakeState.EJECT);
  }

  /**
   * Command to stop the intake and stow the elevator wrist.
   * Does the following in parallel:
   * <ul>
   * <li>Sets the {@link ElevatorWristCTL#setElevatorWrist(ElevatorWristState)
   * elevator wrist state} to {@link ElevatorWristState#HOLD "stow"}.</li>
   * <li>Sets the {@link IntakeCommands#set(IntakeState) intake state} to
   * {@link IntakeState#STOP "stop"}.</li>
   * </ul>
   * 
   * 
   * @return returns the command described above
   */
  public Command stopIntake() {
    return Commands.parallel(elevatorWristSet(ElevatorWristState.HOLD),
        intakeCommands.set(IntakeState.STOP).unless(() -> intakeCommands.getIntake().beamBreakTriggered()));
  }

  public Command elevatorWristSet(ElevatorWristState state) {
    Command targetCommand;

    switch (state) {
      case L4:
        targetCommand = Commands.parallel(elevatorWristCommands.setElevatorWrist(state),
            intakeCommands.boost());
      default:
        targetCommand = elevatorWristCommands.setElevatorWrist(state);
    }

    if (lastState == ElevatorWristState.L4 && state != ElevatorWristState.L4) {
      targetCommand = Commands.parallel(elevatorWristCommands.setElevatorWrist(state),
          intakeCommands.unboost());
    }

    lastState = state;

    return targetCommand;
  }

  /**
   * Sets the rumble on the controller for 0.3 seconds.
   * 
   * Does this by setting the
   * {@link edu.wpi.first.wpilibj.GenericHID#setRumble(edu.wpi.first.wpilibj.GenericHID.RumbleType, double)
   * GenericHID setRumble()}
   * method to {@link edu.wpi.first.wpilibj.GenericHID.RumbleType#kBothRumble
   * kBothRumble}. This makes all motors on a controller rumble.
   * 
   * @return A command that does what is described above for 0.3 seconds, then
   *         returns rumble to 0.
   */
  public Command rumbleController() {
    return Commands.sequence(
        Commands.runOnce(() -> controller.setRumble(GenericHID.RumbleType.kBothRumble, 1)),
        Commands.waitSeconds(0.3),
        Commands.runOnce(() -> controller.setRumble(GenericHID.RumbleType.kBothRumble, 0)))
        .handleInterrupt(() -> controller.setRumble(GenericHID.RumbleType.kBothRumble, 0));
  }
}
