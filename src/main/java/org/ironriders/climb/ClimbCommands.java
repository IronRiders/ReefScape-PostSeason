package org.ironriders.climb;

import edu.wpi.first.wpilibj2.command.Command;

public class ClimbCommands {

  public final ClimbSubsystem climb;

  /**
   * Publishes commands to set climb to various positions to {@linkplain edu.wpi.first.wpilibj.smartdashboard.SmartDashboard SmartDashboard};
   * <ul>
   * <li>Publish {@code "Climb"}; {@linkplain #set(ClimbConstants.ClimbTargets) set} {@linkplain ClimbConstants.ClimbTargets#CLIMBED Climbed}</li>
   * <li>Publish {@code "Climb MAX"}; {@linkplain #set(ClimbConstants.ClimbTargets) set} {@linkplain ClimbConstants.ClimbTargets#MAX Max}</li>
   * <li>Publish {@code "Climb MIN"}; {@linkplain #set(ClimbConstants.ClimbTargets) set} {@linkplain ClimbConstants.ClimbTargets#MIN Min}</li>
   * <li>Publish {@code "Rehome"}; {@linkplain #home() home()}</li>
   * </ul>
   */
  public ClimbCommands(ClimbSubsystem climb) {
    this.climb = climb;

    climb.publish("Climb", set(ClimbConstants.ClimbTargets.CLIMBED));
    climb.publish("Climb MAX", set(ClimbConstants.ClimbTargets.MAX));
    climb.publish("Climb MIN", set(ClimbConstants.ClimbTargets.MIN)); 
    climb.publish("Rehome", home());
  }

  /**
   * Passes to {@link ClimbSubsystem#setGoal(ClimbConstants.ClimbTargets) climb.setGoal()}.
   * @param target the target to set the climb to (using {@link ClimbConstants.ClimbTargets ClimbConstants})
   * @return a command that sets the goal to {@code ClimbConstants.Targets target}
   */
  public Command set(ClimbConstants.ClimbTargets target) {
    return climb.runOnce(() -> climb.setGoal(target));
  }

  /**
   * Passes to {@link ClimbSubsystem#home() climb.home()}.
   * @return a command that homes the climber
   */
  public Command home() {
    return climb.runOnce(() -> climb.home());
  }
}
