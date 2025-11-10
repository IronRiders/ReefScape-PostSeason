package org.ironriders.core;

import com.ctre.phoenix6.hardware.Pigeon2;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import java.util.function.DoubleSupplier;
import org.ironriders.drive.DriveCommands;

import org.ironriders.targeting.TargetingCommands;

/**
 * These commands require more complex logic and are not directly tied to a
 * subsystem. They
 * generally interface w/ multiple subsystems via their commands and are
 * higher-level.
 *
 * <p>
 * These commands are those which the driver controls call.
 */
@SuppressWarnings("unused") // Targeting and climb are unused by high-level commands
public class RobotCommands {
  private final DriveCommands driveCommands;
  private final TargetingCommands targetingCommands;

  private final GenericHID controller;

  /**
   * Creates final variables for all command classes.
   *
   * @param driveCommands       DriveCommands instance
   * @param targetingCommands   TargetingCommands instance
   * @param coralIntakeCommands CoralIntakeCommands instance
   * @param climbCommands       ClimbCommands instance
   * @param controller          GenericHID controller (joystick/gamepad) instance
   */
  public RobotCommands(
      DriveCommands driveCommands,
      TargetingCommands targetingCommands,
      GenericHID controller) {
    this.driveCommands = driveCommands;
    this.targetingCommands = targetingCommands;

    this.controller = controller;
    // TODO: More named commands, implement good autos



    SmartDashboard.putData("RobotCommands/Reset Gyro", resetGyroAngle());
  }

  /** Initialize all subsystems when first enabled. */


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
    return driveCommands.driveTeleop(inputTranslationX, inputTranslationY, inputRotation, true);
  }

  /**
   * Small translation that is robot-centered rather than field-centered. For
   * example, moving a
   * little 30 degrees will move 30 degrees relative to the front of the robot,
   * rather than relative
   * to the field.
   *
   * @param robotRelativeAngleDegrees The angle to move, in degrees relative to
   *                                  where the robot is
   *                                  facing
   * @return Returns command object that calls the
   *         {@link DriveCommands#jog(double)} method
   */
  public Command jog(double robotRelativeAngleDegrees) {
    return driveCommands.jog(robotRelativeAngleDegrees);
  }



  public Command resetGyroAngle() {
    return Commands.runOnce(() -> resetPigeon());
  }

  public void resetPigeon() {
    Pigeon2 pigeon2 = new Pigeon2(9);
    pigeon2.reset();
    pigeon2.close();
    driveCommands.resetRotation();
  }

  

  
  /**
   * Sets the rumble on the controller for 0.3 seconds.
   *
   * <p>
   * Does this by setting the {@link
   * edu.wpi.first.wpilibj.GenericHID#setRumble(edu.wpi.first.wpilibj.GenericHID.RumbleType, double)
   * GenericHID setRumble()} method to {@link
   * edu.wpi.first.wpilibj.GenericHID.RumbleType#kBothRumble kBothRumble}. This
   * makes all motors on
   * a controller rumble.
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
