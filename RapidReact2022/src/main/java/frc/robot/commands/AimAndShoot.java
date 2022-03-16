// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.constants.IndexerConstants;
import frc.robot.constants.ShooterConstants;
import frc.robot.constants.ShooterConstants.HoodConstants;
import frc.robot.subsystems.DiagonalIndexerSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.VerticalIndexerSubsystem;
import frc.robot.subsystems.vision.VisionSystem;

public class AimAndShoot extends CommandBase {
  /** Creates a new AimAndShoot. */
  private ShooterSubsystem shooterSubsystem;
  private VerticalIndexerSubsystem indexerSubsystem;
  private DiagonalIndexerSubsystem diagonalIndexerSubsystem;
  private VisionSystem visionSystem;

  public AimAndShoot(ShooterSubsystem shooterSubsystem, VerticalIndexerSubsystem indexerSubsystem, DiagonalIndexerSubsystem diagonalIndexerSubsystem,
      VisionSystem visionSystem) {
    this.shooterSubsystem = shooterSubsystem;
    this.indexerSubsystem = indexerSubsystem;
    this.diagonalIndexerSubsystem = diagonalIndexerSubsystem;
    this.visionSystem = visionSystem;


    addRequirements(shooterSubsystem, indexerSubsystem, diagonalIndexerSubsystem);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    shooterSubsystem.setFlyFeederPoint(ShooterConstants.defaultFlyWheelRPM);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double distance = visionSystem.getDistance() / 12.0;
    if (visionSystem.isValidTarget()) {
      double flyWheelPoint = shooterSubsystem.flyWheelCalculations(distance);
      shooterSubsystem.setFlyWheelPoint(flyWheelPoint);
      double hoodPoint = shooterSubsystem.hoodCalculations(distance);
      shooterSubsystem.setHoodSetPoint(hoodPoint);
      shooterSubsystem.spinUpWheelRPM();
      shooterSubsystem.pidHood();

      shooterSubsystem.spinUpFeederRPM();
    } 

    shooterSubsystem.determineIfReadyToShoot();

    if (shooterSubsystem.getReadyToShoot()) {
      indexerSubsystem.setVerticalIndexerPoint(IndexerConstants.indexerRPM);
      indexerSubsystem.runWithVelocityControl();

      diagonalIndexerSubsystem.setDiagonalIndexerPoint(IndexerConstants.indexerRPM);
      diagonalIndexerSubsystem.runWithVelocityControl();
    }else{
      indexerSubsystem.setVerticalIndexerPoint(0);
      indexerSubsystem.setVerticalIndexer(0);

      diagonalIndexerSubsystem.setDiagonalIndexerPoint(0);
      diagonalIndexerSubsystem.setDiagonalIndexer(0);
    }


  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    shooterSubsystem.spinFeeder(0);
    shooterSubsystem.setFlyWheelPoint(0);
    shooterSubsystem.spinFlyWheel(0);
    shooterSubsystem.stopHood();
    shooterSubsystem.setFlyFeederPoint(0);
    shooterSubsystem.spinFeeder(0);

    indexerSubsystem.setVerticalIndexer(0);
    diagonalIndexerSubsystem.setDiagonalIndexer(0);
    System.out.println("Code ended");
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
