package org.ironriders.intake;

import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAKE_BEAMBREAK;
import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAKE_MOTOR_LEFT;
import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAKE_MOTOR_LEFT_INVERSION;
import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAKE_MOTOR_RIGHT;
import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAKE_MOTOR_RIGHT_INVERSION;
import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAKE_MOTOR_TOP;
import static org.ironriders.intake.CoralIntakeConstants.CORAL_INTAK_NEUTRAL_MODE;

import org.ironriders.intake.CoralIntakeConstants.CoralIntakeState;
import org.ironriders.lib.IronSubsystem;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.MotorArrangementValue;

import com.ctre.phoenix6.configs.TalonFXSConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFXS;
import com.ctre.phoenix6.signals.ExternalFeedbackSensorSourceValue;
import com.revrobotics.spark.SparkLimitSwitch;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.units.AngularVelocityUnit;
import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj.DigitalInput;

import com.revrobotics.spark.config.SparkMaxConfig;

public class CoralIntakeSubsystem extends IronSubsystem {

    private final CoralIntakeCommands commands;

    private final TalonFX primaryIntakeMotor = new TalonFX(CORAL_INTAKE_MOTOR_LEFT);
    private final TalonFX rightIntake = new TalonFX(CORAL_INTAKE_MOTOR_RIGHT);
    private final TalonFX rollerIntake = new TalonFX(CORAL_INTAKE_MOTOR_TOP);
    private final DigitalInput beamBreak = new DigitalInput(CORAL_INTAKE_BEAMBREAK);


    public CoralIntakeSubsystem() {
       TalonFXConfiguration primaryConfiguration = new TalonFXConfiguration();
       primaryConfiguration.withCurrentLimits(
            new CurrentLimitsConfigs()
                .withStatorCurrentLimitEnable(true)
                .withSupplyCurrentLimitEnable(true)
                .withStatorCurrentLimit(30)
                .withSupplyCurrentLimit(40)
                .withSupplyCurrentLowerLimit(30)
                .withSupplyCurrentLowerTime(1.0)
            ).withMotorOutput(
            new MotorOutputConfigs()
                .withInverted(CORAL_INTAKE_MOTOR_LEFT_INVERSION)
                .withNeutralMode(CORAL_INTAK_NEUTRAL_MODE)
            );
        primaryIntakeMotor.getConfigurator().apply(primaryConfiguration);
        rightIntake.setControl(new Follower(CORAL_INTAKE_MOTOR_LEFT, !primaryConfiguration.MotorOutput.Inverted.equals(CORAL_INTAKE_MOTOR_RIGHT_INVERSION))); //TODO maybe make seperate function to check this
        rollerIntake.setControl(new Follower(CORAL_INTAKE_MOTOR_LEFT, !primaryConfiguration.MotorOutput.Inverted.equals(CORAL_INTAKE_MOTOR_RIGHT_INVERSION))); //TODO
        commands = new CoralIntakeCommands(this);
    }

    @Override
    public void periodic() {
        publish("Velocity", primaryIntakeMotor.getVelocity().getValue().in(Units.DegreesPerSecond));
        publish("Limit Switch Triggered", hasGamePiece());
    }

    public void set(CoralIntakeState state) {
        primaryIntakeMotor.set(state.getSpeed());

        publish("Set State", state.name());
    }

    public boolean hasGamePiece() {
        return beamBreak.get();
    }

    public CoralIntakeCommands getCommands() {
        return commands;
    }
}
