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

    private final String diagnosticName = this.getClass().getSimpleName();
    private final String dashboardPrefix = "Subsystems/" + diagnosticName + "/";

    public ElevatorWristCTL() {
        publish("Set to STOW", setElevatorWrist(ElevatorWristState.HOLD));
        publish("Set to INTAKING", setElevatorWrist(ElevatorWristState.INTAKING));
        publish("Set to L2", setElevatorWrist(ElevatorWristState.L2));
        publish("Set to L3", setElevatorWrist(ElevatorWristState.L3));
        publish("Set to L4", setElevatorWrist(ElevatorWristState.L4));

        SmartDashboard.putData(dashboardPrefix + "Reset", reset());
        NamedCommands.registerCommand("Elevator Wrist Reset", (Command) reset());
    }

    public enum ElevatorLevel { // Position in inches
        DOWN(0),
        L2(19.5),
        L3(39),
        L4(53);

        public final double pos;

        ElevatorLevel(double pos) {
            this.pos = pos;
        }
    }

    public enum WristRotation { // Position in degrees
        HOLD(0),
        INTAKING(-85),
        L2L3(40),
        L4(0);

        public final double pos;

        WristRotation(double pos) {
            this.pos = pos;
        }
    }

    public enum ElevatorWristState {
        HOLD(ElevatorLevel.DOWN, WristRotation.HOLD),
        INTAKING(ElevatorLevel.DOWN, WristRotation.INTAKING),
        L2(ElevatorLevel.L2, WristRotation.L2L3),
        L3(ElevatorLevel.L3, WristRotation.L2L3),
        L4(ElevatorLevel.L4, WristRotation.L4);

        public final ElevatorLevel eLevel;
        public final WristRotation wRot;

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

    public ElevatorSubsystem getElevatorSubsystem() {
        return elevatorSubsystem;
    }

    public WristSubsystem getWristSubsystem() {
        return wristSubsystem;
    }

    /*
     * This command sets both a elevator position and a wrist position.
     */

    public Command setElevatorWrist(ElevatorWristState state) {
        logMessage("goes to " + state.toString());
        return Commands.sequence(wristCommands.set(WristRotation.HOLD), elevatorCommands.set(state.eLevel),  wristCommands.set(state.wRot));
    }

    /*
     * This command, in parallel, moves the wrist all the way in and does \a PID
     * reset, as well as moving the elevator all the way down, rehoming it for good
     * measure, and then resetting it's PID.
     */

    public Command reset() {
        return Commands.sequence(logMessage("reseting"), wristCommands.stowReset(), elevatorCommands.home());
    }

}
