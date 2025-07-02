package src;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;


public class App{

        // Simple Dark Theme
        static Color lightDark = new Color(100, 100, 100);
        static Color regularDark = new Color(60, 60, 60);
        static Color darkDark = new Color(30, 30, 30);

        static Color accentSkyBlue = new Color(135, 206, 250); // Light Sky Blue
        static Color accentRedOrange = new Color(255, 69, 0); // Red-Orange
        static Color accentLightGreen = new Color(144, 238, 144); // Light Green

        static Color sysLight = lightDark;
        static Color sysColor = regularDark;
        static Color sysDark = darkDark;
        static Color accent1 = accentSkyBlue;
        static Color accent2 = accentRedOrange;
        static Color accent3 = accentLightGreen;

     
    public static void main(String[] args) {

        MyCanvas canvas = new MyCanvas();
        JPanel topTray = new JPanel();
        JPanel sideBar = new JPanel();
        ////////////////////////////////////
        //Defines window and content frames
        ////////////////////////////////////
        

        JFrame window = new JFrame("PaintSticker");
        
        //Just declared this now
        Image scaledImage;
        int brushMode;
        Color colorSel = Color.black;

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
        undoButton.setPreferredSize(new Dimension(25, 25));
        topTray.add(undoButton);

        // Toolkit break
        JLabel toolkitLabel = new JLabel("Select Tool: ");
        topTray.add(toolkitLabel);

        //Brush Selector
        ImageIcon brushIcon = new ImageIcon("images/brushIcon.png");
        ImageIcon brushHighlighted = new ImageIcon("images/brushIcon2.png");
        JButton brush = new JButton(brushIcon);
        brush.setPreferredSize(new Dimension(30, 30));
        brush.setBackground(sysLight);
        topTray.add(brush);

        //Eraser Selector
        ImageIcon eraserIcon = new ImageIcon("images/eraser.png");
        ImageIcon eraserHighlighted = new ImageIcon("images/eraser2.png");
        JButton eraser = new JButton(eraserIcon);
        eraser.setPreferredSize(new Dimension(30, 30));
        eraser.setBackground(sysLight);
        topTray.add(eraser);

        //Color Selector   
        String[] colorsList = {"Black", "White", "Red", "Blue", "Green"};
        JComboBox<String> selectColor = new JComboBox<>(colorsList);
        selectColor.setFont(new Font("Arial Rounded", Font.BOLD, 15));
        topTray.add(selectColor);

        //Brush Size Selector
        Integer[] brushSizesList = {1, 2, 5, 10, 15, 30};
        JComboBox<Integer> brushSizeSelector;
        brushSizeSelector = new JComboBox<>(brushSizesList);
        topTray.add(brushSizeSelector);
                     
        //Some alignment and settings for the TopTray
        FlowLayout topLayout = new FlowLayout(FlowLayout.LEFT);
        topTray.setBackground(sysLight);
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
        sideBar.setBackground(sysColor);
        sideBar.setPreferredSize( new Dimension(55, window.getHeight()));
        sideBar.setVisible(true);

        int scaledImageSize = 40;
        int sideBarButtonSize = 45;
        Dimension sideBarButtonDimension = new Dimension(sideBarButtonSize, sideBarButtonSize);
        
        //Sidebar components: 
        ImageIcon addIcon = new ImageIcon("images/addIcon.png");
        scaledImage = addIcon.getImage().getScaledInstance(scaledImageSize, scaledImageSize, 0);
        addIcon.setImage(scaledImage);
        JButton newButton = new JButton(addIcon);
        newButton.setBackground(lightDark);
        newButton.setPreferredSize(sideBarButtonDimension);

        ImageIcon saveIcon = new ImageIcon("images/saveIcon.png");
        scaledImage = saveIcon.getImage().getScaledInstance(scaledImageSize, scaledImageSize, 0);
        saveIcon.setImage(scaledImage);
        JButton fileButton = new JButton(saveIcon);
        fileButton.setBackground(lightDark);
        fileButton.setPreferredSize(sideBarButtonDimension);
        
        ImageIcon settingsIcon = new ImageIcon("images/settingsIcon.png");
        scaledImage = settingsIcon.getImage().getScaledInstance(scaledImageSize, scaledImageSize, 0);
        settingsIcon.setImage(scaledImage);
        JButton settingButton = new JButton(settingsIcon);
        settingButton.setBackground(lightDark);
        settingButton.setPreferredSize(sideBarButtonDimension);


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
            canvas.setBrushMode(1);
        });

        //Eraser Button 
        eraser.addActionListener(e -> {
            brush.setIcon(brushIcon);
            eraser.setIcon(eraserHighlighted);
            canvas.setBrushMode(2);
              //TODO --- Implement eraser
        });

        //Color Selector
        selectColor.addActionListener(e ->{
            Color colorChosen = colorSel;
            String stickerChose = (String) selectColor.getSelectedItem();
            if(stickerChose.equals("Red")){
                colorChosen = Color.red;
            } else if (stickerChose.equals("Blue")){
                colorChosen = Color.blue;
            } else if (stickerChose.equals("Green")){
                colorChosen = Color.green;
            } else if (stickerChose.equals("Black")){
                colorChosen = Color.black;
            } else if (stickerChose.equals("White")){
                colorChosen = Color.white;
            }
            canvas.setColorChosen(colorChosen);
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
            public void mousePressed(MouseEvent e){  //Determines when click starts
                 canvas.startNewGroup();
                 canvas.newStroke(e.getX(), e.getY());
                 canvas.repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e){
                canvas.repaint();
            }
        });
        //Detecting drag
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
           public void mouseDragged(MouseEvent e){
                canvas.newStroke(e.getX(), e.getY());
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
    }
} 