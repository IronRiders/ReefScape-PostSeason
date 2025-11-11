package org.ironriders.intake;

import com.ctre.phoenix6.hardware.TalonFX;

public class IntakeSubsystem {
    
    private final TalonFX primaryMotor = new TalonFX(1);
    private final TalonFX secondaryMotor = new TalonFX(2);
    private final TalonFX rollerMotor = new TalonFX(3);
    public IntakeSubsystem IntakeSubsystem(){
        

    }
}
