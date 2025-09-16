package org.ironriders.drive;

import java.util.Optional;

import org.ironriders.LimelightHelpers;

import com.ctre.phoenix6.hardware.Pigeon2;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import swervelib.SwerveDrive;

/**
 * Vision is not a subsystem. This class is a utility class for the
 * DriveSubsystem
 * and controls all of the apriltag processing and pose estimation.
 *
 * (Why is it not a subsystem? Because it doesn't need to be.)
 */
public class Vision {

  private static final double AMBIGUITY_TOLERANCE = 0.4; // percentage
  private static final double DISTANCE_TOLERANCE = 2.5; // meters
  private SwerveDrive swerveDrive = null;
  private Pigeon2 pigeon = new Pigeon2(9);
  public boolean hasPose = false;

  public Vision(SwerveDrive drive) {
    this.swerveDrive = drive;
  }

  public void updatePose() {
    // First, tell Limelight your robot's current orientation
    double robotYaw = pigeon.getYaw().getValueAsDouble();
    LimelightHelpers.SetRobotOrientation("", robotYaw, 0.0, 0.0, 0.0, 0.0, 0.0);

    // Get the pose estimate
    Optional<Alliance> ally = DriverStation.getAlliance();
    LimelightHelpers.PoseEstimate limelightMeasurement = new LimelightHelpers.PoseEstimate();
    if (ally.isPresent()) {
      if (ally.get() == Alliance.Red) {
        limelightMeasurement = LimelightHelpers.getBotPoseEstimate_wpiRed(DriveConstants.LIMELIGHT_NAME);
      }
      if (ally.get() == Alliance.Blue) {
        limelightMeasurement = LimelightHelpers.getBotPoseEstimate_wpiBlue(DriveConstants.LIMELIGHT_NAME);
      }
    } else {
      return;
    }

    // Add it to your pose estimator
    swerveDrive.setVisionMeasurementStdDevs(VecBuilder.fill(DriveConstants.VISION_X_TRUST,
        DriveConstants.VISION_Y_TRUST, DriveConstants.VISION_ANGLE_TRUST));
    swerveDrive.addVisionMeasurement(
        limelightMeasurement.pose,
        limelightMeasurement.timestampSeconds);

  }

}
