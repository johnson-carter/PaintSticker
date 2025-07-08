package src;
import java.awt.Color;

/*
 *      This class is responsible for creating an object capable of storing
 *      all user inputs as a group of pixels at (x,y) along with a size and 
 *      color parameter.
 */

public class BrushStroke {
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_TEXT = 2;

    private int x;
    private int y;
    private Color color;
    private int size;
    private int type = TYPE_NORMAL;
    private String text;

    // Normal stroke constructor
    public BrushStroke(int x, int y, Color color, int size){
        this.x = x;
        this.y = y;
        this.color = color;
        this.size = size;
        this.type = TYPE_NORMAL;
    }

    // Text stroke constructor
    public BrushStroke(int x, int y, Color color, int size, String text){
        this.x = x;
        this.y = y;
        this.color = color;
        this.size = size;
        this.type = TYPE_TEXT;
        this.text = text;
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
    public int getType() {
        return type;
    }
    public String getText() {
        return text;
    }
}
