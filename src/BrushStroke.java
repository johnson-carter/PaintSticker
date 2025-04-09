package src;
import java.awt.Color;

/*
 *      This class is responsible for creating an object capable of storing
 *      all user inputs as a group of pixels at (x,y) along with a size and 
 *      color parameter.
 */

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
