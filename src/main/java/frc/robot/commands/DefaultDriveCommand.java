package frc.robot.commands;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.HardwareMap;
import frc.robot.subsystems.Drivetrain;

public class DefaultDriveCommand extends Command {
    
    //Base power multiplier
    public static final double USER_POWER_SLOW = 0.5; //%
    public static final double USER_POWER_FAST = 0.8; //%
    public static final long USER_POWER_SHIFT_TIME = 1500; //ms

    private long lastMax;
    
    //Gets joystick and drivetrain references
    private final Joystick drive = HardwareMap.Joysticks.drive;
    private final Drivetrain drivetrain;

    //Constructor
    public DefaultDriveCommand(Drivetrain drivetrain) {
        //Saves drivetrain reference
        this.drivetrain = drivetrain;

        //Requires drivetrain for functionality
        requires(drivetrain);
    }

    //Allows drivetrain to drive
    @Override
    protected void execute() {
        //Gets joystick positions
        double left = -drive.getRawAxis(HardwareMap.Joysticks.LEFT_Y_AXIS);
        double right = -drive.getRawAxis(HardwareMap.Joysticks.RIGHT_Y_AXIS);
        
        double power = USER_POWER_SLOW;
        
        if (Math.abs(left) + Math.abs(right) > 1.75){
            if (lastMax + USER_POWER_SHIFT_TIME < System.currentTimeMillis()){
                power = USER_POWER_FAST;
            }
        } else {
            lastMax = System.currentTimeMillis();
        }
    
        SmartDashboard.putNumber("User Raw Pow", Math.abs(left) + Math.abs(right));
        SmartDashboard.putNumber("User Actuated Pow", Math.abs(left * power) + Math.abs(right * power));
        
        //Drives drivetrain
        drivetrain.drive(left * power, right * power);
    }

    //Is never finished (must be canceled)
    @Override
    protected boolean isFinished() {
        return false;
    }
}
