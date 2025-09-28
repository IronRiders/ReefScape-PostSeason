package org.ironriders.drive;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.PathConstraints;
import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import org.ironriders.lib.GameState;

@Logged
public class DriveCommands {

  private final DriveSubsystem driveSubsystem;

  public DriveCommands(DriveSubsystem driveSubsystem) {
    this.driveSubsystem = driveSubsystem;

    this.driveSubsystem.publish("Drive to Target", pathfindToTarget());
    this.driveSubsystem.publish("Invert", Commands.runOnce(() -> GameState.invertControl()));
  }

  public Command drive(
      Supplier<Translation2d> translation, DoubleSupplier rotation, BooleanSupplier fieldRelative) {
    return driveSubsystem.runOnce(
        () -> {
          driveSubsystem.drive(
              translation.get(), rotation.getAsDouble(), fieldRelative.getAsBoolean());
        });
  }

  public Command driveTeleop(
      DoubleSupplier inputTranslationX,
      DoubleSupplier inputTranslationY,
      DoubleSupplier inputRotation,
      boolean fieldRelative) {
    if (DriverStation.isAutonomous()) {
      return Commands.none();
    }

    double invert =
        DriverStation.getAlliance().isEmpty()
                || DriverStation.getAlliance().get() == DriverStation.Alliance.Blue
            ? 1
            : -1;

    return drive(
        () ->
            new Translation2d(inputTranslationX.getAsDouble(), inputTranslationY.getAsDouble())
                .times(DriveConstants.SWERVE_DRIVE_MAX_SPEED)
                .times(invert),
        () -> inputRotation.getAsDouble() * DriveConstants.SWERVE_DRIVE_MAX_SPEED * invert,
        () -> fieldRelative);
  }

  /**
   * Move the robot a small amount in a given direction.
   *
   * @param robotRelativeAngleDegrees the direction
   * @return the command to do that
   */
  public Command jog(double robotRelativeAngleDegrees) {
    // Note - PathFinder does not do well with small moves so we move manually

    // Compute distance to travel (TODO - distance is slightly fictional without PID
    // control)
    var distance = Units.inchesToMeters(DriveConstants.JOG_DISTANCE_INCHES);

    // Compute velocity
    var vector = new Translation2d(distance, Rotation2d.fromDegrees(robotRelativeAngleDegrees));
    var scale =
        Math.max(Math.abs(vector.getX()), Math.abs(vector.getY())) / DriveConstants.JOG_SPEED;
    var velocity = vector.div(scale);

    return driveSubsystem.runOnce(
        () -> {
          var startPosition = driveSubsystem.getPose().getTranslation();

          driveTeleop(velocity::getX, velocity::getY, () -> 0, false)
              .repeatedly()
              .until(
                  () ->
                      driveSubsystem.getPose().getTranslation().getDistance(startPosition)
                          > distance)
              .schedule();
        });
  }

  /**
   * Make the robot drive to a given pose.
   *
   * @param targetPose Where to go
   * @return The command to go there
   */
  public Command pathfindToPose(Pose2d targetPose) {
    return driveSubsystem.defer(
        () -> {
          driveSubsystem.pathfindCommand =
              AutoBuilder.pathfindToPose(
                  targetPose,
                  new PathConstraints(
                      DriveConstants.SWERVE_MAXIMUM_SPEED_AUTO,
                      DriveConstants.SWERVE_MAXIMUM_ACCELERATION_AUTO,
                      DriveConstants.SWERVE_MAXIMUM_ANGULAR_VELOCITY_AUTO,
                      DriveConstants.SWERVE_MAXIMUM_ANGULAR_ACCELERATION_AUTO));
          return driveSubsystem.pathfindCommand;
        });
  }

  /**
   * Invert the driver controls. Hopefully should NEVER be used but is
   *
   * @return Command to do that
   */
  public Command invertControls() {
    return driveSubsystem.runOnce(
        () -> {
          driveSubsystem.switchInvertControl();
        });
  }

  /**
   * Path find to the target from the targeting system. Mostly unused
   *
   * @return Command to do that
   */
  public Command pathfindToTarget() {
    return driveSubsystem.defer(
        () -> {
          var pose = GameState.getTargetRobotPose();
          if (pose.isEmpty()) {
            return Commands.none();
          }

          return pathfindToPose(pose.get().toPose2d());
        });
  }

  /**
   * Abort any autonomous movements running.
   *
   * @return Command to do that
   */
  public Command cancelPathfind() {
    return driveSubsystem.runOnce(
        () -> {
          if (driveSubsystem.pathfindCommand != null) {
            driveSubsystem.pathfindCommand.cancel();
          }
        });
  }

  public Command setDriveTrainSpeed(double speed) {
    return driveSubsystem.runOnce(
        () -> {
          driveSubsystem.setSpeed(speed);
        });
  }
}
