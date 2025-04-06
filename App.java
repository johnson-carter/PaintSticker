import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;


public class App{

    public static void main(String[] args) {

        //Defines window and content frames
        JFrame window = new JFrame("PaintSticker");
        MyCanvas canvas = new MyCanvas();
        JPanel topTray = new JPanel();
        JPanel sideBar = new JPanel();
        Color sysColor = new Color(50, 45, 49);
        Color sysDark = new Color(40, 32, 39);
        Color sysLight = new Color(240, 220, 240);

        //Defines top bar components
        ImageIcon brushIcon = new ImageIcon("images/brushIcon.png");
        ImageIcon logoImg = new ImageIcon("images/logo.png");
        
        JButton resetButton = new JButton("CLEAR");
        JButton brush = new JButton(brushIcon);
        brush.setSize(new Dimension(10, 20));
        brush.setBackground(sysLight);

        String[] stickersList = {"SELECT A STICKER","Tree", "Rock", "Mountain", "Bird", "Moose", "Flower"};
        JComboBox<String> selectSticker = new JComboBox<>(stickersList);
        selectSticker.setFont(new Font("Arial Rounded", Font.BOLD, 15));
        
        JLabel logo = new JLabel(logoImg);
        logo.setSize(40, 40);
      
        
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
        topTray.add(brush);
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
        JButton backgroundButton = new JButton(downloadIcon);
        JButton settingButton = new JButton(settingsIcon);
        newButton.setBackground( new Color(40, 32, 39));
        backgroundButton.setBackground(new Color(40, 32, 39));
        settingButton.setBackground(new Color(40, 32, 39));
        settingButton.setBounds(new Rectangle(new Dimension(60,60)));

        sideBar.add(newButton);
        sideBar.add(backgroundButton);
        sideBar.add(settingButton);

        
        
        
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
            canvas.addItem(stickerChosen);
            System.out.println("Select sticker called");
        });


        resetButton.addActionListener(e -> {
           canvas.resetList();
           canvas.setCanvasState(0);
           canvas.repaint(); 
        });
        
        newButton.addActionListener(e -> {
            canvas.resetList();
            canvas.setCanvasState(1);
            canvas.repaint();
        });


    }
    
}