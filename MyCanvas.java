import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JPanel;



class MyCanvas extends JPanel{
    private List<Sticker> items = new ArrayList<>();
    private int x = 0;
    private int y = 0;
    private int state = 0;
    private String stickerUnderstood;

    public void addItem(String sticker, int xCord, int yCord){
        items.add(new Sticker(sticker, xCord, yCord));
        stickerUnderstood = sticker;
    }
    public void addItem(String sticker){
        stickerUnderstood = sticker;
    }
    
    public void resetList(){
        items.clear();
    }

    public void setCanvasState(int state){
        this.state = state;
    }
    

    public MyCanvas(){
        setPreferredSize(new Dimension(1080, 720));
        setBackground(Color.white);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                x = e.getX();
                y = e.getY();
                System.out.println("MOUSE DETECTED @: " + x + ", " + y);
                
                items.add(new Sticker(stickerUnderstood, x, y));
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        //int height = 1200;
        //int width = 800;
        Paintbrush myBrush = new Paintbrush(g);
        myBrush.setBackground(state);
        for (Sticker item : items){
            myBrush.addSticker(item.getXval(), item.getYval(), item.getSticker());
        }

    }
}
