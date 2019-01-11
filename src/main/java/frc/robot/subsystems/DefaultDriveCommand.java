package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.HardwareMap;

public class DefaultDriveCommand extends Command {

    public static final double USER_POWER = 0.8;
    public double currentLeft = 0.0;
    public double currentRight = 0.0;

    private final Joystick drive = HardwareMap.Joysticks.drive;
    private final Drivetrain drivetrain;

    public DefaultDriveCommand(Drivetrain drivetrain) {
        super("Default Drive");
        this.drivetrain = drivetrain;

        requires(drivetrain);
    }

    @Override
    protected void execute() {
        double left = -drive.getRawAxis(HardwareMap.Joysticks.LEFT_Y_AXIS)*USER_POWER;
        double right = -drive.getRawAxis(HardwareMap.Joysticks.RIGHT_Y_AXIS)*USER_POWER;

        currentLeft = weightedAverage(left, currentLeft);
        currentRight = weightedAverage(right, currentRight);

        drivetrain.drive(currentLeft, currentRight);
    }

    private double weightedAverage(double newVal, double oldVal) {
        return (oldVal * 9 + newVal) / 10;
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
