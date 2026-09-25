import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
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
    }

    private static final int ICON_SIZE = 40;
    private static final Dimension BUTTON_SIZE = new Dimension(45, 45);

    private final JPanel panel = new JPanel();
    private final JButton newButton;
    private final JButton fileButton;
    private final JButton settingButton;

    public SideBar(SideBarListener listener) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(55, 0));
        panel.setBorder(new MatteBorder(0, 0, 0, 3, Color.black));

        newButton = iconButton(loadScaledIcon("images/addIcon.png", ICON_SIZE));
        fileButton = iconButton(loadScaledIcon("images/saveIcon.png", ICON_SIZE));
        settingButton = iconButton(loadScaledIcon("images/settingsIcon.png", ICON_SIZE));

        newButton.addActionListener(_ -> listener.onNewImage());
        fileButton.addActionListener(_ -> listener.onSaveImage());
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

    public JPanel getPanel() {
        return panel;
    }

    // The three buttons this sidebar owns, exposed so a layout mode (e.g.
    // Top-Oriented) can relocate them into a different container. Swing
    // re-parents a component automatically when it's added elsewhere.
    public List<JButton> getRelocatableButtons() {
        return List.of(newButton, fileButton, settingButton);
    }

    // Rebuilds this panel's own arrangement from scratch: new/save at top,
    // settings pinned to the bottom via vertical glue. Safe to call even if
    // the buttons currently live in another container (e.g. after a
    // Top-Oriented -> Standard layout switch).
    public void restoreStandardArrangement() {
        panel.removeAll();
        panel.add(newButton);
        panel.add(fileButton);
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
