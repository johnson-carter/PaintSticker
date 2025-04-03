import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;



class MyCanvas extends JPanel{
    private String sticker = "Rock";
    private int xCord = 0;
    private int yCord = 0;

    public void setSticker(String sticker){
        this.sticker = sticker;
    }
    public void setCoordinates(int xCord, int yCord){
        this.xCord = xCord;
        this.yCord = yCord;
    }
    

    public MyCanvas(){
        setPreferredSize(new Dimension(1080, 720));
        setBackground(Color.white);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Paintbrush myBrush = new Paintbrush(g);
        myBrush.setBackground();
        
        myBrush.addSticker(xCord, yCord, sticker);
    }
}
