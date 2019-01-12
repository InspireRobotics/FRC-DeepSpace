package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.TurnCommand;

public class Robot extends TimedRobot {

    private Drivetrain drivetrain;

    @Override
    public void robotInit() {
        System.out.println("Robot init!");

        drivetrain = new Drivetrain();
        drivetrain.updateDashboard();

        SmartDashboard.putData("Drivetrain", drivetrain);
    }

    @Override
    public void disabledInit() {
        System.out.println("Robot disabled!");
    }

    @Override
    public void disabledPeriodic() {
        drivetrain.updateDashboard();
    }

    @Override
    public void autonomousInit() {
        System.out.println("Running Auto!");

        Scheduler.getInstance().removeAll();
        Scheduler.getInstance().add(new TurnCommand(drivetrain, 90));
    }

    @Override
    public void autonomousPeriodic() {
        drivetrain.updateDashboard();
        Scheduler.getInstance().run();
    }

    @Override
    public void teleopInit() {
        System.out.println("Running teleop!");
    }

    @Override
    public void teleopPeriodic() {
        drivetrain.updateDashboard();

        Scheduler.getInstance().run();
    }
}
