import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;

public class LoadWindow extends Main{
	private JButton classic;
	private JButton imperium;
	private JButton loader;
	private JTextField pather;
	private static GridBagger window;
	private Map map;
	private static String classicMap = "classic";
	private static String imperiumMap = "imperium";
	private static String europeMap = "europe";
	
	public LoadWindow() {
		makeApplet();
	}
	public void makeApplet() {
		Container contentPane = getContentPane();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		contentPane.setLayout(gridbag);
		c.gridwidth = 3;
		c.weightx = 2;
		
		classic = new JButton("Make Classic Map");
		classic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				window = new GridBagger(false, classicMap);
				mapWindow = window;
		        System.out.println("classic");
		        window.inAnApplet = false;
		        window.setTitle("Earf");
		        window.pack();
		        window.setVisible(true);
		        setVisible(false);
			}
		});
		c.insets = new Insets(20, 70, 90, 50); //top left bottom right//
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(classic, c);
		contentPane.add(classic);
		
		imperium = new JButton("Make Imperium Map");
		imperium.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				window = new GridBagger(true, europeMap);
				mapWindow = window;
		        System.out.println(europeMap);
		        window.inAnApplet = false;
		        window.setTitle("Earf");
		        window.pack();
		        window.setVisible(true);
		        setVisible(false);
			}
		});
		c.insets = new Insets(90, 70, 50, 70);
		c.gridx = 1;
		c.gridy = 0;
		gridbag.setConstraints(imperium, c);
		contentPane.add(imperium);
		
		pather = new JTextField();
		pather.setColumns(8);
		pather.addKeyListener(new KeyAdapter() {
	        @Override
	        public void keyPressed(KeyEvent e) {
	            if(e.getKeyCode() == KeyEvent.VK_ENTER)loader.doClick();
	        }
	    });
		c.insets = new Insets(90, 70, 50, 70);
		c.gridx = 2;
		c.gridy = 1;
		gridbag.setConstraints(pather, c);
		contentPane.add(pather);
		
		loader = new JButton("Load Map");
		loader.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				window = new GridBagger(true, europeMap);
				mapWindow = window;
				window.loadState(pather.getText().toString());
		        System.out.println("loaded");
		        window.inAnApplet = false;
		        window.setTitle("Earf");
		        window.pack();
		        window.setVisible(true);
		        setVisible(false);
			}
		});
		c.insets = new Insets(90, 70, 0, 70);
		c.gridx = 2;
		c.gridy = 1;
		gridbag.setConstraints(loader, c);
		contentPane.add(loader);
	}
	public static GridBagger getWindow() {return window;}
}
