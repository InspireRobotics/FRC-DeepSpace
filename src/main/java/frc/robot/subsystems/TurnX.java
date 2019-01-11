package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Command;

public class TurnX extends Command {
    private double originPin;
    private Drivetrain drivetrain;
    private double target;
    private double rate;
    private boolean finished = false;

    public TurnX(Drivetrain drivetrain, double degrees, double rate) {
        this.drivetrain = drivetrain;
        originPin = drivetrain.getHeading();
        this.rate = rate;
        this.target = degrees;
    }

    @Override
    protected void execute() {
        if (!finished) {
            if (Math.abs(target - originPin) < 1) {
                finished = true;
            }
            double margin = target - originPin;
            drivetrain.drive(margin * rate, -margin * rate);
        }
    }

    @Override
    protected boolean isFinished() {
        return finished;
    }
}