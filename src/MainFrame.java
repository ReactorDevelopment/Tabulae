import java.awt.EventQueue;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Component;
import javax.swing.BorderFactory;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.Toolkit;
import java.awt.LayoutManager;
import javax.swing.JLabel;
import javax.swing.JFrame;

public class MainFrame extends JFrame
{
    private MainPanel mainPanel;
    private JLabel infoLabel;
    
    public MainFrame() {
        this.initComponents();
    }
    
    private void initComponents() {
        this.setDefaultCloseOperation(3);
        this.setExtendedState(6);
        this.setLayout(null);
        this.setTitle("Zoomable Panel");
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int width = (int)screenSize.getWidth();
        final int height = (int)screenSize.getHeight();
        try {
            final BufferedImage image = ImageIO.read(new File("C:/Users/agent/OneDrive/Documents/pgrm/TabuleaResources/maps/classicOutline.png"));
            (this.mainPanel = new MainPanel(image)).setBounds(50, 50, width - 100, height - 240);
            this.mainPanel.setBorder(BorderFactory.createLineBorder(Color.black));
            this.add((Component)this.mainPanel);
            this.mainPanel.setVisible(true);
        }
        catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        (this.infoLabel = new JLabel("Roll to zoom. Click and drag to move.", 0)).setFont(new Font(this.infoLabel.getFont().getFontName(), 0, 26));
        this.infoLabel.setBounds(50, height - 180, width - 100, 80);
        this.add(this.infoLabel);
        this.infoLabel.setVisible(true);
    }
    
    
}
