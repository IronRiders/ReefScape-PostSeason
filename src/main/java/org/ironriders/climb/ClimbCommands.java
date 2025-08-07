package org.ironriders.climb;
import edu.wpi.first.wpilibj2.command.Command;

public class ClimbCommands {
    public final ClimbSubsystem climb;


    public ClimbCommands(ClimbSubsystem climb) {
        this.climb = climb;

        climb.publish("Climb Max", set(ClimbConstants.Targets.CLIMBED));
        climb.publish("Climb Down", set(ClimbConstants.Targets.EXTENDED));
    }

    public Command set(ClimbConstants.Targets targets) {
        return climb
            .runOnce(() -> climb.set(targets));
    }


}