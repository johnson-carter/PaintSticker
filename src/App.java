package src;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;


public class App {
    private static int brushMode = 1;
    private static Color selectedColor = Color.black;
    private static int brushSizeSelected = 15;
    // Simple Dark Theme
    private final static Color LIGHTDARK = new Color(100, 100, 100);
    private final static Color REGULARDARK = new Color(60, 60, 60);
    private final static Color DARKDARK = new Color(30, 30, 30);

    private final static Color SKYBLUE = new Color(135, 206, 250); // Light Sky Blue
    private final static Color REDORANGE = new Color(255, 69, 0); // Red-Orange
    private final static Color LIGHTGREEN = new Color(144, 238, 144); // Light Green

    static Color sysLight = LIGHTDARK;
    static Color sysColor = REGULARDARK;
    static Color sysDark = DARKDARK;

    static Color accent1 = SKYBLUE;
    static Color accent2 = REDORANGE;
    static Color accent3 = LIGHTGREEN;


    public static void main(String[] args) {

        setBrushMode(1);

        MyCanvas canvas = new MyCanvas();
        canvas.setBrushMode(1);
        JPanel topTray = new JPanel();
        JPanel sideBar = new JPanel();
    

        JFrame window = new JFrame("PaintSticker");

        Image scaledImage;

        //PaintSticker logo
        ImageIcon logoImg = new ImageIcon("images/logo.png");
        scaledImage = logoImg.getImage().getScaledInstance(50, 50, 0);
        logoImg.setImage(scaledImage);
        JLabel logo = new JLabel(logoImg);
        topTray.add(logo);

        //Undo Button
        ImageIcon undoIcon = new ImageIcon("images/undoIcon.png");
        scaledImage = undoIcon.getImage().getScaledInstance(20, 20, 0);
        undoIcon.setImage(scaledImage);
        JButton undoButton = new JButton(undoIcon);
        undoButton.setBackground(accent2);
        undoButton.setFocusPainted(false);
        undoButton.setBorder(new LineBorder(sysDark, 2));
        undoButton.setPreferredSize(new Dimension(30, 30));
        topTray.add(undoButton);

        // Toolkit break
        JLabel toolkitLabel = new JLabel(" Select Tool: ");
        toolkitLabel.setFont(new Font("Verdana", Font.BOLD, 12));
        toolkitLabel.setForeground(accent1);
        topTray.add(toolkitLabel);

        //Brush Selector
        ImageIcon brushIcon = new ImageIcon("images/brushIcon.png");
        ImageIcon brushHighlighted = new ImageIcon("images/brushIcon2.png");
        JButton brush = new JButton(brushHighlighted);
        brush.setPreferredSize(new Dimension(30, 30));
        brush.setFocusPainted(false);
        brush.setBorder(new LineBorder(sysDark, 2));
        brush.setBackground(sysDark);
        topTray.add(brush);

        //Eraser Selector
        ImageIcon eraserIcon = new ImageIcon("images/eraser.png");
        ImageIcon eraserHighlighted = new ImageIcon("images/eraser2.png");
        JButton eraser = new JButton(eraserIcon);
        eraser.setPreferredSize(new Dimension(30, 30));
        eraser.setBackground(sysLight);
        eraser.setFocusPainted(false);
        eraser.setBorder(new LineBorder(sysDark, 2));
        topTray.add(eraser);

        JButton textField = new JButton("t|");
        textField.setFont(new Font("Verdana", Font.BOLD, 12));
        textField.setPreferredSize(new Dimension(30, 30));
        textField.setBackground(sysLight);
        textField.setFocusPainted(false);
        textField.setBorder(new LineBorder(sysDark, 2));
        textField.setForeground(sysDark);
        topTray.add(textField);

        // Line Tool Button
        JButton lineButton = new JButton("/");
        lineButton.setFont(new Font("Verdana", Font.BOLD, 16));
        lineButton.setPreferredSize(new Dimension(30, 30));
        lineButton.setBackground(sysLight);
        lineButton.setFocusPainted(false);
        lineButton.setBorder(new LineBorder(sysDark, 2));
        lineButton.setForeground(sysDark);
        topTray.add(lineButton);

        //Color Selector
        JButton selectColor = new JButton("Select Color");
        selectColor.setFont(new Font("Verdana", Font.PLAIN, 12));
        selectColor.setPreferredSize(new Dimension(120,30));
        selectColor.setBackground(selectedColor);
        selectColor.setFocusPainted(false);
        selectColor.setBorder(new LineBorder(sysDark, 2));
        selectColor.setForeground(Color.white);
        topTray.add(selectColor);

        // Background Color Selector
        JButton backgroundColorButton = new JButton("Image Setup");
        backgroundColorButton.setFont(new Font("Verdana", Font.PLAIN, 12));
        backgroundColorButton.setPreferredSize(new Dimension(120,30));
        backgroundColorButton.setBackground(Color.white);
        backgroundColorButton.setFocusPainted(false);
        backgroundColorButton.setBorder(new LineBorder(sysDark, 2));
        backgroundColorButton.setForeground(Color.black);
        topTray.add(backgroundColorButton);

        //Brush Size Selector
        Integer[] brushSizesList = {1, 2, 5, 10, 15, 30, 50, 100, 150, 200};
        JComboBox<Integer> brushSizeSelector = new JComboBox<>(brushSizesList);
        brushSizeSelector.setSelectedIndex(4);
        brushSizeSelector.setEditable(true);
        brushSizeSelector.setPreferredSize(new Dimension(100, 30));
        topTray.add(brushSizeSelector);

        //Some alignment and settings for the TopTray
        FlowLayout topLayout = new FlowLayout(FlowLayout.LEFT);
        topTray.setBackground(sysLight);
        topTray.setPreferredSize(new Dimension(window.getWidth(), 50));
        topTray.setLayout(topLayout);
        topTray.setVisible(true);
        //TopTray Border
        MatteBorder topTrayBorder = new MatteBorder(0, 0, 2, 0, Color.black);
        EmptyBorder topTrayPadding = new EmptyBorder(0, 0, 15, 0);
        topTray.setBorder(new CompoundBorder(topTrayBorder, topTrayPadding));

        //Sidebar properties
        sideBar.setBackground(sysLight);
        sideBar.setPreferredSize(new Dimension(55, window.getHeight()));
        sideBar.setVisible(true);
        sideBar.setBorder(new MatteBorder(0,0,0,3, Color.black));

        int scaledImageSize = 40;
        int sideBarButtonSize = 45;
        Dimension sideBarButtonDimension = new Dimension(sideBarButtonSize, sideBarButtonSize);

        //Sidebar components:
        ImageIcon addIcon = new ImageIcon("images/addIcon.png");
        scaledImage = addIcon.getImage().getScaledInstance(scaledImageSize, scaledImageSize, 0);
        addIcon.setImage(scaledImage);
        JButton newButton = new JButton(addIcon);
        newButton.setBackground(null);
        newButton.setBorder(null);
        newButton.setFocusPainted(false);
        newButton.setPreferredSize(sideBarButtonDimension);

        ImageIcon saveIcon = new ImageIcon("images/saveIcon.png");
        scaledImage = saveIcon.getImage().getScaledInstance(scaledImageSize, scaledImageSize, 0);
        saveIcon.setImage(scaledImage);
        JButton fileButton = new JButton(saveIcon);
        fileButton.setBackground(null);
        fileButton.setBorder(null);
        fileButton.setFocusPainted(false);
        fileButton.setPreferredSize(sideBarButtonDimension);

        ImageIcon settingsIcon = new ImageIcon("images/settingsIcon.png");
        scaledImage = settingsIcon.getImage().getScaledInstance(scaledImageSize, scaledImageSize, 0);
        settingsIcon.setImage(scaledImage);
        JButton settingButton = new JButton(settingsIcon);
        settingButton.setBackground(null);
        settingButton.setBorder(null);
        settingButton.setPreferredSize(sideBarButtonDimension);
        settingButton.setFocusPainted(false);

        sideBar.add(newButton);
        sideBar.add(fileButton);
        sideBar.add(settingButton, BorderLayout.SOUTH);

        // Window properties
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(canvas);
        window.add(sideBar, BorderLayout.WEST);
        window.add(topTray, BorderLayout.NORTH);
        
        // --- Add status bar for zoom and coordinates ---
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusBar.setBackground(sysDark);
        JLabel zoomLabel = new JLabel("Zoom: 100%");
        zoomLabel.setForeground(Color.white);
        JLabel coordLabel = new JLabel("X: 0, Y: 0");
        coordLabel.setForeground(Color.white);
        JSlider zoomSlider = new JSlider(10, 400, 100); // 10% to 400%
        zoomSlider.setBackground(sysDark);
        zoomSlider.setPreferredSize(new Dimension(120, 15));
        statusBar.add(zoomLabel);
        statusBar.add(zoomSlider);
        statusBar.add(coordLabel);
        
        window.add(statusBar, BorderLayout.SOUTH);
        
        window.pack();
        window.setResizable(true);
        window.setMinimumSize(new Dimension(1000, 500));
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Button Logic

        //Undo Button
        undoButton.addActionListener(a -> {
            canvas.undoAction();
            canvas.repaint();
        });

        //Brush Button
        brush.addActionListener(a -> {
            brush.setIcon(brushHighlighted);
            brush.setBackground(sysDark);
            eraser.setIcon(eraserIcon);
            eraser.setBackground(sysLight);
            textField.setForeground(sysDark);
            textField.setBackground(sysLight);
            canvas.setBrushMode(1);
            setBrushMode(1);
        });

        //Eraser Button
        eraser.addActionListener(a -> {
            brush.setIcon(brushIcon);
            brush.setBackground(sysLight);
            eraser.setIcon(eraserHighlighted);
            eraser.setBackground(sysDark);
            textField.setForeground(sysDark);
            textField.setBackground(sysLight);
            canvas.setBrushMode(2);
            setBrushMode(2);
        });

        textField.addActionListener(a -> {
            brush.setIcon(brushIcon);
            brush.setBackground(sysLight);
            eraser.setIcon(eraserIcon);
            eraser.setBackground(sysLight);
            textField.setForeground(accent1);
            textField.setBackground(sysDark);
            canvas.setBrushMode(3);
            setBrushMode(3);
        });

        // Line Tool Button
        lineButton.addActionListener(a -> {
            brush.setIcon(brushIcon);
            brush.setBackground(sysLight);
            eraser.setIcon(eraserIcon);
            eraser.setBackground(sysLight);
            textField.setForeground(sysDark);
            textField.setBackground(sysLight);
            lineButton.setForeground(accent1);
            lineButton.setBackground(sysDark);
            canvas.setBrushMode(4);
            setBrushMode(4);
        });

        //Color Selector
        selectColor.addActionListener(a -> {
            Color colorChosen = JColorChooser.showDialog(null, "Pick a Color", Color.black);
            selectedColor = colorChosen;
            selectColor.setBackground(selectedColor);
            if (colorChosen != null) {
                canvas.setColorChosen(colorChosen);
            }
            selectColor.setForeground(getContrastColor(colorChosen)); // <-- update foreground
        });

        // Background Color Selector
        backgroundColorButton.addActionListener(a -> {
            CanvasSettingsDialog dialog = new CanvasSettingsDialog(window, canvas.getWidth(), canvas.getHeight(), canvas.getBackground());
            JCheckBox transparentBox = new JCheckBox("Transparent Background");
            // Add the checkbox directly to the dialog, not by getComponent(0)
            dialog.add(transparentBox, BorderLayout.SOUTH);
            dialog.pack();
            dialog.setVisible(true);

            if (dialog.isApproved()) {
                Color colorChosen = dialog.getSelectedColor();
                int width = dialog.getCanvasWidth();
                int height = dialog.getCanvasHeight();
                if (transparentBox.isSelected()) {
                    colorChosen = new Color(0, 0, 0, 0); // Fully transparent
                }
                backgroundColorButton.setBackground(colorChosen);
                backgroundColorButton.setForeground(getContrastColor(colorChosen));
                canvas.setBackgroundColor(colorChosen);
                canvas.setPreferredSize(new Dimension(width, height));
                canvas.revalidate();
                canvas.repaint();
                window.pack();
            }
        });

        //Brush Size Selector
        brushSizeSelector.addActionListener(a -> {
            brushSizeSelected = (int) brushSizeSelector.getSelectedItem();
            canvas.chooseSize(brushSizeSelected);
        });


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
                Point imgPt = canvas.toImageCoords(e.getPoint());
                if (getBrushMode() == 3) {
                    if (activeTextField != null) {
                        return;
                    }
                    JTextField textFieldInput = new JTextField();
                    textFieldInput.setFont(new Font("Verdana", Font.BOLD, brushSizeSelected * 2));
                    textFieldInput.setForeground(canvas.getForeground());
                    textFieldInput.setBackground(new Color(255,255,255,180));
                    textFieldInput.setBorder(BorderFactory.createLineBorder(accent1));
                    int fieldHeight = brushSizeSelected * 2;
                    // Place the text field at the correct zoomed position
                    int x = (int)(imgPt.x * canvas.getZoom());
                    int y = (int)(imgPt.y * canvas.getZoom());
                    textFieldInput.setBounds(x, y, window.getWidth() - x, fieldHeight);
                    canvas.setLayout(null);
                    canvas.add(textFieldInput);
                    textFieldInput.requestFocusInWindow();
                    activeTextField = textFieldInput;

                    textFieldInput.addActionListener(ev -> commitText(canvas, textFieldInput));
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
                canvas.repaint();
            }
        });

        //Detecting drag
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e){
                Point imgPt = canvas.toImageCoords(e.getPoint());
                if (getBrushMode() == 4) {
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
                coordLabel.setText("X: " + p.x + ", Y: " + p.y);
            }
        });

        // SideBar Button Logic

        newButton.addActionListener(a -> {
            canvas.clearAll();
            canvas.importImage();
        });
        fileButton.addActionListener(a -> {
            canvas.exportImage();
        });
        settingButton.addActionListener(a -> {
            SettingsDialog settingsDialog = new SettingsDialog(window, (theme, layout) -> {
                System.out.println("Theme selected: " + theme);
                System.out.println("Layout selected: " + layout);
            });
            settingsDialog.setVisible(true);
        });
        
        // --- Zoom slider logic ---
        zoomSlider.addChangeListener(e -> {
            int zoom = zoomSlider.getValue();
            zoomLabel.setText("Zoom: " + zoom + "%");
            canvas.setZoom(zoom / 100.0);
        });

        // --- Mouse coordinate display ---
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = canvas.toImageCoords(e.getPoint());
                coordLabel.setText("X: " + p.x + ", Y: " + p.y);
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = canvas.toImageCoords(e.getPoint());
                coordLabel.setText("X: " + p.x + ", Y: " + p.y);
            }
        });
    }

    public static void setBrushMode(int mode) {brushMode = mode;}
    public static int getBrushMode(){return brushMode;}
    // Add this helper method to App class (outside main)
    private static Color getContrastColor(Color color) {
        // Calculate luminance
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        return luminance > 0.5 ? Color.black : Color.white;
    }
}