package frc.robot.hardware;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.HardwareMap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Optional;

public class PixyCam {

    private static final int PIXY_START_WORD = 0xaa55;
    private static final int PIXY_START_WORD_X = 0x55aa;
    private I2C io = new I2C(I2C.Port.kOnboard, 0);

    private int[] tempFrameData = new int[6];
    private final ArrayList<Shape> shapes = new ArrayList<>();

    public void setBrightness(int brightness) {
        byte[] writing = new byte[3];
        writing[0] = numberToByte(0x00);
        writing[1] = numberToByte(0xfe);
        writing[2] = numberToByte(brightness);
        io.writeBulk(writing);
    }

    public void updateFrame() {
        ArrayList<Shape> temp = new ArrayList<>();
        tempFrameData = new int[6];
        tempFrameData[0] = 0x01;
        tempFrameData[1] = 0x01;

        long startTime = System.currentTimeMillis();
        while (synced() && syncByteNotPresent() && (System.currentTimeMillis() - startTime) < 50){
            tempFrameData[0] = readWord();
            tempFrameData[1] = readWord();
            if (syncByteNotPresent() && notNull()) {
                readShape().ifPresent(temp::add);
            }
        }
        if (!synced()){
            io.read(HardwareMap.Sensors.PIXY_CAM, 1, ByteBuffer.allocate(1));
        }

        setShapes(temp);
    }

    private Optional<Shape> readShape() {
        int checkSum = tempFrameData[1];
        for (int i = 2; i < 6; i++) {
            tempFrameData[i] = readWord();
            checkSum += tempFrameData[i];
        }

        if (checkSum == tempFrameData[0]) {
            return Optional.of(new Shape(tempFrameData[1], tempFrameData[2],
                    tempFrameData[3], tempFrameData[4], tempFrameData[5]));
        }

        return Optional.empty();
    }

    private boolean syncByteNotPresent() {
        return !(tempFrameData[0] == PIXY_START_WORD && tempFrameData[1] == PIXY_START_WORD);
    }

    private boolean notNull() {
        return !(tempFrameData[0] == 0 && tempFrameData[1] == 0);
    }
    private boolean synced() {
        return tempFrameData[1] != PIXY_START_WORD_X;
    }
    public void updateDashboard() {
        SmartDashboard.putNumber("Shape Count", getShapes().size());
        try{
            SmartDashboard.putString("First Shape", getShapes().get(0).toString());
        } catch (Exception e){
            SmartDashboard.putString("First Shape", "None");
        }
    }

    public static byte numberToByte(int input) {
        return (byte) (input & 0xFF);
    }

    public static int getUnsignedInt(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getShort() & 0xffff;
    }

    public int readWord() {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        boolean abortedWhileReading = io.read(HardwareMap.Sensors.PIXY_CAM, 2, buffer);
        if (!abortedWhileReading) {
            return getUnsignedInt(buffer.array());
        } else {
            return 0;
        }
    }

    private void setShapes(ArrayList<Shape> temp) {
        synchronized (shapes) {
            shapes.clear();
            shapes.addAll(temp);
        }
    }

    public ArrayList<Shape> getShapes() {
        synchronized (shapes) {
            return shapes;
        }
    }
}