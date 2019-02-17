package frc.robot.commands;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.HardwareMap;
import frc.robot.subsystems.Drivetrain;

public class DefaultDriveCommand extends Command {
    
    //Base power multiplier
    private static final double USER_POWER_SLOW = 0.65; //%
    //Boosted power multiplier
    private static final double USER_POWER_FAST = 0.75; //%
    //Boost activation time
    private static final long USER_POWER_SHIFT_TIME = 1000; //ms

    //Last time since the robot hit max power
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
        
        //Sets power to default "slow"
        double power = USER_POWER_SLOW;
        
        //If joysticks maxed...else...
        if (Math.abs(left + right) > 1.75){
            //...if time elapsed has passed threshold...
            if (lastMax + USER_POWER_SHIFT_TIME < System.currentTimeMillis()){
                //...set power to "fast" setting
                power = USER_POWER_FAST;
            }
        } else {
            //...reset threshold time
            lastMax = System.currentTimeMillis();
        }
    
        //Puts joystick and drive values to dashboard
        SmartDashboard.putNumber("User Raw Pow", Math.abs(left + right));
        SmartDashboard.putNumber("User Actuated Pow", Math.abs(left * power + right * power));
        
        //Drives drivetrain
        drivetrain.drive(left * power, right * power);
    }

    //Is never finished (must be canceled)
    @Override
    protected boolean isFinished() {
        return false;
    }
}
