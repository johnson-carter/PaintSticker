package src;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class App{
    public static void main(String[] args) {


        ////////////////////////////////////
        //Defines window and content frames
        ////////////////////////////////////
        
        
        JFrame window = new JFrame("PaintSticker");
        //Custom JPanel object - see MyCanvas.Java
        MyCanvas canvas = new MyCanvas();
        
        // GUI panels
        JPanel topTray = new JPanel();
        JPanel sideBar = new JPanel();

        // Color variables - Update for new theme.
        Color sysColor = new Color(50, 45, 49);
        Color sysDark = new Color(40, 32, 39);
        Color sysLight = new Color(240, 240, 240);
        Color colorSel = Color.black;
                    // TODO --- add themes, selector in settings.
        
        ///////////////////////////////////
        //Tookit / TopTray Components
        ///////////////////////////////////
        
        //PaintSticker logo
        ImageIcon logoImg = new ImageIcon("images/logo.png");
        JLabel logo = new JLabel(logoImg);
        logo.setSize(40, 40);
        topTray.add(logo);
        

        //Reset Button
        JButton undoButton = new JButton("Undo");
        undoButton.setFont(new Font("Cambria", Font.BOLD, 15));
        undoButton.setBackground(Color.red);
        undoButton.setForeground(Color.white);
        undoButton.setSize(new Dimension(50, 20));
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

        //////////////////////////////
        // SideBar & Components
        //////////////////////////////

        //Sidebar properties
        sideBar.setBackground(sysColor);
        sideBar.setPreferredSize( new Dimension(60, window.getHeight()));
        sideBar.setVisible(true);
        
        //Sidebar components: 
        ImageIcon newFileIcon = new ImageIcon("images/newFile.png");
        ImageIcon downloadIcon = new ImageIcon("images/download.png");
        ImageIcon settingsIcon = new ImageIcon("images/settings.png");
        JButton newButton = new JButton(newFileIcon);
        JButton fileButton = new JButton(downloadIcon);
        JButton settingButton = new JButton(settingsIcon);
        JPopupMenu settingsMenu = new JPopupMenu("Appliication Settings");

        newButton.setBackground( new Color(40, 32, 39));
        fileButton.setBackground(new Color(40, 32, 39));
        settingButton.setBackground(new Color(40, 32, 39));
        settingButton.setBounds(new Rectangle(new Dimension(60,60)));

        sideBar.add(newButton);
        sideBar.add(fileButton);
        sideBar.add(settingButton, BorderLayout.SOUTH);
        settingButton.setComponentPopupMenu(settingsMenu);
                
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
        
        // ** Not yet implemented **

        newButton.addActionListener(e -> {
            canvas.clearAll();
            canvas.setCanvasState(1);
            canvas.repaint();
        });
        fileButton.addActionListener(e -> {
            
        });
        settingButton.addActionListener(e -> {

        });


    }
    
}