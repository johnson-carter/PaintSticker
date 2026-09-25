import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
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
    private int brushStatus = 1; // 1 = paintbrush, 2 = eraser, etc.
    private int size = 15;
    private Color colorSel = Color.black;
    private BufferedImage backgroundImage = null;
    private Color backgroundColor = Color.white;
    private double zoom = 1.0;

    // Committed strokes, baked in once and reused every frame instead of being
    // replayed from totalStrokes on every repaint. Lives in the same unzoomed
    // "image space" as backgroundImage, so zoom changes never require a rebuild.
    // Must be TYPE_INT_ARGB: erasing relies on AlphaComposite.Clear actually
    // zeroing alpha, which only works on a surface with a real alpha channel.
    private BufferedImage committedLayer;

    // For line tool preview
    private Point linePreviewStart = null;
    private Point linePreviewEnd = null;
    private Color linePreviewColor = null;
    private int linePreviewSize = 1;

    /////////////////////////
    // Methods for generating content from App.java controlled inputs
    /////////////////////////
     
    //These next methods take input from App.java MouseEvent and accordingly create brush strokes
    public void startNewGroup(){
        strokes = new ArrayList<>();
        totalStrokes.add(strokes);
    }
    //When the mouse is dragged it records a series of x, y pairs and adds them to our List of Lists
    public void newStroke(int x, int y){
        Color paintCol = colorSel;
    	if(brushStatus == 1 || brushStatus == 2) { // 1 = paintbrush, 2 = eraser
            if (brushStatus == 2) { // If eraser, set color to transparent
                paintCol = new Color(0, 0, 0, 0); // Transparent color
            }
            BrushStroke currentStroke = new BrushStroke(x, y, paintCol, size);
            strokes.add(currentStroke);
            paintOnLayer(currentStroke);
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
                    BrushStroke interp = new BrushStroke(interpX, interpY, curr.getColor(), curr.getSize());
                    strokes.add(interp);
                    paintOnLayer(interp);
                }
            }
        }
    }

    // Bake one stroke onto the committed layer immediately (O(1) per stroke)
    // instead of waiting to replay the whole history on the next repaint.
    private void paintOnLayer(BrushStroke stroke) {
        ensureLayerCapacity();
        Graphics2D layerG = committedLayer.createGraphics();
        new Paintbrush(layerG).drawStroke(stroke);
        layerG.dispose();
    }

    // (Re)allocate the committed layer to at least the currently-needed size,
    // growing it (and preserving existing pixels) rather than shrinking it.
    private void ensureLayerCapacity() {
        if (backgroundImage != null) {
            if (committedLayer == null) {
                rebuildLayer(backgroundImage.getWidth(), backgroundImage.getHeight());
            }
            return;
        }
        int neededW = Math.max(1, (int) Math.ceil(getWidth() / zoom));
        int neededH = Math.max(1, (int) Math.ceil(getHeight() / zoom));
        if (committedLayer == null) {
            rebuildLayer(neededW, neededH);
        } else if (committedLayer.getWidth() < neededW || committedLayer.getHeight() < neededH) {
            int newW = Math.max(committedLayer.getWidth(), neededW);
            int newH = Math.max(committedLayer.getHeight(), neededH);
            BufferedImage old = committedLayer;
            committedLayer = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D layerG = committedLayer.createGraphics();
            layerG.drawImage(old, 0, 0, null);
            layerG.dispose();
        }
    }

    // Hard reset: allocate a blank layer at the given size and replay the full
    // stroke history onto it. Only used for rare, discrete events (undo,
    // explicit canvas resize, new background image) - never per-frame.
    private void rebuildLayer(int width, int height) {
        committedLayer = new BufferedImage(Math.max(1, width), Math.max(1, height), BufferedImage.TYPE_INT_ARGB);
        Graphics2D layerG = committedLayer.createGraphics();
        new Paintbrush(layerG).drawStrokes(totalStrokes);
        layerG.dispose();
    }

    //These all perform some action corresponding to their button
    public void undoAction(){
        if (!totalStrokes.isEmpty()){
            totalStrokes.remove(totalStrokes.size() - 1);
            ensureLayerCapacity();
            rebuildLayer(committedLayer.getWidth(), committedLayer.getHeight());
            repaint();
        }
    }
    //Currently ran by the new file button, but may be implemented differently in the future
    public void clearAll(){
        totalStrokes.clear();
        if (committedLayer != null) {
            rebuildLayer(committedLayer.getWidth(), committedLayer.getHeight());
        }
        repaint();
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
    public void setCanvasSize(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        rebuildLayer(width, height);
        revalidate();
        repaint();
    }
    public void setZoom(double zoom) {
        this.zoom = zoom;
        revalidate();
        repaint();
    }

    public double getZoom() {
        return zoom;
    }

    // Convert screen point to image (unzoomed) coordinates
    public Point toImageCoords(Point p) {
        return new Point((int)(p.x / zoom), (int)(p.y / zoom));
    }

    @Override
    public Dimension getPreferredSize() {
        int w = (int)((backgroundImage != null ? backgroundImage.getWidth() : super.getPreferredSize().width) * zoom);
        int h = (int)((backgroundImage != null ? backgroundImage.getHeight() : super.getPreferredSize().height) * zoom);
        return new Dimension(w, h);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        ensureLayerCapacity();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.scale(zoom, zoom);
        Paintbrush myBrush = new Paintbrush(g2);
        myBrush.drawBackgroundRect(backgroundColor, getWidth(), getHeight());
        if(backgroundImage != null){
            g2.drawImage(backgroundImage, 0, 0, this);
        }
        g2.drawImage(committedLayer, 0, 0, this);
        // Draw line preview if active
        if (linePreviewStart != null && linePreviewEnd != null) {
            g2.setColor(linePreviewColor != null ? linePreviewColor : Color.black);
            g2.setStroke(new java.awt.BasicStroke(linePreviewSize));
            g2.drawLine(linePreviewStart.x, linePreviewStart.y, linePreviewEnd.x, linePreviewEnd.y);
        }
        g2.dispose();
    }      

    // --- When importing, scale down large images to fit workspace ---
    public void importImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter(
            "Image files", ImageIO.getReaderFileSuffixes()));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                BufferedImage img = ImageIO.read(file);
                if (img == null) {
                    JOptionPane.showMessageDialog(this,
                        "Unsupported or corrupt image file.",
                        "Load Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Scale down if too large for screen
                Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                int maxW = (int)(screen.width * 0.8), maxH = (int)(screen.height * 0.8);
                if (img.getWidth() > maxW || img.getHeight() > maxH) {
                    double scale = Math.min((double)maxW / img.getWidth(), (double)maxH / img.getHeight());
                    int newW = (int)(img.getWidth() * scale);
                    int newH = (int)(img.getHeight() * scale);
                    Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
                    BufferedImage scaledImg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = scaledImg.createGraphics();
                    g2.drawImage(scaled, 0, 0, null);
                    g2.dispose();
                    img = scaledImg;
                }
                backgroundImage = img;
                rebuildLayer(backgroundImage.getWidth(), backgroundImage.getHeight());
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
	    // Export at the canvas's native (unzoomed) resolution, not the current
	    // on-screen zoomed pixel size - the committed layer already holds the
	    // baked strokes at that resolution, so we just composite the layers.
	    ensureLayerCapacity();
	    int w = committedLayer.getWidth(), h = committedLayer.getHeight();
	    BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = out.createGraphics();
	    // Draw background color rectangle
	    Paintbrush exportBrush = new Paintbrush(g2);
	    exportBrush.drawBackgroundRect(backgroundColor, w, h);
	    // draw background image if present
	    if (backgroundImage != null) {
	        g2.drawImage(backgroundImage, 0, 0, null);
	    }
	    g2.drawImage(committedLayer, 0, 0, null);
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

    // Add this method back to support text strokes
    public void addTextStroke(int x, int y, String text) {
        BrushStroke textStroke = new BrushStroke(x, y, colorSel, size, text);
        List<BrushStroke> textGroup = new ArrayList<>();
        textGroup.add(textStroke);
        totalStrokes.add(textGroup);
        paintOnLayer(textStroke);
        repaint();
    }

    public void setLinePreview(Point start, Point end, Color color, int size) {
        this.linePreviewStart = start;
        this.linePreviewEnd = end;
        this.linePreviewColor = color;
        this.linePreviewSize = size;
        repaint();
    }
    public void clearLinePreview() {
        this.linePreviewStart = null;
        this.linePreviewEnd = null;
        repaint();
    }
    public Point getLinePreviewStart() {
        return linePreviewStart;
    }

    // Add a method to add a permanent line stroke
    public void addLineStroke(Point start, Point end, Color color, int size) {
        List<BrushStroke> lineGroup = new ArrayList<>();
        // Use Bresenham's line algorithm or simple interpolation
        int x0 = start.x, y0 = start.y, x1 = end.x, y1 = end.y;
        int dx = Math.abs(x1 - x0), dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        while (true) {
            lineGroup.add(new BrushStroke(x0, y0, color, size));
            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x0 += sx; }
            if (e2 < dx) { err += dx; y0 += sy; }
        }
        totalStrokes.add(lineGroup);
        for (BrushStroke s : lineGroup) {
            paintOnLayer(s);
        }
        repaint();
    }

}






