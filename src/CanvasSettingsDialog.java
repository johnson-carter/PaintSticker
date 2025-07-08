package src;

import javax.swing.*;
import java.awt.*;

public class CanvasSettingsDialog extends JDialog {
    private Color selectedColor;
    private int canvasWidth;
    private int canvasHeight;
    private boolean approved = false;

    public CanvasSettingsDialog(Frame owner, int currentWidth, int currentHeight, Color currentColor) {
        super(owner, "Canvas Settings", true);

        selectedColor = currentColor != null ? currentColor : Color.white;
        canvasWidth = currentWidth > 0 ? currentWidth : 1080;
        canvasHeight = currentHeight > 0 ? currentHeight : 720;

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Color selection
        gbc.gridx = 0; gbc.gridy = 0;
        JButton colorButton = new JButton("Choose Color");
        colorButton.setBackground(selectedColor);
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Background Color", selectedColor);
            if (newColor != null) {
                selectedColor = newColor;
                colorButton.setBackground(newColor);
            }
        });
        panel.add(colorButton, gbc);

        // Width input
        gbc.gridy = 1;
        panel.add(new JLabel("Width:"), gbc);
        JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(canvasWidth, 100, 3840, 10));
        gbc.gridx = 1;
        panel.add(widthSpinner, gbc);

        // Height input
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Height:"), gbc);
        JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(canvasHeight, 100, 2160, 10));
        gbc.gridx = 1;
        panel.add(heightSpinner, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            canvasWidth = (Integer)widthSpinner.getValue();
            canvasHeight = (Integer)heightSpinner.getValue();
            approved = true;
            dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel);
        pack();
        setLocationRelativeTo(owner);
    }

    public Color getSelectedColor() { return selectedColor; }
    public int getCanvasWidth() { return canvasWidth; }
    public int getCanvasHeight() { return canvasHeight; }
    public boolean isApproved() { return approved; }
}
