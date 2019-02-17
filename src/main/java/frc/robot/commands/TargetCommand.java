package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.hardware.PixyCam;
import frc.robot.hardware.Shape;
import frc.robot.subsystems.Drivetrain;

import java.util.ArrayList;

//        Pixy Camera Lens FOV:
//        75 degrees horizontal, 47 degrees vertical

//        NOTE: This assumes the robot is already in front of the target, and is simply precision aligning.

public class TargetCommand extends Command {

    private static final double MARGIN_VIEW = 5.0;

    private final Drivetrain drivetrain;
    private final PixyCam pixyCam;
    private int id;
    private long startFinishTime = -1;
    private double centerX = -100;

    public TargetCommand(Drivetrain drivetrain, PixyCam pixyCam, int id){
        this.drivetrain = drivetrain;
        this.pixyCam = pixyCam;
        this.id = id;

        requires(drivetrain);
    }

    @Override
    protected void execute() {
        Shape[] markers = new Shape[2];
        byte markerCount = 0;
        ArrayList<Shape> temp = pixyCam.getShapes();
        for (var i = 0; i < temp.size(); i++){
            if (temp.get(i).getId() == id){
                markers[markerCount] = temp.get(i);
                markerCount++;
                if (markerCount >= 2){
                    i = temp.size();
                }
            }
        }
        if (markerCount == 2){
            try {
                centerX = ((float) (markers[0].getX() + markers[1].getX())) / 2;
            } catch (Exception e){
                centerX = 160;
            }
            double diff = 160 - centerX;
            SmartDashboard.putNumber("Difference from center:", diff);
            double drive = 0.2;
            if (diff < 0)
                drive *= -1;
            double power = Math.min(Math.max(drive + diff/200, -0.5), 0.5);
            drivetrain.drive(-power, power);
        } else if (startFinishTime == -1){
            drivetrain.drive(0.3, -0.3);
        }
    }

    @Override
    protected boolean isFinished() {
        if(onTarget()){
            if(startFinishTime == -1) {
                startFinishTime = System.currentTimeMillis();
            }else if(startFinishTime + 500 < System.currentTimeMillis()){
                System.out.println("Finished turning to the target.");
                return true;
            }
        }else{
            startFinishTime = -1;
        }
        return false;
    }

    private boolean onTarget(){
        return Math.abs(160 - centerX) < MARGIN_VIEW;
    }
}
