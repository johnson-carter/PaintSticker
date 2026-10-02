import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Graphics2D;


class Paintbrush  {
    private final Graphics g;

    public Paintbrush(Graphics graphics){
        g = graphics;
    }


    // Erase (make transparent) at the given location/size
    private void eraseAt(int x, int y, int size) {
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillOval(x, y, size, size);
            g2d.dispose();
        }
    }

    // Draw a single stroke. Used both to bake one new stroke onto a persistent
    // layer as it's created, and (via a history replay) to rebuild a layer
    // from its recorded actions.
    public void drawStroke(BrushStroke stroke) {
        // Skip strokes at (0,0)
        if (stroke.getXval() == 0 && stroke.getYval() == 0) return;
        if (stroke.getType() == BrushStroke.TYPE_TEXT) {
            g.setColor(stroke.getColor());
            g.setFont(new Font("Verdana", java.awt.Font.BOLD, stroke.getSize() * 2));
            g.drawString(stroke.getText(), stroke.getXval(), stroke.getYval());
        } else {
            Color c = stroke.getColor();
            if (c == null || (c.getAlpha() == 0)) {
                eraseAt(stroke.getXval() - (stroke.getSize() / 2), stroke.getYval() - (stroke.getSize() / 2), stroke.getSize());
            } else {
                g.setColor(c);
                g.fillOval(stroke.getXval() - (stroke.getSize() / 2), stroke.getYval() - (stroke.getSize() / 2), stroke.getSize(), stroke.getSize());
            }
        }
    }

    // Draw a background rectangle of the given color
    public void drawBackgroundRect(Color color, int width, int height) {
        if (color != null && g != null) {
            Color old = g.getColor();
            g.setColor(color);
            g.fillRect(0, 0, width, height);
            g.setColor(old);
        }
    }

    // Draw a two-tone checkerboard, the conventional stand-in for a
    // transparent background so the user can tell "no color" apart from
    // "black" or "white" while editing. Never used for the exported image -
    // only as an on-screen preview.
    public void drawCheckerboard(int width, int height, int cell) {
        if (g == null) return;
        Color old = g.getColor();
        Color light = new Color(210, 210, 210);
        Color dark = new Color(160, 160, 160);
        for (int y = 0; y < height; y += cell) {
            for (int x = 0; x < width; x += cell) {
                boolean isLight = ((x / cell) + (y / cell)) % 2 == 0;
                g.setColor(isLight ? light : dark);
                g.fillRect(x, y, cell, cell);
            }
        }
        g.setColor(old);
    }
}
