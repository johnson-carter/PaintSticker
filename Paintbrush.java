import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.lang.Math;

class Paintbrush {
    private Graphics g;
    public Paintbrush(Graphics graphics){
        g = graphics;
    } 



    public void addSticker(int xCord, int yCord, String sticker){
        yCord = Math.abs(yCord - 720);
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
            g.fillRect(xCord, yCord, 20, 60);
            Polygon leaves = new Polygon();
            leaves.addPoint(xCord + 40, yCord );
            leaves.addPoint(xCord - 20, yCord);
            leaves.addPoint(xCord+ 10, yCord - 120);
            g.setColor(new Color(01,32,20));
            g.fillPolygon(leaves);
            leaves.reset();
            leaves.addPoint(xCord + 40, yCord -30);
            leaves.addPoint(xCord - 20, yCord -30);
            leaves.addPoint(xCord+ 10, yCord - 150);
            g.fillPolygon(leaves);
        }
        else if (sticker.equals("Mountain")){
            g.setColor(Color.darkGray);
            Polygon mountain = new Polygon();
            mountain.addPoint(xCord - 100, yCord + 50);
            mountain.addPoint( xCord + 100, yCord + 50);
            mountain.addPoint(xCord, yCord - 150);
            g.fillPolygon(mountain);
            mountain.reset();
            g.setColor(Color.white);
            mountain.addPoint(xCord - 20, yCord - 110);
            mountain.addPoint(xCord, yCord - 122);
            mountain.addPoint(xCord + 5, yCord - 115);
            mountain.addPoint( xCord + 15, yCord - 120);
            mountain.addPoint(xCord, yCord - 150);
            g.fillPolygon(mountain);
        }
        else if (sticker.equals("Bird")){      
            g.setColor(Color.red);
            g.fillOval(xCord, yCord, 40, 20);

    // Head
            g.setColor(Color.red);
            g.fillOval(xCord + 30, yCord - 10, 20, 20);

    // Beak
            g.setColor(Color.ORANGE);
            int[] xPoints = {xCord + 50, xCord + 60, xCord + 50};
            int[] yPoints = {yCord, yCord + 5, yCord + 10};
            g.fillPolygon(xPoints, yPoints, 3);

        }
        else if (sticker.equals("Moose")){
            g.setColor(new Color(59, 29, 0));
            g.fillRect(xCord - 80, yCord + 35, 80, 35);
            g.setColor(Color.lightGray);
            g.fillRect(xCord - 15, yCord + 5, 30, 18);
        }
        else if (sticker.equals("Flower")){

        } else {

        }
    }















    public void setBackground(){
        g.setColor(new Color(61, 61, 144));
        g.fillRect(0, 0, 1080, 720);
        g.setColor(new Color(0,78,24));
        g.fillRect(0, 590, 1080, 130);
    }
}
