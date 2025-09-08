package org.ironriders.lib;

import java.util.Optional;
import java.util.function.Supplier;

import org.ironriders.elevator.ElevatorConstants;
import org.ironriders.lib.field.FieldPose;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;

/**
 * Current robot state required by multiple subsystems.
 */
public class GameState {

  public static boolean controlInverted;

  private static Field2d field = new Field2d();
  private static Supplier<Optional<Pose2d>> robotPose = () -> Optional.empty();
  private static Supplier<Optional<FieldPose>> targetRobotPose = () -> Optional.empty();

  // these represent our current elevator targets for their respective game
  // pieces.
  private static ElevatorConstants.Level coralTarget = ElevatorConstants.Level.L1;
  private static ElevatorConstants.Level algaeTarget = ElevatorConstants.Level.L2;

  private GameState() {
  }

  public static Field2d getField() {
    return field;
  }

  public static void setField(Field2d field) {
    GameState.field = field;
  }

  public static Optional<Pose2d> getRobotPose() {
    return robotPose.get();
  }

  public static void setRobotPose(Supplier<Optional<Pose2d>> robotPose) {
    GameState.robotPose = robotPose;
  }

  public static Optional<FieldPose> getTargetRobotPose() {
    return targetRobotPose.get();
  }

  public static void setTargetRobotPose(
      Supplier<Optional<FieldPose>> robotPose) {
    GameState.targetRobotPose = robotPose;
  }

  public static ElevatorConstants.Level getCoralTarget() {
    return coralTarget;
  }

  public static void setCoralTarget(ElevatorConstants.Level coralTarget) {
    GameState.coralTarget = coralTarget;
  }

  public static ElevatorConstants.Level getAlgaeTarget() {
    return algaeTarget;
  }

  public static void setAlgaeTarget(ElevatorConstants.Level algaeTarget) {
    GameState.algaeTarget = algaeTarget;
  }

  public static boolean getInvertControl() {
    return controlInverted;
  }

  public static void invertControl() {
    GameState.controlInverted = !GameState.controlInverted;
  }
}
