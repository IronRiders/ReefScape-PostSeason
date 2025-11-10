// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.ironriders.core;

import com.pathplanner.lib.auto.AutoBuilder;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import org.ironriders.drive.DriveCommands;
import org.ironriders.drive.DriveConstants;
import org.ironriders.drive.DriveSubsystem;
import org.ironriders.lib.RobotUtils;
import org.ironriders.targeting.TargetingCommands;
import org.ironriders.targeting.TargetingSubsystem;

/**
 * Different button configurations for the driver controls PRIMARY_DRIVER: same as Driver Centered
 * Control Layout in the doc PRIMARY_DRIVER_WITH_BOOST: same as `William Boost buttons + primary
 * focus` in the doc SECONDARY_DRIVER_WITH_BOOST: same as `Secondary driver elevator controls` in
 * the doc
 */
enum Config {
  PRIMARY_DRIVER,
  PRIMARY_DRIVER_WITH_BOOST,
  SECONDARY_DRIVER_WITH_BOOST;
}

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  public final DriveSubsystem driveSubsystem = new DriveSubsystem();
  public final DriveCommands driveCommands = driveSubsystem.getCommands();

  public final TargetingSubsystem targetingSubsystem = new TargetingSubsystem();
  public final TargetingCommands targetingCommands = targetingSubsystem.getCommands();


  public final Double triggerThreshold = 0.75;

  private final SendableChooser<Command> autoChooser;

  private final CommandXboxController primaryController =
      new CommandXboxController(DriveConstants.PRIMARY_CONTROLLER_PORT);
  private final CommandGenericHID secondaryController =
      new CommandJoystick(DriveConstants.KEYPAD_CONTROLLER_PORT);

  public final RobotCommands robotCommands =
      new RobotCommands(
          driveCommands,
          targetingCommands,
          primaryController.getHID());

  /**
   * The container for the robot. Contains subsystems, IO devices, and commands.
   *
   * <p>builds the autos using {@link com.pathplanner.lib.auto.AutoBuilder#buildAutoChooser()
   * buildAutoChooser()} posts the auto selection to {@link SmartDashboard#putData(String,
   * SendableChooser) SmartDashboard}
   */
  public RobotContainer() {
    // Configure the trigger bindings
    configureBindings();

    autoChooser = AutoBuilder.buildAutoChooser();
    SmartDashboard.putData("Auto Select", autoChooser);
  }

  /**
   * Th {@link CommandGenericHID#button(int)} method (such as {@link
   * CommandXboxController#button(int)}, {@link CommandJoystick#button(int)}, or one of the {@link
   * edu.wpi.first.wpilibj2.command.button.Trigger#Trigger(java.util.function.BooleanSupplier)}
   * constructors.
   */
  private void configureBindings() {
    DriverStation.silenceJoystickConnectionWarning(true);

    // This configures what control scheme the controller will use.
    // Changing this before the match will change the control layout for the driver
    // this may be useful if different drivers prefer different configurations.
    // See following document for configurations:
    // https://docs.google.com/document/d/1xFyZLRxw_a8ykvMorcS_41jLqNv0-CR9rZUkbbDNRMI/edit?copiedFromTrash&tab=t.0
    Config buttonConfiguration = Config.PRIMARY_DRIVER_WITH_BOOST;

    // DRIVE CONTROLS
    driveSubsystem.setDefaultCommand(
        
        robotCommands.driveTeleop(
            () ->
                RobotUtils.controlCurve(
                    -primaryController.getLeftY() // This sets the robot's x
                        // translation (as
                        // seen in driveTeleop) to
                        // the left
                        // joystick's y value
                        * driveSubsystem.controlSpeedMultipler,
                    DriveConstants.TRANSLATION_CONTROL_EXPONENT,
                    DriveConstants.TRANSLATION_CONTROL_DEADBAND), // the deadband for the
            // controller, not being
            // used
            // right now
            () ->
                RobotUtils.controlCurve(
                    -primaryController.getLeftX() // this sets the robot's y
                        // translation (as
                        // seen in driveTeleop) to
                        // the left
                        // joystick's x value
                        * driveSubsystem.controlSpeedMultipler, // for all these, see getLeftY
                    DriveConstants.TRANSLATION_CONTROL_EXPONENT,
                    DriveConstants.TRANSLATION_CONTROL_DEADBAND),
            () ->
                RobotUtils.controlCurve(
                    -primaryController.getRightX() // this rotates the robot
                        // based on the
                        // right joysticks x value
                        // (y value is
                        // unused)
                        * driveSubsystem.controlSpeedMultipler,
                    DriveConstants.ROTATION_CONTROL_EXPONENT,
                    DriveConstants.ROTATION_CONTROL_DEADBAND)));

    switch (buttonConfiguration) { // configures buttons based on selected config. see the
      // buttonConfiguration to
      // know the currently active configuration
      // currently, 'PRIMARY_DRIVER_WITH_BOOST' is the 'normal' configuration, when in
      // doubt use this one. // (see
      // https://europe1.discourse-cdn.com/unity/original/3X/5/8/58e7b2a50ec35ea142ae9c4d27c9df2d372cd1f3.jpeg
      // for button numbers)
      case PRIMARY_DRIVER:
        for (var angle = 0; angle < 360; angle += 45) {
          primaryController.pov(angle).onTrue(driveCommands.jog(-angle));
        }

       

      case PRIMARY_DRIVER_WITH_BOOST:
        primaryController.povRight().onTrue(driveCommands.jog(-90));

        primaryController.povLeft().onTrue(driveCommands.jog(90));



        primaryController
            .leftBumper()
            .onTrue(driveCommands.setDriveTrainSpeed(0.5))
            .onFalse(driveCommands.setDriveTrainSpeed(1));

        primaryController
            .rightBumper()
            .onTrue(driveCommands.setDriveTrainSpeed(1.5))
            .onFalse(driveCommands.setDriveTrainSpeed(1));

        break;

      case SECONDARY_DRIVER_WITH_BOOST:
        for (var angle = 0; angle < 360; angle += 45) {
          primaryController.pov(angle).onTrue(driveCommands.jog(-angle));
        }


        primaryController
            .leftBumper()
            .onTrue(driveCommands.setDriveTrainSpeed(0.5))
            .onFalse(driveCommands.setDriveTrainSpeed(1));
        primaryController
            .rightBumper()
            .onTrue(driveCommands.setDriveTrainSpeed(1.5))
            .onFalse(driveCommands.setDriveTrainSpeed(1));

        
        break;
      default:
        throw new Error("Invalid buttonmap type!");
    } // TODO: figure out what driveteam wants to do with this.

  }

  /**
   * Get command configured in auto chooser.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }
}
