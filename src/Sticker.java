import java.awt.image.BufferedImage;
import java.io.File;

/*
 *      A saved, reusable sticker: a small transparent-background image plus
 *      the display name and backing file used by StickerLibrary.
 */
public class Sticker {
    private final String name;
    private final BufferedImage image;
    private final File file;

    public Sticker(String name, BufferedImage image, File file) {
        this.name = name;
        this.image = image;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public BufferedImage getImage() {
        return image;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return name;
    }
}
