package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.hardware.PixyCam;
import frc.robot.hardware.Shape;
import frc.robot.subsystems.Drivetrain;

import java.util.ArrayList;

public class AlignCommand extends Command {

    private final long LAG_LIMIT = 0; //ms. Change if you often get gaps between detection periods
    private final int CAM_CENTER = 160; //Center of Pixycam gen 1 screen
    private final long FINISH_LIMIT = 100; //ms
    private final double BASE_SPEED = 0.4; //ONLY CHANGE THIS IF YOU KNOW YOUR ROBOT CAN HANDLE IT
    private final double TURN_LIMIT = 0.4; //Power at which the robot begins to turn in place
    private final double BACKUP_LIMIT = 0.6;
    private final double ASPECT_RATIO = 1.5; //Height over Width

    private Drivetrain drivetrain; //Storage for drivetrain object
    private PixyCam pixyCam; //Storage for pixycam object
    private int id; //Storage for target shape ID
    private long lagTime = 0; //Storage for pixycam lagtime
    private long finishTime = -1; //Time in which he robot is in a finished state
    private Shape[] storedShapes = new Shape[2]; //Shapes temporaially stored for the "isFinished" function



    //Initialization command - stores target id, drivetrain object ref, and pixycam object ref
    public AlignCommand(Drivetrain drivetrain, PixyCam pixyCam, int id){
        this.drivetrain = drivetrain;
        this.pixyCam = pixyCam;
        this.id = id;

        requires(drivetrain); //Free the drivetrain
    }
    
    //Updates the storedShapes vars, returns count detected
    private int updateShapes(){
        ArrayList<Shape> temp = pixyCam.getShapes();
    
        Shape[] shapes = new Shape[2];
        int shapeCount = 0;
        for (var i = 0; i < temp.size(); i++){
            if (temp.get(i).getId() == id){
                shapes[shapeCount] = temp.get(i).copy();
                shapeCount++;
                if (shapeCount == 2){
                    storedShapes[0] = shapes[0];
                    storedShapes[1] = shapes[1];
                    break;
                }
            }
        }
        
        return shapeCount;
    }
    
    //Main execution loop
    @Override
    protected void execute() {
        
        int shapeCount = updateShapes(); //Refreshes shapes
        
        
        //if valid...else
        if (shapeCount == 2 && finishTime == -1){
            try {
                lagTime = -1;
                double[] drive = getDrive(storedShapes[0], storedShapes[1]);
                //System.out.println("[" + drive[0] + ",\n" + drive[1] + "]");
                drivetrain.drive(drive[0], drive[1]);
            } catch (Exception e){
                System.out.println("Dropped frame");
            }
        } else {
            //if within lag params...else
            if (lagTime == -1 && LAG_LIMIT != 0) {
                //Begin lag timner
                lagTime = System.currentTimeMillis();
            } else if (lagTime + LAG_LIMIT < System.currentTimeMillis()){
                //Stop drivetrain
                drivetrain.Stop();
            }
        }

    }

    //Is the robot aligned?
    @Override
    protected boolean isFinished() {
        //If not lagged...
        if (lagTime == -1 || lagTime + LAG_LIMIT >= System.currentTimeMillis()){
            //If within parameters...
            if (Math.abs(storedShapes[0].getArea() / storedShapes[1].getArea() - 1) * Math.abs(storedShapes[0].getX() + storedShapes[1].getX() - CAM_CENTER * 2) < 0.5 &&
                    Math.sqrt(storedShapes[0].getArea() * storedShapes[1].getArea()) > 600){
                //If timer not started...else...
                if (finishTime == -1){
                    //Start timer
                    finishTime = System.currentTimeMillis();
                } else if (finishTime + FINISH_LIMIT < System.currentTimeMillis()) {
                    //Print the robot thinks it is finished, stop command
                    System.out.println("Finished");
                    return true;
                }
            } else {
                //Reset timer
                finishTime = -1;
            }
        } else {
            //Reset timer
            finishTime = -1;
        }
        //If conditions not met, return false
        return false;
    }

    //Calculates how the robot should drive, based on what it "sees"
    private double[] getDrive(Shape leftShape, Shape rightShape){
        //Setup vars
        double[] out = new double[2];
        out[0] = BASE_SPEED;
        out[1] = BASE_SPEED;

        //Height:Width ratio, relies on the fact that things appear skinny if not aligned
        double leftRatio = leftShape.getRatio() / ASPECT_RATIO;
        double rightRatio = rightShape.getRatio() / ASPECT_RATIO;
        
        //Variable representing the difference in aspect ratios
        double ratioDiff = leftRatio / rightRatio;
        ratioDiff = ratioDiff < 1 ? 1 - 1 / ratioDiff : ratioDiff - 1;
        
        //Calculate where the drive target is, and how far off we are from it
        double center = Math.min(Math.max(CAM_CENTER + (ratioDiff) * 25, 100), 220);
        double centerDiff = (leftShape.getX() + rightShape.getX() - center * 2) / 2;
    
        //How to drive
        boolean closeLeft = centerDiff > 0;
        double drive = Math.abs(centerDiff / 150);
    
        SmartDashboard.putNumber("Raw Drive", drive);
        //Power shift
        if (drive > 0.1 && drive <= 0.2){
            drive *= 2;
        } else if (drive > 0.2 && drive <= 0.4){
            drive = 0.4;
        }
        
        //Scope override
        if (leftShape.getX() < 40){
            closeLeft = true;
            drive = TURN_LIMIT + 0.01;
        } else if (rightShape.getX() > 280){
            closeLeft = false;
            drive = TURN_LIMIT + 0.01;
        }
        
        //Apply numbers
        if (closeLeft){
            out[0] = BASE_SPEED + drive;
            if (drive > BACKUP_LIMIT) {
                out[0] = -out[0];
                out[1] = -BASE_SPEED;
            } else if (drive > TURN_LIMIT) {
                out[1] = -out[0];
            }
        } else {
            out[1] = BASE_SPEED + drive;
            if (drive > BACKUP_LIMIT) {
                out[1] = -out[1];
                out[0] = -BASE_SPEED;
            } else if (drive > TURN_LIMIT) {
                out[0] = -out[0];
            }
        }
        
        //Return drive values
        return out;
    }
}
