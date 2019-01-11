package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.Drivetrain;

public class Robot extends TimedRobot {

  private Drivetrain drivetrain;

  @Override
  public void robotInit() {
      System.out.println("Robot init!");

      drivetrain = new Drivetrain();
    SmartDashboard.putData("Drivetrain", drivetrain);
  }

  @Override
  public void disabledInit() {
    System.out.println("Robot disabled!");
  }

  @Override
  public void autonomousInit() {
    System.out.println("Running Auto!");
  }

  @Override
  public void teleopInit() {
    System.out.println("Running teleop!");
  }

  @Override
  public void teleopPeriodic() {
    drivetrain.update();

    Scheduler.getInstance().run();
  }
}
