import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

class Paintbrush {
    private Graphics g;
    public Paintbrush(Graphics graphics){
        g = graphics;
    } 



    public void addSticker(int xCord, int yCord, String sticker){
        if (sticker.equals("Rock")){
            g.setColor(new Color(64, 64, 64));
            Polygon top = new Polygon();
            Polygon bottom = new Polygon();
            top.addPoint(xCord - 20, yCord + 15);
            top.addPoint( xCord + 10, yCord + 10);
            top.addPoint(xCord + 8, yCord);
            bottom.addPoint(xCord - 20, yCord + 15);
            bottom.addPoint(xCord - 15, yCord - 10);
            bottom.addPoint(xCord + 8, yCord);
            g.fillPolygon(top);
            g.setColor(new Color(128, 128, 128));
            g.fillPolygon(bottom);
        }
        else if (sticker.equals("Tree")){
            g.setColor(new Color(59, 29, 0));
            g.fillRect(500, 350, 40, 120);
            Polygon leaves = new Polygon();
            leaves.addPoint(480, 400);
            leaves.addPoint(560, 400);
            leaves.addPoint(520, 600);
            g.setColor(Color.green);
            g.fillPolygon(leaves);
        }
        else if (sticker.equals("Mountain")){

        }
        else if (sticker.equals("Bird")){

        }
        else if (sticker.equals("Moose")){
            g.setColor(new Color(59, 29, 0));
            g.fillRect(150, 20, 80, 35);
            g.setColor(Color.lightGray);
            g.fillRect(200, 55, 30, 18);
        }
        else if (sticker.equals("Flower")){

        } else {

        }
    }















    public void setBackground(){
        g.setColor(new Color(61, 61, 144));
        g.fillRect(0, 0, 1080, 720);
    }
    public void drawTree(){
        g.setColor(new Color(59, 29, 0));
        g.fillRect(500, 350, 40, 120);
        Polygon leaves = new Polygon();
        leaves.addPoint(480, 400);
        leaves.addPoint(560, 400);
        leaves.addPoint(520, 600);
        g.setColor(Color.green);
        g.fillPolygon(leaves);
    }
    public void drawMoose(){
        g.setColor(new Color(59, 29, 0));
        g.fillRect(150, 20, 80, 35);
        g.setColor(Color.lightGray);
        g.fillRect(200, 55, 30, 18);
    }
    public void drawRock(int xCord, int yCord){
        
        g.setColor(new Color(64, 64, 64));
        Polygon top = new Polygon();
        Polygon bottom = new Polygon();
        top.addPoint(xCord - 20, yCord + 15);
        top.addPoint( xCord + 10, yCord + 10);
        top.addPoint(xCord + 8, yCord);
        bottom.addPoint(xCord - 20, yCord + 15);
        bottom.addPoint(xCord - 15, yCord - 10);
        bottom.addPoint(xCord + 8, yCord);
        g.fillPolygon(top);
        g.setColor(new Color(128, 128, 128));
        g.fillPolygon(bottom);
    }
}
