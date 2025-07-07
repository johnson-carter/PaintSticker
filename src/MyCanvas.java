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


public class MyCanvas extends JPanel {

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
    private int brushStatus = 1; // 1 = paintbrush, 2 = eraser, etc.
    private int size = 15;
    private Color colorSel = Color.black;
    private BufferedImage backgroundImage = null;
    private Color backgroundColor = Color.white;

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
	    int w = getWidth(), h = getHeight();
	    BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = out.createGraphics();
	    // Draw background color rectangle
	    Paintbrush exportBrush = new Paintbrush(g2);
	    exportBrush.drawBackgroundRect(backgroundColor, w, h);
	    // draw background image if present
	    if (backgroundImage != null) {
	        g2.drawImage(backgroundImage, 0, 0, null);
	    }
	    // do NOT fill with white here, so transparency is preserved

	    // draw your strokes
	    for (List<BrushStroke> group : totalStrokes) {
	        for (BrushStroke s : group) {
	            Color c = s.getColor();
	            if (c == null || c.getAlpha() == 0) {
	                // Eraser: clear with AlphaComposite
	                g2.setComposite(java.awt.AlphaComposite.Clear);
	                g2.fillOval(s.getXval(), s.getYval(), s.getSize(), s.getSize());
	                g2.setComposite(java.awt.AlphaComposite.SrcOver);
	            } else {
	                g2.setComposite(java.awt.AlphaComposite.SrcOver);
	                g2.setColor(c);
	                g2.fillOval(s.getXval(), s.getYval(), s.getSize(), s.getSize());
	            }
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
    public void newStroke(int x, int y){
        Color paintCol = colorSel;
    	if(brushStatus == 1 || brushStatus == 2) { // 1 = paintbrush, 2 = eraser
            if (brushStatus == 2) { // If eraser, set color to transparent
                paintCol = new Color(0, 0, 0, 0); // Transparent color
            }
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
    public void setBrushMode(int status){
        brushStatus = status; // Clarification on brush state - Built it into sooner rather than later as a way to track what the active brush mode is.
    }
    public void setColorChosen(Color color){
        colorSel = color;
    }
    public void chooseSize(int size){
        this.size = size;
    } 
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }
    
    //Constructor, could add arguments? idk why
    public MyCanvas(){
        setPreferredSize(new Dimension(1080, 720));
        setBackground(Color.black);
    }

    @Override
    // Interesting component here, responsible for updating graphics via methods called here to Paintbrush.java
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Paintbrush myBrush = new Paintbrush(g);
        // Draw background color rectangle
        myBrush.drawBackgroundRect(backgroundColor, getWidth(), getHeight());
        if(backgroundImage != null){
            g.drawImage(backgroundImage, 0, 0, this);
        } else {
            // Default background color if no image is set
            // (now handled by drawBackgroundRect)
        }
        myBrush.drawStrokes(totalStrokes);
    }      

        }






