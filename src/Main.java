import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Main extends JFrame{
	public static LoadWindow window;
	public static GridBagger mapWindow;
	public static void main(String args[]) {
		window = new LoadWindow();
        System.out.println("ee");
        window.setTitle("Introp");
        window.setSize(500, 500);
        window.pack();
        window.setVisible(true);
        /*MainFrame main = new MainFrame();
        main.pack();
        main.setVisible(true);
        /*BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Point[] points = {new Point(30, 50)};
	    try {
			img = ImageIO.read(new File("C:/Users/agent/OneDrive/Documents/pgrm/TabuleaResources/overlayTest.png"));
		} catch (Exception e) {e.printStackTrace();}
	    OverlayMaker overlayer = new OverlayMaker(img, points);
	    overlayer.makeOverlays();
	    overlayer.saveImages("C:/Users/agent/OneDrive/Documents/pgrm/TabuleaResources/", "tst");
        JFrame frame = new JFrame();
	    frame.setSize(100, 100);
	    frame.setLayout(null);
	    Map map = new Map("background");
	    BufferedImage saved = img;
	    map.addImage(saved);
	    map.setSize(100, 100);
	    map.setBounds(0, 0, 100, 100);
	    frame.add(map);
	    //floodCopy(img, 5, 4, new Color(25, 155, 105).getRGB());
	    frame.setPreferredSize(new Dimension(200, 200));
	    frame.pack();
	    //frame.setVisible(true);*/
    }
	public static GridBagger getWindow() {return window.getWindow();}
	
	public static GridBagger getMapWindow() {return mapWindow;}
	
	protected String formatInt(int num, int digits) {
		String formatted = "";
		for(int i=0; i<digits-(""+num).length(); i++) {
			formatted += "0";
		}
		return formatted + num;
	}
	protected String formatDouble(double num, int digits) {
		String formatted = "";
		formatted = ""+num;
		if(formatted.length() >= digits) formatted = formatted.substring(0, digits);
		else 
			for(int i=0; i<digits-formatted.length(); i++)
				formatted = "0"+formatted;
		return formatted;
	}
}
