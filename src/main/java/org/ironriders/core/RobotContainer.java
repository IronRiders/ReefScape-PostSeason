// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.ironriders.core;

import org.ironriders.climb.ClimbCommands;
import org.ironriders.climb.ClimbConstants;
import org.ironriders.climb.ClimbSubsystem;
import org.ironriders.core.ElevatorWirstCTL.ElevatorWristState;
import org.ironriders.drive.DriveCommands;
import org.ironriders.drive.DriveConstants;
import org.ironriders.drive.DriveSubsystem;
import org.ironriders.intake.IntakeCommands;
import org.ironriders.intake.IntakeConstants.IntakeState;
import org.ironriders.intake.IntakeSubsystem;
import org.ironriders.lib.RobotUtils;
import org.ironriders.targeting.TargetingCommands;
import org.ironriders.targeting.TargetingSubsystem;

import com.pathplanner.lib.auto.AutoBuilder;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {

    // The robot's subsystems and commands are defined here...
    public final DriveSubsystem driveSubsystem = new DriveSubsystem();
    public final DriveCommands driveCommands = driveSubsystem.getCommands();

    public final TargetingSubsystem targetingSubsystem = new TargetingSubsystem();
    public final TargetingCommands targetingCommands = targetingSubsystem.getCommands();

    public final ElevatorWirstCTL elevatorWristCommands = new ElevatorWirstCTL();

    public final IntakeSubsystem intakeSubsystem = new IntakeSubsystem();
    public final IntakeCommands intakeCommands = intakeSubsystem.getCommands();

    public final ClimbSubsystem climbSubsystem = new ClimbSubsystem();
    public final ClimbCommands climbCommands = climbSubsystem.getCommands();

    private final SendableChooser<Command> autoChooser;

    private final CommandXboxController primaryController = new CommandXboxController(
            DriveConstants.PRIMARY_CONTROLLER_PORT);
    private final CommandGenericHID secondaryController = new CommandJoystick(
            DriveConstants.KEYPAD_CONTROLLER_PORT);

    public final RobotCommands robotCommands = new RobotCommands(
            driveCommands,
            targetingCommands,
            intakeCommands,
            elevatorWristCommands,
            climbCommands,
            primaryController.getHID());

    /**
     * The container for the robot. Contains subsystems, IO devices, and commands.
     */
    public RobotContainer() {
        // Configure the trigger bindings
        configureBindings();

        autoChooser = AutoBuilder.buildAutoChooser();
        SmartDashboard.putData("Auto Select", autoChooser);
    }

    private void configureBindings() {
        DriverStation.silenceJoystickConnectionWarning(true);

        enum Config {
            PRIMARY_DRIVER,
            PRIMARY_DRIVER_WITH_BOOST,
            SECONDARY_DRIVER_WITH_BOOST;
        }

        Config buttonConfiguration = Config.PRIMARY_DRIVER; /*
                                                             * 0 is the primary driver focused, 1 is bumper boosts with
                                                             * primary focus, 2 is secondary driver elevator with boosts
                                                             * bumpers
                                                             */

        // DRIVE CONTROLS
        driveSubsystem.setDefaultCommand(
                robotCommands.driveTeleop(
                        () -> RobotUtils.controlCurve(
                                -primaryController.getLeftY() *
                                        driveSubsystem.controlSpeedMultipler *
                                        driveSubsystem.getinversionStatus(),
                                DriveConstants.TRANSLATION_CONTROL_EXPONENT,
                                DriveConstants.TRANSLATION_CONTROL_DEADBAND),
                        () -> RobotUtils.controlCurve(
                                -primaryController.getLeftX() *
                                        driveSubsystem.controlSpeedMultipler *
                                        driveSubsystem.getinversionStatus(),
                                DriveConstants.TRANSLATION_CONTROL_EXPONENT,
                                DriveConstants.TRANSLATION_CONTROL_DEADBAND),
                        () -> RobotUtils.controlCurve(
                                -primaryController.getRightX() *
                                        driveSubsystem.controlSpeedMultipler *
                                        driveSubsystem.getinversionStatus(),
                                DriveConstants.ROTATION_CONTROL_EXPONENT,
                                DriveConstants.ROTATION_CONTROL_DEADBAND)));

        switch (buttonConfiguration) {
            case PRIMARY_DRIVER:
                for (var angle = 0; angle < 360; angle += 45) {
                    primaryController.pov(angle).onTrue(driveCommands.jog(-angle));
                }

                // Go to intaking, then grab until told to stop
                primaryController.rightTrigger(.4)
                        .onTrue(elevatorWristCommands.setElevatorWrist(ElevatorWristState.INTAKING))
                        .onTrue(intakeCommands.set(IntakeState.GRAB))
                        .onFalse(intakeCommands.set(IntakeState.STOP));

                // Score coral
                primaryController.leftTrigger(.4)
                        .onTrue(intakeCommands.set(IntakeState.SCORE))
                        .onFalse(intakeCommands.set(IntakeState.STOP));

                // Elevator Down
                primaryController.rightBumper()
                        .onTrue(elevatorWristCommands.setElevatorWrist(ElevatorWristState.INTAKING));

                // Eject coral
                primaryController.leftBumper()
                        .onTrue(intakeCommands.set(IntakeState.EJECT))
                        .onFalse(intakeCommands.set(IntakeState.STOP));

                primaryController.button(1) // works for L2 as well
                        .onTrue(elevatorWristCommands.setElevatorWrist(ElevatorWristState.L2));
                primaryController.button(2) // works for L1 as well
                        .onTrue(elevatorWristCommands.setElevatorWrist(ElevatorWristState.L2));

                primaryController.button(3)
                        .onTrue(elevatorWristCommands.setElevatorWrist(ElevatorWristState.L3));
                primaryController.button(4)
                        .onTrue(elevatorWristCommands.setElevatorWrist(ElevatorWristState.L4));
                break;

            case PRIMARY_DRIVER_WITH_BOOST:
                for (var angle = 0; angle < 360; angle += 45) {
                    primaryController.pov(angle).onTrue(driveCommands.jog(-angle));
                }

                // Intake and then go down
                primaryController.rightTrigger(.4)
                        .onTrue(robotCommands.intake())
                        .onFalse(robotCommands.stopIntake());

                primaryController.leftTrigger(.4)
                        .onTrue(intakeCommands.set(IntakeState.SCORE))
                        .onFalse(intakeCommands.set(IntakeState.STOP));

                primaryController.leftBumper()
                        .onTrue(driveCommands.setDriveTrainSpeed(0.5))
                        .onFalse(driveCommands.setDriveTrainSpeed(1));

                primaryController.rightBumper()
                        .onTrue(driveCommands.setDriveTrainSpeed(1.5))
                        .onFalse(driveCommands.setDriveTrainSpeed(1));

                primaryController.button(1) // works for L2 as well
                        .onTrue(elevatorWristCommands.setElevatorWrist(ElevatorWristState.L2));
                primaryController.button(2) // works for L1 as well
                        .onTrue(elevatorWristCommands.setElevatorWrist(ElevatorWristState.L2));

                primaryController.button(3)
                        .onTrue(elevatorWristCommands.setElevatorWrist(ElevatorWristState.L3));
                primaryController.button(4)
                        .onTrue(elevatorWristCommands.setElevatorWrist(ElevatorWristState.L4));
                break;

            case SECONDARY_DRIVER_WITH_BOOST:
                for (var angle = 0; angle < 360; angle += 45) {
                    primaryController.pov(angle).onTrue(driveCommands.jog(-angle));
                }

                primaryController.rightTrigger(.4)
                        .onTrue(robotCommands.intake())
                        .onFalse(robotCommands.stopIntake());

                primaryController.leftTrigger(.4)
                        .onTrue(intakeCommands.set(IntakeState.SCORE))
                        .onFalse(intakeCommands.set(IntakeState.STOP));

                primaryController.leftBumper()
                        .onTrue(driveCommands.setDriveTrainSpeed(0.5))
                        .onFalse(driveCommands.setDriveTrainSpeed(1));
                primaryController.rightBumper()
                        .onTrue(driveCommands.setDriveTrainSpeed(1.5))
                        .onFalse(driveCommands.setDriveTrainSpeed(1));

                secondaryController
                        .button(5) // TODO but actual button #
                        .onTrue(elevatorWristCommands.setElevatorWrist(ElevatorWristState.L2));
                secondaryController
                        .button(6) // TODO but actual button #
                        .onTrue(elevatorWristCommands.setElevatorWrist(ElevatorWristState.L2));
                secondaryController
                        .button(7) // TODO but actual button #
                        .onTrue(elevatorWristCommands.setElevatorWrist(ElevatorWristState.L3));
                secondaryController
                        .button(8) // TODO but actual button #
                        .onTrue(elevatorWristCommands.setElevatorWrist(ElevatorWristState.L4));
                secondaryController
                        .button(9) // TODO but actual button #
                        .onTrue(elevatorWristCommands.setElevatorWrist(ElevatorWristState.INTAKING));
                secondaryController
                        .button(10) // TODO but actual button #
                        .onTrue(elevatorWristCommands.setElevatorWrist(ElevatorWristState.STOW));
                break;
            default:
                throw new Error("Invalid buttonmap type!");
        }

        secondaryController
                .button(14) // TODO set correct value
                .whileTrue(climbCommands.set(ClimbConstants.Targets.CLIMBED));
        secondaryController
                .button(15) // TODO set correct value
                .whileTrue(climbCommands.set(ClimbConstants.Targets.MIN));
        secondaryController
                .button(16) // TODO set correct value
                .whileTrue(climbCommands.set(ClimbConstants.Targets.MAX));
    }

    /**
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }
}
