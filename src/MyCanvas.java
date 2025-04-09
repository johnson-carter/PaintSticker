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
    private int state = 0;
    private int brushStatus = 1;
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
    //When the mouse is dragged it records a series of x,y pairs and adds them to our List of Lists
    public void newStroke(int x, int y){
        BrushStroke currentStroke = new BrushStroke(x, y, colorSel, size);
        strokes.add(currentStroke);
        //Interpolation algorithm will run with >1 point present
        if (strokes.size() >= 2) {
            BrushStroke prev = strokes.get(strokes.size() - 2);
            BrushStroke curr = currentStroke;

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
    //Currently ran by the new file button, but may be implemented differently in the future
    public void clearAll(){
        totalStrokes.clear();
        repaint();
    }
    public void setCanvasState(int state){
        this.state = state;     
    }
    public void setBrushMode(int state){
        brushStatus = state; // Clarification on brush state - Built it into sooner rather than later as a way to track what the active brush mode is.
    }
    public void setColorChosen(Color color){
        colorSel = color;
    }
    public void chooseSize(int size){
        this.size = size;
    } 
    
    //Constructor, could add arguments? idk why
    public MyCanvas(){
        setPreferredSize(new Dimension(1080, 720));
        setBackground(Color.white);
    }

    @Override
    // Interesting component here, responsible for updating graphics via methods called here to Paintbrush.java
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Paintbrush myBrush = new Paintbrush(g);
        myBrush.setBackground(state);    
        /*
            Brush State references:
            0 - Original sky/grass background
            1 - Plain white
            2 - UserUploaded file? Not implemented yet
            Currently the new file and download/fileupload button
            navigates between state 0 and 1. No other control
            at this time
        */      
        myBrush.drawStrokes(totalStrokes);
        }      
    }
        
    
        
    

