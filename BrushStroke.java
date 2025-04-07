import java.awt.Color;

public class BrushStroke {
    private int x;
    private int y;
    private Color color;

    public BrushStroke(int x, int y, Color color){
        this.x = x;
        this.y = y;
        this.color = color;
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

}
