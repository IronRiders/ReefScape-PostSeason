package org.ironriders.climb;

import static org.ironriders.climb.ClimbConstants.*;

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
import org.ironriders.lib.IronSubsystem;

public class ClimbSubsystem extends IronSubsystem {

  private final SparkMax climbMotor = new SparkMax(
    ClimbConstants.CLIMBER_MOTOR_CAN_ID,
    SparkLowLevel.MotorType.kBrushless
  );
  private final SparkMaxConfig climbMotorConfig = new SparkMaxConfig();
  RelativeEncoder encoder = climbMotor.getEncoder();
  boolean reachedTopLimit = false;
  boolean reachedBottomLimit = false;

  private final TrapezoidProfile.Constraints m_constraints =
    new TrapezoidProfile.Constraints(ROTATION_MAXSPEED, ROTATION_MAXACCEL);

  private final ProfiledPIDController profiledPIDController =
    new ProfiledPIDController(P, I, D, m_constraints, T);

  private final ClimbCommands commands;

  public ClimbSubsystem() {
    climbMotorConfig.idleMode(IdleMode.kBrake);
    climbMotorConfig.smartCurrentLimit(ClimbConstants.CURRENT_LIMIT);
    climbMotor.configure(
      climbMotorConfig,
      ResetMode.kResetSafeParameters,
      PersistMode.kPersistParameters
    );

    commands = new ClimbCommands(this);

    climbMotorConfig.softLimit
      .reverseSoftLimit(ROTATION_MAXDOWN)
      .reverseSoftLimitEnabled(true);
    climbMotorConfig.softLimit
      .forwardSoftLimit(ROTATION_MAXUP)
      .forwardSoftLimitEnabled(true);
  }

  public void set(ClimbConstants.Targets target) {
    profiledPIDController.setGoal(
      MathUtil.clamp(target.pos, ROTATION_MAXDOWN, ROTATION_MAXUP)
    );
  }

  public ClimbCommands getCommands() {
    return commands;
  }

  @Override
  public void periodic() {
    publish("Climber::encoder", encoder.getPosition());
    publish("Climber::PIDsetpoint", profiledPIDController.getGoal().position);
  }
}
