import java.awt.Color;

public class Stroke{
    private int x;
    private int y;
    private Color color;
    private int size;
    public Stroke previousStroke;

    public Stroke(int x, int y, Color color, int size){
        this.x = x;
        this.y = y;
        this.color = color;
        this.size = size;

    }
    public Stroke(int x, int y, Color color, int size, Stroke previousStroke){
        this.x = x;
        this.y = y;
        this.color = color;
        this.size = size;
        this.previousStroke = previousStroke;
    }

    //Self explanatory, but allows us to access values
    public int getXval( ){
        return x;
    }
    public int getYval (){
        return y;
    }
    public Color getColor(){
        return color;
    }
    public int getSize(){
        return size;
    }
}