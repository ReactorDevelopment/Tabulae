import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.lang.invoke.MutableCallSite;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Province extends GridBagger{
	public final static double SCALE_H = 465.0/Main.getMapWindow().getMap().getH();
	public final static double SCALE_W = 915.0/Main.getMapWindow().getMap().getW();
	public final static int OVER_ADD_START = 1;
	protected int id;
	protected int cId;
	protected double x;
	protected double y;
	public double overX;
	public double overY;
	protected double w;
	protected double h;
	protected String name;
	protected JButton button;
	protected String code;
	private Province self;
	protected boolean selected;
	private ArrayList<Province> bordering;
	protected JLayeredPane panel;
	protected boolean imperium;
	protected double intrest;
	protected int development;
	protected double attrition;
	protected GridBagger window;
	public int overAdds = OVER_ADD_START;
	protected Map map;
	protected AffineTransform mapTrans;
	protected AffineTransform invMapTrans;
	protected static final Dimension MASTER_DIM = new Dimension(30, 30);
	private static final Color NAMED_COLOR = new Color(80, 149, 84);
	private static final Color BORDERED_COLOR = new Color(36, 196, 224);
	private Color unselectColor = Color.GRAY;
	public Province() {}
	public Province(int id, double x, double y, JLayeredPane panel, boolean imperium) {
		System.out.println("Added: "+id);
		window = Main.getMapWindow();
		map = window.getMap();
		mapTrans = map.getTransform();
		System.out.println("ee"+SCALE_H+", "+SCALE_W);
		try {invMapTrans = mapTrans.createInverse();} catch (NoninvertibleTransformException e) {e.printStackTrace();}
		this.imperium = imperium;
		bordering = new ArrayList<Province>(0);
		code = "Province at ("+x+", "+y+") with id "+id+" with borders "+stringBorders();
		System.out.println(code);
		self = this;
		name = "";
		intrest = 0.5;
		development = 1;
		attrition = 1;
		cId = -1;
		overX = 0;
		overY = 0;
		this.id = id;
		this.x = x*invMapTrans.getScaleX() + invMapTrans.getTranslateX();
		this.y = y*invMapTrans.getScaleY() + invMapTrans.getTranslateY();
		w = MASTER_DIM.width;
		h = MASTER_DIM.height;
		this.panel = panel;
		makeButton();
	}
	public void makeButton() {
		int w = (int)this.w;
		int h = (int)this.h;
		button = new JButton(""+id);
		button.setMargin(new Insets(1,1,1,1));
		button.setFont(new Font(button.getFont().getFontName(), button.getFont().getStyle(), w/2));
		button.setBackground(unselectColor);
		Insets insets = panel.getInsets();
		int invX = (int) (this.x*mapTrans.getScaleX()+mapTrans.getTranslateX());
		int invY = (int)(this.y*mapTrans.getScaleY()+mapTrans.getTranslateY());
        button.setBounds(invX + insets.left - w/2, invY + insets.top - h/2, w, h);
        button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField info = window.getInfo();
				if(window.getCSelected() != null) {
					if(!selected) {
						select();
						window.getCSelected().addProvince(self, window.getCSelected().getProvinces());
						cId = window.getCSelected().getId();
					}
					else {
						unselect();
						window.getCSelected().removeProvince(self, window.getCSelected().getProvinces());
					}
				}else if(window.getSelected() == null) {
					select();
					lastProv = self;
					codeOut.setText(name);
					changeFull = false;
					window.setSelected(self);
					selectProvinces(bordering);
					info.setText("X: "+window.getMap().getClickX()+ ", Y: "+window.getMap().getClickY()+", Selected: none");
				}
				else if(window.getSelected().getId() == id) {
					window.setSelected(null);
					window.unselectAll();
				}
				else if (window.getSelected().getId() != id) {
					if(!selected) {
						select();
						window.getSelected().addProvince(self, window.getSelected().getBordering());
					}
					else {
						unselect();
						window.getSelected().removeProvince(self, window.getSelected().getBordering());
					}
				}
				try {
					info.setText("X: "+window.getMap().getClickX()+ ", Y: "+window.getMap().getClickY()+", Selected: "+window.getSelected().getId());
				} catch (NullPointerException e2) { /*e2.printStackTrace();*/ }
				toClassCode();
			}});
	}
	public String toClassCode() {
		String code = "";
		//Point xy = map.scalePoint(this.codeX, this.codeY);
		System.out.println(name+": x: "+overX+", Y: "+overY);
		Point xy = new Point((int)(overX), (int)(overY));
		if(!imperium) {
			for(int i=0; i<bordering.size(); i++)
				code += "    borders.add("+i+", "+bordering.get(i).getId()+"); ";
			code += "\n\t    provinceList["+(id-1)+"] = new Province(context, "+id+", "+cId+", bordering(), (int)(scaleX*"
					+scale(xy.x, SCALE_W)+"+shiftX), (int)(scaleY*"+scale(xy.y, SCALE_H)+"+shiftY), \""+name+"\", "+intrest+"+vary());\r\n" + 
					"\t    borders = new ArrayList<>();";
			return code;
		}else {
			for(int i=0; i<bordering.size(); i++)
				code += "    borders.add("+i+", "+bordering.get(i).getId()+"); ";
			code += "\n\t    provinceList["+(id-1)+"] = new Province(context, "+id+", bordering(), (int)(scaleX*"
					+scale(xy.x, SCALE_W)+"+shiftX), (int)(scaleY*"+scale(xy.y, SCALE_H)+"+shiftY), \""+name+"\", "+intrest+"+vary(), "+development+", "+attrition+");\r\n" + 
					"\t    borders = new ArrayList<>();";
			return code;
		}
	}
	/*public String getEditCode() {
		int w = (int)this.w;
		int h = (int)this.h;
		//"id x y { 001 002 003 }"
		String code = "";

		code += formatInt(id, 3) + " ";
		code += formatInt(x, 3) + " ";
		code += formatInt(y, 3) + " ";
		code += formatInt((int)overX, 3) + " ";
		code += formatInt((int)overY, 3) + " ";
		code += formatInt(w, 3) + " ";
		code += formatInt(h, 3) + " ";
		if(!imperium) code += formatInt(cId, 2) + " ";
		code += formatDouble(intrest, 3) + " ";
		if(imperium) {
			code += formatInt(development, 2) + " ";
			code += formatDouble(attrition, 4) + " ";
		}
		
		code += "{ ";
		for(int i=0; i<bordering.size(); i++) {
			code += formatInt(bordering.get(i).getId(), 3) + " ";
		}if(!imperium) return code + "} "+name+ " | Key: \"id x y overX overY width height continentId intrest{ provinces }\"";
		else return code + "} "+name+ " | Key: \"id x y overX overY width height intrest development attrition { provinces }\"";

	}*/
	public String saveCode() {
		int w = (int)this.w;
		int h = (int)this.h;
		String code = "";

		code += formatInt(id, 3) + " ";
		code += formatDouble(x-.3, 5) + " ";
		code += formatDouble(y-.3, 5) + " ";
		code += formatInt((int)(overX), 3) + " ";
		code += formatInt((int)(overY), 3) + " ";
		code += formatInt(w, 3) + " ";
		code += formatInt(h, 3) + " ";
		if(!imperium) code += formatInt(cId, 2) + " ";
		code += formatDouble(intrest, 3) + " ";
		if(imperium) {
			code += formatInt(development, 2) + " ";
			code += formatDouble(attrition, 4) + " ";
		}
		code += "{ ";
		for(int i=0; i<bordering.size(); i++) {
			code += formatInt(bordering.get(i).getId(), 3) + " ";
		}
		code += "} "+name + " |";
		return formatInt(code.length(), 3) + code;
	}
	
	public void parseCode(String code) {
		//"id 00x 00y 00w 00h { 001 002 003 } name"
		System.out.println("Prov: ["+code+"]");
		code = code.substring(0, code.indexOf('|')-1);
		int at = 0;
		try {
			id = Integer.parseInt(code.substring(at, at+3));
			at += 3+1;
			button.setText(""+id);
			x = Double.parseDouble(code.substring(at, at+5));//5
			at += 5+1;//5
			y = Double.parseDouble(code.substring(at, at+5));//5
			at += 5+1;//5
			overX = Integer.parseInt(code.substring(at, at+3));
			at += 3+1;
			overY = Integer.parseInt(code.substring(at, at+3));
			at += 3+1;
			w = Integer.parseInt(code.substring(at, at+3));
			at += 3+1;
			h = Integer.parseInt(code.substring(at, at+3));
			at += 3+1;
			if(!imperium) { cId = Integer.parseInt(code.substring(at, at+2));
			at += 2+1;
			intrest = Double.parseDouble(code.substring(at, at+3));
			at += 3+1;}
			else {intrest = Double.parseDouble(code.substring(at, at+3));
			at += 3+1;
			development = Integer.parseInt(code.substring(at, at+2));
			at += 2+1;
			attrition = Double.parseDouble(code.substring(at, at+4));
			at += 4+1;}
			double invX = this.x*mapTrans.getScaleX()+mapTrans.getTranslateX();
			double invY = this.y*mapTrans.getScaleY()+mapTrans.getTranslateY();
			move(invX+1, invY+1); //also should resize
			bordering = new ArrayList<Province>();
			for(int i=code.indexOf('{')+2; i<code.indexOf('{')+1+(code.substring(code.indexOf('{')+2, code.indexOf('}')).length()); i+=4){
				bordering.add(Main.getWindow().provinceFromId(Integer.parseInt(code.substring(i, i+3))));
				System.out.println(code.substring(i, i+3));
			}
			if(code.length() >= code.indexOf('}')+3) name = code.substring(code.indexOf('}')+2);
			if(name != "") unselectColor = NAMED_COLOR;
			if(bordering.size() > 0) unselectColor = BORDERED_COLOR;
			button.setBackground(unselectColor);
		}catch (Exception e) {e.printStackTrace();}
	}
	public void parseName(String nameIn) {
		name = nameIn;
		if(nameIn != "") unselectColor = NAMED_COLOR;
	}
	public String stringBorders() {
		String borderIds = "{ ";
		for(int i=0; i<bordering.size(); i++) {
			borderIds += bordering.get(i).getId()+" ";
		}
		return borderIds+"}";
	}
	public boolean idInList(int id, ArrayList<Province> list) {
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getId() == id)
				return true;
		}
		return false;
	}
	public void selectProvinces(ArrayList<Province> list) {
		for(int i=0; i<list.size(); i++) {
			list.get(i).select();
		}
	}
	public void addProvince(Province add, ArrayList<Province> list) {
		if(!idInList(add.getId(), list)) {
			list.add(add);
			unselectColor = BORDERED_COLOR;
		}
	}
	public void removeProvince(Province kill, ArrayList<Province> list) {
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getId() == kill.getId()) 
				list.remove(i);
		}
	}
	public int getX() {return (int)x;}
	public void setX(int set) {x = set;}
	public int getY() {return (int)y;}
	public void setY(int set) {y = set;}
	public int getW() {return (int)w;}
	public int getH() {return (int)h;}
	public int getId() {return id;}
	public void setId(int set) {id = set;}
	public int getCId() {return cId;}
	public void setCId(int set) {cId = set;}
	public JButton getButton() {return button;}
	public ArrayList<Province> getBordering() {return bordering;}
	
	public void move(double x, double y) {
		int w = (int)this.w;
		int h = (int)this.h;
		this.x = x*invMapTrans.getScaleX() + invMapTrans.getTranslateX();
		this.y = y*invMapTrans.getScaleY() + invMapTrans.getTranslateY();
		Insets insets = panel.getInsets();
		int invX = (int) (this.x*mapTrans.getScaleX()+mapTrans.getTranslateX());
		int invY = (int)(this.y*mapTrans.getScaleY()+mapTrans.getTranslateY());
        button.setBounds(invX + insets.left - w/2, invY + insets.top - h/2, w, h);
	}
	public void zoom(double scaleRatio) {
		Insets insets = panel.getInsets();
		mapTrans = map.getTransform();
		try {invMapTrans = mapTrans.createInverse();} catch (NoninvertibleTransformException e) {e.printStackTrace();}

		int w = (int)this.w;
		int h = (int)this.h;
		
		int invX = (int) (this.x*mapTrans.getScaleX()+mapTrans.getTranslateX());
		int invY = (int)(this.y*mapTrans.getScaleY()+mapTrans.getTranslateY());
		
        button.setBounds(invX + insets.left - w/2, invY + insets.top - h/2, w, h);

        //System.out.println(this.x+", "+this.y);
	}
	public void unselect() {
		selected = false;
		button.setBackground(unselectColor);
	}
	public void select() {
		selected = true;
		if(window.getCSelected() != null) button.setBackground(window.getCSelected().getColor());
		else if(window.getSelected() == null) button.setBackground(Color.YELLOW);
		else if(window.getSelected().getId() != id) button.setBackground(Color.PINK);
	}
	public void safeDelete() {
		for(Province p : Main.getMapWindow().getProvinces()) {
			if(p.getId() > id) p.setId(p.getId()-1);
			for(Province p2 : p.getBordering()) {
				if(p2.getId() == self.getId()) 
					p.removeProvince(self, bordering);
			}
		}for(Continent c : Main.getMapWindow().getContinents())
			for(Province p : c.getProvinces()) if(p.getId() == id) c.getProvinces().remove(p);
		Main.getWindow().removeProvince(self);
	}
	protected int scale(int num, double scaler){return (int)(num*scaler);}
}
