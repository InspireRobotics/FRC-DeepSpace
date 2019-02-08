package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.HardwareMap;

public class DefaultDriveCommand extends Command {
    
    //Base power multiplier
    public static final double USER_POWER = 1.0;

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
        double left = -drive.getRawAxis(HardwareMap.Joysticks.LEFT_Y_AXIS)*USER_POWER;
        double right = -drive.getRawAxis(HardwareMap.Joysticks.RIGHT_Y_AXIS)*USER_POWER;

        //Drives drivetrain
        drivetrain.drive(left, right);
    }

    //Is never finished (must be canceled)
    @Override
    protected boolean isFinished() {
        return false;
    }
}
