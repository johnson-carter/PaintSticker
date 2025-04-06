import java.awt.*;
import javax.swing.*;


public class App{

    public static void main(String[] args) {
        JFrame window = new JFrame("PaintSticker");
        MyCanvas canvas = new MyCanvas();
        JPanel topTray = new JPanel();
        JPanel sideBar = new JPanel();
        JButton enterButton = new JButton("ADD");
        JButton resetButton = new JButton("CLEAR");
//        JTextField xIn = new JTextField("Enter X Coordinate");
//        JTextField yIn = new JTextField("Enter Y Coordinate");
        JSlider xCord = new JSlider(0, 1080);
        JSlider yCord = new JSlider(0, 720);
        String[] stickersList = {"SELECT A STICKER","Tree", "Rock", "Mountain", "Bird", "Moose", "Flower"};
        JComboBox<String> selectSticker = new JComboBox<>(stickersList);
        ImageIcon logoImg = new ImageIcon("images/logo.png");
        JLabel logo = new JLabel(logoImg);
        logo.setSize(40, 40);
        topTray.add(logo);

        
        
        selectSticker.setFont(new Font("Arial Rounded", Font.BOLD, 15));
        
        resetButton.setMargin(new Insets(5, 15, 5, 15));
        resetButton.setFont(new Font("Cambria", Font.BOLD, 15));
        resetButton.setBackground(Color.red);
        resetButton.setForeground(Color.white);
        resetButton.setSize(new Dimension(50, 20));

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

        sideBar.setBackground(new Color(50, 45, 49));
        sideBar.setPreferredSize( new Dimension(60, window.getHeight()));
        sideBar.setVisible(true);
        
        //Sidebar components: 
        
        ImageIcon newFileIcon = new ImageIcon("images/newFile.png");
        ImageIcon downloadIcon = new ImageIcon("images/download.png");
        JButton newButton = new JButton(newFileIcon);
        JButton backgroundButton = new JButton(downloadIcon);
        newButton.setBackground( new Color(40, 32, 39));
        backgroundButton.setBackground(new Color(40, 32, 39));
        sideBar.add(newButton);
        sideBar.add(backgroundButton);

        FlowLayout topLayout = new FlowLayout(FlowLayout.LEFT);
        topTray.setBackground(Color.white);
        topTray.setPreferredSize(new Dimension(window.getWidth(), 50));
        topTray.setLayout(topLayout);
        topTray.add(selectSticker);
        /*
        topTray.add(xCord);
        topTray.add(yCord);
        topTray.add(enterButton); */
        topTray.add(resetButton);
        topTray.setVisible(true);
        selectSticker.setVisible(true);
        enterButton.setVisible(true);
        xCord.setVisible(true);
        yCord.setVisible(true);
        
        
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