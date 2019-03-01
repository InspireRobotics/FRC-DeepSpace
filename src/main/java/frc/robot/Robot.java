package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.*;
import frc.robot.hardware.Camera;
import frc.robot.hardware.Distance;
import frc.robot.hardware.HardwareThread;
import frc.robot.hardware.PixyCam;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Pneumatics;
import frc.robot.subsystems.Wedge;

public class Robot extends TimedRobot {

    private Thread hardwareThread;
    private Drivetrain drivetrain;
    private PixyCam pixyCam;
    private Thread cameraThread;
    private Pneumatics pneumatics;
    private Wedge wedge;
    private Climber climber;
    private Distance distance;

    @Override
    public void robotInit() {
        System.out.println("Robot init!");

        cameraThread = Camera.create();

        drivetrain = new Drivetrain();
        
        pneumatics = new Pneumatics(HardwareMap.Pnuematic.singleList, HardwareMap.Pnuematic.doubleList, HardwareMap.Pnuematic.compresserId);
        
        wedge = new Wedge(2);
        
        climber = new Climber(1, new int[]{1,1});
        
        distance = new Distance(0);
        
        int[] toExtendClimb = {0, 3};
        int[] toExtendPanel = {4, 7};
        
        //new JoystickButton(HardwareMap.Joysticks.drive, HardwareMap.Joysticks.A_BUTTON).whenPressed(new PnuematicDoubleCommand(pneumatics, DoubleSolenoid.Value.kForward, toExtendClimb));
        //new JoystickButton(HardwareMap.Joysticks.drive, HardwareMap.Joysticks.B_BUTTON).whenPressed(new PnuematicDoubleCommand(pneumatics, DoubleSolenoid.Value.kReverse, toExtendClimb));
    
        new JoystickButton(HardwareMap.Joysticks.aux, HardwareMap.Joysticks.Y_BUTTON).whenPressed(new PnuematicDoubleCommand(pneumatics, DoubleSolenoid.Value.kForward, toExtendPanel));
        new JoystickButton(HardwareMap.Joysticks.aux, HardwareMap.Joysticks.B_BUTTON).whenPressed(new PnuematicDoubleCommand(pneumatics, DoubleSolenoid.Value.kReverse, toExtendPanel));
        new JoystickButton(HardwareMap.Joysticks.aux, HardwareMap.Joysticks.A_BUTTON).whenPressed(new WedgeCommand(wedge, 1.0, 1000));
        new JoystickButton(HardwareMap.Joysticks.aux, HardwareMap.Joysticks.X_BUTTON).whenPressed(new WedgeCommand(wedge, -1.0, 1000));
        
        SmartDashboard.putData("Drivetrain", drivetrain);
        pixyCam = new PixyCam();

        hardwareThread = HardwareThread.create(this);
    
        new JoystickButton(HardwareMap.Joysticks.drive, HardwareMap.Joysticks.X_BUTTON).whenPressed(new DriveCommand(drivetrain, -0.4, 1000));
        new JoystickButton(HardwareMap.Joysticks.drive, HardwareMap.Joysticks.B_BUTTON).whenPressed(new DriveCommand(drivetrain, 0.4, 1000));
        
        
        new JoystickButton(HardwareMap.Joysticks.drive, HardwareMap.Joysticks.LEFT_SHOULDER_BUTTON).whenPressed(new ClimbClampCommand(climber, -1,1000));
        new JoystickButton(HardwareMap.Joysticks.drive, HardwareMap.Joysticks.RIGHT_SHOULDER_BUTTON).whenPressed(new ClimbClampCommand(climber, -1,1000));
        new JoystickButton(HardwareMap.Joysticks.aux, HardwareMap.Joysticks.LEFT_SHOULDER_BUTTON).whenPressed(new ClimbClampCommand(climber, -1,1000));
        new JoystickButton(HardwareMap.Joysticks.aux, HardwareMap.Joysticks.RIGHT_SHOULDER_BUTTON).whenPressed(new ClimbClampCommand(climber, -1,1000));
    
    }

    @Override
    public void robotPeriodic() {
        pixyCam.updateDashboard();

        SmartDashboard.putNumber("Gyro Heading", drivetrain.getHeading());
        
        SmartDashboard.putBoolean("Hardware Thread", hardwareThread.isAlive());
        
        SmartDashboard.putBoolean("Pressurized", pneumatics.systemPressurized());
        
        SmartDashboard.putNumber("Distance (cm)", distance.GetDist());
    }
    

    @Override
    public void disabledInit() {
        drivetrain.Stop();
        System.out.println("Robot disabled!");
    }

    @Override
    public void disabledPeriodic() { }

    @Override
    public void autonomousInit() {
        System.out.println("Running Auto!");

        Scheduler.getInstance().removeAll();
        Scheduler.getInstance().add(new DefaultDriveCommand(drivetrain));
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
        return this.drivetrain;
    }

    public PixyCam getPixyCam() {
        return pixyCam;
    }
}
