// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.PneumaticSubsystem;

public class PistonForward extends CommandBase {
  /** Creates a new PistonForward. */
  PneumaticSubsystem pneumatic;
  public PistonForward(PneumaticSubsystem pneumatic) {
    // Use addRequirements() here to declare subsystem dependencies.\
    this.pneumatic = pneumatic;
    addRequirements(pneumatic);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    pneumatic.pistonForward();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
