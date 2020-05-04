import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.NumericShaper.Range;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GridBagger extends Main{
	boolean inAnApplet = true;
	public static final int MAP_H = 500;
	private String path;
	private JButton maker; 
	private static JButton cMaker;
	private JButton borderer;
	private JButton adder;
	private JButton changer;
	private JButton renamer;
	private JButton printer;
	private JButton mover;
	private JButton remover;
	private JButton updater;
	private JButton saver;
	private JButton overlayer;
	private JButton overAdder;
	private JTextField saveWriter;
	private JLayeredPane mapHolder;
	protected JTextField infoField;
	protected static JTextField codeOut;
	public Province lastProv;
	private Map map;
	private String saveName;
	private String full = "";
	private String last = "";
	private String mapFile;
	private int currentId = 0;
	private int currentCId = -1;
	private int state = 0;
	private ArrayList<Province> provinces;
	private ArrayList<Continent> continents;
	private boolean imperium;
	protected Province selected;
	protected Continent cSelected;
	protected BufferedImage[] overlayImages;
	private static JLayeredPane contentPane;
	protected static boolean changeFull;
	private GridBagLayout gridbag = new GridBagLayout();
	private GridBagConstraints c = new GridBagConstraints();
	
	public GridBagger() {}
	public GridBagger(boolean imperium, String mapFile) {
		contentPane = new JLayeredPane();
		path = "C:/Users/agent/OneDrive/Documents/pgrm/TabuleaResources/";
		this.imperium = imperium;
		saveName = "unnamed";
		provinces = new ArrayList<>(0);
		continents = new ArrayList<>(0);
		overlayImages = new BufferedImage[0];
		changeFull = true;
		this.mapFile = mapFile;
		makeApplet();
		update();
		getContentPane().add(contentPane);
		System.out.println("d");
	}
	public void makeApplet() {
		contentPane.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				Graphics2D maphGraph = (Graphics2D) mapHolder.getGraphics();
				AffineTransform transform = new AffineTransform();
				transform.scale(2, 2);
				maphGraph.transform(transform);
			}
		});
		contentPane.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL; 
		//c.gridwidth = 5;
		infoField = new JTextField();
		infoField.setText("Last Click X: "+ 0 + ", Y: " + 0 +"|");
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 3;
		gridbag.setConstraints(infoField, c);
		contentPane.add(infoField);
		
		codeOut = new JTextField();
		codeOut.addKeyListener(new KeyAdapter() {
	        @Override
	        public void keyPressed(KeyEvent e) {
	            if(e.getKeyCode() == KeyEvent.VK_ENTER)updater.doClick();
	        }
	    });
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 5;
		gridbag.setConstraints(codeOut, c);
		contentPane.add(codeOut);
		c.gridwidth = 1;
		maker = new JButton("Make Province");
		maker.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("made");
				last = "" + map.getClickX() + ", " + map.getClickY();
				full += last;
				provinces.add(new Province(provinces.size()+1, map.getClickX(), map.getClickY(), mapHolder, imperium));
				JButton provButton = provinces.get(provinces.size()-1).getButton();
				mapHolder.add(provButton, 0);
				//mapHolder.setComponentZOrder(provButton, 1);
			}
		});
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = .25;
		gridbag.setConstraints(maker, c);
		contentPane.add(maker);
		cMaker = new JButton("Make Continent");
		cMaker.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentCId ++;
				last = "" + map.getClickX() + ", " + map.getClickY();
				full += last;
				continents.add(new Continent(currentCId, map.getClickX(), map.getClickY(), mapHolder, imperium));
				mapHolder.add(continents.get(continents.size()-1).getButton(), 0);
			}
		});
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = .25;
		gridbag.setConstraints(cMaker, c);
		if(!imperium) contentPane.add(cMaker);
		
		changer = new JButton("Change info");
		changer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeFull = true;
				if(selected != null) codeOut.setText(selected.saveCode());
				if(cSelected != null) codeOut.setText(cSelected.saveCode());
			}});
		if(!imperium) c.gridx = 2;
		else c.gridx = 1;
		c.gridy = 0;
		c.weightx = .5;
		gridbag.setConstraints(changer, c);
		contentPane.add(changer);
		
		updater = new JButton("Update info");
		updater.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selected != null) {
					if(changeFull) selected.parseCode(codeOut.getText().substring(3));
					else selected.parseName(codeOut.getText());
				}
				if(cSelected != null) {
					if(changeFull) cSelected.parseCode(codeOut.getText().substring(3));
					else cSelected.parseName(codeOut.getText());
				}
				unCSelectAll(); unselectAll();
				codeOut.setText("");
			}});
		c.gridx = 3;
		c.gridy = 5;
		c.weightx = .25;
		gridbag.setConstraints(updater, c);
		contentPane.add(updater);
		
		saveWriter = new JTextField("File Name");
		c.gridx = 3;
		c.gridy = 0;
		c.weightx = .25;
		gridbag.setConstraints(saveWriter, c);
		contentPane.add(saveWriter);
		
		saver = new JButton("Save File");
		saver.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = saveWriter.getText().toString();
				if(name.equals("")) name = "unnamed";
				saveName = name;
				saveState(name);
			}});
		c.gridx = 3;
		c.gridy = 1;
		c.weightx = .25;
		gridbag.setConstraints(saver, c);
		contentPane.add(saver);
		
		overlayer = new JButton("Make Overlay(s)");
		overlayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selected == null) {
					for (Province p : provinces) {
						p.overAdds = Province.OVER_ADD_START;
					}
					makeOverlays();
				}else makeOverlays(selected.getId());
			}});
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = .5;
		gridbag.setConstraints(overlayer, c);
		contentPane.add(overlayer);
		
		renamer = new JButton("Rename");
		renamer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeFull = false;
				if(selected != null) {
					String code = selected.saveCode();
					codeOut.setText(code.substring(code.indexOf('}')+2, code.indexOf('|')-1));
				}
				if(cSelected != null) {
					String code = cSelected.saveCode();
					codeOut.setText(code.substring(code.indexOf('}')+2, code.indexOf('|')-1));
				}
			}});
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = .5;
		gridbag.setConstraints(renamer, c);
		contentPane.add(renamer);
		
		printer = new JButton("OverWrite Code");
		printer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String code = makeClassicHeader(saveName) + printCode();
				codeOut.setText(code);
				if(!saveName.equals("unnamed"))
			    try {
			    	if(!new File(path+"/code/").exists())
						new File(path+"/code/").mkdir();
			    	FileWriter fileWriter = new FileWriter(path+"code/"+saveName+"Code.txt");
					fileWriter.write(code);
					fileWriter.close();
					fileWriter = new FileWriter("C:/Users/agent/OneDrive/Documents/pgrm/Imperium/app/src/main/java/com/reactordevelopment/imperium/"
							+ ""+saveName.substring(0, 1).toUpperCase()+saveName.substring(1)+".java");
					fileWriter.write(code);
					fileWriter.close();
				} catch (IOException e1) {e1.printStackTrace();}
			}});
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = .5;
		gridbag.setConstraints(printer, c);
		contentPane.add(printer);
		
		mover = new JButton("Move");
		mover.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selected != null) selected.move((double)map.getClickX(), map.getClickY());
				if(cSelected != null) cSelected.move((double)map.getClickX(), map.getClickY());
			}});
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = .5;
		gridbag.setConstraints(mover, c);
		contentPane.add(mover);
		
		remover = new JButton("Remove");
		remover.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selected != null) selected.safeDelete();
				if(cSelected != null) cSelected.safeDelete();
			}});
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = .5;
		gridbag.setConstraints(remover, c);
		contentPane.add(remover);
		
		overAdder = new JButton("Add to Overlay");
		overAdder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//addToOverlays();
			}});
		c.gridx = 2;
		c.gridy = 2;
		c.weightx = .5;
		gridbag.setConstraints(overAdder, c);
		contentPane.add(overAdder);
		
		mapHolder = new JLayeredPane();
		mapHolder.addKeyListener(new KeyAdapter() {
	        @Override
	        public void keyPressed(KeyEvent e) {
	            if(e.getKeyCode() == KeyEvent.VK_P)maker.doClick();
	            else if(e.getKeyCode() == KeyEvent.VK_C)cMaker.doClick();
	        }
	    });
		c.weightx = 0.3;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 3;
		gridbag.setConstraints(mapHolder, c);
		createAndShowGUI();
		contentPane.add(mapHolder);
		pack();
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	try {saveState("tmp");}
            		catch (Exception e2) {e2.printStackTrace();}
                if (inAnApplet) {
                    dispose();
                } else {
                    System.exit(0);
                }
            }
        });
	}
	public void setCodeOut(String set) {
		codeOut.setText(set);
	}
	/*private void addToOverlays() {
		File overPath = new File(path+"overlays/"+saveName+"Map/");
		if(overlayImages.length < provinces.size()) {
			overlayImages = new BufferedImage[overPath.listFiles().length];
			for(int i=0; i<overPath.listFiles().length; i++) 
				try {
					if(new File(path+"overlays/"+saveName+"Map/"+i+".png").exists())
						overlayImages[i] = ImageIO.read(new File(path+"overlays/"+saveName+"Map/"+i+".png"));
		        } catch (IOException ex) {ex.printStackTrace();}}
		int scaledClickX = 0;
		int scaledClickY = 0;
		try {
			AffineTransform trans = map.getTransform().createInverse();
			scaledClickX = (int) (map.getClickX()*trans.getScaleX() + trans.getTranslateX());
			scaledClickY = (int) (map.getClickY()*trans.getScaleY() + trans.getTranslateY());
			} catch (NoninvertibleTransformException e2) {e2.printStackTrace();}
		Point[] scaledClick = {new Point(scaledClickX, scaledClickY)};
		OverlayMaker adder = new OverlayMaker(scaledClick);
		System.out.print("addingg________________________________________________________________");
		BufferedImage addedImage = adder.makeOverlays()[0];
		try {
	    	File imageFile = new File(path+"overlays/"+saveName+"Map/added.png");
			ImageIO.write(addedImage, "png", imageFile);
		} catch (IOException e1) {e1.printStackTrace();}
		
		Point compStart = new Point(0,0);
		double scale = map.getImgScale();
		//determine composite start
		if(adder.getStarts()[0].x < getSelected().overX) compStart.x = adder.getStarts()[0].x;
		else compStart.setLocation(selected.overX, compStart.getY());
		if(adder.getStarts()[0].y < getSelected().overY) compStart.y = adder.getStarts()[0].y;
		else compStart.setLocation(compStart.getX(), selected.overY);
		BufferedImage mapImage = map.getImage();
		BufferedImage selectedImage = null;
		try {
            selectedImage = ImageIO.read(new File(path+"overlays/"+saveName+"Map/"+getSelected().getId()+".png"));
        } catch (IOException ex) {ex.printStackTrace();}
		
		
		BufferedImage composite = new BufferedImage(map.getImage().getWidth(), map.getImage().getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		Point end = new Point(0,0);
		Point start = new Point(composite.getWidth(), composite.getHeight());
		Point adderStart = new Point((int)(adder.getStarts()[0].x/scale), (int)(adder.getStarts()[0].y/scale));
		Point selectedStart = new Point((int)(selected.overX/scale), (int)(selected.overY/scale));
		for(int x=0; x<composite.getWidth(); x++) {
			for(int y=0; y<composite.getHeight(); y++) {
				
				if(x > selectedStart.x && x < selectedStart.x+selectedImage.getWidth() && y > selectedStart.y && y < selectedStart.y+selectedImage.getHeight()) {
					if(new Color(selectedImage.getRGB(x-selectedStart.x, y-selectedStart.y)).getRGB() != -16777216) {
						composite.setRGB(x, y, mapImage.getRGB(x+selected.overAdds, y+selected.overAdds));
					}
				}
				//System.out.println("inY: "+x+", "+y+", "+adderStart.x+", "+adderStart.y+",("+selectedStart.x+", "+selectedStart.y);
				if((x > adderStart.x && x <  adderStart.x+addedImage.getWidth()) & (y > adderStart.y && y < adderStart.y+addedImage.getHeight())) {
					//System.out.println("addedTop");
					if(new Color(addedImage.getRGB(x-adderStart.x, y-adderStart.y)).getRGB() != -16777216) {
						System.out.println("addedNext"+new Color(addedImage.getRGB(x-adderStart.x, y-adderStart.y)).getRGB());
						composite.setRGB(x, y, mapImage.getRGB(x+Province.OVER_ADD_START, y+Province.OVER_ADD_START));
						if(x > end.x) end.x = x; if(y > end.y) end.y = y;
						if(x < start.x) start.x = x; if(y < start.y) start.y = y;
					}
					
				}
				else if(x > selectedStart.x && x < selectedStart.x+selectedImage.getWidth() && y > selectedStart.y && y < selectedStart.y+selectedImage.getHeight()) {
					//System.out.println("overTop");
					if(new Color(selectedImage.getRGB(x-selectedStart.x, y-selectedStart.y)).getRGB() != -16777216) {
						//System.out.println("overNext");
						composite.setRGB(x, y, mapImage.getRGB(x+selected.overAdds, y+selected.overAdds));
						//x+(int)((compStart.x-selectedStart.x)), y+(int)((compStart.y-selectedStart.y))
						if(x > end.x) end.x = x; if(y > end.y) end.y = y;
						if(x < start.x) start.x = x; if(y < start.y) start.y = y;
					}
				}
			}
		}
		compStart.setLocation(start.x*scale, start.y*scale);
		selected.overAdds ++; //corrective shift for overlays
		selected.overX = compStart.x;
		selected.overY = compStart.y;
		//start = compStart;
		composite.setRGB(selectedStart.x, selectedStart.y, new Color(255, 0, 0, 255).getRGB());
		composite.setRGB(adderStart.x, adderStart.y, new Color(0, 255, 0, 255).getRGB());
		composite.setRGB((int)(compStart.x/scale), (int) (compStart.y/scale), new Color(0, 0, 255, 255).getRGB());
		try {
	    	File imageFile = new File(path+"overlays/"+saveName+"Map/compo.png");
			ImageIO.write(composite, "png", imageFile);
		} catch (IOException e1) {e1.printStackTrace();}
		BufferedImage temp = composite;
		composite = new BufferedImage(end.x-start.x, end.y-start.y, BufferedImage.TYPE_INT_ARGB);
		for(int x=0; x<end.x-start.x; x++)
			for(int y=0; y<end.y-start.y; y++)
				composite.setRGB(x, y, temp.getRGB(start.x+x, start.y+y));
		//BufferedImage addedImage = adder.makeOverlays()[0];
		/*for(int x=0; x<addedImage.getWidth(); x++)
			for(int y=0; y<addedImage.getHeight(); y++) {
				if(overlayImages[getSelected().getId()-1].getRGB(x, y) != map.getImage().getRGB(x, y))
					overlayImages[getSelected().getId()-1].setRGB(x, y, addedImage.getRGB(x, y));
			}
		overlayImages[getSelected().getId()-1] = composite;
		try {
	    	File imageFile = new File(path+"overlays/"+saveName+"Map/"+getSelected().getId()+".png");
			ImageIO.write(overlayImages[getSelected().getId()-1], "png", imageFile);
		} catch (IOException e1) {e1.printStackTrace();}
	}*/
	private void makeOverlays() {
		System.out.println("OverlaypressedStart");
		Point[] points = new Point[provinces.size()];
		for(int i=0; i<provinces.size(); i++) 
			points[i] = new Point((int) (provinces.get(i).getX()),(int)(provinces.get(i).getY()));
		
		OverlayMaker cutter = new OverlayMaker(points);
		System.out.println("OverlayInstantated");
	    overlayImages = cutter.getOverlays().toArray(new BufferedImage[0]);
	    cutter.saveImages(path+"overlays/"+saveName+"Map/", "");
	    for(int i=0; i<provinces.size(); i++) {
	    	System.out.println(provinces.get(i).name+": StartX: "+cutter.getStarts()[i].x
				+", StartY: "+cutter.getStarts()[i].y);
	    	provinces.get(i).overX = (int)(cutter.getStarts()[i].x);
	    	provinces.get(i).overY = (int)(cutter.getStarts()[i].y);
	    }
	    System.out.println("OverylaypressedEnd");
	}
	private void makeOverlays(int provId) {
		System.out.println("OverlaypressedStart");
		Point[] points = new Point[1];
		points[0] = new Point((int) (provinces.get(provId-1).getX()),(int)(provinces.get(provId-1).getY()));
		
		OverlayMaker cutter = new OverlayMaker(points);
	    overlayImages[selected.getId()-1] = cutter.getOverlays().get(0);
	    try {
	    	File imageFile = new File(path+"overlays/"+saveName+"Map/"+getSelected().getId()+".png");
			ImageIO.write(overlayImages[selected.getId()-1], "png", imageFile);
		} catch (IOException e1) {e1.printStackTrace();}
	    provinces.get(provId-1).overX = (int)(cutter.getStarts()[0].x);
	    provinces.get(provId-1).overY = (int)(cutter.getStarts()[0].y);
	    System.out.println("OverylaypressedEnd");
	}
	private void saveState(String fileName) {
		fileName = path+"/saves/"+fileName+".txt";
		String state = ""+formatInt(provinces.size(), 3)+formatInt(continents.size(), 3);
		for(int i=0; i<provinces.size(); i++)
			state += provinces.get(i).saveCode();
		for(int i=0; i<continents.size(); i++)
			state += continents.get(i).saveCode();
		if(imperium) state += 1;
		else state += 0;
		state += "/"+mapFile+"/";
		try {
			if(!new File(path+"/saves/").exists())
				new File(path+"/saves/").mkdir();
	    	FileWriter fileWriter = new FileWriter(fileName);
			fileWriter.write(state);
			fileWriter.close();
		} catch (IOException e1) {e1.printStackTrace();}
	}
	public void loadState(String fileName) {
		saveName = fileName;
		fileName = path+"/saves/"+fileName+".txt";
		String loaded = "";
		
		try {
			File file = new File(fileName); 
			BufferedReader br = new BufferedReader(new FileReader(file)); 
			String st; 
			while ((st = br.readLine()) != null) {
				loaded += st;
				//System.out.println(st);
			}
		  }catch (IOException e) {e.printStackTrace();}
		mapFile = loaded.substring(loaded.indexOf('/')+1, loaded.indexOf('/', loaded.indexOf('/')+1));
		map.addImageFile(mapFile); //next '/' after first '/'
		imperium = loaded.charAt(loaded.indexOf('/')-1) == '1';
		if(!imperium) {
			cMaker.setAlignmentY(1);
			changer.setAlignmentY(2);
		}
		System.out.println("Loaded: ["+loaded+"]");
		int provLen = Integer.parseInt(loaded.substring(0, 3));
		int contLen = Integer.parseInt(loaded.substring(3, 6));
		int place = 6;
		for(int i=0; i<provLen; i++) {
			provinces.add(new Province(Integer.parseInt(loaded.substring(place+3, place+6)), 0, 0, mapHolder, imperium));
			mapHolder.add(provinces.get(provinces.size()-1).getButton(), 0);
			place = loaded.indexOf('|', place)+1;
		}
		for(int i=0; i<contLen; i++) {
			continents.add(new Continent(Integer.parseInt(loaded.substring(place+3, place+6)), 0, 0, mapHolder, imperium));
			mapHolder.add(continents.get(continents.size()-1).getButton(), 0);
			place = loaded.indexOf('|', place)+1;
		}
		place = 6;
		for(int i=0; i<provLen; i++) {
			provinces.get(i).parseCode(loaded.substring(place + 3));
			place = loaded.indexOf('|', place)+1;
		}
		for(int i=0; i<contLen; i++) {
			continents.get(i).parseCode(loaded.substring(place + 3));
			place = loaded.indexOf('|', place)+1;
		}
		initImages();
	}
	private void initImages() {
		if(overlayImages.length != provinces.size()) overlayImages = new BufferedImage[provinces.size()];
		for(int i=0; i<overlayImages.length; i++) 
			if(overlayImages[i] == null) 
				try {
					overlayImages[i] = ImageIO.read(new File(path+"overlays/"+saveName+"Map/"+(i+1)+".png"));
				} catch (Exception ex) {System.out.println("Could not read overlay "+i);}
	}
	private void update() {
		Thread thread = new Thread() {
		    @Override
		    public void run() {
		        try {
		            while(true) {
		                sleep(100);
		                if(map.inside()) {
		                	String text = infoField.getText().substring(0, infoField.getText().indexOf('|')+1)
									+"Current position X: "+(map.getCurrentX()-getX()-8)+", Y: "+(map.getCurrentY()-getY()-86);
		                	if(lastProv != null)
								text += "Last Province: "+lastProv.getName()+", ID: "+lastProv.getId();
		                infoField.setText(text);
		                }
		            }
		        } catch (InterruptedException e) {e.printStackTrace();}
		    }
		};
		thread.start();
	}
	public JLayeredPane getMapHolder() {return mapHolder;}
	public void setInfoText(String txt) {
		infoField.setText(txt);
	}
	public JTextField getInfo() {return infoField;}
	public Map getMap() {return map;}
	public Province getSelected() {return selected;}
	public Continent getCSelected() {return cSelected;}
	public void setSelected(Province selected) {this.selected = selected;}
	public void setCSelected(Continent selected) {this.cSelected = selected;}
	public ArrayList<Province> getProvinces(){return provinces;}
	public ArrayList<Continent> getContinents(){return continents;}
	
	public Province provinceFromId(int id) {
		for(int i=0; i<provinces.size(); i++) {
			//System.out.println("id: "+provinces.get(i).getId());
			if(provinces.get(i).getId() == id)
				return provinces.get(i);
		}System.out.println("here be null");
		return null;
	}
	public void removeProvince(Province kill) {
		for(int i=0; i<provinces.size(); i++) {
			if(provinces.get(i).getId() == kill.getId()) 
				provinces.remove(i);
		}
		mapHolder.remove(kill.getButton());
		selected = null;
	}
	public String printCode() {
		String code = "";
		int previousCLength = -1;
		if(!imperium) {
			for(int i=0; i<continents.size(); i++) {
				code += "\n\t"+continents.get(i).toClassCode();
				for(int j=0; j<continents.get(i).getProvinces().size(); j++) 
					code += "\n\t"+continents.get(i).getProvinces().get(j).toClassCode();
				code += "\n\t    continents["+continents.get(i).getId()+"].fill("+(previousCLength+1)+", "+(previousCLength+continents.get(i).getProvinces().size())+");";
				previousCLength += continents.get(i).getProvinces().size();
			}
		}else {
			for(int i=0; i<provinces.size(); i++) {
				code += "\n\t"+provinces.get(i).toClassCode();
			}
		}code += "\n    }\n}";
		return code;
	}
	public void unselectAll() {
		selected = null;
		for(int i=0; i<provinces.size(); i++) {
			provinces.get(i).unselect();
		}
	}
	public void unCSelectAll() {
		for(int i=0; i<continents.size(); i++) {
			continents.get(i).unselect();
		}
	}
	public String makeClassicHeader(String name) {
		String capName = name.substring(0, 1).toUpperCase() + name.substring(1);
		String code = "package com.reactordevelopment.Imperium;\r\n" +
				"\r\n" + 
				"import android.content.Context;\r\n" + 
				"import android.util.Log;\r\n"
				+ "import android.graphics.Color;" + 
				"\r\n" + 
				"import java.util.ArrayList;\r\n" + 
				"\r\n" + 
				"public class "+capName+" extends Map {\r\n" + 
				"public static final int MAP_DRAWABLE = R.drawable."+mapFile+";\r\n"+
				"    public "+capName+"(Context context){\r\n" + 
				"        provinceList = new Province["+provinces.size()+"];\r\n"+
				"        id = ;\r\n";
				if(!imperium) { code +="        continents = new Continent["+continents.size()+"];\r\n"
								   + "        imperiumMap = 0;\r\n";
				}
				else code += "        imperiumMap = 1;\r\n";
				code +="        overScale = .8;\r\n"+
				"		statusScale = 1;\r\n"+
		        "        mapFilePath = \""+name+"Map/\";\r\n" +
				"        assemble(context);\r\n" + 
				"    }\r\n" + 
				"    public void assemble(Context context){\r\n" + 
				"        double scaleX = 1;\r\n" + 
				"        double scaleY = 1;\r\n"+
				"        double shiftX = 0;\r\n" + 
				"        double shiftY = 0;\r\n"
				+ "        int color;\r\n";
				code += "        borders = new ArrayList<>();";
				return code;
	}
	public void addComponentsToPane(Container pane) {
        pane.setLayout(null);
        map = new Map("Europe", pane);
		map.setPath(path+"maps/");
		map.addImageFile(mapFile);
        pane.add(map);
        pane.setComponentZOrder(map, 0);
    }
 
    public void createAndShowGUI() {
        addComponentsToPane(mapHolder);
        Insets insets = mapHolder.getInsets();
        Dimension dim = new Dimension(map.getWidth() + insets.left + insets.right,
                MAP_H + insets.top + insets.bottom);
        mapHolder.setPreferredSize(dim);
        mapHolder.setVisible(true);
    }
   
}
