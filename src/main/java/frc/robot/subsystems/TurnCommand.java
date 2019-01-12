package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Command;

public class TurnCommand extends Command {

    private static final double BASE_POWER = .58;
    private static final double MARGIN_DEGREES = 1.0;
    private static final double P_VAL = 0.003;

    private long startFinishTime;
    private Drivetrain drivetrain;
    private double target;

    public TurnCommand(Drivetrain drivetrain, double degrees) {
        this.drivetrain = drivetrain;
        this.target = degrees + drivetrain.getHeading();

        requires(drivetrain);
    }

    @Override
    protected void execute() {
        if (!onTarget()) {
            double error = target - drivetrain.getHeading();

            double output = error * P_VAL;
            output += BASE_POWER;
            output *= Math.signum(error);

            drivetrain.drive(output, -output);
        }else{
            drivetrain.drive(0,0);
        }
    }

    @Override
    protected boolean isFinished() {
        if(onTarget()){
            if(startFinishTime == -1) {
                startFinishTime = System.currentTimeMillis();
            }else if(startFinishTime + 250 < System.currentTimeMillis()){
                System.out.println("Finished turning to: " + target);
                return true;
            }
        }else{
            startFinishTime = -1;
        }

        return false;
    }

    private boolean onTarget(){
        return Math.abs(target - drivetrain.getHeading()) < MARGIN_DEGREES;
    }
}