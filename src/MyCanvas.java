import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

    // Diameter, in unzoomed image-space pixels, of the resize handle drawn at
    // a pending sticker's corner and used to hit-test drag start.
    private static final int STICKER_HANDLE_SIZE = 14;

    // These all receive a corresponding value from toolkit inputs
    private int brushStatus = 1; // 1 = paintbrush, 2 = eraser, etc.
    private int size = 15;
    private Color colorSel = Color.black;
    private BufferedImage backgroundImage = null;
    // null (or alpha 0) means "transparent canvas" - see isBackgroundTransparent().
    private Color backgroundColor = Color.white;
    private double zoom = 1.0;

    // Layer stack, ordered bottom (index 0) to top (last index) - this is
    // also the order they're composited in. Layers are identified by a
    // stable id rather than position so history actions and the active-layer
    // pointer stay valid across reordering.
    private final List<Layer> layers = new ArrayList<>();
    private int nextLayerId = 1;
    private int activeLayerId;

    // Global undo/redo history. Each action is tagged with the layer it
    // belongs to, so undoing/redoing only ever has to rebuild that one
    // layer's raster rather than replaying everything. Starting a new
    // action always invalidates the redo stack, same as any standard
    // undo/redo implementation.
    private final List<CanvasAction> history = new ArrayList<>();
    private final List<CanvasAction> redoStack = new ArrayList<>();
    private List<BrushStroke> currentGroupStrokes = new ArrayList<>();

    // For line tool preview
    private Point linePreviewStart = null;
    private Point linePreviewEnd = null;
    private Color linePreviewColor = null;
    private int linePreviewSize = 1;

    // Sticker placement state - a sticker being positioned/scaled before
    // it's baked onto the active layer. Coordinates are unzoomed image-space,
    // same as everything else on this canvas.
    private BufferedImage pendingSticker;
    private boolean stickerAwaitingAnchor;
    private boolean stickerAnchored;
    private double stickerX, stickerY, stickerW, stickerH;
    private boolean stickerResizeDrag;
    private boolean stickerMoveDrag;
    private double stickerMoveGrabDX, stickerMoveGrabDY;

    public MyCanvas() {
        Layer initial = new Layer(nextLayerId++, "Layer 1", null);
        layers.add(initial);
        activeLayerId = initial.getId();
    }

    /////////////////////////
    // Methods for generating content from App.java controlled inputs
    /////////////////////////

    //These next methods take input from App.java MouseEvent and accordingly create brush strokes
    public void startNewGroup(){
        currentGroupStrokes = new ArrayList<>();
        history.add(new CanvasAction(activeLayerId, currentGroupStrokes));
        redoStack.clear();
    }
    //When the mouse is dragged it records a series of x, y pairs and adds them to our List of Lists
    public void newStroke(int x, int y){
        Color paintCol = colorSel;
    	if(brushStatus == 1 || brushStatus == 2) { // 1 = paintbrush, 2 = eraser
            if (brushStatus == 2) { // If eraser, set color to transparent
                paintCol = new Color(0, 0, 0, 0); // Transparent color
            }
            BrushStroke currentStroke = new BrushStroke(x, y, paintCol, size);
            currentGroupStrokes.add(currentStroke);
            paintOnActiveLayer(currentStroke);
            //Interpolation algorithm will run with >1 point present
            if (currentGroupStrokes.size() >= 2) {
                BrushStroke prev = currentGroupStrokes.get(currentGroupStrokes.size() - 2);
                BrushStroke curr = currentStroke;

                double distance = Math.hypot(curr.getXval() - prev.getXval(), curr.getYval() - prev.getYval());
                if (distance > 100) return;

                for (double i = 0; i <= distance; i += 1.0) {
                    double t = i / distance;
                    int interpX = (int) Math.round(prev.getXval() + t * (curr.getXval() - prev.getXval()));
                    int interpY = (int) Math.round(prev.getYval() + t * (curr.getYval() - prev.getYval()));
                    BrushStroke interp = new BrushStroke(interpX, interpY, curr.getColor(), curr.getSize());
                    currentGroupStrokes.add(interp);
                    paintOnActiveLayer(interp);
                }
            }
        }
    }

    private Layer getActiveLayer() {
        Layer found = findLayer(activeLayerId);
        return found != null ? found : layers.get(layers.size() - 1);
    }

    private Layer findLayer(int id) {
        for (Layer l : layers) {
            if (l.getId() == id) return l;
        }
        return null;
    }

    // Bake one stroke onto the active layer immediately (O(1) per stroke)
    // instead of waiting to replay the whole history on the next repaint.
    private void paintOnActiveLayer(BrushStroke stroke) {
        Layer active = getActiveLayer();
        ensureLayerCapacity(active);
        Graphics2D layerG = active.getImage().createGraphics();
        new Paintbrush(layerG).drawStroke(stroke);
        layerG.dispose();
    }

    private int neededWidth() {
        if (backgroundImage != null) return backgroundImage.getWidth();
        return Math.max(1, (int) Math.ceil(getWidth() / zoom));
    }

    private int neededHeight() {
        if (backgroundImage != null) return backgroundImage.getHeight();
        return Math.max(1, (int) Math.ceil(getHeight() / zoom));
    }

    // (Re)allocate a layer's raster to at least the currently-needed size,
    // growing it (and preserving existing pixels) rather than shrinking it.
    private void ensureLayerCapacity(Layer layer) {
        int neededW = neededWidth();
        int neededH = neededHeight();
        if (layer.getImage() == null) {
            layer.setImage(new BufferedImage(neededW, neededH, BufferedImage.TYPE_INT_ARGB));
            return;
        }
        BufferedImage img = layer.getImage();
        if (img.getWidth() < neededW || img.getHeight() < neededH) {
            int newW = Math.max(img.getWidth(), neededW);
            int newH = Math.max(img.getHeight(), neededH);
            BufferedImage grown = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = grown.createGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
            layer.setImage(grown);
        }
    }

    private void ensureAllLayersCapacity() {
        for (Layer l : layers) {
            ensureLayerCapacity(l);
        }
    }

    // Hard reset: allocate a blank raster at the given size and replay this
    // layer's slice of the history onto it. Only used for rare, discrete
    // events (undo, redo, explicit canvas resize, new background image,
    // layer deletion) - never per-frame.
    private void rebuildLayer(Layer layer, int width, int height) {
        BufferedImage img = new BufferedImage(Math.max(1, width), Math.max(1, height), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        Paintbrush brush = new Paintbrush(g);
        for (CanvasAction action : history) {
            if (action.getLayerId() != layer.getId()) continue;
            if (action.isSticker()) {
                g.drawImage(action.getStickerImage(), action.getStickerX(), action.getStickerY(),
                        action.getStickerW(), action.getStickerH(), null);
            } else {
                for (BrushStroke stroke : action.getStrokes()) {
                    brush.drawStroke(stroke);
                }
            }
        }
        g.dispose();
        layer.setImage(img);
    }

    private void rebuildLayerAuto(Layer layer) {
        ensureLayerCapacity(layer);
        BufferedImage img = layer.getImage();
        rebuildLayer(layer, img.getWidth(), img.getHeight());
    }

    //These all perform some action corresponding to their button
    public void undoAction(){
        if (!history.isEmpty()) {
            CanvasAction popped = history.remove(history.size() - 1);
            redoStack.add(popped);
            Layer layer = findLayer(popped.getLayerId());
            if (layer != null) rebuildLayerAuto(layer);
            repaint();
        }
    }

    public void redoAction(){
        if (redoStack.isEmpty()) return;
        CanvasAction action = redoStack.remove(redoStack.size() - 1);
        Layer layer = findLayer(action.getLayerId());
        if (layer == null) return; // its layer was deleted since the undo - nothing to redo onto
        history.add(action);
        rebuildLayerAuto(layer);
        repaint();
    }

    //Currently ran by the new file button, but may be implemented differently in the future
    public void clearAll(){
        history.clear();
        redoStack.clear();
        layers.clear();
        Layer fresh = new Layer(nextLayerId++, "Layer 1", null);
        layers.add(fresh);
        activeLayerId = fresh.getId();
        cancelStickerPlacement();
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
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    public boolean isBackgroundTransparent() {
        return backgroundColor == null || backgroundColor.getAlpha() == 0;
    }
    public void setCanvasSize(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        for (Layer l : layers) {
            rebuildLayer(l, width, height);
        }
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
        ensureAllLayersCapacity();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.scale(zoom, zoom);
        Paintbrush myBrush = new Paintbrush(g2);
        if (isBackgroundTransparent()) {
            myBrush.drawCheckerboard(getWidth(), getHeight(), 10);
        } else {
            myBrush.drawBackgroundRect(backgroundColor, getWidth(), getHeight());
        }
        if(backgroundImage != null){
            g2.drawImage(backgroundImage, 0, 0, this);
        }
        for (Layer l : layers) {
            if (l.isVisible() && l.getImage() != null) {
                g2.drawImage(l.getImage(), 0, 0, this);
            }
        }
        // Draw line preview if active
        if (linePreviewStart != null && linePreviewEnd != null) {
            g2.setColor(linePreviewColor != null ? linePreviewColor : Color.black);
            g2.setStroke(new java.awt.BasicStroke(linePreviewSize));
            g2.drawLine(linePreviewStart.x, linePreviewStart.y, linePreviewEnd.x, linePreviewEnd.y);
        }
        if (pendingSticker != null) {
            Composite old = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
            g2.drawImage(pendingSticker, (int) Math.round(stickerX), (int) Math.round(stickerY),
                    (int) Math.round(stickerW), (int) Math.round(stickerH), this);
            g2.setComposite(old);
            if (stickerAnchored) {
                g2.setColor(Color.magenta);
                g2.setStroke(new BasicStroke(Math.max(1f, (float) (1.5 / zoom)), BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 10f, new float[]{6f, 4f}, 0f));
                g2.drawRect((int) Math.round(stickerX), (int) Math.round(stickerY),
                        (int) Math.round(stickerW), (int) Math.round(stickerH));
                g2.setStroke(new BasicStroke(1f));
                int hs = STICKER_HANDLE_SIZE;
                g2.fillRect((int) Math.round(stickerX + stickerW - hs / 2.0),
                        (int) Math.round(stickerY + stickerH - hs / 2.0), hs, hs);
            }
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
                for (Layer l : layers) {
                    rebuildLayer(l, backgroundImage.getWidth(), backgroundImage.getHeight());
                }
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
	    // on-screen zoomed pixel size - the layers already hold the baked
	    // content at that resolution, so we just composite the stack.
	    ensureAllLayersCapacity();
	    BufferedImage first = layers.get(0).getImage();
	    int w = first.getWidth(), h = first.getHeight();
	    BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = out.createGraphics();
	    // Draw background color rectangle (skipped entirely when transparent,
	    // so the exported PNG keeps real alpha rather than a checkerboard).
	    Paintbrush exportBrush = new Paintbrush(g2);
	    if (!isBackgroundTransparent()) {
	        exportBrush.drawBackgroundRect(backgroundColor, w, h);
	    }
	    // draw background image if present
	    if (backgroundImage != null) {
	        g2.drawImage(backgroundImage, 0, 0, null);
	    }
	    for (Layer l : layers) {
	        if (l.isVisible() && l.getImage() != null) {
	            g2.drawImage(l.getImage(), 0, 0, null);
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

    // Add this method back to support text strokes
    public void addTextStroke(int x, int y, String text) {
        BrushStroke textStroke = new BrushStroke(x, y, colorSel, size, text);
        List<BrushStroke> textGroup = new ArrayList<>();
        textGroup.add(textStroke);
        history.add(new CanvasAction(activeLayerId, textGroup));
        redoStack.clear();
        paintOnActiveLayer(textStroke);
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
        history.add(new CanvasAction(activeLayerId, lineGroup));
        redoStack.clear();
        for (BrushStroke s : lineGroup) {
            paintOnActiveLayer(s);
        }
        repaint();
    }

    /////////////////////////
    // Layers
    /////////////////////////

    // A defensive copy: callers (LayersDialog) only ever read this to build
    // their own UI state, so handing out the live list would let them
    // reorder/mutate it without going through the methods below.
    public List<Layer> getLayers() {
        return new ArrayList<>(layers);
    }

    public int getActiveLayerId() {
        return activeLayerId;
    }

    public void setActiveLayer(int id) {
        if (findLayer(id) != null) {
            activeLayerId = id;
        }
    }

    public Layer addLayer(String name) {
        BufferedImage img = new BufferedImage(neededWidth(), neededHeight(), BufferedImage.TYPE_INT_ARGB);
        Layer layer = new Layer(nextLayerId++, name, img);
        layers.add(layer);
        activeLayerId = layer.getId();
        repaint();
        return layer;
    }

    public void duplicateLayer(int id) {
        Layer src = findLayer(id);
        if (src == null || src.getImage() == null) return;
        BufferedImage copy = new BufferedImage(src.getImage().getWidth(), src.getImage().getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = copy.createGraphics();
        g.drawImage(src.getImage(), 0, 0, null);
        g.dispose();
        Layer layer = new Layer(nextLayerId++, src.getName() + " copy", copy);
        int idx = layers.indexOf(src);
        layers.add(idx + 1, layer);
        activeLayerId = layer.getId();
        repaint();
    }

    public void removeLayer(int id) {
        if (layers.size() <= 1) return; // always keep at least one layer
        Layer layer = findLayer(id);
        if (layer == null) return;
        layers.remove(layer);
        history.removeIf(a -> a.getLayerId() == id);
        redoStack.removeIf(a -> a.getLayerId() == id);
        if (activeLayerId == id) {
            activeLayerId = layers.get(layers.size() - 1).getId();
        }
        repaint();
    }

    public void setLayerVisible(int id, boolean visible) {
        Layer l = findLayer(id);
        if (l != null) {
            l.setVisible(visible);
            repaint();
        }
    }

    public void renameLayer(int id, String name) {
        Layer l = findLayer(id);
        if (l != null) {
            l.setName(name);
        }
    }

    public void moveLayerUp(int id) {
        int idx = indexOfLayer(id);
        if (idx >= 0 && idx < layers.size() - 1) {
            Collections.swap(layers, idx, idx + 1);
            repaint();
        }
    }

    public void moveLayerDown(int id) {
        int idx = indexOfLayer(id);
        if (idx > 0) {
            Collections.swap(layers, idx, idx - 1);
            repaint();
        }
    }

    private int indexOfLayer(int id) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).getId() == id) return i;
        }
        return -1;
    }

    /////////////////////////
    // Stickers
    /////////////////////////

    // Begin placing a sticker: it follows the mouse (see
    // updateStickerPreviewPosition) until the first click anchors it.
    public void beginStickerPlacement(BufferedImage img) {
        this.pendingSticker = img;
        double maxDim = 160;
        double scale = Math.min(1.0, maxDim / Math.max(img.getWidth(), img.getHeight()));
        this.stickerW = img.getWidth() * scale;
        this.stickerH = img.getHeight() * scale;
        this.stickerX = -stickerW * 2; // off-canvas until the mouse moves over it
        this.stickerY = -stickerH * 2;
        this.stickerAwaitingAnchor = true;
        this.stickerAnchored = false;
        repaint();
    }

    public boolean isStickerAwaitingAnchor() {
        return stickerAwaitingAnchor;
    }

    public boolean isStickerAnchored() {
        return stickerAnchored;
    }

    public void updateStickerPreviewPosition(Point p) {
        if (!stickerAwaitingAnchor) return;
        stickerX = p.x - stickerW / 2.0;
        stickerY = p.y - stickerH / 2.0;
        repaint();
    }

    public void anchorStickerAt(Point p) {
        if (!stickerAwaitingAnchor) return;
        stickerX = p.x - stickerW / 2.0;
        stickerY = p.y - stickerH / 2.0;
        stickerAwaitingAnchor = false;
        stickerAnchored = true;
        repaint();
    }

    public boolean isPointOnStickerHandle(Point p) {
        if (!stickerAnchored) return false;
        double hx = stickerX + stickerW, hy = stickerY + stickerH;
        double half = STICKER_HANDLE_SIZE;
        return Math.abs(p.x - hx) <= half && Math.abs(p.y - hy) <= half;
    }

    public boolean isPointInsideSticker(Point p) {
        return stickerAnchored && p.x >= stickerX && p.x <= stickerX + stickerW
                && p.y >= stickerY && p.y <= stickerY + stickerH;
    }

    public void beginStickerResize() {
        stickerResizeDrag = true;
    }

    public void beginStickerMove(Point p) {
        stickerMoveDrag = true;
        stickerMoveGrabDX = p.x - stickerX;
        stickerMoveGrabDY = p.y - stickerY;
    }

    public boolean isStickerResizeDragging() {
        return stickerResizeDrag;
    }

    public boolean isStickerMoveDragging() {
        return stickerMoveDrag;
    }

    public void endStickerDrag() {
        stickerResizeDrag = false;
        stickerMoveDrag = false;
    }

    public void moveStickerTo(Point p) {
        if (!stickerAnchored) return;
        stickerX = p.x - stickerMoveGrabDX;
        stickerY = p.y - stickerMoveGrabDY;
        repaint();
    }

    // Uniform scale, anchored at the sticker's top-left corner, driven by
    // dragging the bottom-right handle - width leads, height follows the
    // original image's aspect ratio.
    public void resizeStickerTo(Point p) {
        if (!stickerAnchored || pendingSticker == null) return;
        double newW = Math.max(8, p.x - stickerX);
        double aspect = (double) pendingSticker.getWidth() / pendingSticker.getHeight();
        stickerW = newW;
        stickerH = newW / aspect;
        repaint();
    }

    public void confirmStickerPlacement() {
        if (!stickerAnchored || pendingSticker == null) return;
        int x = (int) Math.round(stickerX);
        int y = (int) Math.round(stickerY);
        int w = Math.max(1, (int) Math.round(stickerW));
        int h = Math.max(1, (int) Math.round(stickerH));
        Layer active = getActiveLayer();
        ensureLayerCapacity(active);
        Graphics2D g = active.getImage().createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(pendingSticker, x, y, w, h, null);
        g.dispose();
        history.add(new CanvasAction(active.getId(), pendingSticker, x, y, w, h));
        redoStack.clear();
        cancelStickerPlacement();
    }

    public void cancelStickerPlacement() {
        pendingSticker = null;
        stickerAwaitingAnchor = false;
        stickerAnchored = false;
        stickerResizeDrag = false;
        stickerMoveDrag = false;
        repaint();
    }

}
