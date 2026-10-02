import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 *      Non-modal library of saved stickers: create one either from an image
 *      file or from the small shape-building studio (StickerBuilderDialog),
 *      then hand a chosen one back to App via the listener to start a
 *      place-on-canvas interaction (see MyCanvas's sticker placement API).
 */
public class StickerLibraryDialog extends JDialog {

    public interface StickerLibraryListener {
        void onPlaceSticker(BufferedImage image);
    }

    private final StickerLibrary library;
    private final StickerLibraryListener listener;
    private final DefaultListModel<Sticker> listModel = new DefaultListModel<>();
    private final JList<Sticker> list = new JList<>(listModel);

    public StickerLibraryDialog(Frame owner, StickerLibrary library, StickerLibraryListener listener) {
        super(owner, "Stickers", false);
        this.library = library;
        this.listener = listener;
        setLayout(new BorderLayout());

        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(0);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object value, int index,
                    boolean isSelected, boolean hasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(l, value, index, isSelected, hasFocus);
                Sticker sticker = (Sticker) value;
                Image scaled = sticker.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaled));
                label.setText(sticker.getName());
                label.setHorizontalTextPosition(JLabel.CENTER);
                label.setVerticalTextPosition(JLabel.BOTTOM);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
                return label;
            }
        });
        refresh();

        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(340, 260));
        add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(0, 1, 4, 4));
        JButton fromImageBtn = new JButton("New From Image");
        JButton fromShapesBtn = new JButton("New From Shapes");
        JButton placeBtn = new JButton("Place on Canvas");
        JButton deleteBtn = new JButton("Delete");

        fromImageBtn.addActionListener(_ -> newFromImage());
        fromShapesBtn.addActionListener(_ -> newFromShapes());
        placeBtn.addActionListener(_ -> {
            Sticker sel = list.getSelectedValue();
            if (sel == null) {
                JOptionPane.showMessageDialog(this, "Select a sticker first.", "No Sticker Selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this,
                    "Click on the canvas to place the sticker.\n"
                            + "Drag the corner handle to resize, drag inside to move.\n"
                            + "Press Enter to confirm or Esc to cancel.",
                    "Place Sticker", JOptionPane.INFORMATION_MESSAGE);
            listener.onPlaceSticker(sel.getImage());
        });
        deleteBtn.addActionListener(_ -> {
            Sticker sel = list.getSelectedValue();
            if (sel != null) {
                library.delete(sel);
                refresh();
            }
        });

        for (JButton b : new JButton[]{fromImageBtn, fromShapesBtn, placeBtn, deleteBtn}) {
            buttons.add(b);
        }
        add(buttons, BorderLayout.EAST);

        setSize(460, 340);
        setLocationRelativeTo(owner);
    }

    private void newFromImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = ImageIO.read(chooser.getSelectedFile());
                if (img == null) {
                    JOptionPane.showMessageDialog(this, "Unsupported or corrupt image file.",
                            "Load Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                img = scaleToMax(img, 300);
                String defaultName = chooser.getSelectedFile().getName();
                int dot = defaultName.lastIndexOf('.');
                if (dot > 0) defaultName = defaultName.substring(0, dot);
                String name = JOptionPane.showInputDialog(this, "Sticker name:", defaultName);
                if (name == null) return;
                library.save(name, img);
                refresh();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to load image:\n" + ex.getMessage(),
                        "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void newFromShapes() {
        StickerBuilderDialog builder = new StickerBuilderDialog((Frame) getOwner());
        builder.setVisible(true);
        BufferedImage result = builder.getResultImage();
        if (result != null) {
            String name = JOptionPane.showInputDialog(this, "Sticker name:", "sticker");
            if (name == null) return;
            try {
                library.save(name, result);
                refresh();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to save sticker:\n" + ex.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static BufferedImage scaleToMax(BufferedImage img, int maxDim) {
        if (img.getWidth() <= maxDim && img.getHeight() <= maxDim) return img;
        double scale = Math.min((double) maxDim / img.getWidth(), (double) maxDim / img.getHeight());
        int w = Math.max(1, (int) (img.getWidth() * scale));
        int h = Math.max(1, (int) (img.getHeight() * scale));
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.drawImage(scaled, 0, 0, null);
        g2.dispose();
        return out;
    }

    public void refresh() {
        listModel.clear();
        for (Sticker s : library.getStickers()) {
            listModel.addElement(s);
        }
    }
}
