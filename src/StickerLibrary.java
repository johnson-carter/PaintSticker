import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/*
 *      Manages the on-disk collection of saved stickers, mirroring how the
 *      rest of the app already expects to run from the repo root (images/ is
 *      loaded the same way) - stickers live in a sibling "stickers/" folder,
 *      one PNG per sticker, loaded eagerly on construction.
 */
public class StickerLibrary {
    private static final File DIR = new File("stickers");

    private final List<Sticker> stickers = new ArrayList<>();

    public StickerLibrary() {
        if (!DIR.exists()) {
            DIR.mkdirs();
        }
        File[] files = DIR.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
        if (files != null) {
            for (File f : files) {
                try {
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) {
                        String name = f.getName().substring(0, f.getName().length() - 4);
                        stickers.add(new Sticker(name, img, f));
                    }
                } catch (IOException ignored) {
                    // Skip unreadable files rather than failing the whole library load.
                }
            }
        }
    }

    public List<Sticker> getStickers() {
        return stickers;
    }

    public Sticker save(String requestedName, BufferedImage image) throws IOException {
        String safeName = requestedName == null || requestedName.trim().isEmpty()
                ? "sticker" : requestedName.trim().replaceAll("[^a-zA-Z0-9 _-]", "_");
        File file = new File(DIR, safeName + ".png");
        int suffix = 1;
        while (file.exists()) {
            file = new File(DIR, safeName + "_" + suffix + ".png");
            suffix++;
        }
        ImageIO.write(image, "PNG", file);
        String finalName = file.getName().substring(0, file.getName().length() - 4);
        Sticker sticker = new Sticker(finalName, image, file);
        stickers.add(sticker);
        return sticker;
    }

    public void delete(Sticker sticker) {
        stickers.remove(sticker);
        if (sticker.getFile() != null) {
            sticker.getFile().delete();
        }
    }
}
