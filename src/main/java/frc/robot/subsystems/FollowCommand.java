package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.hardware.PixyCam;
import frc.robot.hardware.Shape;

public class FollowCommand extends Command {
    private final int CENTER_X = 160;

    private Drivetrain drivetrain;
    private PixyCam pixyCam;
    private int id;
    private Shape shape;
    private long lastUpdateTime = 0;

    public FollowCommand(Drivetrain drivetrain, PixyCam pixiCam, int id){
        this.drivetrain = drivetrain;
        this.pixyCam = pixiCam;
        this.id = id;

        requires(drivetrain);
    }
    @Override
    protected void execute(){
        updateShape();

        double leftPower = 0, rightPower = 0;

        if (shape != null) {
            double xDiff = shape.getX() - CENTER_X;

            xDiff /= CENTER_X;
            xDiff /= 2;

            if (xDiff > 0){
                xDiff += 0.45;
            } else {
                xDiff -= 0.45;
            }

            leftPower = xDiff;
            rightPower = -xDiff;
        } else {
            leftPower = 0;
            rightPower = 0;
        }

        SmartDashboard.putString("Tracked Shape",shape == null ? "N/A":shape.toString());
        SmartDashboard.putString("Output Power", String.format("Left = %f   Right=%f", leftPower, rightPower));
        drivetrain.drive(leftPower, rightPower);
    }

    public void updateShape(){
        Shape tempShape = null;
        try {
            for (int i = 0; i < pixyCam.getShapes().size(); i++) {
                if (pixyCam.getShapes().get(i).getId() == id) {
                    tempShape = pixyCam.getShapes().get(i);
                    break;
                }
            }

            if (tempShape != null) {
                shape = tempShape;
                lastUpdateTime = System.currentTimeMillis();
            } else if ((System.currentTimeMillis() - lastUpdateTime) > 500) {
                shape = null;
            }
        } catch (Exception e){

        }
    }

    @Override
    protected boolean isFinished() {
        return false; //For now, we never want it to finish. We may add a full automation sequence later
    }
}
