import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JPanel;



class MyCanvas extends JPanel{
    private List<Sticker> items = new ArrayList<>();
    
    
    public void addItem(String sticker, int xCord, int yCord){
        items.add(new Sticker(sticker, xCord, yCord));
    }
    
    public void resetList(){
        items.clear();
    }
    

    public MyCanvas(){
        setPreferredSize(new Dimension(1080, 720));
        setBackground(Color.white);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        int height = 1200;
        int width = 800;
        Paintbrush myBrush = new Paintbrush(g);
        myBrush.setBackground(height, width);
        for (Sticker item : items){
            myBrush.addSticker(item.getXval(), item.getYval(), item.getSticker());
        }

    }
}
