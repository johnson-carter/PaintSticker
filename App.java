import java.awt.*;
import javax.swing.*;

public class App{
    
    public static void main(String[] args) {
        JFrame window = new JFrame("PaintSticker");
        MyCanvas canvas = new MyCanvas();
        JPanel topTray = new JPanel();
        JButton enterButton = new JButton("Enter");
        JSlider xCord = new JSlider(0, 1080);
        JSlider yCord = new JSlider(0, 680);
        String[] stickersList = {"Tree", "Rock", "Mountain", "Bird", "Moose", "Flower"};
        JComboBox<String> selectSticker = new JComboBox<>(stickersList);

        enterButton.setFont(new Font("Calibri light", Font.BOLD, 14));
        enterButton.setBackground(Color.white);
        enterButton.setBounds(window.getWidth() - 50, 10, 100, 30 );
        

        selectSticker.setFont(new Font("Calibri light", Font.BOLD, 14));
        

        xCord.setBackground(new Color(50, 45, 49));
        yCord.setBackground(new Color(50, 45, 49));

        topTray.setBackground(new Color(50, 45, 49));
        topTray.setPreferredSize(new Dimension(window.getWidth(), 40));
        topTray.add(selectSticker);
        topTray.add(xCord);
        topTray.add(yCord);
        topTray.add(enterButton);
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
        
        enterButton.addActionListener(e -> {
            String stickerChosen = (String) selectSticker.getSelectedItem();
            int xSlider = xCord.getValue();
            int ySlider = yCord.getValue();
 
            canvas.setSticker(stickerChosen);
            canvas.setCoordinates(xSlider, ySlider);
            canvas.repaint();
        });
        
    }
    
}