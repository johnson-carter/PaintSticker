import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class App{
    
    public static void main(String[] args) {

        //Defines window and content frames
        JFrame window = new JFrame("PaintSticker");
        MyCanvas canvas = new MyCanvas();
        //int brushStatus = 0;
        JPanel topTray = new JPanel();
        JPanel sideBar = new JPanel();
        Color sysColor = new Color(50, 45, 49);
        Color sysDark = new Color(40, 32, 39);
        Color sysLight = new Color(240, 240, 240);
        Color colorSel = Color.black;

        //Defines top bar components
        ImageIcon brushIcon = new ImageIcon("images/brushIcon.png");
        ImageIcon brushHighlighted = new ImageIcon("images/brushIcon2.png");
        ImageIcon logoImg = new ImageIcon("images/logo.png");
        
        JButton resetButton = new JButton("CLEAR");
        JLabel toolkitLabel = new JLabel("Select Tool: ");
        JButton brush = new JButton(brushIcon);
        JButton eraser = new JButton();
        brush.setPreferredSize(new Dimension(30, 30));
        brush.setBackground(sysLight);

        String[] stickersList = {"SELECT A STICKER","Tree", "Rock", "Mountain", "Bird", "Moose", "Flower"};
        JComboBox<String> selectSticker = new JComboBox<>(stickersList);
        selectSticker.setFont(new Font("Arial Rounded", Font.BOLD, 15));
        
        JLabel logo = new JLabel(logoImg);
        logo.setSize(40, 40);
        String[] colorsList = {"Black", "White", "Red", "Blue", "Green"};
        JComboBox<String> selectColor = new JComboBox<>(colorsList);
        selectColor.setFont(new Font("Arial Rounded", Font.BOLD, 15));
      
        
//        resetButton.setMargin(new Insets(5, 15, 5, 15));
        resetButton.setFont(new Font("Cambria", Font.BOLD, 15));
        resetButton.setBackground(Color.red);
        resetButton.setForeground(Color.white);
        resetButton.setSize(new Dimension(50, 20));

        FlowLayout topLayout = new FlowLayout(FlowLayout.LEFT);
        topTray.setBackground(Color.white);
        topTray.setPreferredSize(new Dimension(window.getWidth(), 50));
        topTray.setLayout(topLayout);
        topTray.add(logo);
        topTray.add(selectSticker);
        /*
        topTray.add(xCord);
        topTray.add(yCord);
        topTray.add(enterButton); */
        topTray.add(resetButton);
        topTray.add(toolkitLabel);
        topTray.add(brush);
        topTray.add(eraser);
        topTray.add(selectColor);
        topTray.setVisible(true);

        /*Enter Button and original coordinate sliders
        enterButton.setFont(new Font("Berlin Sans FB", Font.BOLD, 15));
        enterButton.setBackground(Color.white);
        enterButton.setForeground(new Color(20, 80, 30));
        enterButton.setBounds(window.getWidth() - 50, 10, 100, 30 );

        xCord.setBackground(new Color(50, 45, 49));
        xCord.setForeground(Color.red);
        xCord.setMajorTickSpacing((1080 / 10));
        xCord.setMinorTickSpacing((1080 / 30));
        xCord.setPaintTicks(true);

        yCord.setBackground(new Color(50, 45, 49));
        yCord.setForeground(Color.red);
        yCord.setMajorTickSpacing(720/10);
        yCord.setMinorTickSpacing(720/30);
        yCord.setPaintTicks(true);
        */

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
        sideBar.add(settingButton);
        settingButton.setComponentPopupMenu(settingsMenu);

        
        
        
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(canvas);
        window.add(sideBar, BorderLayout.WEST);
        window.add(topTray, BorderLayout.NORTH);
        window.pack();
        window.setBackground(new Color(61,61,144));
        window.setResizable(true);
        window.setMinimumSize(new Dimension(1000, 500));
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        
        /* 
        enterButton.addActionListener(e -> {
            String stickerChosen = (String) selectSticker.getSelectedItem();
            int xSlider = xCord.getValue();
            int ySlider = yCord.getValue();
 
            canvas.addItem(stickerChosen, xSlider, ySlider);
            canvas.repaint();
        });
        */
        selectSticker.addActionListener(e -> {
            String stickerChosen = (String) selectSticker.getSelectedItem();
            canvas.setBrushMode(0);
            canvas.addItem(stickerChosen);
            System.out.println("Select sticker called");
        });


        resetButton.addActionListener(e -> {
           canvas.resetList();
           canvas.setCanvasState(0);
           canvas.repaint(); 
        });

        brush.addActionListener(e -> {
            brush.setIcon(brushHighlighted);
            canvas.setBrushMode(1);
        });
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

        canvas.addMouseMotionListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e){
                canvas.addItem(e.getX(),e.getY(), colorSel);
                System.out.println("@ -- " + e.getX() + "/" + e.getY());
            }
        });
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
           public void mouseDragged(MouseEvent e){
                canvas.addFinal(e.getX(), e.getY());
                System.out.println("Following: " + e.getX() + " , " + e.getY());
           } 
        });

        
        newButton.addActionListener(e -> {
            canvas.resetList();
            canvas.setCanvasState(1);
            canvas.repaint();
        });
        fileButton.addActionListener(e -> {
            
        });
        settingButton.addActionListener(e -> {

        });


    }
    
}