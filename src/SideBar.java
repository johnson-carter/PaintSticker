import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

public class SideBar implements Themeable {

    public interface SideBarListener {
        void onNewImage();
        void onSaveImage();
        void onSettingsRequested();
        void onLayersRequested();
    }

    private static final int ICON_SIZE = 40;
    private static final Dimension BUTTON_SIZE = new Dimension(45, 45);

    private final JPanel panel = new JPanel();
    private final JButton newButton;
    private final JButton fileButton;
    private final JButton layersButton;
    private final JButton settingButton;

    public SideBar(SideBarListener listener) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(55, 0));
        panel.setBorder(new MatteBorder(0, 0, 0, 3, Color.black));

        newButton = iconButton(loadScaledIcon("images/addIcon.png", ICON_SIZE));
        fileButton = iconButton(loadScaledIcon("images/saveIcon.png", ICON_SIZE));
        layersButton = iconButton(layersIcon(ICON_SIZE));
        settingButton = iconButton(loadScaledIcon("images/settingsIcon.png", ICON_SIZE));

        newButton.addActionListener(_ -> listener.onNewImage());
        fileButton.addActionListener(_ -> listener.onSaveImage());
        layersButton.addActionListener(_ -> listener.onLayersRequested());
        settingButton.addActionListener(_ -> listener.onSettingsRequested());

        restoreStandardArrangement();
        applyTheme();
    }

    private static JButton iconButton(ImageIcon icon) {
        JButton b = new JButton(icon);
        b.setBackground(null);
        b.setBorder(null);
        b.setFocusPainted(false);
        b.setPreferredSize(BUTTON_SIZE);
        b.setMaximumSize(BUTTON_SIZE);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        return b;
    }

    private static ImageIcon loadScaledIcon(String path, int size) {
        ImageIcon icon = new ImageIcon(path);
        Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        icon.setImage(scaled);
        return icon;
    }

    // No layers icon asset exists, so this draws a small stack-of-panes
    // glyph in code instead - three overlapping rounded rects, matching the
    // visual weight of the other flat-icon SideBar buttons.
    private static ImageIcon layersIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int pad = size / 6;
        int step = size / 5;
        int w = size - pad * 2;
        int h = size - pad * 2;
        g2.setColor(new Color(70, 140, 200));
        g2.fillRoundRect(pad, pad + step * 2, w - step, h - step * 2, 4, 4);
        g2.setColor(new Color(110, 175, 225));
        g2.fillRoundRect(pad + step, pad + step, w - step, h - step, 4, 4);
        g2.setColor(new Color(160, 210, 245));
        g2.fillRoundRect(pad + step * 2, pad, w - step, h - step, 4, 4);
        g2.dispose();
        return new ImageIcon(img);
    }

    public JPanel getPanel() {
        return panel;
    }

    // The three buttons this sidebar owns, exposed so a layout mode (e.g.
    // Top-Oriented) can relocate them into a different container. Swing
    // re-parents a component automatically when it's added elsewhere.
    public List<JButton> getRelocatableButtons() {
        return List.of(newButton, fileButton, layersButton, settingButton);
    }

    // Rebuilds this panel's own arrangement from scratch: new/save at top,
    // settings pinned to the bottom via vertical glue. Safe to call even if
    // the buttons currently live in another container (e.g. after a
    // Top-Oriented -> Standard layout switch).
    public void restoreStandardArrangement() {
        panel.removeAll();
        panel.add(newButton);
        panel.add(fileButton);
        panel.add(layersButton);
        panel.add(Box.createVerticalGlue());
        panel.add(settingButton);
        panel.revalidate();
        panel.repaint();
    }

    @Override
    public void applyTheme() {
        panel.setBackground(Constants.bgSidebar);
    }
}
