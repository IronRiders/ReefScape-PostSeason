package org.ironriders.core;

import java.util.function.DoubleSupplier;
import org.ironriders.coral.CoralIntakeConstants;
import org.ironriders.coral.CoralWristConstants;
import org.ironriders.algae.AlgaeIntakeCommands;
import org.ironriders.algae.AlgaeWristCommands;
import org.ironriders.coral.CoralIntakeCommands;
import org.ironriders.coral.CoralWristCommands;
import org.ironriders.drive.DriveCommands;
import org.ironriders.elevator.ElevatorCommands;
import org.ironriders.elevator.ElevatorConstants;
import org.ironriders.drive.DriveConstants;
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
public class RobotCommands {
	private final DriveCommands driveCommands;
	private final ElevatorCommands elevatorCommands;
	private final CoralWristCommands coralWristCommands;
	private final CoralIntakeCommands coralIntakeCommands;
	private final AlgaeWristCommands algaeWristCommands;
	private final AlgaeIntakeCommands algaeIntakeCommands;
	private final GenericHID controller;

	public RobotCommands(
			DriveCommands driveCommands,
			ElevatorCommands elevatorCommands,
			CoralWristCommands coralWristCommands, CoralIntakeCommands coralIntakeCommands,
			AlgaeWristCommands algaeWristCommands, AlgaeIntakeCommands
			algaeIntakeCommands,
			GenericHID controller) {

		this.driveCommands = driveCommands;
		this.elevatorCommands = elevatorCommands;
		this.coralWristCommands = coralWristCommands;
		this.coralIntakeCommands = coralIntakeCommands;
		this.algaeWristCommands = algaeWristCommands;
		this.algaeIntakeCommands = algaeIntakeCommands;
		this.controller = controller;

		// register named commands
		// NamedCommands.registerCommand("Prepare to Score Algae",
		// this.prepareToScoreAlgae());
		// NamedCommands.registerCommand("Score Algae", this.scoreAlgae());

		NamedCommands.registerCommand("Prepare to Score Coral L1", this.prepareToScoreCoral(ElevatorConstants.Level.L1));
		NamedCommands.registerCommand("Prepare to Score Coral L2", this.prepareToScoreCoral(ElevatorConstants.Level.L2));
		NamedCommands.registerCommand("Prepare to Score Coral L3", this.prepareToScoreCoral(ElevatorConstants.Level.L3));
		NamedCommands.registerCommand("Prepare to Score Coral L4", this.prepareToScoreCoral(ElevatorConstants.Level.L4));
		NamedCommands.registerCommand("Score Coral", this.scoreCoral());

		// NamedCommands.registerCommand("Prepare to Grab Algae",
		// this.prepareToGrabAlgae());
		// NamedCommands.registerCommand("Grab Algae", this.grabAlgae());

		// NamedCommands.registerCommand("Prepare to Grab Coral",
		// this.prepareToGrabCoral());
		// NamedCommands.registerCommand("Grab Coral", this.grabCoral());
	}

	/**
	 * Initialize all subsystems when first enabled.
	 * 
	 * This primarily involves homing.  We need to home sequentially coral -> algae -> elevator due to physical
	 * limitations.
	 */
	public Command startup() {
		return
			coralWristCommands.home()
			.andThen(algaeWristCommands.home())
			.andThen(elevatorCommands.home());
	}

	/**
	 * Command to drive the robot given controller input.
	 * 
	 * @param inputTranslationX DoubleSupplier, value from 0-1.
	 * @param inputTranslationY DoubleSupplier, value from 0-1.
	 * @param inputRotation     DoubleSupplier, value from 0-1.
	 */
	public Command driveTeleop(DoubleSupplier inputTranslationX, DoubleSupplier inputTranslationY,
			DoubleSupplier inputRotation) {
		return driveCommands.driveTeleop(inputTranslationX, inputTranslationY, inputRotation, true);
	}

	/**
	 * Produce command to jog the robot at specified robot-relative angle.
	 * 
	 * Jog distance is {@value DriveConstants#JOG_DISTANCE_INCHES}.
	 */
	public Command jog(double robotRelativeAngleDegrees) {
		return driveCommands.jog(robotRelativeAngleDegrees);
	}

	// public Command scoreCoralMiniauto(ElevatorConstants.Level level) {
	// return Commands.sequence(
	// Commands.parallel(
	// driveCommands.driveToPose()
	// this.prepareToScoreCoral(level)),
	// this.scoreCoral());

	// }

	public Command rumble() {
		return Commands.sequence(
				Commands.runOnce(() -> controller.setRumble(GenericHID.RumbleType.kBothRumble, 1)),
				Commands.waitSeconds(0.3),
				Commands.runOnce(() -> controller.setRumble(GenericHID.RumbleType.kBothRumble, 0)))
				.handleInterrupt(() -> controller.setRumble(GenericHID.RumbleType.kBothRumble, 0));
	}

	public Command toggleClimber() {
		return Commands.none();
		// TODO
	}

	// public Command prepareToScoreAlgae() {
	// return Commands.parallel(
	// elevatorCommands.set(ElevatorConstants.Level.Down),
	// algaeWristCommands.set(AlgaeWristState.EXTENDED));
	// }

	// public Command scoreAlgae() {
	// // TODO: option to grab coral in parallel
	// return Commands.sequence(
	// algaeIntakeCommands.set(AlgaeIntakeState.EJECT),
	// algaeIntakeCommands.set(AlgaeIntakeState.STOP),
	// algaeWristCommands.set(AlgaeWristState.STOWED));
	// }

	public Command prepareToScoreCoral(ElevatorConstants.Level level) {
		return Commands.parallel(
				elevatorCommands.set(level),
				coralWristCommands.set(switch (level) {
					case L1, L2, L3 -> CoralWristConstants.State.L1toL3;
					case L4 -> CoralWristConstants.State.L4;
					default -> {
						throw new IllegalArgumentException(
								"Cannot score coral to level: " + level);
					}
				}));
	}

	public Command scoreCoral() {
		return Commands.sequence(
				coralIntakeCommands.set(CoralIntakeConstants.State.EJECT),
				Commands.parallel(
						coralWristCommands.set(CoralWristConstants.State.STOWED),
						elevatorCommands.set(ElevatorConstants.Level.Down)));
	}

	// public Command prepareToGrabAlgae(ElevatorConstants.Level level) {
	// return Commands.parallel(
	// elevatorCommands.set(level),
	// algaeWristCommands.set(AlgaeWristState.EXTENDED),
	// algaeIntakeCommands.set(AlgaeIntakeState.GRAB));
	// }

	// public Command grabAlgae() {
	// return Commands.sequence(
	// algaeIntakeCommands.set(AlgaeIntakeState.GRAB),
	// algaeWristCommands.set(AlgaeWristState.STOWED),
	// this.rumble());
	// }

	public Command prepareToGrabCoral() {
		return Commands.sequence(
				coralWristCommands.set(CoralWristConstants.State.STATION),
				elevatorCommands.set(ElevatorConstants.Level.CoralStation));
	}

	public Command grabCoral() {
		return Commands.sequence(
				coralIntakeCommands.set(CoralIntakeConstants.State.GRAB),
				coralWristCommands.set(CoralWristConstants.State.STOWED),
				rumble());
	}
}
