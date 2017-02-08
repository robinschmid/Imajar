package net.rs.lamsi.massimager.Settings.image.operations.quantifier;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.utils.FileAndPathUtil;

public class SettingsImage2DQuantifierLinear extends SettingsImage2DQuantifier {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    
    // linear as y = a+bx
    protected double a,b;
	
	
	public SettingsImage2DQuantifierLinear() {
		super(MODE_LINEAR); 
	} 
	@Override
	public void resetAll() {
		super.resetAll(); 
		a=0;
		b=1;
	}
	
	/**
	 * the magic is done here
	 */
	@Override
	public double calcIntensity(Image2D img,  int line, int dp, double intensity) { 
		if(b!=0)
			return (intensity-a)/b;
		else return intensity;
	}

	public boolean isApplicable() {
		return (b!=0);
	}
	
	public double getA() {
		return a;
	}
	public void setA(double a) {
		this.a = a;
	}
	public double getB() {
		return b;
	}
	public void setB(double b) {
		this.b = b;
	} public double getIntercept() {
		return a;
	}
	public void setIntercept(double a) {
		this.a = a;
	}
	public double getSlope() {
		return b;
	}
	public void setSlope(double b) {
		this.b = b;
	}
	
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
	}
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
	} 
	
}
