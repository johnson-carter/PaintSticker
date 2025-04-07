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
    private List<BrushStroke> strokes = new ArrayList<>();
    private int x = 0;
    private int y = 0;
    private int state = 0;
    private int brushStatus = 0;
    private String stickerUnderstood;
    private Color colorSel = Color.black;


    // Methods for generating content from App.java controlled inputs
    public void addItem(String sticker, int xCord, int yCord){
        items.add(new Sticker(sticker, xCord, yCord));
        stickerUnderstood = sticker;
    }
    public void addItem(String sticker){
        stickerUnderstood = sticker;
    }
    public void addItem(int x, int y, Color color){
        strokes.add(new BrushStroke(x, y, color));
    }
    public void addFinal(int x, int y){
        strokes.add(new BrushStroke(x, y, colorSel));
        System.out.println("STROKE MADE");
        repaint();
    }
    
    public void resetList(){
        items.clear();
        strokes.clear();
    }

    public void setCanvasState(int state){
        this.state = state;
    }

    public void setBrushMode(int state){
        brushStatus = state;
    }

    public void setColorChosen(Color color){
        colorSel = color;
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
                
                if(brushStatus == 0){
                    items.add(new Sticker(stickerUnderstood, x, y));
                    repaint();
                } else if (brushStatus == 1){
                    strokes.add(new BrushStroke(x, y, colorSel));
                    repaint();
                }
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
            for(int i = 0; i < strokes.size(); ++i){
                myBrush.startStroke(strokes.get(i).getXval(), strokes.get(i).getYval(), strokes.get(i).getColor());
                int x1 = strokes.get(i).getXval();
                int y1 = strokes.get(i).getYval();

                if(i > 0){
                    int x2 = strokes.get(i - 1).getXval();       
                    int y2 = strokes.get(i - 1).getYval();
                    if(Math.abs(x1 - x2) <= 1 || Math.abs(y1 - y2) <= 5){
                        int x3 = (x1 + x2) / 2;
                        int y3 = (y1 + y2) / 2;
                        myBrush.startStroke(x3, y3, strokes.get(i).getColor());
                        if(((x3 + x1) / 2 <= 5 || (x3 + x2) / 2 <= 5) ||((y1 + y3)/2 <= 5 || (y2 + y3) / 2 <= 5)){
                            int x4 = (x2 + x3)/2;
                            int x5 = (x1 + x3)/2;
                            int y4 = (y2 + y3)/2;
                            int y5 = (y1 + y3)/2;
                            myBrush.startStroke(x5, y5, colorSel);
                            myBrush.startStroke(x4, y4, colorSel);
                        
                    }
                }
            }
        }
    }
}
