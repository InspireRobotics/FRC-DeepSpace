package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {

  @Override
  public void robotInit() {
      System.out.println("Robot init!");
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
}
