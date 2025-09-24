package org.ironriders.climb;

import edu.wpi.first.wpilibj2.command.Command;

public class ClimbCommands {

  public final ClimbSubsystem climb;

  public ClimbCommands(ClimbSubsystem climb) {
    this.climb = climb;

    climb.publish("Climb", set(ClimbConstants.Targets.CLIMBED));
    climb.publish("Climb MAX", set(ClimbConstants.Targets.MAX));
    climb.publish("Climb MIN", set(ClimbConstants.Targets.MIN)); 
    climb.publish("Rehome", home());
  }

  public Command set(ClimbConstants.Targets target) {
    return climb.runOnce(() -> climb.setGoal(target));
  }

  public Command home() {
    return climb.runOnce(() -> climb.home());
  }
}
