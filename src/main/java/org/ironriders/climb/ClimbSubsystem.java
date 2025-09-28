package org.ironriders.climb;

import static org.ironriders.climb.ClimbConstants.D;
import static org.ironriders.climb.ClimbConstants.ENCODER_SCALE;
import static org.ironriders.climb.ClimbConstants.I;
import static org.ironriders.climb.ClimbConstants.MAX_ACC;
import static org.ironriders.climb.ClimbConstants.MAX_VEL;
import static org.ironriders.climb.ClimbConstants.P;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.ironriders.climb.ClimbConstants.ClimbTargets;
import org.ironriders.lib.IronSubsystem;

/** The sumsystem in charge of the climber. */
public class ClimbSubsystem extends IronSubsystem {

  private final SparkMax motor =
      new SparkMax(ClimbConstants.CLIMBER_MOTOR_ID, SparkLowLevel.MotorType.kBrushless);

  private final SparkMaxConfig motorConfig = new SparkMaxConfig();

  private final TrapezoidProfile profile =
      new TrapezoidProfile(new TrapezoidProfile.Constraints(MAX_VEL, MAX_ACC));

  private TrapezoidProfile.State stopped;

  private final PIDController pid = new PIDController(P, I, D);

  public boolean atGoal;

  // goalSetpoint is the final goal. periodicSetpoint is a sort-of inbetween
  // setpoint generated every periodic.
  private TrapezoidProfile.State goalSetpoint = new TrapezoidProfile.State();
  private TrapezoidProfile.State periodicSetpoint = new TrapezoidProfile.State();

  private ClimbTargets currentTarget = ClimbTargets.MIN;

  private final ClimbCommands commands;

  /** initalize. */
  public ClimbSubsystem() {
    motorConfig.idleMode(IdleMode.kBrake);
    motorConfig.smartCurrentLimit(ClimbConstants.CURRENT_LIMIT);

    motor.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    pid.setTolerance(ClimbConstants.TOLERANCE);

    home();

    commands = new ClimbCommands(this);
  }

  @Override
  public void periodic() {
    updateDashboard();

    var currentDegrees = getCurrentAngle();

    // Apply profile and PID to determine output level
    periodicSetpoint = profile.calculate(ClimbConstants.T, periodicSetpoint, goalSetpoint);

    var speed = pid.calculate(currentDegrees, periodicSetpoint.position);
    motor.set(speed);

    atGoal = pid.atSetpoint();

    publish("Pid out", speed);
  }

  private void updateDashboard() {
    publish("Goal State", currentTarget.toString());
    publish("Goal Position", goalSetpoint.position);
    publish("Motor Current", motor.getOutputCurrent());

    publish("Current Position", getCurrentAngle());
    publish("PID", pid);
    publish("at Goal?", atGoal);
    publish("Motor raw angle", motor.getEncoder().getPosition());
  }

  public void reset() {
    pid.reset();
    stopped = new TrapezoidProfile.State(getCurrentAngle(), 0);
    goalSetpoint = stopped;
    periodicSetpoint = stopped;

    motor.set(0);
    logMessage("resetting");
  }

  /** Sets the climbers relative encoder to zero. doesn't actually move it */
  public void home() {
    if (currentTarget != ClimbTargets.MIN) { // The climber is not all the way down, resetting it's
      // encoder would cause
      // it to
      // go boom.
      logMessage("aborting home, climber state is not MIN!");
      return;
    }
    logMessage("rehoming!");
    motor.getEncoder().setPosition(0);
    reset();
  }

  protected void setGoal(ClimbConstants.ClimbTargets target) {
    goalSetpoint = new TrapezoidProfile.State(target.pos, 0);
    currentTarget = target;

    logMessage("goes to " + currentTarget.toString());
  }

  public double getCurrentAngle() {
    return motor.getEncoder().getPosition() * 360 * ENCODER_SCALE;
  }

  public ClimbCommands getCommands() {
    return commands;
  }
}
