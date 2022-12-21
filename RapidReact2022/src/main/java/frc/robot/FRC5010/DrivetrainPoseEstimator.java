// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.FRC5010;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.FRC5010.Vision.AprilTags;
import frc.robot.FRC5010.Vision.AprilTags.AprilTag;
import frc.robot.FRC5010.Vision.VisionValuesPhotonCamera;

/** Add your docs here. */
public class DrivetrainPoseEstimator extends SubsystemBase {
  private VisionSystem vision;
  private final Field2d field2d = new Field2d();
  private final GenericPose poseTracker;

  public DrivetrainPoseEstimator(GenericPose poseTracker, VisionSystem vision) {
    this.poseTracker = poseTracker;
    this.vision = vision;
    ShuffleboardTab tab = Shuffleboard.getTab("Pose");
    tab.addString("Pose (X,Y)", this::getFormattedPose).withPosition(0, 4);
    tab.addNumber("Pose Degrees", () -> getCurrentPose().getRotation().getDegrees()).withPosition(1, 4);
    tab.add(field2d);

    for (AprilTag at: AprilTags.aprilTagPoses) {
      if (at.pose.getX() != 0 && at.pose.getY() != 0 && at.pose.getZ() != 0) {
        field2d.getObject(at.fieldDescriptor).setPose(at.pose.toPose2d());
      }
    }
  }

  private String getFormattedPose() {
    var pose = getCurrentPose();
    return String.format("(%.2f, %.2f)",
        Units.metersToInches(pose.getX()),
        Units.metersToInches(pose.getY()));
  }

  public Pose2d getCurrentPose() {
    return poseTracker.getCurrentPose();
  }

  /**
   * Perform all periodic pose estimation tasks.
   *
   * @param actWheelSpeeds Current Speeds (in m/s) of the drivetrain wheels
   * @param leftDist       Distance (in m) the left wheel has traveled
   * @param rightDist      Distance (in m) the right wheel has traveled
   */
  public void update() {
    for(Pose2d robotPose : vision.getRawValues().getRobotPoses()) {
        double imageCaptureTime = vision.getRawValues().getLatency();
        
        field2d.getObject("MyRobot" + ((VisionValuesPhotonCamera)vision.getRawValues()).getFiducialId()).setPose(robotPose);    
        System.out.println("RobotPoseEst: X: " + robotPose.getX() + " Y: " + robotPose.getY() + " R: " + robotPose.getRotation().getDegrees());
        poseTracker.updateVision(robotPose, imageCaptureTime);
    }
    poseTracker.updatePhysics();
    field2d.setRobotPose(getCurrentPose());
  }

  /**
   * Force the pose estimator to a particular pose. This is useful for indicating
   * to the software
   * when you have manually moved your robot in a particular position on the field
   * (EX: when you
   * place it on the field at the start of the match).
   *
   * @param pose
   */
  public void resetToPose(Pose2d pose) {
    poseTracker.resetToPose(pose);
  }

  /** @return The current best-guess at drivetrain position on the field. */
  public Pose2d getPoseEst() {
    return poseTracker.getCurrentPose();
  }

  @Override
  public void periodic() {
    update();
  }
}
