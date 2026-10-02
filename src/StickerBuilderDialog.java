import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

/*
 *      The "build a sticker from small shapes" studio: a small fixed-size
 *      transparent canvas where the user drags out rectangles/ovals/lines in
 *      a chosen color, then flattens them into a sticker image. This is the
 *      from-scratch alternative to importing an existing image in
 *      StickerLibraryDialog.
 */
public class StickerBuilderDialog extends JDialog {

    private static final int CANVAS_SIZE = 240;

    private final MiniShapeCanvas miniCanvas = new MiniShapeCanvas(CANVAS_SIZE, CANVAS_SIZE);
    private BufferedImage resultImage;

    public StickerBuilderDialog(Frame owner) {
        super(owner, "Build Sticker From Shapes", true);
        setLayout(new BorderLayout());
        add(miniCanvas, BorderLayout.CENTER);

        JPanel toolPanel = new JPanel();
        JComboBox<String> shapeSelector = new JComboBox<>(new String[]{"Rectangle", "Oval", "Line"});
        shapeSelector.addActionListener(_ -> miniCanvas.setActiveShape((String) shapeSelector.getSelectedItem()));
        toolPanel.add(shapeSelector);

        JButton colorBtn = new JButton("Color");
        colorBtn.setBackground(Color.red);
        colorBtn.addActionListener(_ -> {
            Color chosen = JColorChooser.showDialog(this, "Pick a Color", colorBtn.getBackground());
            if (chosen != null) {
                colorBtn.setBackground(chosen);
                miniCanvas.setActiveColor(chosen);
            }
        });
        toolPanel.add(colorBtn);

        JButton undoBtn = new JButton("Undo Shape");
        undoBtn.addActionListener(_ -> miniCanvas.undoLastShape());
        toolPanel.add(undoBtn);

        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(_ -> miniCanvas.clearShapes());
        toolPanel.add(clearBtn);

        add(toolPanel, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel();
        JButton saveBtn = new JButton("Save Sticker");
        saveBtn.addActionListener(_ -> {
            resultImage = miniCanvas.flatten();
            dispose();
        });
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(_ -> dispose());
        actionPanel.add(saveBtn);
        actionPanel.add(cancelBtn);
        add(actionPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    // Null if the dialog was cancelled without saving.
    public BufferedImage getResultImage() {
        return resultImage;
    }

    private static class MiniShapeCanvas extends JPanel {
        private final int w, h;
        private String activeShape = "Rectangle";
        private Color activeColor = Color.red;
        private final List<MiniShape> shapes = new ArrayList<>();
        private Point dragStart, dragCurrent;

        MiniShapeCanvas(int w, int h) {
            this.w = w;
            this.h = h;
            setPreferredSize(new Dimension(w, h));

            MouseAdapter ma = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    dragStart = e.getPoint();
                    dragCurrent = e.getPoint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (dragStart != null) {
                        shapes.add(new MiniShape(activeShape, dragStart, e.getPoint(), activeColor));
                        dragStart = null;
                        dragCurrent = null;
                        repaint();
                    }
                }
            };
            addMouseListener(ma);
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    dragCurrent = e.getPoint();
                    repaint();
                }
            });
        }

        void setActiveShape(String s) { activeShape = s; }
        void setActiveColor(Color c) { activeColor = c; }

        void undoLastShape() {
            if (!shapes.isEmpty()) {
                shapes.remove(shapes.size() - 1);
                repaint();
            }
        }

        void clearShapes() {
            shapes.clear();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Checkerboard so transparent gaps in the final sticker are
            // visible while composing, same convention as the main canvas.
            new Paintbrush(g).drawCheckerboard(w, h, 8);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (MiniShape s : shapes) s.draw(g2);
            if (dragStart != null && dragCurrent != null) {
                new MiniShape(activeShape, dragStart, dragCurrent, activeColor).draw(g2);
            }
        }

        BufferedImage flatten() {
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (MiniShape s : shapes) s.draw(g2);
            g2.dispose();
            return img;
        }
    }

    private static class MiniShape {
        final String type;
        final Point p1, p2;
        final Color color;

        MiniShape(String type, Point p1, Point p2, Color color) {
            this.type = type;
            this.p1 = p1;
            this.p2 = p2;
            this.color = color;
        }

        void draw(Graphics2D g2) {
            g2.setColor(color);
            int x = Math.min(p1.x, p2.x), y = Math.min(p1.y, p2.y);
            int w = Math.abs(p2.x - p1.x), h = Math.abs(p2.y - p1.y);
            switch (type) {
                case "Oval":
                    g2.fillOval(x, y, w, h);
                    break;
                case "Line":
                    g2.setStroke(new BasicStroke(4));
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    break;
                default:
                    g2.fillRect(x, y, w, h);
                    break;
            }
        }
    }
}
