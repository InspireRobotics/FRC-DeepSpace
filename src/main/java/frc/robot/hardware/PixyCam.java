package frc.robot.hardware;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Optional;

import static frc.robot.HardwareMap.Sensors.PIXY_CAM;

public class PixyCam {

    private static final int PIXY_START_WORD = 0xaa55;
    private static final int PIXY_START_WORD_X = 0x55aa;
    private I2C io = new I2C(I2C.Port.kOnboard, 0);

    private int[] tempFrameData = new int[6];
    private final ArrayList<Shape> shapes = new ArrayList<>();
    private final ArrayList<Shape> temp = new ArrayList<>();
    private long nullTimeout;
    private ArrayList<Integer> dataLog = new ArrayList<>();

    public PixyCam(){
        for (int i = 0; i < 6; i++){
            tempFrameData[i] = 0x01;
        }
    }

    public void setBrightness(int brightness) {
        byte[] writing = new byte[3];
        writing[0] = numberToByte(0x00);
        writing[1] = numberToByte(0xfe);
        writing[2] = numberToByte(brightness);
        io.writeBulk(writing);
    }

    public void updateFrame() {
        dataLog.clear();
        temp.clear();
        tempFrameData[0] = readWord();
        readShape().ifPresent(temp::add);
        tempFrameData = new int[6];

        while ((System.currentTimeMillis() - nullTimeout) < 20 && !(syncWord(tempFrameData[0]) && syncWord(tempFrameData[1]))){
            tempFrameData[0] = readWord();
            if (syncWord(tempFrameData[0]) && syncWord(tempFrameData[1])){
                break;
            }
            tempFrameData[1] = readWord();
            if (syncWord(tempFrameData[0]) && !syncWord(tempFrameData[1])) {
                tempFrameData[0] = tempFrameData[1];
                readShape().ifPresent(temp::add);
            }
        }
        setShapes(temp);
    }

    private Optional<Shape> readShape() {
        int sum = 0;
        for (int i = 1; i < 6; i++) {
            tempFrameData[i] = readWord();
            sum += tempFrameData[i];
        }

        if (sum == tempFrameData[0] && notNull(tempFrameData[0])) {
            nullTimeout = System.currentTimeMillis();
            return Optional.of(new Shape(tempFrameData[1], tempFrameData[2],
                    tempFrameData[3], tempFrameData[4], tempFrameData[5]));
        }
        return Optional.empty();
    }

    private boolean syncWord(int word) {
        return word == PIXY_START_WORD;
    }

    private boolean notNull(int word) {
        return !(word == 0);
    }
    private boolean synced(int word) {
        return word != PIXY_START_WORD_X;
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
        boolean abortedWhileReading = io.read(PIXY_CAM, 2, buffer);
        if (!abortedWhileReading) {
            dataLog.add(getUnsignedInt(buffer.array()));
            if (getUnsignedInt(buffer.array()) > 0){
                nullTimeout = System.currentTimeMillis();
            }
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