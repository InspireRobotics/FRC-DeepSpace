package frc.robot.hardware;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Optional;

import static frc.robot.HardwareMap.Sensors.PIXY_CAM;

public class PixyCam {
    
    private static final int PIXY_START_WORD = 0xaa55; //Pixycam shape start word
    private static final int PIXY_START_WORD_X = 0x55aa; //Pixycam shape start word, but backwards (desync error)
    private final int NULL_LIMIT = 20; //How long the cam goes without returning a new "null" frame
    private I2C io = new I2C(I2C.Port.kOnboard, 0); //Raw data port

    private int[] tempFrameData = new int[6]; //Temporary data read storage
    private final ArrayList<Shape> shapes = new ArrayList<>(); //"Public" list of shapes
    private final ArrayList<Shape> temp = new ArrayList<>(); //Private, constantly changing list of shapes
    private long nullTimeout; //Time since last non-null data read
    private int nullFrameCount = 0; //Counter of "null" frames
    private ArrayList<Integer> dataLog = new ArrayList<>(); //Log of ALL READ DATA in the past frame
    
    //Initializes frameData
    public PixyCam(){
        for (int i = 0; i < 6; i++){
            tempFrameData[i] = 0x01;
        }
    }

//    public void setBrightness(int brightness) {
//        byte[] writing = new byte[3];
//        writing[0] = numberToByte(0x00);
//        writing[1] = numberToByte(0xfe);
//        writing[2] = numberToByte(brightness);
//        io.writeBulk(writing);
//    }

    //Main function
    public void updateFrame() {
        nullTimeout = System.currentTimeMillis(); //Reset the null frame timeout
        temp.clear(); //Clear temporary shapes list
        dataLog.clear(); //Clear datalog
        tempFrameData[0] = readWord(); //Attempt to read shape...
        readShape().ifPresent(temp::add); //...
        tempFrameData = new int[6]; //...Reset temp data

        while ((System.currentTimeMillis() - nullTimeout) < NULL_LIMIT && !(syncWord(tempFrameData[0]) && syncWord(tempFrameData[1]))){
            tempFrameData[0] = readWord();
            //Make sure we're synced / on frame
            if (syncWord(tempFrameData[0]) && syncWord(tempFrameData[1]) || !synced(tempFrameData[0])){
                break;
            }
            tempFrameData[1] = readWord();
            //Make sure we're synced
            if (!synced(tempFrameData[1])){
                break;
            }
            //Read shape (if present)
            if (syncWord(tempFrameData[0]) && !syncWord(tempFrameData[1])) {
                tempFrameData[0] = tempFrameData[1];
                readShape().ifPresent(temp::add);
            }
        }
        //Null frame counter
        if (temp.size() == 0){
            nullFrameCount++;
            //System.out.print("Is null");
        } else {
            nullFrameCount = 0;
            //System.out.print("Is real");
        }
        
        //Update shapes list
        setShapes(shapeSort(temp));
    }

    private Optional<Shape> readShape() {
        //Run through checksum
        int sum = 0;
        for (int i = 1; i < 6; i++) {
            tempFrameData[i] = readWord();
            sum += tempFrameData[i];
        }
        
        //If the sum checks out..
        if (sum == tempFrameData[0] && notNull(tempFrameData[0])) {
            nullTimeout = System.currentTimeMillis();
            return Optional.of(new Shape(tempFrameData[1], tempFrameData[2],
                    tempFrameData[3], tempFrameData[4], tempFrameData[5]));
            //Return shape
        }
        //Don't return shape
        return Optional.empty();
    }
    //Auto-sorts shapes by x value. Does this without modifying the original ArrayList.
    private ArrayList<Shape> shapeSort(ArrayList<Shape> shapes){
        
        //Creates new list
        ArrayList<Shape> temp = new ArrayList<>();
        
        //Sorting vars
        ArrayList<Integer> usedShapes = new ArrayList<>();
        int[] bestShape = new int[2];
        
        //For every shape...
        for (var i = 0; i < shapes.size(); i++){
            bestShape[1] = 321; //Variable out of bounds
            //For every shape...
            for (var j = 0; j < shapes.size(); j++){
                //Find the best one that HASN'T been sorted yet
                if (!usedShapes.contains(j) && shapes.get(j).getX() < bestShape[1]){
                    bestShape[1] = shapes.get(j).getX();
                    bestShape[0] = j;
                }
            }
            //Add the shape to the new list, and add the index to the list of already used shapes
            usedShapes.add(bestShape[0]);
            temp.add(shapes.get(bestShape[0]).copy());
        }
        //Return the new list
        return temp;
    }
    
    //Gets the syncword (declutter)
    private boolean syncWord(int word) {
        return word == PIXY_START_WORD;
    }
    
    //Checks if null (declutter)
    private boolean notNull(int word) {
        return !(word == 0);
    }
    
    //Checks if synced (syncs if not)
    private boolean synced(int word) {
        boolean sync = (word != PIXY_START_WORD_X);
        if (!sync){
            io.read(PIXY_CAM, 1, ByteBuffer.allocateDirect(1));
        }
        return sync;
    }
    
    //Updates the dashboard
    public void updateDashboard() {
        ArrayList<Shape> shapes = getShapes();
        SmartDashboard.putNumber("Shape Count", shapes.size());
        
        if (shapes.size() > 1){
            SmartDashboard.putString("First Shape", shapes.get(0).toString());
            SmartDashboard.putString("Second Shape", shapes.get(1).toString());
        } else if (shapes.size() > 0){
            SmartDashboard.putString("First Shape", shapes.get(0).toString());
            SmartDashboard.putString("Second Shape", "None");
        } else {
            SmartDashboard.putString("First Shape", "None");
            SmartDashboard.putString("Second Shape", "None");
        }
        
        SmartDashboard.putNumber("Null Frame Count", nullFrameCount);
    }

    //Gets integer from a list of bytes (assumes little-endian)
    private static int getUnsignedInt(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getShort() & 0xffff;
    }
    
    //Reads a two-byte word
    private int readWord() {
        ByteBuffer buffer = ByteBuffer.allocate(2); //Allocate buffer
        boolean abortedWhileReading = io.read(PIXY_CAM, 2, buffer); //Read bytes into the buffer
        
        //If valid...else...
        if (!abortedWhileReading) {
            //Get the number, and add it to the dataLog
            int num = getUnsignedInt(buffer.array());
            dataLog.add(num);
            
            //If non-zero...
            if (num > 0){
                //...reset the nullTimeout
                nullTimeout = System.currentTimeMillis();
            }
            //Return the number
            return num;
        } else {
            //...Return nothing
            return 0;
        }
    }
    
    //Threadsafe shape updater function
    private void setShapes(ArrayList<Shape> temp) {
        //Synchronizes (resets cache)
        synchronized (shapes) {
            //Reset shapes, then add all objects from input list
            shapes.clear();
            shapes.addAll(temp);
        }
    }

    //Threadsafe shape getter function
    public ArrayList<Shape> getShapes() {
        //Synchronizes
        synchronized (shapes) {
            //Return a COPY (to avoid live reference editing)
            return (ArrayList<Shape>) shapes.clone();
        }
    }
}