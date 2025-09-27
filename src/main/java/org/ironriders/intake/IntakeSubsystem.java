package org.ironriders.intake;

import static org.ironriders.intake.IntakeConstants.INTAKE_BEAMBREAK;
import static org.ironriders.intake.IntakeConstants.INTAKE_MOTOR_LEFT;
import static org.ironriders.intake.IntakeConstants.INTAKE_MOTOR_LEFT_INVERSION;
import static org.ironriders.intake.IntakeConstants.INTAKE_MOTOR_RIGHT;
import static org.ironriders.intake.IntakeConstants.INTAKE_MOTOR_RIGHT_INVERSION;
import static org.ironriders.intake.IntakeConstants.INTAKE_MOTOR_ROLLER_INVERSION;
import static org.ironriders.intake.IntakeConstants.INTAKE_MOTOR_TOP;
import static org.ironriders.intake.IntakeConstants.INTAKE_NEUTRAL_MODE;
import static org.ironriders.intake.IntakeConstants.INTAKE_STATOR_CURRENT;
import static org.ironriders.intake.IntakeConstants.INTAKE_SUPPLY_CURRENT;
import static org.ironriders.intake.IntakeConstants.INTAKE_SUPPLY_CURRENT_LOWER_LIMIT;
import static org.ironriders.intake.IntakeConstants.INTAKE_SUPPLY_CURRENT_LOWER_TIME;
import static org.ironriders.intake.IntakeConstants.LEFT_SPEED_MUL;
import static org.ironriders.intake.IntakeConstants.MAX_ACC;
import static org.ironriders.intake.IntakeConstants.MAX_VEL;
import static org.ironriders.intake.IntakeConstants.RIGHT_SPEED_MUL;
import static org.ironriders.intake.IntakeConstants.ROLLER_SPEED_MUL;
import static org.ironriders.intake.IntakeConstants.TARGET_SETPOINT;
import static org.ironriders.intake.IntakeConstants.TOLERANCE;
import static org.ironriders.intake.IntakeConstants.WHEEL_CIRCUMFERENCE;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj.DigitalInput;
import org.ironriders.intake.IntakeConstants.IntakeState;
import org.ironriders.lib.Elastic.Notification;
import org.ironriders.lib.IronSubsystem;

public class IntakeSubsystem extends IronSubsystem {

  private final IntakeCommands commands;

  private final TalonFX rightIntake = new TalonFX(INTAKE_MOTOR_RIGHT);
  private final TalonFX leftIntake = new TalonFX(INTAKE_MOTOR_LEFT);
  private final TalonFX rollerIntake = new TalonFX(INTAKE_MOTOR_TOP);

  private final TrapezoidProfile profile =
      new TrapezoidProfile(new TrapezoidProfile.Constraints(MAX_VEL, MAX_ACC));
  private final PIDController pidController =
      new PIDController(IntakeConstants.P, IntakeConstants.I, IntakeConstants.D);

  // goalSetpoint is the final goal. periodicSetpoint is a sort-of inbetween
  // setpoint generated every periodic.
  private final TrapezoidProfile.State goalSetpoint = new TrapezoidProfile.State();
  private TrapezoidProfile.State periodicSetpoint = new TrapezoidProfile.State();

  private boolean shouldPIDControl;
  private boolean PIDControlOveride;
  private double targetSpeed = 0;
  private double positionOffset = 0;

  private final DigitalInput beamBreak = new DigitalInput(INTAKE_BEAMBREAK);

