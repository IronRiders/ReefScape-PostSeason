package org.ironriders.intake;

import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAKE_BEAMBREAK;
import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAKE_MOTOR_LEFT;
import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAKE_MOTOR_LEFT_INVERSION;
import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAKE_MOTOR_RIGHT;
import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAKE_MOTOR_RIGHT_INVERSION;
import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAKE_MOTOR_ROLLER_INVERSION;
import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAKE_MOTOR_TOP;
import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAKE_STATOR_CURRENT;
import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAKE_SUPPLY_CURRENT;
import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAKE_SUPPLY_CURRENT_LOWER_LIMIT;
import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAKE_SUPPLY_CURRENT_LOWER_TIME;
import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAKE_NEUTRAL_MODE;
import static org.ironriders.intake.CoralIntakeConstants.LEFT_SPEED_MUL;
import static org.ironriders.intake.CoralIntakeConstants.RIGHT_SPEED_MUL;
import static org.ironriders.intake.CoralIntakeConstants.ROLLER_SPEED_MUL;

import org.ironriders.intake.CoralIntakeConstants.CoralIntakeState;
import org.ironriders.lib.IronSubsystem;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj.DigitalInput;

public class CoralIntakeSubsystem extends IronSubsystem {

  private final CoralIntakeCommands commands;

  private final TalonFX rightIntake = new TalonFX(CORAL_INTAKE_MOTOR_RIGHT);
  private final TalonFX leftIntake = new TalonFX(CORAL_INTAKE_MOTOR_LEFT);
  private final TalonFX rollerIntake = new TalonFX(CORAL_INTAKE_MOTOR_TOP);
  private final DigitalInput beamBreak = new DigitalInput(
      CORAL_INTAKE_BEAMBREAK);

  public CoralIntakeSubsystem() {
    TalonFXConfiguration mainConfig = new TalonFXConfiguration();
    mainConfig
        .withCurrentLimits(
            new CurrentLimitsConfigs()
                .withStatorCurrentLimitEnable(true)
                .withSupplyCurrentLimitEnable(true)
                .withStatorCurrentLimit(CORAL_INTAKE_STATOR_CURRENT)
                .withSupplyCurrentLimit(CORAL_INTAKE_SUPPLY_CURRENT)
                .withSupplyCurrentLowerLimit(CORAL_INTAKE_SUPPLY_CURRENT_LOWER_LIMIT)
                .withSupplyCurrentLowerTime(CORAL_INTAKE_SUPPLY_CURRENT_LOWER_TIME))
        .withMotorOutput(
            new MotorOutputConfigs()
                .withNeutralMode(CORAL_INTAKE_NEUTRAL_MODE));

    // TODO: This is ugly as hell
    leftIntake.getConfigurator().apply(mainConfig);
    leftIntake.getConfigurator().apply(new MotorOutputConfigs().withInverted(CORAL_INTAKE_MOTOR_LEFT_INVERSION));

    rightIntake.getConfigurator().apply(mainConfig);
    rightIntake.getConfigurator().apply(new MotorOutputConfigs().withInverted(CORAL_INTAKE_MOTOR_RIGHT_INVERSION));

    rollerIntake.getConfigurator().apply(mainConfig);
    rollerIntake.getConfigurator().apply(new MotorOutputConfigs().withInverted(CORAL_INTAKE_MOTOR_ROLLER_INVERSION));

    commands = new CoralIntakeCommands(this);
  }

  @Override
  public void periodic() {
    publish(
        "Left Velocity",
        leftIntake.getVelocity().getValue().in(Units.DegreesPerSecond));
    publish(
        "Right Velocity",
        rightIntake.getVelocity().getValue().in(Units.DegreesPerSecond));
    publish("Limit Switch Triggered", hasGamePiece());
  }

  public void set(CoralIntakeState state) {
    publish("Updated Intake State", state.toString());
    leftIntake.set(state.getSpeed() * outputDifferential(state, (LEFT_SPEED_MUL * state.offset)));
    rightIntake.set(state.getSpeed() * outputDifferential(state, (RIGHT_SPEED_MUL * state.offset)));
    rollerIntake.set(state.getSpeed() * outputDifferential(state, (ROLLER_SPEED_MUL * state.offset)));

    publish("Set State", state.name());
  }

  public double outputDifferential(CoralIntakeState state, double controlSpeedMultipler) {
    if (state.toString().equals(CoralIntakeState.GRAB.toString())) {
      return controlSpeedMultipler;
    }
    return 1;
  }

  public boolean hasGamePiece() {
    return beamBreak.get();
  }

  public CoralIntakeCommands getCommands() {
    return commands;
  }
}
