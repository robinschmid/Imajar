package net.rs.lamsi.massimager.Settings.image.operations.quantifier;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.utils.FileAndPathUtil;

public class SettingsImage2DQuantifierIS extends SettingsImage2DQuantifier {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    
    // image for IS
    protected Image2D imgIS;
    
    // double
    protected double concentrationFactor = 1;
	
	
	public SettingsImage2DQuantifierIS() {
		super(MODE_IS); 
	} 
	public SettingsImage2DQuantifierIS(Image2D imgIS) {
		super(MODE_IS); 
		this.imgIS = imgIS;
	} 
	@Override
	public void resetAll() {
		super.resetAll(); 
		imgIS = null;
	}
	
	/**
	 * 
	 */
	@Override
	public double calcIntensity(Image2D img,  int line, int dp, double intensity) {
		if(isApplicable()) {
			double is = imgIS.getI(false, line, dp);
			if(is==0)
				return 0;
			else 
				return (isActive && isApplicable() && line<imgIS.getLineCount(dp) && dp<imgIS.getLineLength(line))? 
						intensity/is*concentrationFactor : intensity;
		} 
		else return intensity;
	}
	/**
	 * force blank
	 * @param img
	 * @param line
	 * @param dp
	 * @param intensity
	 * @param blank
	 * @return
	 */
	public double calcIntensity(Image2D img,  int line, int dp, double intensity, boolean blank) {
		if(isApplicable() && line<imgIS.getLineCount(dp)  && dp<imgIS.getLineLength(line)) {
			if(blank) {
				SettingsImage2DBlankSubtraction b = imgIS.getOperations().getBlankQuantifier();
				boolean tmp = b.isActive();
				b.setActive(blank);
				double is = imgIS.getI(false, line, dp);
				if(is==0)
					return 0;
				//
				intensity = intensity/is*concentrationFactor;
				b.setActive(tmp);
				return intensity;
			} 
		}
		return intensity; 
	}

	public boolean isApplicable() {
		return (imgIS!=null && imgIS.getData()!=null);
	}
	
	public Image2D getImgIS() {
		return imgIS;
	}
	public void setImgIS(Image2D imgIS) {
		this.imgIS = imgIS;
	}
	public double getConcentrationFactor() {
		return concentrationFactor;
	}
	public void setConcentrationFactor(double concentrationFactor) {
		this.concentrationFactor = concentrationFactor;
	}
	
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
	}
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
	} 
	
}
