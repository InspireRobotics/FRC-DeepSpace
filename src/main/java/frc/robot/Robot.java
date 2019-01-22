package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.hardware.HardwareThread;
import frc.robot.hardware.PixyCam;
import frc.robot.subsystems.DefaultDriveCommand;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.FollowCommand;

public class Robot extends TimedRobot {

    private Thread hardwareThread;
    private Drivetrain drivetrain;
    private PixyCam pixyCam;

    @Override
    public void robotInit() {
        System.out.println("Robot init!");

        //CameraServer.getInstance().startAutomaticCapture().setFPS(60);

        drivetrain = new Drivetrain();
        drivetrain.updateDashboard();

        SmartDashboard.putData("Drivetrain", drivetrain);
        pixyCam = new PixyCam();

        hardwareThread = HardwareThread.create(this);
    }

    @Override
    public void robotPeriodic() {
        pixyCam.updateDashboard();
        drivetrain.updateDashboard();

        SmartDashboard.putBoolean("Hardware Thread", hardwareThread.isAlive());
    }

    @Override
    public void disabledInit() {
        System.out.println("Robot disabled!");
    }

    @Override
    public void disabledPeriodic() {

    }

    @Override
    public void autonomousInit() {
        System.out.println("Running Auto!");

        Scheduler.getInstance().removeAll();
        Scheduler.getInstance().add(new FollowCommand(drivetrain, pixyCam, 1));
    }

    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void teleopInit() {
        System.out.println("Running teleop!");
        Scheduler.getInstance().removeAll();
        Scheduler.getInstance().add(new DefaultDriveCommand(drivetrain));
    }

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void testInit() {

    }

    public Drivetrain getDrivetrain() {
        return drivetrain;
    }

    public PixyCam getPixyCam() {
        return pixyCam;
    }
}
