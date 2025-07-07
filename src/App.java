package src;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;


public class App{
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
        ////////////////////////////////////
        //Defines window and content frames
        ////////////////////////////////////
        

        JFrame window = new JFrame("PaintSticker");
        
        //Just declared this now
        Image scaledImage;					// 1 - Circle Brush // 2 - Eraser //

        ///////////////////////////////////
        //Tookit / TopTray Components
        ///////////////////////////////////
        

        //PaintSticker logo
        ImageIcon logoImg = new ImageIcon("images/logo.png");
        scaledImage = logoImg.getImage().getScaledInstance(50, 50,   0);
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
        brush.setBackground(sysLight);
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
        //DEPRACATED -- WATCH FOR REPLACE -- String[] colorsList = {"Black", "White", "Red", "Blue", "Green"};
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
        Integer[] brushSizesList = {1, 2, 5, 10, 15, 30};
        JComboBox<Integer> brushSizeSelector = new JComboBox<>(brushSizesList);
        brushSizeSelector.setSelectedIndex(4);
        topTray.add(brushSizeSelector);
                     
        //Some alignment and settings for the TopTray
        FlowLayout topLayout = new FlowLayout(FlowLayout.LEFT);
        topTray.setBackground(sysColor);
        topTray.setPreferredSize(new Dimension(window.getWidth(), 50));
        topTray.setLayout(topLayout);
        topTray.setVisible(true);
        //TopTray Border
        MatteBorder topTrayBorder = new MatteBorder(0, 0, 2, 0, Color.black);
        EmptyBorder topTrayPadding = new EmptyBorder(00, 0, 15, 0);
        topTray.setBorder(new CompoundBorder(topTrayBorder, topTrayPadding));

        //TODO --- Add new components; shape tool?

        //////////////////////////////
        // SideBar & Components
        //////////////////////////////

        //Sidebar properties
        sideBar.setBackground(sysLight);
        sideBar.setPreferredSize( new Dimension(55, window.getHeight()));
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
                
        ////////////////////////////
        /// Window properties
        ////////////////////////////
        

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(canvas);     // Adds all of our logic from MyCanvas
        window.add(sideBar, BorderLayout.WEST);     
        window.add(topTray, BorderLayout.NORTH);
        window.pack();      // Might: set to preferred width of components?
        window.setResizable(true);
        window.setMinimumSize(new Dimension(1000, 500));
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        ///////////////////////////
        /// Button Logic
        ///////////////////////////
        

        //Undo Button
        undoButton.addActionListener(e -> {
           canvas.undoAction();
           canvas.repaint();    //Important for displaying any input that edits the canvas
        });

        //Brush Button
        brush.addActionListener(e -> {
            brush.setIcon(brushHighlighted);
            eraser.setIcon(eraserIcon);
            textField.setForeground(sysDark);
            canvas.setBrushMode(1);
            setBrushMode(1);
        });

        //Eraser Button 
        eraser.addActionListener(e -> {
            brush.setIcon(brushIcon);
            eraser.setIcon(eraserHighlighted);
            textField.setForeground(sysDark);
            canvas.setBrushMode(2);
            setBrushMode(2);
        });

        textField.addActionListener(e -> {
            brush.setIcon(brushIcon);
            eraser.setIcon(eraserIcon);
            textField.setForeground(accent1);
            canvas.setBrushMode(3);
            setBrushMode(3);
        });

        //Color Selector
        selectColor.addActionListener(e -> {
            Color colorChosen = JColorChooser.showDialog(null, "Pick a Color", Color.black);
            selectedColor = colorChosen; // Update the static variable
            selectColor.setBackground(selectedColor);
            if (colorChosen != null) {
                canvas.setColorChosen(colorChosen);
            }
        });

        // Background Color Selector
        backgroundColorButton.addActionListener(e -> {
            Color colorChosen = JColorChooser.showDialog(null, "Pick Background Color", Color.white);
            if (colorChosen != null) {
                backgroundColorButton.setBackground(colorChosen);
                canvas.setBackgroundColor(colorChosen);
            }
        });

        //Brush Size Selector
        brushSizeSelector.addActionListener(e -> {
            Integer bSizeSelected = (int) brushSizeSelector.getSelectedItem();
            canvas.chooseSize(bSizeSelected);
            
        });


        //////////////////////////
        //Mouse Input Grabber
        //////////////////////////
        
        //Detect & record clicks
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){  //Determines when click starts
                 canvas.startNewGroup();
                 canvas.newStroke(e.getX(), e.getY(), getBrushMode());
                 canvas.repaint();
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
                canvas.newStroke(e.getX(), e.getY(), getBrushMode());
                canvas.repaint();
            } 
        });


        ////////////////////////////////
        /// SideBar Button Logic
        ///////////////////////////////
        
        // ** Not yet implemented ** TODO --- Sidebar

        newButton.addActionListener(e -> {
            canvas.clearAll();
            canvas.importImage();
        });
        fileButton.addActionListener(e -> {
            canvas.exportImage();
        });
        settingButton.addActionListener(e -> {
            SettingsDialog settingsDialog = new SettingsDialog(window, (theme, layout) -> {
                System.out.println("Theme selected: " + theme);
                System.out.println("Layout selected: " + layout);
                
            });
            settingsDialog.setVisible(true);
        });
        
        //Helper functions of sorts
        
    }

    public static void setBrushMode(int mode) {brushMode = mode;}
    public static int getBrushMode(){return brushMode;}
}