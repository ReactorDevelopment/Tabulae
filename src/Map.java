import java.awt.Canvas;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Map extends Canvas implements MouseListener, MouseMotionListener, MouseWheelListener{
	private String pathString;
	private Canvas canvas;
	private JFrame frame;
	private BufferedImage img;
	private int lastDownX = 0;
	private int lastDownY = 0;
	private int w;
	private int h;
	private boolean inside;
	private Point dragStart;
	private Point dragEnd;
	private Container pane;
	public int scaledW;
    private double zoomFactor;
    private double prevZoomFactor;
    private boolean zoomer;
    private boolean dragger;
    private boolean released;
    private double xOffset;
    private double yOffset;
    private int xDiff;
    private int yDiff;
    private JButton button;
    private Point coords;
    private Point size;
    private Point startPoint;
    private double imgScale;
    private AffineTransform at = new AffineTransform();
	public Map(String name, Container pane){
		this.pane = pane;
		inside = false;
		w = 500;
		h = 500;
		imgScale = 1;
		dragStart = new Point(0,0);
		dragEnd = new Point(0,0);
		canvas = new Canvas();
		zoomFactor = 1.0;
		prevZoomFactor = 1.0;
		xOffset = 0.0;
		yOffset = 0.0;
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		coords = new Point(0, 0);
		size = new Point(40, 40);
	}
	public String setPath(String path) {
		pathString = path;
		return pathString;
	}
	public BufferedImage getImage() {return img;}
	public void addImageFile(String file) {
		try {
            img = ImageIO.read(new File(pathString + "/" + file + ".png"));
            System.out.println(img.getHeight());
        } catch (IOException ex) {ex.printStackTrace();}
		scaledW = (int)(img.getWidth()/(double)(img.getHeight())*GridBagger.MAP_H);
		setSize(scaledW, GridBagger.MAP_H);
	}
	public void addImage(BufferedImage img) {
		this.img = img;
		scaledW = (int)(img.getWidth()/(double)(img.getHeight())*GridBagger.MAP_H);
		setSize(scaledW, GridBagger.MAP_H);
		}
	@Override
	public void paint(Graphics g) {
		final Graphics2D g2 = (Graphics2D)g;
        if (this.zoomer) {
        	at = new AffineTransform();
        	final double zoomDiv = this.zoomFactor / this.prevZoomFactor;
            final double xRel = MouseInfo.getPointerInfo().getLocation().getX() - this.getLocationOnScreen().getX();
            final double yRel = MouseInfo.getPointerInfo().getLocation().getY() - this.getLocationOnScreen().getY();
            this.xOffset = zoomDiv * this.xOffset + (1.0 - zoomDiv) * xRel;
            this.yOffset = zoomDiv * this.yOffset + (1.0 - zoomDiv) * yRel;
            coords.x = (int)xOffset; coords.y = (int)yOffset;
            size.x = (int)(size.x*zoomDiv); size.y = (int)(size.y*zoomDiv);
            at.translate(this.xOffset, this.yOffset);
            at.scale(this.zoomFactor, this.zoomFactor);
            this.prevZoomFactor = this.zoomFactor;
            g2.transform(at);
            this.zoomer = false;
        }
        if (this.dragger) {
            at = new AffineTransform();
            at.translate(this.xOffset + this.xDiff, this.yOffset + this.yDiff);
            at.scale(this.zoomFactor, this.zoomFactor);
            g2.transform(at);
            if (this.released) {
                this.xOffset += this.xDiff;
                this.yOffset += this.yDiff;
                coords.x = (int)xOffset; coords.y = (int)yOffset;
                this.dragger = false;
            }
        }
        for(int i=0; i<Main.getMapWindow().getProvinces().size(); i++) 
        	Main.getMapWindow().getProvinces().get(i).zoom(this.zoomFactor / this.prevZoomFactor);
        for(int i=0; i<Main.getMapWindow().getContinents().size(); i++) 
        	Main.getMapWindow().getContinents().get(i).zoom(this.zoomFactor / this.prevZoomFactor);
        //g2.drawImage(img, 0, 0, this);
		g.drawImage(img, 0, 0, w, h, this);
	}
	@Override
	public void setSize(int w, int h) {
		imgScale = (double)h/img.getHeight();
		super.setSize(w, h);
		this.w = w;
		this.h = h;
		repaint();
	}
	public int getH() {return h;}
	public int getW() {return w;}
	public double getImgScale() {return imgScale;}
	public AffineTransform getTransform() {return at;}
	
    public void mouseEntered (MouseEvent mouseEvent) {inside = true;} 
    public void mouseExited (MouseEvent mouseEvent) {inside = false;}  
    public void mousePressed (MouseEvent mouseEvent) {
    	released = false;
        startPoint = MouseInfo.getPointerInfo().getLocation();
    } 
    public void mouseReleased (MouseEvent mouseEvent) {
    	dragEnd = mouseEvent.getPoint();
    	released = true;
        repaint();
    }  
    public void mouseClicked (MouseEvent mouseEvent) {
    	lastDownX = mouseEvent.getX();
    	lastDownY = mouseEvent.getY();
    	final Point curPoint = mouseEvent.getLocationOnScreen();
        xDiff = curPoint.x - this.startPoint.x;
        yDiff = curPoint.y - this.startPoint.y;
        dragger = true;
        repaint();
    	Main.getWindow().setInfoText("Last Click X: "+lastDownX + ", Y: " + lastDownY +"|");
    }
    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        this.zoomer = true;
        if (e.getWheelRotation() < 0) {
            this.zoomFactor *= 1.1;
            this.repaint();
        }
        if (e.getWheelRotation() > 0) {
            this.zoomFactor /= 1.1;
            this.repaint();
        }
    }
    
    @Override
    public void mouseDragged(final MouseEvent e) {
        final Point curPoint = e.getLocationOnScreen();
        this.xDiff = curPoint.x - this.startPoint.x;
        this.yDiff = curPoint.y - this.startPoint.y;
        this.dragger = true;
        this.repaint();
        
    }
    @Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	}
    public int getClickX() {return lastDownX;}
    public int getClickY() {return lastDownY;}
    public Point scalePoint(int x, int y) {return new Point((int)(x/(double)img.getWidth()*scaledW), (int)(y/(double)img.getHeight()*500));}
    public int getCurrentX() {return MouseInfo.getPointerInfo().getLocation().x;}
    public int getCurrentY() {return MouseInfo.getPointerInfo().getLocation().y;}
    public boolean inside() {return inside;}
	
}
