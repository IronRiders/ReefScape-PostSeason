package org.ironriders.wrist;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import org.ironriders.core.ElevatorWristCTL.WristRotation;
import org.ironriders.lib.IronSubsystem;

/** The subsystem in charge of the wrist. */
public class WristSubsystem extends IronSubsystem {
  final SparkMax primaryMotor =
      new SparkMax(WristConstants.PRIMARY_WRIST_MOTOR, MotorType.kBrushless);
  final SparkMax secondaryMotor =
      new SparkMax(WristConstants.SECONDARY_WRIST_MOTOR, MotorType.kBrushless);
  final TrapezoidProfile movementProfile =
      new TrapezoidProfile(new Constraints(WristConstants.MAX_VEL, WristConstants.MAX_ACC));

  private final PIDController pidControler;

  private TrapezoidProfile.State goalSetpoint =
      new TrapezoidProfile.State(); // Acts as a finalsetpoint
  private TrapezoidProfile.State periodicSetpoint =
      new TrapezoidProfile
          .State(); // Acts as atemporary setpoint for calculating the next speed value

  public WristRotation targetRotation = WristRotation.HOLD;

  private TrapezoidProfile.State stopped;

  private final WristCommands commands = new WristCommands(this);

  private final SparkMaxConfig motorConfig = new SparkMaxConfig();

  /** Initalizer. */
  public WristSubsystem() {
    motorConfig
        .smartCurrentLimit(30) // Can go to 40
        .idleMode(IdleMode.kBrake);

    primaryMotor.configure(
        motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    secondaryMotor.configure(
        motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    pidControler = new PIDController(WristConstants.P, WristConstants.I, WristConstants.D);
    pidControler.setTolerance(WristConstants.TOLERANCE);
    reset();
  }

  @Override
  public void periodic() {
    // Apply profile and PID to determine output level
    periodicSetpoint = movementProfile.calculate(WristConstants.T, periodicSetpoint, goalSetpoint);

    double speed = pidControler.calculate(getCurrentAngle(), periodicSetpoint.position);
    setMotors(speed);

    publish("Current PID ouput", speed);
    updateDashboard();
  }

  /** Put all the wrists values to smart dashboard. */
  public void updateDashboard() {
    publish("Current target", targetRotation.toString());
    publish("Current goal pos", goalSetpoint.position);
    publish("Current angle", getCurrentAngle());
    publish("Current angle raw", primaryMotor.getAbsoluteEncoder().getPosition());
    publish("At goal?", isAtPosition());
    publish("Wrist PID", pidControler);
  }

  public double getCurrentAngle() {
    return (primaryMotor.getAbsoluteEncoder().getPosition() - WristConstants.ENCODER_OFFSET) * 360
        + WristConstants.CAD_POSITION_OFFSET;
    /*
     * ENCODER_OFFSET is added to encoder to get it to = 0 when it is fully stowed
     * (against
     * hardstop) CAD_POSITION_OFFSET is adjustment for odd alignment in the CAD
     */
  }

  public boolean isAtPosition() {
    return pidControler.atSetpoint();
  }

  public void reset() {
    logMessage("resetting");

    pidControler.reset();

    stopped = new TrapezoidProfile.State(getCurrentAngle(), 0);

    goalSetpoint = stopped;
    periodicSetpoint = stopped;

    setMotors(0);
  }

  private void setMotors(double speed) {
    primaryMotor.set(speed);
    secondaryMotor.set(-speed);
  }

  protected void setGoal(WristRotation rotation) {
    goalSetpoint = new TrapezoidProfile.State(rotation.pos, 0);
    targetRotation = rotation;
  }

  public WristCommands getCommands() {
    return commands;
  }
}
