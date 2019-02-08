package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.hardware.PixyCam;
import frc.robot.hardware.Shape;

public class FollowCommand extends Command {
    
    //Center of screen
    private final int CENTER_X = 160;
    
    private Drivetrain drivetrain; //Reference to drivetrain
    private PixyCam pixyCam; //Reference to pixycam
    private int id; //Id to follow
    private Shape shape; //Stored shape
    private long lastUpdateTime = 0; //Time since last update

    //Constructor
    public FollowCommand(Drivetrain drivetrain, PixyCam pixiCam, int id){
        this.drivetrain = drivetrain;
        this.pixyCam = pixiCam;
        this.id = id;

        requires(drivetrain);
    }
    
    //Main execution loop
    @Override
    protected void execute(){
        //Updates shape stored in memory
        updateShape();
        
        //Sets up temporary drive calculation
        double leftPower = 0, rightPower = 0;

        //if the shape is non-null...else...
        if (shape != null) {
            //...calculate and turn towards object
            double xDiff = shape.getX() - CENTER_X;

            //Spin power...
            xDiff /= CENTER_X;
            xDiff /= 2;

            //...Spin direction...
            if (xDiff > 0){
                xDiff += 0.45;
            } else {
                xDiff -= 0.45;
            }

            //...Spin set!
            leftPower = xDiff;
            rightPower = -xDiff;
        } else {
            //...stop
            leftPower = 0;
            rightPower = 0;
        }

        //Debug data
        SmartDashboard.putString("Tracked Shape",shape == null ? "N/A":shape.toString());
        SmartDashboard.putString("Output Power", String.format("Left = %f   Right=%f", leftPower, rightPower));
        //Drive drivetrain
        drivetrain.drive(leftPower, rightPower);
    }
    
    //Gets shapes
    public void updateShape(){
        //Temporary shape storage
        Shape tempShape = null;
        for (int i = 0; i < pixyCam.getShapes().size(); i++) {
            if (pixyCam.getShapes().get(i).getId() == id) {
                tempShape = pixyCam.getShapes().get(i);
                break;
            }
        }
        
        //If the shape is not null...else if null for longer than timeout...
        if (tempShape != null) {
            //...save shape
            shape = tempShape;
            lastUpdateTime = System.currentTimeMillis();
        } else if ((System.currentTimeMillis() - lastUpdateTime) > 100) {
            //...set shape to null
            shape = null;
        }
        
    }

    //Is the command finished...
    @Override
    protected boolean isFinished() {
        return false; //For now, we never want it to finish. We may add a full automation sequence later
    }
}
