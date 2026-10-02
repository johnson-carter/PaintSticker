import java.awt.image.BufferedImage;
import java.util.List;

/*
 *      One undoable/redoable unit of history. Every action targets exactly
 *      one layer (by id, see Layer) so undo/redo only ever has to rebuild
 *      that single layer's raster instead of the whole stack.
 *
 *      Two shapes of action exist:
 *        - a stroke group (brush/eraser/text/line), replayed with Paintbrush
 *        - a placed sticker, replayed as a single scaled drawImage
 */
public class CanvasAction {
    private final int layerId;
    private final List<BrushStroke> strokes;
    private final BufferedImage stickerImage;
    private final int stickerX, stickerY, stickerW, stickerH;

    public CanvasAction(int layerId, List<BrushStroke> strokes) {
        this.layerId = layerId;
        this.strokes = strokes;
        this.stickerImage = null;
        this.stickerX = this.stickerY = this.stickerW = this.stickerH = 0;
    }

    public CanvasAction(int layerId, BufferedImage stickerImage, int x, int y, int w, int h) {
        this.layerId = layerId;
        this.strokes = null;
        this.stickerImage = stickerImage;
        this.stickerX = x;
        this.stickerY = y;
        this.stickerW = w;
        this.stickerH = h;
    }

    public int getLayerId() {
        return layerId;
    }

    public boolean isSticker() {
        return stickerImage != null;
    }

    public List<BrushStroke> getStrokes() {
        return strokes;
    }

    public BufferedImage getStickerImage() {
        return stickerImage;
    }

    public int getStickerX() { return stickerX; }
    public int getStickerY() { return stickerY; }
    public int getStickerW() { return stickerW; }
    public int getStickerH() { return stickerH; }
}
