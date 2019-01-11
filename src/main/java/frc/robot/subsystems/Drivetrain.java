package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.HardwareMap;

public class Drivetrain extends Subsystem {

    private final AHRS gyro;
    private final DifferentialDrive drive;

    public Drivetrain(){
        drive = createDrivetrain();
        gyro = new AHRS(SPI.Port.kMXP);

        SmartDashboard.putData("Drive", drive);
    }

    private DifferentialDrive createDrivetrain() {
        Spark leftFront = new Spark(HardwareMap.PWM.LEFT_FRONT_DRIVE);
        Spark leftBack = new Spark(HardwareMap.PWM.LEFT_BACK_DRIVE);
        Spark rightFront = new Spark(HardwareMap.PWM.RIGHT_FRONT_DRIVE);
        Spark rightBack = new Spark(HardwareMap.PWM.RIGHT_BACK_DRIVE);

        SpeedControllerGroup left = new SpeedControllerGroup(leftFront, leftBack);
        SpeedControllerGroup right = new SpeedControllerGroup(rightFront, rightBack);

        return new DifferentialDrive(left, right);
    }

    public void update() {
        drive.stopMotor();

        SmartDashboard.putNumber("Gyro Angle", gyro.getAngle());

    }

    public double getHeading(){
        return gyro.getAngle();
    }

    public void drive(double left, double right){
        drive.tankDrive(left, right);
    }

    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new DefaultDriveCommand(this));
    }
}
