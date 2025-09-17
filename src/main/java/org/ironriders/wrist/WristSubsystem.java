package org.ironriders.wrist;

import org.ironriders.core.ElevatorWristCTL.WristRotation;
import org.ironriders.elevator.ElevatorConstants;
import org.ironriders.lib.IronSubsystem;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;

public class WristSubsystem extends IronSubsystem {
    final SparkMax primaryMotor = new SparkMax(WristConstants.PRIMARY_WRIST_MOTOR, MotorType.kBrushless);
    final SparkMax secondaryMotor = new SparkMax(WristConstants.SECONDARY_WRIST_MOTOR, MotorType.kBrushless);
    final TrapezoidProfile movementProfile = new TrapezoidProfile(
            new Constraints(WristConstants.MAX_VEL, WristConstants.MAX_ACC));
    public boolean atGoal = false;
    private PIDController pidControler;

    private TrapezoidProfile.State goalSetpoint = new TrapezoidProfile.State(); // Acts as a final setpoint
    private TrapezoidProfile.State periodicSetpoint = new TrapezoidProfile.State(); // Acts as a temporary setpoint for
    // calculating the next speed value

    public WristRotation targetRotation = WristRotation.STOW;

    private TrapezoidProfile.State stopped;

    private final WristCommands commands = new WristCommands(this);

    private final SparkMaxConfig motorConfig = new SparkMaxConfig();

    public WristSubsystem() {
        motorConfig
                .smartCurrentLimit(10) // Can go to 40
                .idleMode(IdleMode.kBrake)
                .inverted(true);

        // motorConfig.softLimit
        // .forwardSoftLimit(WristRotation.L4.pos)
        // .forwardSoftLimitEnabled(true);

        // motorConfig.softLimit
        // .reverseSoftLimit(WristRotation.STOW.pos)
        // .reverseSoftLimitEnabled(true);

        primaryMotor.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        secondaryMotor.configure(motorConfig.follow(WristConstants.PRIMARY_WRIST_MOTOR).inverted(false),
                ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);

        pidControler = new PIDController(
                WristConstants.P,
                WristConstants.I,
                WristConstants.D);
        pidControler.setTolerance(WristConstants.TOLERANCE);

        reset();
    }

    @Override
    public void periodic() {
        // Apply profile and PID to determine output level
        periodicSetpoint = movementProfile.calculate(
                WristConstants.T,
                periodicSetpoint,
                goalSetpoint);

        var speed = pidControler.calculate(getCurrentAngle(), periodicSetpoint.position);
        primaryMotor.set(speed);

        updateDashboard();
    }

    public void updateDashboard() {
        publish("Current target", targetRotation.toString());
        publish("Current goal pos", goalSetpoint.position);
        publish("Current angle", getCurrentAngle());
        publish("Current angle raw", primaryMotor.getAbsoluteEncoder().getPosition());

        publish("At goal?", isAtPosition());
    }

    public double getCurrentAngle() {
        return (primaryMotor.getAbsoluteEncoder().getPosition() - 0.935) * 360;
    }

    public boolean isAtPosition() {
        return pidControler.atSetpoint();
    }

    public void reset() {
        logMessage("resetting");

        pidControler.reset();

        //stopped = new TrapezoidProfile.State(getCurrentAngle(), 0);
        stopped = new TrapezoidProfile.State(-90, 0); // for testing


        goalSetpoint = stopped;
        periodicSetpoint = stopped;

        primaryMotor.set(0);
    }

    protected void setGoal(WristRotation rotation) {
        goalSetpoint = new TrapezoidProfile.State(rotation.pos, 0);
        publish("Is rotation bad? please look", goalSetpoint.position);
        targetRotation = rotation;
    }

    public WristCommands getCommands() {
        return commands;
    }

}
