package org.ironriders.wrist;

import org.ironriders.lib.IronSubsystem;
import org.ironriders.lib.RobotUtils;
import org.ironriders.wrist.WristConstants.WristState;

import static org.ironriders.wrist.WristConstants.ABSOLUTE_ENCODER_OFFSET;
import static org.ironriders.wrist.WristConstants.FOLLOWER_MOTOR_ID;
import static org.ironriders.wrist.WristConstants.FRAME_OF_REFERENCE_OFFSET;
import static org.ironriders.wrist.WristConstants.PID_COEFFIECENTS;
import static org.ironriders.wrist.WristConstants.PRIMARY_MOTOR_ID;
import static org.ironriders.wrist.WristConstants.WRIST_TOLERANCE;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;



public class WristSubsystem extends IronSubsystem{
    
    SparkMax primarymotor = new SparkMax(PRIMARY_MOTOR_ID, MotorType.kBrushless); //TODO
    SparkMax followermotor = new SparkMax(FOLLOWER_MOTOR_ID, MotorType.kBrushless); // TODO
    WristCommand command;
    
    TrapezoidProfile.Constraints constrainst = new TrapezoidProfile.Constraints(0, 0);
   
    ProfiledPIDController pidController = new ProfiledPIDController(PID_COEFFIECENTS.p,PID_COEFFIECENTS.i,PID_COEFFIECENTS.d,constrainst);
    
    WristState goalState;

    public WristSubsystem (){
        SparkMaxConfig motorConfig = new SparkMaxConfig();
        SparkMaxConfig followerMotorConfig = new SparkMaxConfig();

        motorConfig.smartCurrentLimit(30);
        followerMotorConfig.apply(motorConfig).follow(PRIMARY_MOTOR_ID, true);
        
        primarymotor.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        followermotor.configure(followerMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        setGoal(WristState.STOWED);
        pidController.setTolerance(WRIST_TOLERANCE);
        debugPublish("Wrist PID", pidController);
        debugPublish("WRIST P VALUE", PID_COEFFIECENTS.p);
        debugPublish("WRIST I VALUE", PID_COEFFIECENTS.i);
        debugPublish("WRIST D VALUE", PID_COEFFIECENTS.d);
        command = new WristCommand(this);
    }

    public void setGoal(WristState state){
        goalState = state;
        debugPublish("goal State", goalState.toString());
        debugPublish("goal State Value", goalState.rotation);
    }

    @Override
    public void periodic() {
        double pidoutput = pidController.calculate(getRotation());
        debugPublish("Motor Output", pidoutput);
        debugPublish("Current Rotation",getRotation());
        primarymotor.set(pidoutput);
    }

    public double getRotation(){
        return ((primarymotor.getAbsoluteEncoder().getPosition() - ABSOLUTE_ENCODER_OFFSET) * 360) - FRAME_OF_REFERENCE_OFFSET;
    }

    public boolean atGoal(){
        return RobotUtils.tolerance(getRotation(), goalState.rotation, WRIST_TOLERANCE);
    }

    public WristCommand getCommands(){
        return command;
    }

}
