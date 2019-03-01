package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.hardware.PixyCam;
import frc.robot.hardware.Shape;
import frc.robot.subsystems.Drivetrain;

import java.util.ArrayList;

public class AlignCommand extends Command {
    
    
    /**
     * BREAKDOWN:
     *
     * 1. Drive towards target
     *      a. faceTarget ("Point towards target first")
     *      c. driveTo
     * 2. Re-align
     *      a. calculateHeading
     *      b. Rotate robot away from target using heading
     *      c. Drive backwards fixed distance
     * 3. Score
     *      a. faceTarget ("Robot lost sight...manually score")
     *      c. driveTo
     *      d. Exit with prompt to make final corrections (if necessary), and prompt to score hatch panel
     *
     * FUNCTIONS:
     *
     * driveTo
     * Drive towards the target, exiting if target is lost for longer than a certain period OR is certain distance away (Exit with warning)
     * faceTarget (Message)
     * Check for the target, and if found, then turn towards it ... otherwise, exit command with Message
     * calculateHeading (Shape1, Shape2)
     * (Uggg...)
     * Alright, first calculate how far away the shapes are given their apparent sizes (d_1 and d_2), then use that, combined with
     * knowledge of the distance between the two targets to find the two lengths of the line segments resulting from the altitude,
     * use this to solve for the altitude and a side length, which gives us the angle to which we are relative to the target!!!
     *
     */
   
    private final double BASE_SPEED = 0.4; //Percent...Power at which the robot drives forward
    private final double TURN_POW = 0.4; //Percent...Power at which the robot turns in place
    private final double BACKUP_POW = 0.6; //Percent...Power level at which the robot backs up
    private final double BACKUP_DIST = 15; //Revolutions...how far the robot backs up
    
    private final double CAM_WIDTH = 75; //Degrees...used to calculate a critical angle
    private final int CAM_CENTER = 160; //Center of Pixycam gen 1 screen
    

    private Drivetrain drivetrain; //Storage for drivetrain object
    private PixyCam pixyCam; //Storage for pixycam object
    private int id; //Storage for target shape ID
    
    
    private Shape[] storedShapes = new Shape[2]; //Shapes temporarily stored for the "isFinished" function
    private int state = 0;
    private double backupHeading;
    private double startBackupHeading;
    private double backupStart;
    private boolean exit = false;



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
        
        int shapeCount = updateShapes();
        
        switch (state){
            case 0:
            case 4:
                if (shapeCount < 2){
                    exit = true;
                    System.out.println("Failed to detect target.");
                } else {
                    double pow = (storedShapes[0].getX() + storedShapes[1].getX() - CAM_CENTER * 2) / 100;
                    pow += pow > 0 ? pow + TURN_POW : pow - TURN_POW;
                    drivetrain.drive(pow, -pow);
                    if (pow < 0.1){
                        state++;
                    }
                }
                break;
            case 1:
            case 5:
                if (shapeCount < 2){
                    exit = true;
                    drivetrain.Stop();
                    System.out.println("Lost target while driving");
                } else if (storedShapes[0].getArea() + storedShapes[1].getArea() > 200) {
                    backupHeading = (getHeading(storedShapes[0], storedShapes[1]) - 90) * 1.5;
                    startBackupHeading = drivetrain.getHeading();
                    state++;
                } else {
                    double[] drive = getDrive(storedShapes[0], storedShapes[1]);
                    drivetrain.drive(drive[0], drive[1]);
                }
                break;
            case 2:
                double pow = (drivetrain.getHeading() - startBackupHeading + backupHeading) / 20;
                drivetrain.drive(pow, -pow);
                if (pow < 0.1){
                    backupStart = drivetrain.wheelPos();
                    state++;
                }
                break;
            case 3:
                drivetrain.drive(-BACKUP_POW, -BACKUP_POW);
                if (BACKUP_DIST < (backupStart - drivetrain.wheelPos())){
                    state++;
                }
                break;
        }
        
        if (state > 5){
            exit = true;
            System.out.println("Finished successfully");
        }

    }

    private double getHeading(Shape leftShape, Shape rightShape) {
        double[] dist = new double[]{1 / leftShape.getArea(), 1 / rightShape.getArea()};
        double angle = (rightShape.getX() - leftShape.getX()) / (CAM_CENTER * 2) * CAM_WIDTH;
        double back = Math.pow(dist[0], 2) + Math.pow(dist[1], 2) - 2 * dist[0] * dist[1] * Math.cos(angle);
        double backComp = (Math.pow(back, 2) + Math.pow(dist[0], 2) - Math.pow(dist[1], 2)) / (2 * back);
    
        boolean right = false;
        if (backComp * 2 > back) {
            backComp = back - backComp;
            right = true;
        }
        
        double trigPiece = back/2 - backComp;
        double smallDist = right ? dist[1] : dist[0];
        
        double ratio = Math.sqrt(Math.pow(smallDist, 2) - Math.pow(backComp, 2)) / trigPiece;
        return right ? 180 - Math.atan(ratio) : Math.atan(ratio);
    }
    
    //Is the robot aligned?
    @Override
    protected boolean isFinished() {
        return exit;
    }

    //Calculates how the robot should drive, based on what it "sees"
    private double[] getDrive(Shape leftShape, Shape rightShape){
        int centerDiff = (leftShape.getX() + rightShape.getX() - CAM_CENTER * 2) / 2;
        double[] out = new double[]{BASE_SPEED + centerDiff / 20, BASE_SPEED - centerDiff / 20};
        return out;
    }
}
