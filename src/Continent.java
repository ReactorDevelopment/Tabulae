import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Continent extends Province{

	private Continent self;
	private ArrayList<Province> provinces;
	private int bonus;
	private Color color;
    public Continent(int id, int x, int y, JLayeredPane panel, boolean imperium) {
    	window = Main.getMapWindow();
		map = window.getMap();
		mapTrans = map.getTransform();
		try {invMapTrans = mapTrans.createInverse();} catch (NoninvertibleTransformException e) {e.printStackTrace();}
		this.imperium = imperium;
		provinces = new ArrayList<Province>();
		code = "";
		self = this;
		name = "";
		intrest = 0.5;
		color = new Color(0, 0, 0);
		bonus = 0;
		this.id = id;
		this.x = (int)(x*invMapTrans.getScaleX() + invMapTrans.getTranslateX());
		this.y = (int)(y*invMapTrans.getScaleY() + invMapTrans.getTranslateY());
		this.panel = panel;
		button = new JButton("C: "+id);
		button.setBackground(Color.GRAY);
        Dimension size = button.getPreferredSize();
        w = size.width;
        h = size.height;
        int w = (int)this.w;
		int h = (int)this.h;
		Insets insets = panel.getInsets();
		int invX = (int) (this.x*mapTrans.getScaleX()+mapTrans.getTranslateX());
		int invY = (int)(this.y*mapTrans.getScaleY()+mapTrans.getTranslateY());
        button.setBounds(invX + insets.left - w/2, invY + insets.top - h/2, w, h);
        button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GridBagger window= Main.getWindow();
				JTextField info = window.getInfo();
				if(window.getCSelected() != null)
					System.out.println("Sel: "+mapWindow.getCSelected().getId()+", id: "+id);
				if(window.getCSelected() == null) {
					selected = true;
					window.setCSelected(self);
					window.unselectAll();
					selectProvinces(provinces);
					button.setBackground(Color.YELLOW);
					info.setText("X: "+window.getMap().getClickX()+ ", Y: "+window.getMap().getClickY()+", Selected: none");
					codeOut.setText(name);
					changeFull = false;
				}
				else if(window.getCSelected().getId() == id) {
					selected = false;
					window.setCSelected(null);
					window.unselectAll();
					window.unCSelectAll();
				}
			}});
    }
    public Color getColor() {return color;}
    public ArrayList<Province> getProvinces() {return provinces;}
    public void safeDelete() {
		Main.getWindow().removeProvince(self);
	}
    /*public String getEditCode() {
    	int w = (int)this.w;
		int h = (int)this.h;
		//"id x y { 001 002 003 }"
		String code = "";

		code += formatInt(id, 3) + " ";
		code += formatInt(x, 3) + " ";
		code += formatInt(y, 3) + " ";
		code += formatInt(w, 3) + " ";
		code += formatInt(h, 3) + " ";
		code += formatDouble(intrest, 3) + " ";
		code += formatInt(bonus, 2) + " ";
		code += formatInt(color.getRed(), 3) + " ";
		code += formatInt(color.getGreen(), 3) + " ";
		code += formatInt(color.getBlue(), 3) + " ";
		
		code += "{ ";
		for(int i=0; i<provinces.size(); i++) {
			code += formatInt(provinces.get(i).getId(), 3) + " ";
		}code += "} " + name;
		
		return code+ " |   | Key: \"id x y width height intrest bonus { provinces }\"";
	}*/
    public String saveCode() {
    	int w = (int)this.w;
		int h = (int)this.h;
		String code = "";

		code += formatInt(id, 3) + " ";
		code += formatDouble(x, 5) + " ";
		code += formatDouble(y, 5) + " ";
		code += formatInt(w, 3) + " ";
		code += formatInt(h, 3) + " ";
		code += formatDouble(intrest, 3) + " ";
		code += formatInt(bonus, 2) + " ";
		code += formatInt(color.getRed(), 3) + " ";
		code += formatInt(color.getGreen(), 3) + " ";
		code += formatInt(color.getBlue(), 3) + " ";
		
		code += "{ ";
		for(int i=0; i<provinces.size(); i++) {
			code += formatInt(provinces.get(i).getId(), 3) + " ";
		}
		code += "} " + name + " |";
		return formatInt(code.length(), 3) + code;
	}
    public void parseCode(String code) {
		//"id 00x 00y 00w 00h { 001 002 003 } name"
    	code = code.substring(0, code.indexOf('|')-1);
    	int at = 0;
		try {
			id = Integer.parseInt(code.substring(at, at+3));
			System.out.println(id);
			at += 3+1;
			x = Double.parseDouble(code.substring(at, at+5));//5
			at += 5+1;//5
			y = Double.parseDouble(code.substring(at, at+5));//5
			at += 5+1;//5
			w = Integer.parseInt(code.substring(at, at+3));
			at += 3+1;
			h = Integer.parseInt(code.substring(at, at+3));
			at += 3+1;
			intrest = Double.parseDouble(code.substring(at, at+3));
			at += 3+1;
			bonus = Integer.parseInt(code.substring(at, at+2));
			at += 2+1;
			color = new Color(Integer.parseInt(code.substring(at, at+3)),
					Integer.parseInt(code.substring(at+4, at+7)),
					Integer.parseInt(code.substring(at+8, at+11)));
			move(x, y); //also should resize
			provinces = new ArrayList<Province>();
			for(int i=code.indexOf('{')+2; i<code.indexOf('{')+1+(code.substring(code.indexOf('{')+2, code.indexOf('}')).length()); i+=4){
				provinces.add(Main.getWindow().provinceFromId(Integer.parseInt(code.substring(i, i+3))));
			}
			if(code.length() >= code.indexOf('}')+3) name = code.substring(code.indexOf('}')+2);
		}catch (Exception e) {e.printStackTrace();}
	}
    public String toClassCode() {
    	//System.out.println("" + (window == null) + (window.getMap() == null));
		String code = "";
		if(!imperium) {
			code += "    color = Color.argb(255, "+color.getRed()+", "+color.getGreen()+", "+color.getBlue()+");\n\r"
			+ "        continents["+id+"] = new Continent("+id+", color, \""+name+"\", "+bonus+", "+intrest+"+vary());";
			return code;
		}
		return "imperium code";
	}
    
}
