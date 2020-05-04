import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class MainPanel extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener
{
    private final BufferedImage image;
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
    
    public MainPanel(final BufferedImage image) {
        this.zoomFactor = 1.0;
        this.prevZoomFactor = 1.0;
        this.xOffset = 0.0;
        this.yOffset = 0.0;
        this.image = image;
        this.initComponent();
    }
    
    private void initComponent() {
        this.addMouseWheelListener(this);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        coords = new Point(0, 0);
        size = new Point(40, 40);
        button = new JButton("button");
        button.setBounds(coords.x, coords.y, size.x, size.y);
        add(button);
    }
    
    @Override
    public void paint(final Graphics g) {
        super.paint(g);
        final Graphics2D g2 = (Graphics2D)g;
        if (this.zoomer) {
            final AffineTransform at = new AffineTransform();
            final double xRel = MouseInfo.getPointerInfo().getLocation().getX() - this.getLocationOnScreen().getX();
            final double yRel = MouseInfo.getPointerInfo().getLocation().getY() - this.getLocationOnScreen().getY();
            final double zoomDiv = this.zoomFactor / this.prevZoomFactor;
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
            final AffineTransform at = new AffineTransform();
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
        button.setBounds(coords.x, coords.y, size.x, size.y);
        g2.drawImage(this.image, 0, 0, this);
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
    public void mouseMoved(final MouseEvent e) {
    }
    
    @Override
    public void mouseClicked(final MouseEvent e) {
    }
    
    @Override
    public void mousePressed(final MouseEvent e) {
        this.released = false;
        this.startPoint = MouseInfo.getPointerInfo().getLocation();
    }
    
    @Override
    public void mouseReleased(final MouseEvent e) {
        this.released = true;
        this.repaint();
    }
    
    @Override
    public void mouseEntered(final MouseEvent e) {
    }
    
    @Override
    public void mouseExited(final MouseEvent e) {
    }
}
