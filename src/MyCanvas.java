package src;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JPanel;

/*      
 *  MyCanvas Class - Interprets inputs and uses Paintbrush.java to 
 *  display all of the great artwork that your heart desires
 */


class MyCanvas extends JPanel {

    ///////////////////////
    /// Fields
    ///////////////////////
    

    // These all recieve a corresponding value from toolkit inputs
    private List<List<BrushStroke>> totalStrokes = new ArrayList<>();
    private List<BrushStroke> strokes = new ArrayList<>();
    private int x = 0;
    private int y = 0;
    private int state = 0;
    private int brushStatus = 0;
    private int size = 2;
    private Color colorSel = Color.black;
    

    /////////////////////////
    // Methods for generating content from App.java controlled inputs
    /////////////////////////
     
    //These next methods take input from App.java MouseEvent and accordingly create brush strokes
    public void startNewGroup(){
        strokes = new ArrayList<>();
        totalStrokes.add(strokes);

    }
    public void newStroke(int x, int y){
        BrushStroke currentStroke = new BrushStroke(x, y, colorSel, size);
        strokes.add(currentStroke);
        if (strokes.size() >= 2) {
            BrushStroke prev = strokes.get(strokes.size() - 2);
            BrushStroke curr = currentStroke;

            if (!prev.getColor().equals(curr.getColor()) || prev.getSize() != curr.getSize()) return;

            double distance = Math.hypot(curr.getXval() - prev.getXval(), curr.getYval() - prev.getYval());
            if (distance > 100) return;

            for (double i = 0; i <= distance; i += 1.0) {
                double t = i / distance;
                int interpX = (int) Math.round(prev.getXval() + t * (curr.getXval() - prev.getXval()));
                int interpY = (int) Math.round(prev.getYval() + t * (curr.getYval() - prev.getYval()));
                strokes.add(new BrushStroke(interpX, interpY, curr.getColor(), curr.getSize()));
                
            }
        }
    }
    
    //These all perform some action corresponding to their button
    public void undoAction(){
        if (!totalStrokes.isEmpty()){
            totalStrokes.remove(totalStrokes.size() - 1);
            repaint();
        }
    }
    public void clearAll(){
        totalStrokes.clear();
        repaint();
    }
    public void setCanvasState(int state){
        this.state = state;     //  Clarification on canvas state - Built it into sooner rather than later as a way to track what the active brush mode is.
    }
    public void setBrushMode(int state){
        brushStatus = state;
    }
    public void setColorChosen(Color color){
        colorSel = color;
    }
    public void chooseSize(int size){
        this.size = size;
    } 
    

    public MyCanvas(){
        setPreferredSize(new Dimension(1080, 720));
        setBackground(Color.white);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        //int height = 1200;
        //int width = 800;
        Paintbrush myBrush = new Paintbrush(g);
        myBrush.setBackground(state);          
        myBrush.drawStrokes(totalStrokes);
        }      
    }
        
    
        
    

