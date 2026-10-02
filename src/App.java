import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;

public class App {
    private static int brushMode = 1;
    private static Color selectedColor = Color.black;
    private static int brushSizeSelected = 15;

    private static JFrame window;
    private static MyCanvas canvas;
    private static ToolBar toolBar;
    private static SideBar sideBar;
    private static StatusBar statusBar;
    private static List<Themeable> themeables;
    private static String currentLayout = "Standard";
    private static LayersDialog layersDialog;
    private static StickerLibrary stickerLibrary;
    private static StickerLibraryDialog stickerLibraryDialog;

    // Snaps the tool row back to Brush after a sticker placement is
    // confirmed or cancelled - sticker mode has no button of its own in that
    // row (it's entered via the "Stickers" library instead), so leaving it
    // active with no pending sticker would be a dead end for the user.
    private static void resetToolToBrush() {
        setBrushMode(ToolBar.TOOL_BRUSH);
        canvas.setBrushMode(ToolBar.TOOL_BRUSH);
        toolBar.forceSelectTool(ToolBar.TOOL_BRUSH);
    }

    public static void main(String[] args) {

        Theme.DARK.apply();
        setBrushMode(1);

        canvas = new MyCanvas();
        canvas.setBrushMode(1);
        canvas.setFocusable(true);
        stickerLibrary = new StickerLibrary();

        window = new JFrame("PaintSticker");

        toolBar = new ToolBar(new ToolBar.ToolBarListener() {
            @Override
            public void onToolSelected(int mode) {
                setBrushMode(mode);
                canvas.setBrushMode(mode);
            }

            @Override
            public void onUndo() {
                canvas.undoAction();
                canvas.repaint();
            }

            @Override
            public void onRedo() {
                canvas.redoAction();
                canvas.repaint();
            }

            @Override
            public void onColorChosen(Color color) {
                selectedColor = color;
                canvas.setColorChosen(color);
            }

            @Override
            public void onImageSetupRequested() {
                CanvasSettingsDialog dialog = new CanvasSettingsDialog(
                        window, canvas.getWidth(), canvas.getHeight(), canvas.getBackgroundColor());
                dialog.setVisible(true);

                if (dialog.isApproved()) {
                    Color colorChosen = dialog.getSelectedColor();
                    int width = dialog.getCanvasWidth();
                    int height = dialog.getCanvasHeight();
                    toolBar.setImageSetupSwatch(colorChosen);
                    canvas.setBackgroundColor(colorChosen);
                    canvas.setCanvasSize(width, height);
                    window.pack();
                }
            }

            @Override
            public void onStickerLibraryRequested() {
                if (stickerLibraryDialog == null) {
                    stickerLibraryDialog = new StickerLibraryDialog(window, stickerLibrary, image -> {
                        setBrushMode(ToolBar.TOOL_STICKER);
                        canvas.setBrushMode(ToolBar.TOOL_STICKER);
                        canvas.beginStickerPlacement(image);
                        canvas.requestFocusInWindow();
                    });
                } else {
                    stickerLibraryDialog.refresh();
                }
                stickerLibraryDialog.setVisible(true);
                stickerLibraryDialog.toFront();
            }

            @Override
            public void onBrushSizeChanged(int size) {
                brushSizeSelected = size;
                canvas.chooseSize(size);
            }
        });

        sideBar = new SideBar(new SideBar.SideBarListener() {
            @Override
            public void onNewImage() {
                canvas.clearAll();
                canvas.importImage();
                if (layersDialog != null) layersDialog.refresh();
            }

            @Override
            public void onSaveImage() {
                canvas.exportImage();
            }

            @Override
            public void onSettingsRequested() {
                SettingsDialog settingsDialog = new SettingsDialog(
                        window, Theme.current, currentLayout,
                        (theme, layout) -> {
                            applyTheme(theme);
                            applyLayout(layout);
                        });
                settingsDialog.setVisible(true);
            }

            @Override
            public void onLayersRequested() {
                if (layersDialog == null) {
                    layersDialog = new LayersDialog(window, canvas);
                } else {
                    layersDialog.refresh();
                }
                layersDialog.setVisible(true);
                layersDialog.toFront();
            }
        });

        statusBar = new StatusBar(zoom -> canvas.setZoom(zoom / 100.0));

        themeables = List.of(toolBar, sideBar, statusBar);

        window.setIconImage(toolBar.getWindowIcon());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(canvas);
        window.add(sideBar.getPanel(), BorderLayout.WEST);
        window.add(toolBar.getPanel(), BorderLayout.NORTH);
        window.add(statusBar.getPanel(), BorderLayout.SOUTH);

        canvas.setBackground(Constants.bgWindow);

        window.pack();
        window.setResizable(true);
        window.setMinimumSize(new Dimension(1000, 500));
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        //Mouse Input Grabber
        canvas.addMouseListener(new MouseAdapter() {
            JTextField activeTextField = null;
            Point lineStart = null;
            boolean drawingLine = false;

            private void commitText(MyCanvas canvas, JTextField textField) {
                if (textField != null) {
                    String input = textField.getText();
                    if (input != null && !input.trim().isEmpty()) {
                        // Convert to image coordinates for text placement
                        // ! TODO : Fix y-coordinate offset issue
                        Point imgPt = canvas.toImageCoords(new Point(textField.getX(), textField.getY() + textField.getHeight() - 5));
                        canvas.addTextStroke(imgPt.x, imgPt.y, input.trim());
                    }
                    canvas.remove(textField);
                    canvas.repaint();
                    activeTextField = null;
                }
            }

            @Override
            public void mousePressed(MouseEvent e){
                canvas.requestFocusInWindow();
                Point imgPt = canvas.toImageCoords(e.getPoint());
                if (getBrushMode() == ToolBar.TOOL_STICKER) {
                    if (canvas.isStickerAwaitingAnchor()) {
                        canvas.anchorStickerAt(imgPt);
                    } else if (canvas.isStickerAnchored()) {
                        if (canvas.isPointOnStickerHandle(imgPt)) {
                            canvas.beginStickerResize();
                        } else if (canvas.isPointInsideSticker(imgPt)) {
                            canvas.beginStickerMove(imgPt);
                        } else {
                            canvas.confirmStickerPlacement();
                            resetToolToBrush();
                        }
                    }
                    return;
                }
                if (getBrushMode() == 3) {
                    if (activeTextField != null) {
                        return;
                    }
                    JTextField textFieldInput = new JTextField();
                    textFieldInput.setFont(new Font("Verdana", Font.BOLD, brushSizeSelected * 2));
                    textFieldInput.setForeground(canvas.getForeground());
                    textFieldInput.setBackground(new Color(255,255,255,180));
                    textFieldInput.setBorder(BorderFactory.createLineBorder(Constants.accent));
                    int fieldHeight = brushSizeSelected * 2;
                    // Place the text field at the correct zoomed position
                    int x = (int)(imgPt.x * canvas.getZoom());
                    int y = (int)(imgPt.y * canvas.getZoom());
                    textFieldInput.setBounds(x, y, window.getWidth() - x, fieldHeight);
                    canvas.setLayout(null);
                    canvas.add(textFieldInput);
                    textFieldInput.requestFocusInWindow();
                    activeTextField = textFieldInput;

                    textFieldInput.addActionListener(_ -> commitText(canvas, textFieldInput));
                    textFieldInput.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent ev) {
                            commitText(canvas, textFieldInput);
                        }
                    });
                } else if (getBrushMode() == 4) {
                    lineStart = imgPt;
                    drawingLine = true;
                    canvas.setLinePreview(lineStart, lineStart, selectedColor, brushSizeSelected);
                } else {
                    canvas.startNewGroup();
                    canvas.newStroke(imgPt.x, imgPt.y);
                    canvas.repaint();
                }
            }
            @Override
            public void mouseReleased(MouseEvent e){
                if (getBrushMode() == 4 && drawingLine && lineStart != null) {
                    Point imgPt = canvas.toImageCoords(e.getPoint());
                    canvas.addLineStroke(lineStart, imgPt, selectedColor, brushSizeSelected);
                    canvas.clearLinePreview();
                    drawingLine = false;
                    lineStart = null;
                }
                if (getBrushMode() == ToolBar.TOOL_STICKER) {
                    canvas.endStickerDrag();
                }
                canvas.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (getBrushMode() == ToolBar.TOOL_STICKER && e.getClickCount() == 2
                        && canvas.isStickerAnchored()
                        && canvas.isPointInsideSticker(canvas.toImageCoords(e.getPoint()))) {
                    canvas.confirmStickerPlacement();
                    resetToolToBrush();
                }
            }
        });

        //Detecting drag
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e){
                Point imgPt = canvas.toImageCoords(e.getPoint());
                statusBar.setCoordinates(imgPt.x, imgPt.y);
                if (getBrushMode() == ToolBar.TOOL_STICKER) {
                    if (canvas.isStickerResizeDragging()) {
                        canvas.resizeStickerTo(imgPt);
                    } else if (canvas.isStickerMoveDragging()) {
                        canvas.moveStickerTo(imgPt);
                    }
                } else if (getBrushMode() == 4) {
                    // Update preview line to follow the cursor
                    canvas.setLinePreview(canvas.getLinePreviewStart(), imgPt, selectedColor, brushSizeSelected);
                } else {
                    canvas.newStroke(imgPt.x, imgPt.y);
                    canvas.repaint();
                }
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = canvas.toImageCoords(e.getPoint());
                statusBar.setCoordinates(p.x, p.y);
                if (getBrushMode() == ToolBar.TOOL_STICKER && canvas.isStickerAwaitingAnchor()) {
                    canvas.updateStickerPreviewPosition(p);
                }
            }
        });

        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (getBrushMode() != ToolBar.TOOL_STICKER) return;
                if (e.getKeyCode() == KeyEvent.VK_ENTER && canvas.isStickerAnchored()) {
                    canvas.confirmStickerPlacement();
                    resetToolToBrush();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    canvas.cancelStickerPlacement();
                    resetToolToBrush();
                }
            }
        });
    }

    private static void applyTheme(String themeLabel) {
        Theme.fromLabel(themeLabel).apply();
        for (Themeable t : themeables) {
            t.applyTheme();
        }
        canvas.setBackground(Constants.bgWindow);
        window.repaint();
    }

    // Standard: today's NORTH/WEST/SOUTH/CENTER arrangement.
    // Top-Oriented: sidebar's buttons relocate into the toolbar, canvas
    // reclaims the freed width.
    // Free: freeform docking is deliberately out of scope for this pass
    // (no docking framework, no build tool to introduce one cleanly) -
    // falls back to Standard with an explicit message rather than silently
    // doing nothing.
    private static void applyLayout(String layout) {
        if ("Free".equals(layout)) {
            JOptionPane.showMessageDialog(window,
                    "Free layout isn't implemented yet - using Standard layout.");
            layout = "Standard";
        }

        window.remove(sideBar.getPanel());
        for (JButton b : sideBar.getRelocatableButtons()) {
            toolBar.removeTrailingComponent(b);
        }

        if ("Top-Oriented".equals(layout)) {
            for (JButton b : sideBar.getRelocatableButtons()) {
                toolBar.addTrailingComponent(b);
            }
        } else {
            sideBar.restoreStandardArrangement();
            window.add(sideBar.getPanel(), BorderLayout.WEST);
            layout = "Standard";
        }

        currentLayout = layout;
        window.revalidate();
        window.repaint();
    }

    public static void setBrushMode(int mode) {brushMode = mode;}
    public static int getBrushMode(){return brushMode;}
}
