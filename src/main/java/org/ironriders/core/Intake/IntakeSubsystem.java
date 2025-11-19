package org.ironriders.core.Intake;
import static org.ironriders.core.Intake.IntakeConstants.INTAKE_STATOR_CURRENT;
import static org.ironriders.core.Intake.IntakeConstants.INTAKE_SUPPLY_CURRENT;
import static org.ironriders.core.Intake.IntakeConstants.INTAKE_MOTOR_LEFT;
import static org.ironriders.core.Intake.IntakeConstants.INTAKE_MOTOR_RIGHT;
import static org.ironriders.core.Intake.IntakeConstants.INTAKE_MOTOR_TOP;
import static org.ironriders.core.Intake.IntakeConstants.INTAKE_NEUTRAL_MODE;

import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs; // Ensure this is the correct class name
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.Command;

import org.ironriders.core.RobotCommands;
import org.ironriders.core.Intake.IntakeConstants;
import org.ironriders.core.Intake.IntakeCommands; 
import org.ironriders.core.Intake.IntakeConstants.IntakeState;


    public class IntakeSubsystem extends org.ironriders.lib.IronSubsystem {
            private final TalonFX rightMotor = new TalonFX(INTAKE_MOTOR_RIGHT);
        private final TalonFX leftMotor = new TalonFX(INTAKE_MOTOR_LEFT);
    private final TalonFX rollerMotor = new TalonFX(INTAKE_MOTOR_TOP);
    private final IntakeCommands commands;

    public void debugPublish(String name, Command command) {

    // Implementation for publishing debug commands TOTALLY NOT GITHUB COPILLIOT

    System.out.println("Debug Command Published: " + name);
    }

    public Command runOnce(Runnable toRun) {

        return new Command() {

            @Override
            public void initialize() {
                toRun.run();
            }

            @Override
            public boolean isFinished() {
                return true;
            }
        };
    }

    public IntakeSubsystem() {
        TalonFXConfiguration mainConfig = new TalonFXConfiguration();
        mainConfig.withCurrentLimits(
            new CurrentLimitsConfigs()
                .withStatorCurrentLimitEnable(true)
                .withSupplyCurrentLimitEnable(true)
                .withStatorCurrentLimit(INTAKE_STATOR_CURRENT)
                .withSupplyCurrentLimit(INTAKE_SUPPLY_CURRENT))
                // Software leads need to help with .withMotorOutput 
            .withMotorOutput(new MotorOutputConfigs()
            .withNeutralMode(INTAKE_NEUTRAL_MODE));
            leftMotor.getConfigurator().apply(mainConfig);  
        leftMotor.getConfigurator().apply(mainConfig);  
        commands = new IntakeCommands(this);
    }

    public IntakeCommands getCommands() {
        return commands;
    }

    @Override
    public void periodic() {
        debugPublish("Left Velocity",
            leftMotor.getVelocity().getValue().in(Unite.DegreesPerSeconda));
        debugPublish("Right Velocity",
            rightMotor.getVelocity().getValue().in(Unite.DegreesPerSeconda));
    }

    public void setMotors(IntakeConstants.IntakeState state) {
        leftMotor.set(state.speed);
        rightMotor.set(state.speed);
        rollerMotor.set(state.speed);
    }
    public void set(IntakeState state) {
        setMotors(state);
    }
}
