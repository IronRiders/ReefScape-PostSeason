package org.ironriders.core;

import org.ironriders.elevator.ElevatorCommands;
import org.ironriders.elevator.ElevatorSubsystem;
import org.ironriders.lib.IronSubsystem;
import org.ironriders.wrist.WristCommands;
import org.ironriders.wrist.WristSubsystem;

import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

// This class contains all the state for the moving the elevator and wrist together. You should not call the wrist or elevator commands independently
public class ElevatorWristCTL extends IronSubsystem {
    private final WristSubsystem wristSubsystem = new WristSubsystem();
    private final WristCommands wristCommands = wristSubsystem.getCommands();

    private final ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
    private final ElevatorCommands elevatorCommands = elevatorSubsystem.getCommands();

    private final String diagnosticName = this.getClass().getSimpleName().replaceAll("Subsystem$", "");
    private final String dashboardPrefix = "Subsystems/" + diagnosticName + "/";

    public ElevatorWristCTL() {
        publish("Set to STOW", setElevatorWrist(ElevatorWristState.STOW));
        publish("Set to INTAKING", setElevatorWrist(ElevatorWristState.INTAKING));
        publish("Set to L2", setElevatorWrist(ElevatorWristState.L2));
        publish("Set to L3", setElevatorWrist(ElevatorWristState.L3));
        publish("Set to L4", setElevatorWrist(ElevatorWristState.L4));

        SmartDashboard.putData(dashboardPrefix + "Reset", reset());
        NamedCommands.registerCommand("Elevator Wrist Reset", (Command) reset());
    }

    public enum ElevatorLevel { // Position in inches
        DOWN(0),
        L2(7.5), // TODO - These need to be tuned
        L3(21),
        L4(53);

        public double pos;

        ElevatorLevel(double pos) {
            this.pos = pos;
        }
    }

    public enum WristRotation { // Position in degrees (theoretically)
        STOW(30), // TODO - These potentialy need an offset.
        INTAKING(-89),
        L2L3(40),
        L4(-7);

        public double pos;

        WristRotation(double pos) {
            this.pos = pos;
        }
    }

    public enum ElevatorWristState {
        STOW(ElevatorLevel.DOWN, WristRotation.STOW),
        INTAKING(ElevatorLevel.DOWN, WristRotation.INTAKING),
        L2(ElevatorLevel.L2, WristRotation.L2L3),
        L3(ElevatorLevel.L3, WristRotation.L2L3),
        L4(ElevatorLevel.L4, WristRotation.L4);

        public ElevatorLevel eLevel;
        public WristRotation wRot;

        ElevatorWristState(ElevatorLevel eLevel, WristRotation wRot) {
            this.eLevel = eLevel;
            this.wRot = wRot;
        }
    }

    public double getWristRotation() {
        return wristSubsystem.getCurrentAngle();
    }

    public double getElevatorHight() {
        return elevatorSubsystem.getHeight();
    }

    /*
     * This command sets both a elevator position and a wrist position.
     */

    public Command setElevatorWrist(ElevatorWristState state) {
        return Commands.parallel(wristCommands.set(state.wRot), elevatorCommands.set(state.eLevel),
                logMessage("goes to " + state.toString()));
    }

    /*
     * This command, in parallel, moves the wrist all the way in and does a PID
     * reset, as well as moving the elevator all the way down, rehoming it for good
     * measure, and then resetting it's PID.
     */

    public Command reset() {
        return Commands.parallel(wristCommands.stowReset(), elevatorCommands.downRehomeReset(), logMessage("reseting"));
    }

}
