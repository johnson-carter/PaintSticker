import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class StatusBar implements Themeable {

    public interface StatusBarListener {
        void onZoomChanged(int zoomPercent);
    }

    private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    private final JLabel zoomLabel = new JLabel("Zoom: 100%");
    private final JLabel coordLabel = new JLabel("X: 0, Y: 0");
    private final JSlider zoomSlider = new JSlider(10, 400, 100); // 10% to 400%

    public StatusBar(StatusBarListener listener) {
        zoomSlider.setPreferredSize(new Dimension(120, 15));

        panel.add(zoomLabel);
        panel.add(zoomSlider);
        panel.add(coordLabel);

        zoomSlider.addChangeListener(_ -> {
            int zoom = zoomSlider.getValue();
            zoomLabel.setText("Zoom: " + zoom + "%");
            listener.onZoomChanged(zoom);
        });

        applyTheme();
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setCoordinates(int x, int y) {
        coordLabel.setText("X: " + x + ", Y: " + y);
    }

    @Override
    public void applyTheme() {
        panel.setBackground(Constants.bgStatusBar);
        zoomSlider.setBackground(Constants.bgStatusBar);
        zoomLabel.setForeground(Constants.textPrimary);
        coordLabel.setForeground(Constants.textMuted);
    }
}
