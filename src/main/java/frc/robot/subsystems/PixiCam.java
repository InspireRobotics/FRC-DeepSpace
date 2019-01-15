package frc.robot.subsystems;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.HardwareMap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

class Shape {
    public int id;
    public int x;
    public int y;
    public int width;
    public int height;
    public Shape(int id, int x, int y, int width, int height){
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return String.format("%d: (%d, %d)", id, x, y);
    }
}

public class PixiCam {

    private static final int PIXY_START_WORD = 0xaa55;
    private static final int PIXY_START_WORD_CC = 0xaa56;
    private I2C io = new I2C(I2C.Port.kOnboard, HardwareMap.Sensors.PIXY_CAM);

    private int[] tempFrameData = new int[6];
    private final ArrayList<Shape> shapes = new ArrayList<>();

    public void setBrightness(int brightness){
        byte[] writing = new byte[3];
        writing[0] = numberToByte(0x00);
        writing[1] = numberToByte(0xfe);
        writing[2] = numberToByte(brightness);
        io.writeBulk(writing);
    }
    public void updateFrame() {
        ArrayList<Shape> temp = new ArrayList<>();

        while (syncByteNotPresent()) {
            tempFrameData[0] = readWord();

            if (syncByteNotPresent()) {
                temp.add(readShape());
            }

            SmartDashboard.putString("p", System.currentTimeMillis() + ": " + shapes.size());
        }
        shapes.clear();
        shapes.addAll(temp);
    }

    private Shape readShape() {
        int checkSum = tempFrameData[0];
        for (int i = 1; i < 6; i++) {
            tempFrameData[i] = readWord();
            checkSum += tempFrameData[i];
        }
        if (checkSum == tempFrameData[0]) {
            return new Shape(tempFrameData[1], tempFrameData[2], tempFrameData[3], tempFrameData[4], tempFrameData[5]);
        } else {
            System.out.println("Object dropped due to checkSum error.");
        }
        return null;
    }

    private boolean syncByteNotPresent(){
        return !(tempFrameData[0] == PIXY_START_WORD || tempFrameData[0] == PIXY_START_WORD_CC);
    }

    public void updateDashboard(){
        for(int i = 0; i < shapes.size(); i++){
            SmartDashboard.putString("Shape " + i, shapes.get(i).toString());
        }
    }

    public static byte numberToByte(int input){
        return (byte) (input & 0xFF);
    }
    public static int getUnsignedInt(byte[] data){
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getShort() & 0xffff;
    }
    public int readWord(){
        ByteBuffer buffer = ByteBuffer.allocate(2);
        boolean abortedWhileReading = io.readOnly(buffer, 2);
        if (!abortedWhileReading) {
            SmartDashboard.putNumber("last word: ", getUnsignedInt(buffer.array()));
            return getUnsignedInt(buffer.array());
        } else {
            return 0;
        }
    }

    public ArrayList<Shape> getShapes(){
        return shapes;
    }
}