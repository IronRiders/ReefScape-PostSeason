package org.ironriders.wrist;

import org.ironriders.core.ElevatorWristCTL.WristRotation;
import org.ironriders.lib.IronSubsystem;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.pathplanner.lib.path.RotationTarget;
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
    final PIDController pid = new PIDController(WristConstants.P, WristConstants.I, WristConstants.D);

    private TrapezoidProfile.State goalSetpoint = new TrapezoidProfile.State(); // Acts as a final setpoint
    private TrapezoidProfile.State periodicSetpoint = new TrapezoidProfile.State(); // Acts as a temporary setpoint for
    // calculating the next speed value

    public WristRotation targetRotation;

    private TrapezoidProfile.State stopped;

    private final WristCommands commands = new WristCommands(this);

    private final SparkMaxConfig motorConfig = new SparkMaxConfig();

    public WristSubsystem() {
        motorConfig
                .smartCurrentLimit(10) // Can go to 40
                .idleMode(IdleMode.kBrake);

        motorConfig.softLimit
                .forwardSoftLimit(WristRotation.L4.pos)
                .forwardSoftLimitEnabled(true);

        motorConfig.softLimit
                .reverseSoftLimit(WristRotation.STOW.pos)
                .reverseSoftLimitEnabled(true);

        primaryMotor.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        secondaryMotor.configure(motorConfig.follow(WristConstants.PRIMARY_WRIST_MOTOR).inverted(true),
                ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);

        pid.setTolerance(WristConstants.TOLERANCE);

        reset();
    }

    @Override
    public void periodic() {
        var currentDegrees = getCurrentAngle();

        // Apply profile and PID to determine output level
        periodicSetpoint = movementProfile.calculate(
                WristConstants.T,
                periodicSetpoint,
                goalSetpoint);

        var speed = pid.calculate(currentDegrees, periodicSetpoint.position);
        primaryMotor.set(speed);

        atGoal = pid.atSetpoint();
    }

    public void updateDashboard() {
        publish("Current target", targetRotation.toString());
        publish("Current goal pos", goalSetpoint.position);
        publish("Current angle", getCurrentAngle());
        publish("At goal?", atGoal);
    }

    public double getCurrentAngle() {
        return primaryMotor.getEncoder().getPosition() * 360;
    }

    public void reset() {
        logMessage("reseting");

        pid.reset();

        stopped = new TrapezoidProfile.State(getCurrentAngle(), 0);

        goalSetpoint = stopped;
        periodicSetpoint = stopped;

        primaryMotor.set(0);
    }

    protected void setGoal(WristRotation rotation) {
        goalSetpoint = new TrapezoidProfile.State(rotation.pos, 0);
        targetRotation = rotation;
    }

    public WristCommands getCommands() {
        return commands;
    }

}
