package src;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;


class Paintbrush  {
    private final Graphics g;
    //private BufferedImage moose;

    public Paintbrush(Graphics graphics){
        g = graphics;
    } 


// Based on string value passed from parameters, different polygons are displayed
//Eventual change -- Replace with illustrations
// ---- ORIGINAL STICKER LOGIC ---- To be reimplemented later, image based stickers
    /*public void addSticker(int xCord, int yCord, String sticker){
        // yCord = Math.abs(yCord - 720);
        if (sticker.equals("Rock")){
            g.setColor(new Color(128,128,128));
            Polygon top = new Polygon();
            Polygon bottom = new Polygon();
            top.addPoint(xCord -20, yCord - 20);
            top.addPoint( xCord + 5, yCord - 10);
            top.addPoint(xCord + 12, yCord);
            top.addPoint(xCord, yCord);
            bottom.addPoint(xCord - 20, yCord -20);
            bottom.addPoint(xCord - 10, yCord + 15);
            bottom.addPoint(xCord + 8, yCord + 5);
            bottom.addPoint(xCord + 8, yCord);
            bottom.addPoint(xCord, yCord);
            g.fillPolygon(top);
            g.setColor(new Color(64,64,64));
            g.fillPolygon(bottom);
        }
        else if (sticker.equals("Tree")){
            //trunk
            g.setColor(new Color(59, 29, 0));
            g.fillRect(xCord, yCord, 20, 60);
            //leaves at bottom
            Polygon leaves = new Polygon();
            leaves.addPoint(xCord + 40, yCord );
            leaves.addPoint(xCord - 20, yCord);
            leaves.addPoint(xCord+ 10, yCord - 120);
            g.setColor(new Color(01,32,20));
            g.fillPolygon(leaves);
            //upper leaves
            leaves.reset();
            leaves.addPoint(xCord + 40, yCord -30);
            leaves.addPoint(xCord - 20, yCord -30);
            leaves.addPoint(xCord+ 10, yCord - 150);
            g.fillPolygon(leaves);
        }
        else if (sticker.equals("Mountain")){
            g.setColor(Color.darkGray);
            System.out.println("X: " + xCord + " .  Y: " + yCord);
            //mtn base
            Polygon mountain = new Polygon();
            mountain.addPoint(xCord - 100, yCord + 50);
            mountain.addPoint( xCord + 100, yCord + 50);
            mountain.addPoint(xCord, yCord - 150);
            g.fillPolygon(mountain);
            mountain.reset();
            //SNOWCAP ON MOUNTAIN
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
            try {
                moose = ImageIO.read(new File("images/moose.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(moose != null){
                g.drawImage(moose, xCord - 100, yCord - 80, 300,200, null);
            } else {
                g.drawString("No image found", xCord, yCord);
            }
        }
        else if (sticker.equals("Flower")){

            g.setColor(Color.GREEN);
            g.fillRect(xCord + 3, yCord, 5, 40);

    // Petals
            g.setColor(Color.PINK);
            g.fillOval(xCord - 10, yCord - 10, 20, 20);
            g.fillOval(xCord + 5, yCord - 10, 20, 20);
            g.fillOval(xCord - 10, yCord + 5, 20, 20);
            g.fillOval(xCord + 5, yCord + 5, 20, 20);

    // Center
            g.setColor(Color.YELLOW);
            g.fillOval(xCord, yCord, 10, 10);

        } else {

        }
    }*/


    // Erase (make transparent) at the given location/size
    private void eraseAt(int x, int y, int size) {
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillOval(x, y, size, size);
            g2d.dispose();
        }
    }

    public void drawStrokes(List<List<BrushStroke>> inList){
        for (List<BrushStroke> group : inList) {
            for (BrushStroke stroke : group) {
                // Skip strokes at (0,0)
                if (stroke.getXval() == 0 && stroke.getYval() == 0) continue;
                if (stroke.getType() == BrushStroke.TYPE_TEXT) {
                    g.setColor(stroke.getColor());
                    g.setFont(new java.awt.Font("Verdana", java.awt.Font.BOLD, stroke.getSize() * 2));
                    g.drawString(stroke.getText(), stroke.getXval(), stroke.getYval());
                } else {
                    Color c = stroke.getColor();
                    if (c == null || (c.getAlpha() == 0)) {
                        eraseAt(stroke.getXval() - (stroke.getSize() / 2), stroke.getYval() - (stroke.getSize() / 2), stroke.getSize());
                    } else {
                        g.setColor(c);
                        g.fillOval(stroke.getXval() - (stroke.getSize() / 2), stroke.getYval() - (stroke.getSize() / 2), stroke.getSize(), stroke.getSize());
                    }
                }
            }
        }
    }

    // Draw a background rectangle of the given color
    public void drawBackgroundRect(Color color, int width, int height) {
        if (color != null && g != null) {
            Color old = g.getColor();
            g.setColor(color);
            g.fillRect(0, 0, width, height);
            g.setColor(old);
        }
    }

    public void setBackground(int state){
        if (state == 0){
            g.setColor(new Color(61, 61, 144));
            g.fillRect(0, 0, 1920, 1080);        //Defines sky backdrop
            g.setColor(new Color(0,78,24));
            g.fillRect(0, 590, 1920, 490);      //Defines the grass
        }
        else if (state == 1){
            g.setColor(new Color(250, 250, 250));
            g.fillRect(0, 0, 1920, 1080);
        }
    }
}

