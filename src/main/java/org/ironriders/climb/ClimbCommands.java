package org.ironriders.climb;

import edu.wpi.first.wpilibj2.command.Command;

public class ClimbCommands {

  public final ClimbSubsystem climb;

  /**
   * Initalizer.
   *
   * @param climb the climb subsystem
   */
  public ClimbCommands(ClimbSubsystem climb) {
    this.climb = climb;

    climb.publish("Climb", set(ClimbConstants.ClimbTargets.CLIMBED));
    climb.publish("Climb MAX", set(ClimbConstants.ClimbTargets.MAX));
    climb.publish("Climb MIN", set(ClimbConstants.ClimbTargets.MIN));
    climb.publish("Rehome", home());
  }

  /**
   * Set where the climber should go.
   *
   * @param target where
   * @return Command to do it
   */
  public Command set(ClimbConstants.ClimbTargets target) {
    return climb.runOnce(() -> climb.setGoal(target));
  }

  /**
   * Reset the relative enocder to zero.
   *
   * @return Command to do that
   */
  public Command home() {
    return climb.runOnce(() -> climb.home());
  }
}
