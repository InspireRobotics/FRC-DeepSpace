package frc.robot.hardware;

public class Shape {
    private final int id;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    public Shape(int id, int x, int y, int width, int height){
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("%d: (%d, %d)", id, x, y);
    }
}
