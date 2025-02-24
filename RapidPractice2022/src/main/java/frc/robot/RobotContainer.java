// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxLimitSwitch;
import com.revrobotics.SparkMaxLimitSwitch.Type;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.BabyNeo;
import frc.robot.commands.IntakeDefault;
import frc.robot.commands.LedBlink;
import frc.robot.commands.LedColor;
import frc.robot.commands.PistonForward;
import frc.robot.commands.PistonReverse;
import frc.robot.commands.SetPipeline;
import frc.robot.commands.auto.GalacticSearch;
import frc.robot.commands.auto.HubToBall2;
import frc.robot.commands.auto.HubToBall3;
import frc.robot.commands.auto.LowerCargoToHub;
import frc.robot.commands.auto.ManyBallAuto;
import frc.robot.constants.ControlConstants;
import frc.robot.mechanisms.Drive;
import frc.robot.subsystems.CameraSubsystem;
import frc.robot.subsystems.LedSubsystem;
import frc.robot.subsystems.PneumaticSubsystem;
import frc.robot.subsystems.vision.VisionLimeLight;
import frc.robot.subsystems.vision.VisionLimeLightH;
import frc.robot.subsystems.vision.VisionSystem;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  private Joystick driver;
  private JoystickButton togglePiston;
  private Joystick operator;
  private JoystickButton babyNeoButton; 
  private SendableChooser<Command> command = new SendableChooser<>();
  private SendableChooser<Command> teamColor = new SendableChooser<>();

  private VisionLimeLight shooterVision;
  private VisionSystem intakeVision;

  private CameraSubsystem cameraSubsystem;
  private LedSubsystem ledSubsystem;

  private PneumaticSubsystem pneumaticSubsystem;
  //private CANSparkMax motor = new CANSparkMax(7, MotorType.kBrushless);


  private Drive drive; 

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    driver = new Joystick(ControlConstants.driverJoystick);
    operator = new Joystick(ControlConstants.operatorJoystick);
    
    shooterVision = new VisionLimeLight("limelight-shooter", 19.25, 14.562694, 102.559, ControlConstants.shooterVisionColumn);
    intakeVision = new VisionLimeLightH("limelight-intake", 24, -5, 6, ControlConstants.shooterVisionColumn);

    drive = new Drive(driver,shooterVision);
    
    cameraSubsystem = new CameraSubsystem(operator);
    ledSubsystem = new LedSubsystem(0, 300);

    //pneumaticSubsystem = new PneumaticSubsystem();
    //pneumaticSubsystem.setDefaultCommand(new IntakeDefault(pneumaticSubsystem, driver));

    command.addOption("LowerCargoToHub", new LowerCargoToHub());
    command.addOption("HubBall2", new HubToBall2());
    command.addOption("HubBall3", new HubToBall3());
    command.addOption("ManyBall", new ManyBallAuto());
    command.addOption("Galactic Search", new GalacticSearch(drive.getDriveTrainMain(), shooterVision, drive.getPose()));

    teamColor.setDefaultOption("VTargets", new InstantCommand(() -> shooterVision.setPipeline(2)));
    teamColor.addOption("Red", new InstantCommand(() -> shooterVision.setPipeline(1)));
    teamColor.addOption("Blue", new InstantCommand(() -> shooterVision.setPipeline(0)));

    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    Command red = new InstantCommand(() -> shooterVision.setPipeline(1));
    Command blue = new InstantCommand(() -> shooterVision.setPipeline(0));
    Command vTargets = new InstantCommand(() -> shooterVision.setPipeline(2));
    Command ledOrange = new InstantCommand(()-> ledSubsystem.setSolidColor(255, 20, 0));
    // this adds auto selections in SmartDashboard
    Shuffleboard.getTab(ControlConstants.SBTabDriverDisplay).getLayout("Auto", BuiltInLayouts.kList)
        .withPosition(ControlConstants.autoColumn, 0).withSize(3, 1).add("Choose an Auto Mode", command)
        .withWidget(BuiltInWidgets.kSplitButtonChooser);

    SmartDashboard.putData("red", new SetPipeline(1, shooterVision));
    SmartDashboard.putData("blue", new SetPipeline(0, shooterVision));
    SmartDashboard.putData("default", new SetPipeline(2, shooterVision));
    SmartDashboard.putData("Leds Orange", new LedColor(255, 25, 0, ledSubsystem));
    SmartDashboard.putData("Leds Green", new LedColor(0, 255, 0, ledSubsystem));
    SmartDashboard.putData("Leds Off", new LedColor(0, 0, 0, ledSubsystem));
    SmartDashboard.putData("Led Blink Blue", new LedBlink(0, 0, 255, 100, ledSubsystem));
    /*SmartDashboard.putData("Piston Forward", new PistonForward(pneumaticSubsystem));
    SmartDashboard.putData("Piston Reverse", new PistonReverse(pneumaticSubsystem));
    togglePiston = new JoystickButton(driver, ControlConstants.launchButton);
    togglePiston.whenPressed(new InstantCommand(() -> pneumaticSubsystem.togglePiston(), pneumaticSubsystem));
*/
   // SparkMaxLimitSwitch forward = motor.getForwardLimitSwitch(Type.kNormallyOpen);
   // SparkMaxLimitSwitch reverse = motor.getReverseLimitSwitch(Type.kNormallyOpen);
   // forward.enableLimitSwitch(true);
  //  reverse.enableLimitSwitch(true);
   // babyNeoButton = new JoystickButton(operator, ControlConstants.ButtonNums.A_BUTTON.ordinal());
   // babyNeoButton.whileHeld(new BabyNeo(motor, operator));

  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    return command.getSelected();
  }
}
