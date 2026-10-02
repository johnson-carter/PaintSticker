import java.awt.image.BufferedImage;

/*
 *      Represents a single paintable layer: its own baked raster plus display
 *      state (name, visibility). Identified by a stable id rather than its
 *      position in the layer stack, so reordering layers never invalidates
 *      references held by history actions (see CanvasAction).
 */
public class Layer {
    private final int id;
    private String name;
    private BufferedImage image;
    private boolean visible = true;

    public Layer(int id, String name, BufferedImage image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
