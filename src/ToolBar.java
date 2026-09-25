import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class ToolBar implements Themeable {

    public interface ToolBarListener {
        void onToolSelected(int mode);
        void onUndo();
        void onColorChosen(Color color);
        void onImageSetupRequested();
        void onBrushSizeChanged(int size);
    }

    public static final int TOOL_BRUSH = 1;
    public static final int TOOL_ERASER = 2;
    public static final int TOOL_TEXT = 3;
    public static final int TOOL_LINE = 4;

    private final ToolBarListener listener;
    private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private final ImageIcon logoIcon;
    private final JButton undoButton;
    private final JLabel toolkitLabel;

    private final ImageIcon brushIcon, brushHighlighted, eraserIcon, eraserHighlighted;
    private final JButton brushButton, eraserButton, textButton, lineButton;
    private final List<JButton> toolButtons = new ArrayList<>();
    private int currentTool = TOOL_BRUSH;

    private final JButton selectColorButton;
    private final JButton imageSetupButton;
    private final JComboBox<Integer> brushSizeSelector;

    public ToolBar(ToolBarListener listener) {
        this.listener = listener;

        panel.setPreferredSize(new Dimension(0, 50));
        panel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 2, 0, Color.black),
                new EmptyBorder(0, 0, 15, 0)));

        logoIcon = loadScaledIcon("images/logo.png", 50);
        JLabel logo = new JLabel(logoIcon);
        panel.add(logo);

        ImageIcon undoIcon = loadScaledIcon("images/undoIcon.png", 20);
        undoButton = new JButton(undoIcon);
        undoButton.setFocusPainted(false);
        undoButton.setPreferredSize(new Dimension(30, 30));
        undoButton.addActionListener(_ -> listener.onUndo());
        panel.add(undoButton);

        toolkitLabel = new JLabel(" Select Tool: ");
        toolkitLabel.setFont(Constants.FONT_HEADING);
        panel.add(toolkitLabel);

        brushIcon = new ImageIcon("images/brushIcon.png");
        brushHighlighted = new ImageIcon("images/brushIcon2.png");
        brushButton = new JButton(brushHighlighted);
        brushButton.setFocusPainted(false);
        brushButton.setPreferredSize(new Dimension(30, 30));
        brushButton.addActionListener(_ -> selectTool(TOOL_BRUSH));
        panel.add(brushButton);
        toolButtons.add(brushButton);

        eraserIcon = new ImageIcon("images/eraser.png");
        eraserHighlighted = new ImageIcon("images/eraser2.png");
        eraserButton = new JButton(eraserIcon);
        eraserButton.setFocusPainted(false);
        eraserButton.setPreferredSize(new Dimension(30, 30));
        eraserButton.addActionListener(_ -> selectTool(TOOL_ERASER));
        panel.add(eraserButton);
        toolButtons.add(eraserButton);

        textButton = new JButton("t|");
        textButton.setFont(Constants.FONT_HEADING);
        textButton.setFocusPainted(false);
        textButton.setPreferredSize(new Dimension(30, 30));
        textButton.addActionListener(_ -> selectTool(TOOL_TEXT));
        panel.add(textButton);
        toolButtons.add(textButton);

        lineButton = new JButton("/");
        lineButton.setFont(Constants.FONT_GLYPH);
        lineButton.setFocusPainted(false);
        lineButton.setPreferredSize(new Dimension(30, 30));
        lineButton.addActionListener(_ -> selectTool(TOOL_LINE));
        panel.add(lineButton);
        toolButtons.add(lineButton);

        panel.add(new JLabel("   "));

        selectColorButton = new JButton("Select Color");
        selectColorButton.setFont(Constants.FONT_BUTTON);
        selectColorButton.setFocusPainted(false);
        selectColorButton.setPreferredSize(new Dimension(120, 30));
        selectColorButton.setBackground(Color.black);
        selectColorButton.setForeground(Color.white);
        selectColorButton.addActionListener(_ -> {
            Color chosen = JColorChooser.showDialog(panel, "Pick a Color", Color.black);
            if (chosen != null) {
                setSelectedColorSwatch(chosen);
                listener.onColorChosen(chosen);
            }
        });
        panel.add(selectColorButton);

        imageSetupButton = new JButton("Image Setup");
        imageSetupButton.setFont(Constants.FONT_BUTTON);
        imageSetupButton.setFocusPainted(false);
        imageSetupButton.setPreferredSize(new Dimension(120, 30));
        imageSetupButton.setBackground(Color.white);
        imageSetupButton.setForeground(Color.black);
        imageSetupButton.addActionListener(_ -> listener.onImageSetupRequested());
        panel.add(imageSetupButton);

        JLabel sizeLabel = new JLabel(" Size: ");
        sizeLabel.setFont(Constants.FONT_LABEL);
        panel.add(sizeLabel);

        Integer[] brushSizesList = {1, 2, 5, 10, 15, 30, 50, 100, 150, 200};
        brushSizeSelector = new JComboBox<>(brushSizesList);
        brushSizeSelector.setSelectedIndex(4);
        brushSizeSelector.setEditable(true);
        brushSizeSelector.setPreferredSize(new Dimension(100, 30));
        brushSizeSelector.addActionListener(_ ->
                listener.onBrushSizeChanged((int) brushSizeSelector.getSelectedItem()));
        panel.add(brushSizeSelector);

        applyTheme();
    }

    private void selectTool(int mode) {
        currentTool = mode;
        highlightButtons();
        listener.onToolSelected(mode);
    }

    // Replaces what used to be 4 near-identical "reset everyone else, highlight
    // me" listener bodies: reset every tool button to idle, then highlight
    // only the currently-selected one. As a side effect this also fixes a
    // pre-existing bug where selecting Line then a different tool left the
    // Line button visually stuck in its highlighted state (its old listener
    // never reset the OTHER buttons' stale highlight in every direction).
    private void highlightButtons() {
        for (JButton b : toolButtons) {
            b.setBackground(Constants.buttonIdleBg);
            b.setForeground(Constants.textPrimary);
            b.setBorder(new LineBorder(Constants.border, Constants.borderWidth));
        }
        brushButton.setIcon(currentTool == TOOL_BRUSH ? brushHighlighted : brushIcon);
        eraserButton.setIcon(currentTool == TOOL_ERASER ? eraserHighlighted : eraserIcon);

        JButton active;
        switch (currentTool) {
            case TOOL_ERASER: active = eraserButton; break;
            case TOOL_TEXT: active = textButton; break;
            case TOOL_LINE: active = lineButton; break;
            default: active = brushButton; break;
        }
        active.setBackground(Constants.buttonSelectedBg);
        active.setForeground(Constants.accent);
    }

    private static Color contrastColor(Color color) {
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        return luminance > 0.5 ? Color.black : Color.white;
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

    public Image getWindowIcon() {
        return logoIcon.getImage();
    }

    // Used by a layout mode (e.g. Top-Oriented) to relocate another panel's
    // components into this toolbar.
    public void addTrailingComponent(JComponent c) {
        panel.add(c);
        panel.revalidate();
        panel.repaint();
    }

    public void removeTrailingComponent(JComponent c) {
        panel.remove(c);
        panel.revalidate();
        panel.repaint();
    }

    // The Select Color / Image Setup buttons intentionally do NOT follow the
    // theme - their background IS the color the user picked (paint color /
    // canvas background color), so theming their chrome would hide that.
    public void setSelectedColorSwatch(Color color) {
        selectColorButton.setBackground(color);
        selectColorButton.setForeground(contrastColor(color));
    }

    public void setImageSetupSwatch(Color color) {
        imageSetupButton.setBackground(color);
        imageSetupButton.setForeground(contrastColor(color));
    }

    @Override
    public void applyTheme() {
        panel.setBackground(Constants.bgToolbar);
        toolkitLabel.setForeground(Constants.accent);
        undoButton.setBackground(Constants.accentWarn);
        undoButton.setBorder(new LineBorder(Constants.border, Constants.borderWidth));
        selectColorButton.setBorder(new LineBorder(Constants.border, Constants.borderWidth));
        imageSetupButton.setBorder(new LineBorder(Constants.border, Constants.borderWidth));
        highlightButtons();
        panel.repaint();
    }
}
