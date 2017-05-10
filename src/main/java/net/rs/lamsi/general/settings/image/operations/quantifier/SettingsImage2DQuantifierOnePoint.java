package net.rs.lamsi.general.settings.image.operations.quantifier;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.utils.FileAndPathUtil;

public class SettingsImage2DQuantifierOnePoint extends SettingsImage2DQuantifier {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    
    // image for one point
    protected Image2D imgEx;  
    // regression version to track changes
    protected int regressionVersionID = 0;
	
	public SettingsImage2DQuantifierOnePoint(Image2D ex) {
		super(MODE.ONE_POINT); 
		this.imgEx = ex;
	} 
	@Override
	public void resetAll() {
		super.resetAll(); 
		imgEx = null;
		regressionVersionID = 0;
	}

	@Override
	public Class getSuperClass() {
		return SettingsImage2DQuantifier.class; 
	}
	
	@Override
	public double calcIntensity(Image2D img, int line, int dp, double intensity) {
		SimpleRegression r = getRegression();
		if(r==null)
			return intensity;
		else {
			int nid = imgEx.getSettings().getSettSelections().getRegressionVersionID();
			if(regressionVersionID==nid)
				return (intensity - r.getIntercept())/r.getSlope();
			else {
				// regression has changed
				regressionVersionID = nid;
				img.fireIntensityProcessingChanged();
				return calcIntensity(img, line, dp, intensity);
			}
		}
	}
	
	public boolean isApplicable() {
		return getRegression() != null;
	}
	
	private SimpleRegression getRegression() {
		if(imgEx==null)
			return null;
		SettingsSelections s = imgEx.getSettings().getSettSelections();
		return s.getRegression();
	}
	
	public Image2D getImgEx() {
		return imgEx;
	}
	/**
	 * 
	 * @param ex
	 * @return true if external img has changed
	 */
	public boolean setImgEx(Image2D ex) {
		boolean state = ex==null || !ex.equals(this.imgEx);
		this.imgEx = ex;
		return state;
	}
	
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		if(imgEx!=null)
			toXML(elParent, doc, "externalSTDImage", imgEx);
	}
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
	} 
	
}
