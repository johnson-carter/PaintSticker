package src;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;


public class App {
    private static int brushMode;
    private static Color selectedColor = Color.black;
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

        //Color Selector
        JButton selectColor = new JButton("Select Color");
        selectColor.setFont(new Font("Verdana", Font.PLAIN, 12));
        selectColor.setPreferredSize(new Dimension(120,30));
        selectColor.setBackground(selectedColor);
        selectColor.setFocusPainted(false);
        selectColor.setBorder(new LineBorder(sysDark, 2));
        selectColor.setForeground(sysLight);
        topTray.add(selectColor);

        // Background Color Selector
        JButton backgroundColorButton = new JButton("Background");
        backgroundColorButton.setFont(new Font("Verdana", Font.PLAIN, 12));
        backgroundColorButton.setPreferredSize(new Dimension(120,30));
        backgroundColorButton.setBackground(Color.white);
        backgroundColorButton.setFocusPainted(false);
        backgroundColorButton.setBorder(new LineBorder(sysDark, 2));
        backgroundColorButton.setForeground(sysLight);
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

        //Color Selector
        selectColor.addActionListener(a -> {
            Color colorChosen = JColorChooser.showDialog(null, "Pick a Color", Color.black);
            selectedColor = colorChosen;
            selectColor.setBackground(selectedColor);
            if (colorChosen != null) {
                canvas.setColorChosen(colorChosen);
            }
        });

        // Background Color Selector
        backgroundColorButton.addActionListener(a -> {
            CanvasSettingsDialog dialog = new CanvasSettingsDialog(window, canvas.getWidth(), canvas.getHeight(), canvas.getBackground());
            dialog.setVisible(true);

            if (dialog.isApproved()) {
                Color colorChosen = dialog.getSelectedColor();
                int width = dialog.getCanvasWidth();
                int height = dialog.getCanvasHeight();
                backgroundColorButton.setBackground(colorChosen);
                canvas.setBackgroundColor(colorChosen);
                canvas.setPreferredSize(new Dimension(width, height));
                canvas.revalidate();
                canvas.repaint();
                window.pack();
            }
        });

        //Brush Size Selector
        brushSizeSelector.addActionListener(a -> {
            Integer bSizeSelected = (int) brushSizeSelector.getSelectedItem();
            canvas.chooseSize(bSizeSelected);
        });


        //Mouse Input Grabber
        canvas.addMouseListener(new MouseAdapter() {
            JTextField activeTextField = null;

            private void commitText(MyCanvas canvas, JTextField textField) {
                if (textField != null) {
                    String input = textField.getText();
                    if (input != null && !input.trim().isEmpty()) {
                        canvas.addTextStroke(textField.getX(), textField.getY() + textField.getHeight() - 5, input.trim());
                    }
                    canvas.remove(textField);
                    canvas.repaint();
                    activeTextField = null;
                }
            }

            @Override
            public void mousePressed(MouseEvent e){
                if (getBrushMode() == 3) {
                    if (activeTextField != null) {
                        return;
                    }
                    JTextField textFieldInput = new JTextField();
                    textFieldInput.setFont(new Font("Verdana", Font.BOLD, canvas.getFont().getSize() * 2));
                    textFieldInput.setForeground(canvas.getForeground());
                    textFieldInput.setBackground(new Color(255,255,255,180));
                    textFieldInput.setBorder(BorderFactory.createLineBorder(accent1));
                    int fieldHeight = textFieldInput.getFont().getSize() + 10;
                    textFieldInput.setBounds(e.getX(), e.getY(), 200, fieldHeight);
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
                } else {
                    canvas.startNewGroup();
                    canvas.newStroke(e.getX(), e.getY());
                    canvas.repaint();
                }
            }
            @Override
            public void mouseReleased(MouseEvent e){
                canvas.repaint();
            }
        });

        //Detecting drag
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e){
                canvas.newStroke(e.getX(), e.getY());
                canvas.repaint();
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
    }

    public static void setBrushMode(int mode) {brushMode = mode;}
    public static int getBrushMode(){return brushMode;}
}