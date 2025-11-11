package org.ironriders.intake;
public class IntakeSubsystem{
    public Main IntakeSubsystem(){
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.withCurrentLimits(
            new CurrentLimitsConfigs()
                .withStatorCurrentLimitEnable(true)
                .withSupplyCurrentLimitEnable(true)
                .withStatorCurrentLimit(INTAKE_STATOR_CURRENT)
                .withSupplyCurrentLimit(INTAKE_SUPPLY_CURRENT)
                .withSupplyCurrentLowerLimit(INTAKE_SUPPLY_CURRENT_LOWER_LIMIT)
                .withSupplyCurrentLowerTime(INTAKE_SUPPLY_CURRENT_LOWER_TIME))
        .withMotorOutput(new MotorOutputConfigs().withNeutralMode(INTAKE_NEUTRAL_MODE));

    }
    TalonFX primaryMotor = new TalonFX(1);
    TalonFX secondaryMotor = new TalonFX(2);
    TalonFX rollerMotor = new TalonFX(3);
    primaryMotor.getConfigurator.
    
}