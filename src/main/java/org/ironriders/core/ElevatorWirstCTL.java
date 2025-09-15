package org.ironriders.core;

import org.ironriders.elevator.ElevatorCommands;
import org.ironriders.elevator.ElevatorSubsystem;
import org.ironriders.lib.IronSubsystem;
import org.ironriders.wrist.WristCommands;
import org.ironriders.wrist.WristSubsystem;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

// This class contains all the state for the moving the elevator and wrist together. You should not call the wrist or elevator commands independently
public class ElevatorWirstCTL extends IronSubsystem {
    private final WristSubsystem wristSubsystem = new WristSubsystem();
    private final WristCommands wristCommands = wristSubsystem.getCommands();

    private final ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
    private final ElevatorCommands elevatorCommands = elevatorSubsystem.getCommands();

    public ElevatorWirstCTL() {
        publish("Set to STOW", setElevatorWrist(ElevatorWristState.STOW));
        publish("Set to INTAKING", setElevatorWrist(ElevatorWristState.INTAKING));
        publish("Set to L2", setElevatorWrist(ElevatorWristState.L2));
        publish("Set to L3", setElevatorWrist(ElevatorWristState.L3));
        publish("Set to L4", setElevatorWrist(ElevatorWristState.L4));
    }

    public enum ElevatorLevel { // Position in inches
        DOWN(0),
        L2(7.5), // todo
        L3(21), // todo
        L4(53); // todo

        public double pos;

        ElevatorLevel(double pos) {
            this.pos = pos;
        }
    }

    public enum WristRotation { // Position in degrees (theoretically)
        STOW(30), // TODO - These potentialy need an offset.
        INTAKING(260),
        L2L3(40),
        L4(350);

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

    /*
     * This command sets both a elevator position and a wrist possition.
     */

    public Command setElevatorWrist(ElevatorWristState state) {
        return Commands.parallel(wristCommands.set(state.wRot), elevatorCommands.set(state.eLevel),
                logMessage("goes to " + state.toString()));
    }

    /*
     * This command, in parallel, moves the wrist all the way in and does a PID
     * reset, as well as moving the elevator all the way down, rehoming it for good
     * measure, and then reseting it's PID.
     */

    public Command reset() {
        return Commands.parallel(wristCommands.stowReset(), elevatorCommands.downRehomeReset(), logMessage("reseting"));
    }

}
