package net.rs.lamsi.massimager.Settings.image.operations.quantifier;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.utils.FileAndPathUtil;

public class SettingsImage2DQuantifierOnePoint extends SettingsImage2DQuantifier {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    
    // image for one point
    protected Quantifier imgEx;  
	
	public SettingsImage2DQuantifierOnePoint(Image2D ex) {
		super(MODE_ONE_POINT); 
		this.imgEx = new Quantifier();
		imgEx.setImg(ex);
		imgEx.setMode(Quantifier.MODE_AVERAGE);
	} 
	@Override
	public void resetAll() {
		super.resetAll(); 
		imgEx = null;
	}
	
	
	@Override
	public double calcIntensity(Image2D img, int line, int dp, double intensity) {
		return isApplicable()? intensity/imgEx.getAverageIntensity()*imgEx.getConcentration() : intensity;
	}
	
	public boolean isApplicable() {
		return (imgEx!=null);
	}
	
	public Quantifier getImgEx() {
		return imgEx;
	}
	public void setImgEx(Image2D ex) {
		this.imgEx.setImg(ex);
	}
	public double getConcentrationEx() {
		return imgEx.getConcentration();
	}
	public void setConcentrationEx(double c) {
		imgEx.setConcentration(c);
	}
	
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
	}
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
	} 
	
}
