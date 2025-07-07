package src;

import java.awt.*;
import javax.swing.*;

public class SettingsDialog extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3486768414631139560L;
	private JComboBox<String> themeDropdown;
    private JComboBox<String> layoutDropdown;

    public interface SettingsListener {
        void onSettingsSelected(String theme, String layout);
    }

    public SettingsDialog(JFrame parent, SettingsListener listener) {
        super(parent, "Settings", true);
        setSize(300, 200);
        setLayout(new BorderLayout());

        String[] themes = {"Light", "Dark", "High-Contrast"};
        String[] layouts = {"Standard", "Top-Oriented", "Free"};

        themeDropdown = new JComboBox<>(themes);
        layoutDropdown = new JComboBox<>(layouts);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Theme:"));
        inputPanel.add(themeDropdown);
        inputPanel.add(new JLabel("Layout:"));
        inputPanel.add(layoutDropdown);

        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(_ -> {
            String theme = (String) themeDropdown.getSelectedItem();
            String layout = (String) layoutDropdown.getSelectedItem();

            listener.onSettingsSelected(theme, layout);
            dispose(); // Close the dialog
        });

        add(inputPanel, BorderLayout.CENTER);
        add(applyButton, BorderLayout.SOUTH);
        setLocationRelativeTo(parent);
    }
}
