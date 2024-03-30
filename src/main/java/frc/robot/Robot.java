package frc.robot;

//NO INTAKE, SHOOTER, OR FEEDER CODE

import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.Timer;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {

  private static final String kNothingAuto = "NO";
  private static final String kLaunchAndDrive = "SHOOT_GO";
  private static final String kLaunch = "2NOTES";
  private static final String kDrive = "AWAY";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  CANSparkBase leftRear = new CANSparkMax(2, MotorType.kBrushed);
  CANSparkBase leftFront = new CANSparkMax(3, MotorType.kBrushed);
  CANSparkBase rightRear = new CANSparkMax(1, MotorType.kBrushed);
  CANSparkBase rightFront = new CANSparkMax(4, MotorType.kBrushed);

  DifferentialDrive m_drivetrain;

  XboxController m_driver = new XboxController(0);
  XboxController m_operator = new XboxController(1);

  static final double LAUNCHER_SPEED = 1.0;

  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("NO", kNothingAuto);
    m_chooser.addOption("SHOOT_GO", kLaunchAndDrive);
    m_chooser.addOption("2NOTES", kLaunch);
    m_chooser.addOption("AWAY", kDrive);
    SmartDashboard.putData("Auto choices", m_chooser);

    leftRear.follow(leftFront);
    rightRear.follow(rightFront);

    leftFront.setInverted(true);
    rightFront.setInverted(false);

    m_drivetrain = new DifferentialDrive(leftFront, rightFront);
  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("Time(s)", Timer.getFPGATimestamp());
  }

  double AUTO_LAUNCH_DELAY_S;
  double AUTO_DRIVE_DELAY_S;
  double AUTO_DRIVE_TIME_S;
  double AUTO_DRIVE_SPEED;
  double AUTO_LAUNCHER_SPEED;
  double autonomousStartTime;

  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();

    leftRear.setIdleMode(IdleMode.kBrake);
    leftFront.setIdleMode(IdleMode.kBrake);
    rightRear.setIdleMode(IdleMode.kBrake);
    rightFront.setIdleMode(IdleMode.kBrake);

    AUTO_LAUNCH_DELAY_S = 3;
    AUTO_DRIVE_DELAY_S = 3;
    AUTO_DRIVE_TIME_S = 2.0;
    AUTO_DRIVE_SPEED = -0.5;
    AUTO_LAUNCHER_SPEED = 1;

    autonomousStartTime = Timer.getFPGATimestamp();
  }

  @Override
  public void autonomousPeriodic() {

    double time = Timer.getFPGATimestamp() - autonomousStartTime;

//its an SHOOT_GO
    if (m_autoSelected.equals(kLaunchAndDrive)) {
      if (time < AUTO_LAUNCH_DELAY_S) {
        // launch
        // m_launcher.setSpeed(AUTO_LAUNCHER_SPEED);
      } else if (time < AUTO_DRIVE_DELAY_S) {
        // stop launch
        // m_launcher.setSpeed(0);
      } else if (time < AUTO_DRIVE_DELAY_S + AUTO_DRIVE_TIME_S) {
        // drive backwards (figure out + or -)
        //speed = -0.5
        m_drivetrain.arcadeDrive(AUTO_DRIVE_SPEED, 0);
      } else {
        // stop drive and stop any kind of motor
        m_drivetrain.arcadeDrive(0, 0);
      }
//its a 2NOTES
    } else if (m_autoSelected.equals(kLaunch)) {
      if (time < AUTO_LAUNCH_DELAY_S) {
        // launch note immediately
                      // m_launcher.setSpeed(AUTO_LAUNCHER_SPEED);
      } else if (time < AUTO_LAUNCH_DELAY_S + 2) {
          // after 2 seconds of shooting, go back for another 4 seconds
          // start intaking after stopping launching
          // m_launcher.setSpeed(0);
          m_drivetrain.arcadeDrive(-0.5, 0);
                      //INTAKING CODE HERE
      } else if (time < AUTO_LAUNCH_DELAY_S + 6) {
          // for next 4 seconds, stop intaking and go forward
          m_drivetrain.arcadeDrive(0.5, 0);
                    //STOP INTAKING CODE HERE
      } else if (time < AUTO_LAUNCH_DELAY_S + 15) {
          // for the rest of auto, stop driving and shoot the note
          m_drivetrain.arcadeDrive(0, 0);
                    // m_launcher.setSpeed(1.0);
      } else {
        m_drivetrain.arcadeDrive(0, 0);
      }
//its an AWAY
    } else if (m_autoSelected.equals(kDrive)) {
      //delay = 3 seconds
       if (time < AUTO_LAUNCH_DELAY_S) {
          // after 2 seconds go back
          m_drivetrain.arcadeDrive(-0.5, 0);
                      //INTAKING CODE HERE
      } else if (time < AUTO_LAUNCH_DELAY_S + 4) {
          // for next 4 seconds, stop intaking and go forward
          m_drivetrain.arcadeDrive(0, 0);
                    //STOP INTAKING CODE HERE
      } else {
        // stop drive
        m_drivetrain.arcadeDrive(0, 0);
      }
    }

  }

  @Override
  public void teleopInit() {
    leftRear.setIdleMode(IdleMode.kCoast);
    leftFront.setIdleMode(IdleMode.kCoast);
    rightRear.setIdleMode(IdleMode.kCoast);
    rightFront.setIdleMode(IdleMode.kCoast);
  }

  @Override
  public void teleopPeriodic() {
    double speed = m_driver.getLeftY();
    double rotation = m_driver.getRightX();

    m_drivetrain.arcadeDrive(speed, rotation, false);

    if (m_driver.getLeftBumper()) {
      m_drivetrain.arcadeDrive(1, 0);
    } else if (m_driver.getRightBumper()) {
      m_drivetrain.arcadeDrive(-1, 0);
    }
  }
}
