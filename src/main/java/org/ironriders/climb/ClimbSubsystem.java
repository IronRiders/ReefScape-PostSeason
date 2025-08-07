package org.ironriders.climb;


import static org.ironriders.climb.ClimbConstants.ROTATION_MAXDOWN;
import static org.ironriders.climb.ClimbConstants.ROTATION_MAXUP;

import java.util.Random;

import org.ironriders.lib.IronSubsystem;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class ClimbSubsystem extends IronSubsystem {

    private final SparkMax climbMotor = new SparkMax(ClimbConstants.CLIMBER_MOTOR_CAN_ID,
            SparkLowLevel.MotorType.kBrushless);
    private final SparkMaxConfig climbMotorConfig = new SparkMaxConfig();
    RelativeEncoder encoder = climbMotor.getEncoder();
    boolean reachedTopLimit = false;
    boolean reachedBottomLimit = false;
    

    double kMaxVelocity = 60; //in degrees
    double kMaxAcceleration = 60; // in degrees
    double kP = 1.0; // idk what this is in //TODO
    double kI = 0; // idk what this is in //TODO
    double kD = 0; // idk what this is in //TODO
    double kDt = 0.02; // default is 0.02 
    private final TrapezoidProfile.Constraints m_constraints =
      new TrapezoidProfile.Constraints(kMaxVelocity, kMaxAcceleration);
    private final ProfiledPIDController profiledPIDController =
      new ProfiledPIDController(kP, kI, kD, m_constraints, kDt);
    

    private final ClimbCommands commands;

    public ClimbSubsystem() {
        climbMotorConfig.idleMode(IdleMode.kBrake); 
        climbMotorConfig.smartCurrentLimit(ClimbConstants.CURRENT_LIMIT);
        climbMotor.configure(climbMotorConfig, ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);
                
        commands = new ClimbCommands(this);

        climbMotorConfig.softLimit.reverseSoftLimit(ROTATION_MAXDOWN).reverseSoftLimitEnabled(true);
        climbMotorConfig.softLimit.forwardSoftLimit(ROTATION_MAXUP).forwardSoftLimitEnabled(true);
    }

    @Override
    public void periodic() {
        publish("Climber/encoder", encoder.getPosition());
        publish("Climber/PIDsetpoint", profiledPIDController.getGoal().position);
            
        }

    public void set(ClimbConstants.Targets target){
        profiledPIDController.setGoal(MathUtil.clamp(target.pos, ROTATION_MAXDOWN, ROTATION_MAXUP));
    }

    // public void set(ClimbConstants.State state) {
    //     if(state.speed < 0){
    //         if(reachedTopLimit){ 
    //             climbMotor.set(0);
    //         } else {
    //             climbMotor.set(state.speed);


    //         }
    //     } else {
    //         if(reachedBottomLimit){
    //             climbMotor.set(0);
    //         } else {
    //             climbMotor.set(state.speed); 
    //         }
    //     }
    // }

    public ClimbCommands getCommands() {
        return commands;
    }


}