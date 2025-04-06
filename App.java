import java.awt.*;
import javax.swing.*;


public class App{
    public static void main(String[] args) {
        JFrame window = new JFrame("PaintSticker");
        MyCanvas canvas = new MyCanvas();
        JPanel topTray = new JPanel();
        JButton enterButton = new JButton("ADD");
        JButton resetButton = new JButton("DELETE");
//        JTextField xIn = new JTextField("Enter X Coordinate");
//        JTextField yIn = new JTextField("Enter Y Coordinate");
        JSlider xCord = new JSlider(0, 1080);
        JSlider yCord = new JSlider(0, 720);
        String[] stickersList = {"Tree", "Rock", "Mountain", "Bird", "Moose", "Flower"};
        JComboBox<String> selectSticker = new JComboBox<>(stickersList);
        

        //
        //Properties for combobox and button
        //
        enterButton.setFont(new Font("Berlin Sans FB", Font.BOLD, 15));
        enterButton.setBackground(Color.white);
        enterButton.setForeground(new Color(20, 80, 30));
        enterButton.setBounds(window.getWidth() - 50, 10, 100, 30 );

        resetButton.setFont(new Font("Berlin Sans FB", Font.BOLD, 15));
        resetButton.setBackground(Color.white);
        resetButton.setForeground(new Color(50, 20, 20));
        resetButton.setBounds(window.getWidth() - 50, 10, 100, 30 );
        selectSticker.setFont(new Font("Arial Rounded", Font.BOLD, 15));
        
        //
        // Sets the properties for (x,y) sliders
        //
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

        topTray.setBackground(new Color(50, 45, 49));
        topTray.setPreferredSize(new Dimension(window.getWidth(), 40));
        topTray.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
        topTray.add(selectSticker);
        topTray.add(xCord);
        topTray.add(yCord);
        topTray.add(enterButton);
        topTray.add(resetButton);
        topTray.setVisible(true);
        selectSticker.setVisible(true);
        enterButton.setVisible(true);
        xCord.setVisible(true);
        yCord.setVisible(true);
        
        
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(canvas);
        window.add(topTray, BorderLayout.NORTH);
        window.pack();
        window.setBackground(new Color(61,61,144));
        window.setResizable(false);
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
           canvas.repaint(); 
        });
        
    }
    
}