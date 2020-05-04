import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class OverlayMaker {
	private static Color[][] colors;
	private BufferedImage original;
	private ArrayList<BufferedImage> images;
	private Map map;
	private Point[] starts;
	private Point start;
	private Point end;
	private static final int MAX_TURNS = 2000;
	private ArrayList<Point> savedPoints;
	private double imgScale;
	private int count = 0;
	private int stopped = 0;
	private Point[] points;
	private double percentAt;
	private double prevPercentAt;
	private long milis = System.currentTimeMillis();
	private long prevMilis = System.currentTimeMillis();
	public static final int BORDER_COLOR = new Color(0, 0, 0, 255).getRGB();
	//public static final int OVERLAY_COLOR = new Color(255, 255, 255, 10).getRGB();
	public static final int STOP_COLOR = new Color(77, 100, 200).getRGB();
	public static final int BRIDGE_COLOR = new Color(255, 112, 222).getRGB();
	public static final int BRIDGE_END_COLOR = new Color(36, 255, 229).getRGB();
	
	public OverlayMaker(Point[] buttonPoints) {
		original = Main.getMapWindow().getMap().getImage();
		map = Main.getMapWindow().getMap();
		points = buttonPoints;
		percentAt = 0;
		prevPercentAt = 0;
		imgScale = Main.getMapWindow().getMap().getImgScale();
		savedPoints = new ArrayList<Point>(0);
		images = new ArrayList<BufferedImage>(0);
		starts = new Point[buttonPoints.length];
		percentComplete();
		complete();
		while(stopped < points.length-1) {
			System.out.println("Cycle start");
			images = new ArrayList<BufferedImage>();
			transparent();
			makeOverlays();
			System.out.println("Cycle End");
		}
		System.out.println("Constructor End");
	}
	private void complete() {
		Thread completor = new Thread() {
			@Override
			public void run() {
				
			}
		};completor.start();
	}
	private void percentComplete() {
		Thread percent = new Thread(){
            @Override
            public void run(){
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(100);
                        milis = System.currentTimeMillis();
                        if(percentAt >= 99) {
                        	Main.getWindow().setCodeOut("Complete");
                        }
                        if(percentAt != prevPercentAt) {
                			Main.getWindow().setCodeOut(percentAt+"% Complete, "+(milis-prevMilis)/1000.0+" seconds per image");
                			prevPercentAt = percentAt;
                			prevMilis = milis;
                		}
                        
                    }
                } catch (InterruptedException e) {e.printStackTrace(); }
            }
        };percent.start();
		
	}
	private void transparent() {
		try {
			for(int i=stopped; i<points.length; i++) {
				System.out.println(i);
				images.add(new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB));
				stopped = i;
			}
		}catch (OutOfMemoryError e) {e.printStackTrace();}
		
		System.out.println("OriginalDims: "+original.getWidth()+", "+original.getHeight());
	}
	public void floodGates(int x, int y, int imgNum) {
		floodCopy(x, y, imgNum);
	}
	private void floodCopy(int x, int y, int imgNum) { 
		//System.out.println("{"+x+" of "+original.getWidth()+" , "+y+" of "+original.getHeight()+"}");
		//System.out.println(imgNum + " of "+ images.size());
		if (x < 0 || x >= original.getWidth()-1 || y < 0 || y >= original.getHeight()-1) {/*System.out.print("boundsEnded");*/ return;}
		if(images.get(imgNum).getRGB(x, y) == STOP_COLOR) {
			images.get(imgNum).setRGB(x, y, original.getRGB(x, y));/*System.out.print("StopsscolorEnded");*/
			return;
		}
	    if(colorRange(original.getRGB(x, y), BORDER_COLOR, .6)) {
	    	images.get(imgNum).setRGB(x, y, original.getRGB(x, y));
	    	/*System.out.print("colorEnded");*/return;
	    	}
	    if(noAlpha(images.get(imgNum).getRGB(x, y)) == original.getRGB(x, y)) {return;}
	    
	    if(colorRange(original.getRGB(x, y), BRIDGE_COLOR, .25)) {
	    	System.out.print("Bridge");
	    	count++;
	    	images.get(imgNum).setRGB(x, y, RGBA(original.getRGB(x, y), 1));
	    	if(colorRange(original.getRGB(x+1, y), BRIDGE_COLOR, .5)
	    			|| colorRange(original.getRGB(x+1, y), BRIDGE_END_COLOR, .25))floodCopy(x+1, y, imgNum); 
	    	if(colorRange(original.getRGB(x-1, y), BRIDGE_COLOR, .5)
	    			|| colorRange(original.getRGB(x-1, y), BRIDGE_END_COLOR, .25))floodCopy(x-1, y, imgNum); 
	    	if(colorRange(original.getRGB(x, y+1), BRIDGE_COLOR, .5)
	    			|| colorRange(original.getRGB(x, y+1), BRIDGE_END_COLOR, .25))floodCopy(x, y+1, imgNum); 
	    	if(colorRange(original.getRGB(x, y-1), BRIDGE_COLOR, .5)
	    			|| colorRange(original.getRGB(x, y-1), BRIDGE_END_COLOR, .25))floodCopy(x, y-1, imgNum);
	    	return;
	    }
	    
		if(count >= MAX_TURNS) { 
			if(images.get(imgNum).getRGB(x, y) != STOP_COLOR) {images.get(imgNum).setRGB(x, y, STOP_COLOR);}
			return;
		}
		if(x > end.x) end.x = x; if(y > end.y) end.y = y;
		if(x < start.x) start.x = x; if(y < start.y) start.y = y;
		
	    count++;
	    //System.out.println("{"+x+", "+y+"}");
	    
	    images.get(imgNum).setRGB(x, y, original.getRGB(x, y));
	    
	    floodCopy(x+1, y, imgNum); 
	    floodCopy(x-1, y, imgNum); 
	    floodCopy(x, y+1, imgNum); 
	    floodCopy(x, y-1, imgNum);
	} 
	public Point[] getStarts() {return starts;}
	private void makeOverlays() {
		for(int i=0; i<images.size();i++) {
			System.out.println(i+" Of " + images.size());
			percentAt = (int)((double)i/points.length*100);
			count = 0;
			end = new Point(0,0);
			start = new Point(original.getWidth(), original.getHeight());
			floodGates((int) (points[i].getX()/imgScale), (int) (points[i].getY()/imgScale), i);
			count = 0;
			int iterations = 0;
			for(int x=0; x<original.getWidth(); x++) {
				for(int y=0; y<original.getHeight(); y++) {
					if(images.get(i).getRGB(x, y) == STOP_COLOR && iterations < 50) {
						System.out.println("Stop resume at: "+x+", "+y);
						images.get(i).setRGB(x, y, new Color(0, 0, 0, 0).getRGB());
						floodGates(x, y, i);
						x=0;y=0;count=0;
						iterations++;
					}
				}
				starts[i] = new Point((int)(start.getX()/original.getWidth()*map.getW()), (int)(start.getY()/original.getHeight()*map.getH()));
			}
			BufferedImage temp = images.get(i);
			try{images.set(i, new BufferedImage(end.x-start.x, end.y-start.y, BufferedImage.TYPE_INT_ARGB));}
			catch (IllegalArgumentException e) {System.out.println("Province "+i+" is no a border, must be moved");}
			for(int x=0; x<end.x-start.x; x++)
				for(int y=0; y<end.y-start.y; y++)
					images.get(i).setRGB(x, y, temp.getRGB(start.x+x, start.y+y));
			//images.get(i).setRGB((int) (points[i].getX()/imgScale), (int) (points[i].getY()/imgScale), new Color(200, 0, 200).getRGB());
			//images.get(i).setRGB(start.x, start.y, new Color(200, 0, 200).getRGB());
			//images.get(i).setRGB(end.x, end.y, new Color(0, 0, 0).getRGB());
			System.out.println("Point "+i+":"+points[i].getX()/imgScale+", "+points[i].getY()/imgScale);
			/*try {
		    	File imageFile = new File("C:/Users/agent/OneDrive/Documents/pgrm/TabuleaResources/overlays/classicMap/"+(i+1)+".png");
				ImageIO.write(images.get(i), "png", imageFile);
			} catch (IOException e1) {e1.printStackTrace(); System.exit(0);}*/
		}
				
	}
	public void saveImages(String path, String name) {
		System.out.println("Saving:"+name+" to "+path);
		File dir = new File(path);
		if(!dir.exists())
			dir.mkdir();
		for(int i=0; i<images.size();i++) {
			try {
		    	File imageFile = new File(path+name+(i+1)+".png");
				ImageIO.write(images.get(i), "png", imageFile);
			} catch (IOException e1) {e1.printStackTrace();}
		}
			
	}
	public ArrayList<BufferedImage> getOverlays(){return images;}
	public boolean colorRange(int colorOne, int colorTwo, double percent) {
		int maxDiff = (int)(255*percent);
		Color one = new Color(colorOne); Color two = new Color(colorTwo);
		return Math.abs(one.getBlue() - two.getBlue()) < maxDiff && Math.abs(one.getRed() - two.getRed()) < maxDiff
				&& Math.abs(one.getGreen() - two.getGreen()) < maxDiff;
	}
	private int RGBA(int rgb, int alpha) {
		Color color = new Color(rgb);
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha).getRGB();
	}
	private int noAlpha(int rgb) {
		return RGBA(rgb, 255);
	}
	// Driver code 
	/*public static void main(String[] args)  { 
	    int screen[][] ={
	    		{1, 1, 1, 1, 1, 1, 1, 1}, 
	            {1, 1, 1, 1, 1, 1, 0, 0}, 
	            {1, 0, 0, 1, 1, 0, 1, 1}, 
	            {1, 2, 2, 2, 2, 0, 1, 0}, 
	            {1, 1, 1, 2, 1, 0, 1, 0}, 
	            {1, 1, 1, 2, 1, 2, 2, 0}, 
	            {1, 1, 2, 1, 1, 2, 1, 1},
	            {1, 2, 1, 1, 1, 2, 2, 1}, 
	                    }; 
	    int x = 4, y = 4, newC = 3; 
	    //floodFill(screen, x, y, newC); 
	    BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
	    int rgb = new Color(255, 255, 255).getRGB();
	    
	    img.setRGB(5, 5, rgb);
	    img.setRGB(4, 5, rgb);
	    img.setRGB(3, 5, rgb);
	    img.setRGB(3, 4, rgb);
	    
	    
	    
	    System.out.println("Updated screen after call to floodFill: "); 
	    for (int i = 0; i < M; i++) 
	    { 
	        for (int j = 0; j < N; j++) 
	        System.out.print(screen[i][j] + " "); 
	        System.out.println(); 
	    } 
	    
	} */
	//if(savedPoints.contains(new Point(x, y))) {
	/*try {
    	File imageFile = new File("C:/Users/agent/Desktop/current.png");
		ImageIO.write(images.get(i), "png", imageFile);
	} catch (IOException e1) {e1.printStackTrace();}
	System.out.println("repeatExit");
	return null;*/
	//break;
//}
}