  public IntakeSubsystem() {
    TalonFXConfiguration mainConfig = new TalonFXConfiguration();
    mainConfig
        .withCurrentLimits(
            new CurrentLimitsConfigs()
                .withStatorCurrentLimitEnable(true)
                .withSupplyCurrentLimitEnable(true)
                .withStatorCurrentLimit(INTAKE_STATOR_CURRENT)
                .withSupplyCurrentLimit(INTAKE_SUPPLY_CURRENT)
                .withSupplyCurrentLowerLimit(INTAKE_SUPPLY_CURRENT_LOWER_LIMIT)
                .withSupplyCurrentLowerTime(INTAKE_SUPPLY_CURRENT_LOWER_TIME))
        .withMotorOutput(new MotorOutputConfigs().withNeutralMode(INTAKE_NEUTRAL_MODE));

    // TODO: This is ugly as hell
    leftIntake.getConfigurator().apply(mainConfig);
    leftIntake
        .getConfigurator()
        .apply(new MotorOutputConfigs().withInverted(INTAKE_MOTOR_LEFT_INVERSION));

    rightIntake.getConfigurator().apply(mainConfig);
    rightIntake
        .getConfigurator()
        .apply(new MotorOutputConfigs().withInverted(INTAKE_MOTOR_RIGHT_INVERSION));

    rollerIntake.getConfigurator().apply(mainConfig);
    rollerIntake
        .getConfigurator()
        .apply(new MotorOutputConfigs().withInverted(INTAKE_MOTOR_ROLLER_INVERSION));

    pidController.setTolerance(TOLERANCE);

    commands = new IntakeCommands(this);
  }

  @Override
  public void periodic() {
    if (beamBreakTriggered() && !shouldPIDControl) { // leading edge
      shouldPIDControl = true;
      positionOffset = getRotation();
      goalSetpoint.position = TARGET_SETPOINT;
      logMessage("starting pid control");
    }

    if (atGoal() && shouldPIDControl) { // trailing edge
      shouldPIDControl = false;
      targetSpeed = 0;
      logMessage("stoping pid control, at setpoint");
    }

    periodicSetpoint = profile.calculate(IntakeConstants.T, periodicSetpoint, goalSetpoint);

    if (shouldPIDControl && !PIDControlOveride) {
      double pidOutput = pidController.calculate(getOffsetRotation(), periodicSetpoint.position);

      setMotorsNoDiff(pidOutput);
    } else {
      setMotors(targetSpeed);
    }

    updateDashboard();
  }

  public void setMotors(double speed) {
    leftIntake.set(speed * outputDifferential(speed, LEFT_SPEED_MUL));
    rightIntake.set(speed * outputDifferential(speed, RIGHT_SPEED_MUL));
    rollerIntake.set(speed * outputDifferential(speed, ROLLER_SPEED_MUL));
  }

  public void setMotorsNoDiff(double speed) {
    leftIntake.set(speed);
    rightIntake.set(speed);
    rollerIntake.set(speed);
  }

  public void setGoal(double goal) {
    goalSetpoint.position = goal;
  }

  public double getGoal() {
    return goalSetpoint.position;
  }

  public boolean atGoal() {
    return pidController.atSetpoint();
  }

  public void updateDashboard() {
    publish("Left Velocity", leftIntake.getVelocity().getValue().in(Units.DegreesPerSecond));
    publish("Right Velocity", rightIntake.getVelocity().getValue().in(Units.DegreesPerSecond));
    publish("Beam Break Triggered", beamBreakTriggered());
    publish("Target Speed", targetSpeed);
    publish("Under PID Control?", shouldPIDControl);
    publish("Intake PID", pidController);
  }

  public void set(IntakeState state) {
    publish("Intake State", state.toString());
    // logMessage("goes to " + state.toString());]
    switch (state) {
      case GRAB:
        PIDControlOveride = false;
        break;

      default:
        PIDControlOveride = true;
        break;
    }

    targetSpeed = state.speed;
  }

  public double outputDifferential(Double speed, double controlSpeedMultipler) {
    if (speed != IntakeState.GRAB.speed) {
      return controlSpeedMultipler;
    }
    return 1;
  }

  public double getOffsetRotation() {
    if (positionOffset == 0) {
      notifyWarning(new Notification("Offset not set!", "Bad things will happen!"));
    }

    double offsetRotation = getRotation() - positionOffset;

    return offsetRotation;
  }

  public boolean hasHighCurrent() {
    return false;
    // return average > 12 && !beamBreak.get(); disabled because beam break made it
    // hard to intake
  }

  public boolean beamBreakTriggered() {
    return !beamBreak.get();
  }

  /*
   * Returns in INCHES!
   */
  public double getRotation() {
    return ((leftIntake.getPosition().getValueAsDouble()
                + rightIntake.getPosition().getValueAsDouble())
            / 2f)
        * WHEEL_CIRCUMFERENCE;
  }

  public IntakeCommands getCommands() {
    return commands;
  }
}
