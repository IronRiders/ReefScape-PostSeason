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
import static org.ironriders.intake.IntakeConstants.RIGHT_SPEED_MUL;
import static org.ironriders.intake.IntakeConstants.ROLLER_SPEED_MUL;
import static org.ironriders.intake.IntakeConstants.VERTICAL_ENCODER_SCALE;

import org.ironmaple.simulation.IntakeSimulation.IntakeSide;
import org.ironriders.intake.IntakeConstants.IntakeState;
import org.ironriders.lib.IronSubsystem;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj.DigitalInput;

public class IntakeSubsystem extends IronSubsystem {

  private final IntakeCommands commands;

  private PIDController pidControler;
  private TrapezoidProfile.State stopped;

  private TrapezoidProfile.State goalSetpoint = new TrapezoidProfile.State(); // Acts as a final setpoint
  private TrapezoidProfile.State periodicSetpoint = new TrapezoidProfile.State(); //Acts a intermidete setpoint
  final TrapezoidProfile movementProfile = new TrapezoidProfile(
            new Constraints(IntakeConstants.MAX_VEL, IntakeConstants.MAX_ACC));

  private IntakeState intakeState = IntakeState.STOP;

  private final TalonFX rightIntake = new TalonFX(INTAKE_MOTOR_RIGHT);
  private final TalonFX leftIntake = new TalonFX(INTAKE_MOTOR_LEFT);
  private final TalonFX rollerIntake = new TalonFX(INTAKE_MOTOR_TOP);

  private double average = 0;

  private final DigitalInput beamBreak = new DigitalInput(
    INTAKE_BEAMBREAK);

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
        .withMotorOutput(
            new MotorOutputConfigs()
                .withNeutralMode(INTAKE_NEUTRAL_MODE));

    // TODO: This is ugly as hell
    leftIntake.getConfigurator().apply(mainConfig);
    leftIntake.getConfigurator().apply(new MotorOutputConfigs().withInverted(INTAKE_MOTOR_LEFT_INVERSION));

    rightIntake.getConfigurator().apply(mainConfig);
    rightIntake.getConfigurator().apply(new MotorOutputConfigs().withInverted(INTAKE_MOTOR_RIGHT_INVERSION));

    rollerIntake.getConfigurator().apply(mainConfig);
    rollerIntake.getConfigurator().apply(new MotorOutputConfigs().withInverted(INTAKE_MOTOR_ROLLER_INVERSION));


    pidControler = new PIDController(
                IntakeConstants.PROPORTIONAL,
                IntakeConstants.INTEGRAL,
                IntakeConstants.DERIVATIVE);
        pidControler.setTolerance(IntakeConstants.TOLERANCE);

    commands = new IntakeCommands(this);
  }

  @Override
  public void periodic() {
    publish(
        "Left Velocity",
        leftIntake.getVelocity().getValue().in(Units.DegreesPerSecond));
    publish(
        "Right Velocity",
        rightIntake.getVelocity().getValue().in(Units.DegreesPerSecond));
    publish("Beam Break Triggered", hasHighCurrent());
    average = (leftIntake.getTorqueCurrent().getValueAsDouble() + rightIntake.getTorqueCurrent().getValueAsDouble())
        / 2f;
    publish("Current average", average);


    periodicSetpoint = movementProfile.calculate(
                0.02,
                periodicSetpoint,
                goalSetpoint);

        double speed = pidControler.calculate(getCurrentAngle(), periodicSetpoint.position);
        if(intakeState.equals(IntakeState.JOG)){
          leftIntake.set(speed);
          rightIntake.set(speed);
        }
  }

  public void set(IntakeState state) {
    intakeState = state;
    publish("Intake State", state.toString());
    // logMessage("goes to " + state.toString());
      if(state.toString().equals(IntakeState.JOG.toString())){
        leftIntake.set(state.speed * outputDifferential(state, LEFT_SPEED_MUL));
        rightIntake.set(state.speed * outputDifferential(state, RIGHT_SPEED_MUL));
        rollerIntake.set(state.speed * outputDifferential(state, ROLLER_SPEED_MUL));
      }
  }

  public double outputDifferential(IntakeState state, double controlSpeedMultipler) {
    if (state.toString().equals(IntakeState.GRAB.toString())) {
      return controlSpeedMultipler;
    }
    return 1;
  }

  public boolean hasHighCurrent() {
    return false; 
    // return average > 12 && !beamBreak.get(); disabled because beam break made it hard to intake
  }

  public boolean beamBreakTriggered() {
    return !beamBreak.get();
  }

  public double getCurrentAngle(){
    return rightIntake.getPosition().getValue().in(Units.Degrees) * VERTICAL_ENCODER_SCALE;
  }

  public void setGoalAngle(double angleOffsetFromCurrent){ // The intake could be in any position and we just want to set it to a value 
    goalSetpoint = new TrapezoidProfile.State(getCurrentAngle() +angleOffsetFromCurrent, 0);
  }

  public IntakeCommands getCommands() {
    return commands;
  }
}
