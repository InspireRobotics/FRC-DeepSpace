package frc.robot.hardware;


public class Shape {
    
    //A bunch of variables to store all of the data
    private final int id;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    
    //Stores data
    public Shape(int id, int x, int y, int width, int height){
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    //A ton of getters
    public int getId() {
        return id;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public float getArea(){
        return width * height;
    }
    
    public float getRatio(){
        return ((float) width) / ((float) height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    //Makes a safe copy
    public Shape copy() {
        return new Shape(id, x, y, width, height);
    }
    
    //A better toString function
    @Override
    public String toString() {
        return String.format("%d: (%d, %d), %d x %d", id, x, y, width, height);
    }
}
