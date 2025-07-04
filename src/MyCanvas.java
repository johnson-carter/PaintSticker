package src;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/*      
 *  MyCanvas Class - Interprets inputs and uses Paintbrush.java to 
 *  display all of the great artwork that your heart desires
 */


class MyCanvas extends JPanel {

    ///////////////////////
    /// Fields
    ///////////////////////
    

    /**
	 * 
	 */
	private static final long serialVersionUID = -1695609604884035420L;
	// These all receive a corresponding value from toolkit inputs
    private List<List<BrushStroke>> totalStrokes = new ArrayList<>();
    private List<BrushStroke> strokes = new ArrayList<>();
    private int state = 1;
    private int brushStatus = 1;
    private int size = 15;
    private Color colorSel = Color.black;
    private BufferedImage backgroundImage = null;

    public void importImage() {
	    JFileChooser chooser = new JFileChooser();
	    chooser.setFileFilter(new FileNameExtensionFilter(
	        "Image files", ImageIO.getReaderFileSuffixes()));
	    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
	        try {
	            File file = chooser.getSelectedFile();
	            backgroundImage = ImageIO.read(file);
	            // resize canvas if you like:
	            setPreferredSize(new Dimension(
	                backgroundImage.getWidth(),
	                backgroundImage.getHeight()));
	            revalidate();
	            repaint();
	        } catch (IOException ex) {
	            JOptionPane.showMessageDialog(this,
	                "Failed to load image:\n" + ex.getMessage(),
	                "Load Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}
	
	public void exportImage() {
	    // create a combined image
	    int w = getWidth(), h = getHeight();
	    BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = out.createGraphics();
	    // draw background
	    if (backgroundImage != null) {
	        g2.drawImage(backgroundImage, 0, 0, null);
	    } else {
	        // optional: fill with white
	        g2.setColor(Color.WHITE);
	        g2.fillRect(0, 0, w, h);
	    }
	    // draw your strokes
	    // you can reuse your Paintbrush or draw directly:
	    for (List<BrushStroke> group : totalStrokes) {
	        for (BrushStroke s : group) {
	            g2.setColor(s.getColor());
	            g2.fillOval(s.getXval(), s.getYval(), s.getSize(), s.getSize());
	        }
	    }
	    g2.dispose();
	
	    // save to file
	    JFileChooser chooser = new JFileChooser();
	    chooser.setFileFilter(new FileNameExtensionFilter("PNG Image", "png"));
	    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
	        File file = chooser.getSelectedFile();
	        if (!file.getName().toLowerCase().endsWith(".png")) {
	            file = new File(file.getAbsolutePath() + ".png");
	        }
	        try {
	            ImageIO.write(out, "PNG", file);
	        } catch (IOException ex) {
	            JOptionPane.showMessageDialog(this,
	                "Failed to save image:\n" + ex.getMessage(),
	                "Save Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}

    /////////////////////////
    // Methods for generating content from App.java controlled inputs
    /////////////////////////
     
    //These next methods take input from App.java MouseEvent and accordingly create brush strokes
    public void startNewGroup(){
        strokes = new ArrayList<>();
        totalStrokes.add(strokes);

    }
    //When the mouse is dragged it records a series of x,y pairs and adds them to our List of Lists
    public void newStroke(int x, int y, int brush){
        Color paintCol = colorSel;
    	if(brush == 1) {
            BrushStroke currentStroke = new BrushStroke(x, y, paintCol, size);
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
        if(backgroundImage != null){
            g.drawImage(backgroundImage, 0, 0, this);
        }
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
        
    
        
    

