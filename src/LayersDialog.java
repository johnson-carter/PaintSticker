import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/*
 *      A live, non-modal editor for MyCanvas's layer stack. Every button
 *      here calls straight through to MyCanvas's own layer API and then
 *      refreshes the list - there's no separate listener interface (unlike
 *      ToolBar/SideBar) because this dialog's entire purpose is to drive
 *      that API directly, so a 1:1 forwarding interface would only add
 *      boilerplate without decoupling anything real.
 */
public class LayersDialog extends JDialog {

    private final MyCanvas canvas;
    private final DefaultListModel<Layer> listModel = new DefaultListModel<>();
    private final JList<Layer> list = new JList<>(listModel);

    public LayersDialog(Frame owner, MyCanvas canvas) {
        super(owner, "Layers", false);
        this.canvas = canvas;
        setLayout(new BorderLayout());

        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object value, int index,
                    boolean isSelected, boolean hasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(l, value, index, isSelected, hasFocus);
                Layer layer = (Layer) value;
                String prefix = layer.isVisible() ? "" : "[hidden] ";
                String suffix = layer.getId() == canvas.getActiveLayerId() ? "  (active)" : "";
                label.setText(prefix + layer.getName() + suffix);
                return label;
            }
        });
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Layer sel = list.getSelectedValue();
                if (sel != null && sel.getId() != canvas.getActiveLayerId()) {
                    canvas.setActiveLayer(sel.getId());
                    canvas.repaint();
                    refresh();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(220, 220));
        add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(0, 1, 4, 4));
        JButton addBtn = new JButton("Add Layer");
        JButton dupBtn = new JButton("Duplicate");
        JButton delBtn = new JButton("Delete");
        JButton upBtn = new JButton("Move Up");
        JButton downBtn = new JButton("Move Down");
        JButton visBtn = new JButton("Toggle Visibility");
        JButton renameBtn = new JButton("Rename");

        addBtn.addActionListener(_ -> {
            canvas.addLayer("Layer " + (canvas.getLayers().size() + 1));
            canvas.repaint();
            refresh();
        });
        dupBtn.addActionListener(_ -> withSelected(sel -> canvas.duplicateLayer(sel.getId())));
        delBtn.addActionListener(_ -> withSelected(sel -> canvas.removeLayer(sel.getId())));
        upBtn.addActionListener(_ -> withSelected(sel -> canvas.moveLayerUp(sel.getId())));
        downBtn.addActionListener(_ -> withSelected(sel -> canvas.moveLayerDown(sel.getId())));
        visBtn.addActionListener(_ -> withSelected(sel -> canvas.setLayerVisible(sel.getId(), !sel.isVisible())));
        renameBtn.addActionListener(_ -> withSelected(sel -> {
            String name = JOptionPane.showInputDialog(this, "Layer name:", sel.getName());
            if (name != null && !name.trim().isEmpty()) {
                canvas.renameLayer(sel.getId(), name.trim());
            }
        }));

        for (JButton b : new JButton[]{addBtn, dupBtn, delBtn, upBtn, downBtn, visBtn, renameBtn}) {
            buttons.add(b);
        }
        add(buttons, BorderLayout.EAST);

        setSize(380, 280);
        setLocationRelativeTo(owner);
        refresh();
    }

    private interface LayerAction {
        void run(Layer layer);
    }

    private void withSelected(LayerAction action) {
        Layer sel = list.getSelectedValue();
        if (sel != null) {
            action.run(sel);
            canvas.repaint();
            refresh();
        }
    }

    public void refresh() {
        listModel.clear();
        List<Layer> layers = canvas.getLayers();
        // MyCanvas stores the stack bottom-first (index 0 = bottom); shown
        // topmost-first here to match the usual layers-panel convention.
        Layer active = null;
        for (int i = layers.size() - 1; i >= 0; i--) {
            Layer l = layers.get(i);
            listModel.addElement(l);
            if (l.getId() == canvas.getActiveLayerId()) active = l;
        }
        list.setSelectedValue(active, true);
    }
}
