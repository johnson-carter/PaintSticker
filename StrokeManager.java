import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StrokeManager {
    private final List<List<BrushStroke>> strokeGroups = new ArrayList<>();
    private List<BrushStroke> currentGroup = null;

    public void startNewGroup() {
        currentGroup = new ArrayList<>();
        strokeGroups.add(currentGroup);
    }

    public void addStroke(int x, int y, Color color, int size) {
        BrushStroke stroke = new BrushStroke(x, y, color, size);
        currentGroup.add(stroke);
        System.out.println("stroke created at: " + stroke.getXval() + " " + stroke.getYval() + " ------------------------------------------------------------------------------");

        if (currentGroup.size() >= 2) {
            BrushStroke prev = currentGroup.get(currentGroup.size() - 2);
            BrushStroke curr = stroke;

            if (!prev.getColor().equals(curr.getColor()) || prev.getSize() != curr.getSize()) return;

            double distance = Math.hypot(curr.getXval() - prev.getXval(), curr.getYval() - prev.getYval());
            if (distance > 100) return;

            for (double i = 0; i <= distance; i += 1.0) {
                double t = i / distance;
                int interpX = (int) Math.round(prev.getXval() + t * (curr.getXval() - prev.getXval()));
                int interpY = (int) Math.round(prev.getYval() + t * (curr.getYval() - prev.getYval()));
                currentGroup.add(new BrushStroke(interpX, interpY, curr.getColor(), curr.getSize()));
            }
        }
    }

    public void renderAll(Graphics g) {
        for (List<BrushStroke> group : strokeGroups) {
            for (BrushStroke stroke : group) {
                g.setColor(stroke.getColor());
                g.fillOval(stroke.getXval(), stroke.getYval(), stroke.getSize(), stroke.getSize());
            }
        }
    }
}
