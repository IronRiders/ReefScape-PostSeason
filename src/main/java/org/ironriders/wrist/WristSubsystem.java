package org.ironriders.wrist;

import static org.ironriders.intake.CoralIntakeConstants.MAX_ACC;
import static org.ironriders.intake.CoralIntakeConstants.MAX_VEL;

import org.ironriders.core.ElevatorWirstCTL.WristRotation;
import org.ironriders.intake.CoralIntakeCommands;
import org.ironriders.lib.IronSubsystem;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;

public class WristSubsystem extends IronSubsystem {
    final SparkMax primaryMotor = new SparkMax(WristConstants.PRIMARY_WRIST_MOTOR, MotorType.kBrushless);
    final SparkMax secondaryMotor = new SparkMax(WristConstants.SECONDARY_WRIST_MOTOR, MotorType.kBrushless);
    final TrapezoidProfile movementProfile = new TrapezoidProfile(new Constraints(MAX_VEL, MAX_ACC));
    public boolean atGoal = false;
    final PIDController pid = new PIDController(WristConstants.P, WristConstants.I, WristConstants.D);

    TrapezoidProfile.State goalSetpoint = new TrapezoidProfile.State(); // Acts as a final setpoint
    TrapezoidProfile.State periodicSetpoint = new TrapezoidProfile.State(); // Acts as a temporary setpoint for
                                                                            // calculating the next speed value

    TrapezoidProfile.State stopped = new TrapezoidProfile.State(getCurrentAngle(), 0);

    private final WristCommands commands = new WristCommands(this);

    final SparkMaxConfig motorConfig = (SparkMaxConfig) new SparkMaxConfig()
            .smartCurrentLimit(10) // Can go to 40
            .idleMode(IdleMode.kBrake);

    public WristSubsystem() {
        primaryMotor.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        secondaryMotor.configure(motorConfig.follow(WristConstants.PRIMARY_WRIST_MOTOR).inverted(true),
                ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);

        pid.setTolerance(WristConstants.TOLERANCE);
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

    public double getCurrentAngle() {
        return primaryMotor.getEncoder().getPosition() * 360;
    }

    public void reset() {
        pid.reset();

        goalSetpoint = stopped;
        periodicSetpoint = stopped;

        primaryMotor.set(0);
    }

    protected void setGoal(WristRotation rotation) {
        goalSetpoint = new TrapezoidProfile.State(rotation.pos, 0);
    }

    public WristCommands getCommands() {
        return commands;
    }

}
