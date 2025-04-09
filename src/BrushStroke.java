package src;
import java.awt.Color;

public class BrushStroke {
    private int x;
    private int y;
    private Color color;
    private int size;

    public BrushStroke(int x, int y, Color color, int size){
        this.x = x;
        this.y = y;
        this.color = color;
        this.size = size;
    }

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
