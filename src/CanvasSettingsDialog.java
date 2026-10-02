import javax.swing.*;
import java.awt.*;

public class CanvasSettingsDialog extends JDialog {
    /**
	 *
	 */
	private Color selectedColor;
    private int canvasWidth;
    private int canvasHeight;
    private boolean approved = false;
    private boolean transparent;

    public CanvasSettingsDialog(Frame owner, int currentWidth, int currentHeight, Color currentColor) {
        super(owner, "Canvas Settings", true);

        transparent = currentColor == null || currentColor.getAlpha() == 0;
        selectedColor = (currentColor != null && !transparent) ? currentColor : Color.white;
        canvasWidth = currentWidth > 0 ? currentWidth : 1080;
        canvasHeight = currentHeight > 0 ? currentHeight : 720;

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Constants.bgWindow);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Color selection
        gbc.gridx = 0; gbc.gridy = 0;
        JButton colorButton = new JButton("Choose Color");
        colorButton.setBackground(selectedColor);
        colorButton.setEnabled(!transparent);
        colorButton.addActionListener(_ -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Background Color", selectedColor);
            if (newColor != null) {
                selectedColor = newColor;
                colorButton.setBackground(newColor);
            }
        });
        panel.add(colorButton, gbc);

        // Transparent background toggle
        gbc.gridx = 1;
        JCheckBox transparentBox = new JCheckBox("Transparent", transparent);
        transparentBox.setForeground(Constants.textPrimary);
        transparentBox.setOpaque(false);
        transparentBox.addActionListener(_ -> {
            transparent = transparentBox.isSelected();
            colorButton.setEnabled(!transparent);
        });
        panel.add(transparentBox, gbc);

        // Width input
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel widthLabel = new JLabel("Width:");
        widthLabel.setForeground(Constants.textPrimary);
        panel.add(widthLabel, gbc);
        JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(canvasWidth, 100, 3840, 10));
        gbc.gridx = 1;
        panel.add(widthSpinner, gbc);

        // Height input
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel heightLabel = new JLabel("Height:");
        heightLabel.setForeground(Constants.textPrimary);
        panel.add(heightLabel, gbc);
        JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(canvasHeight, 100, 2160, 10));
        gbc.gridx = 1;
        panel.add(heightSpinner, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Constants.bgWindow);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(_ -> {
            canvasWidth = (Integer)widthSpinner.getValue();
            canvasHeight = (Integer)heightSpinner.getValue();
            approved = true;
            dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel);
        pack();
        setLocationRelativeTo(owner);
    }

    // Returns a fully-transparent color when the "Transparent" box is
    // checked, regardless of whatever color was previously picked - the
    // rest of the app treats alpha-0 as the signal for "no background".
    public Color getSelectedColor() { return transparent ? new Color(0, 0, 0, 0) : selectedColor; }
    public boolean isTransparent() { return transparent; }
    public int getCanvasWidth() { return canvasWidth; }
    public int getCanvasHeight() { return canvasHeight; }
    public boolean isApproved() { return approved; }
}
