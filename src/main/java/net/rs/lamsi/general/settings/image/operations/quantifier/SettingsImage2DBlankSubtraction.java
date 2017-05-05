package net.rs.lamsi.general.settings.image.operations.quantifier;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.utils.FileAndPathUtil;

public class SettingsImage2DBlankSubtraction extends SettingsImage2DQuantifier {
	// do not change the version!
    private static final long serialVersionUID = 1L;

    /**
     * for MODE ACTUAL DP the imgBlank has to be the same size like img
     */
    public static final int MODE_AVERAGE = 0, MODE_ACTUAL_DP = 1, MODE_AVERAGE_PER_LINE = 2;
    // data in a img or at the start of the file
    public static final int MODE_DATA_IMG = 0, MODE_DATA_START = 1;
     
    protected int mode = 0, modeData = 0;
    
    // MOde1 blank as full image - point for point
    protected Image2D imgBlank; 
    
    // Mode2 use data start
    private Quantifier qSameImage;
    private boolean showInChart = false;
	
	
	public SettingsImage2DBlankSubtraction() {
		super(MODE_BLANK); 
		resetAll();
	} 
	public SettingsImage2DBlankSubtraction(Image2D imgBlank) { 
		this(); 
		this.imgBlank = imgBlank; 
	} 
	
	@Override
	public void resetAll() {
		isActive = false;
		imgBlank = null; 
		mode = 2;
		modeData = 1;
		qSameImage = new Quantifier();
		qSameImage.setUseStart(true);
		qSameImage.setUseMiddle(false);
		qSameImage.setUseEnd(false);
		qSameImage.setUseSelectedExcluded(false);
		qSameImage.setUseRawData(true);
		qSameImage.setLowerBound(0);
		qSameImage.setUpperBound(-1); 
		qSameImage.setMode(Quantifier.MODE_AVERAGE_PER_LINE);
		showInChart = false;
	}
	

	/**
	 * the magic is done here
	 */
	@Override
	public double calcIntensity(Image2D img,  int line, int dp, double intensity) {  
		if(isActive && isApplicable()) {
			// data mode
			return intensity - getAverageIntensity(line, dp);
		}
		else return intensity;
	}
	
	/**
	 * Average intensity of a line or whatever
	 * @param line
	 * @param dp
	 * @return
	 */
	public double getAverageIntensity(int line, int dp) {
		if(isApplicable()) {
			if(modeData==MODE_DATA_IMG) {
				// one image as blank
				if(mode==MODE_AVERAGE) return (imgBlank.getAverageIProcessed());
				else if(mode==MODE_ACTUAL_DP) return (imgBlank.getI(false,line, dp));
				else return (imgBlank.getAverageIProcessed(line));
			} 
			else {
				// same image for blank: img parameter
				// TODO do it with a quantifier
				return qSameImage.getAverageIntensity(line);
			}
		}
		return -1;
	}
	 
	public Image2D getImgBlank() {
		return imgBlank;
	}
	public void setImgBlank(Image2D imgBlank) {
		this.imgBlank = imgBlank;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
		if(mode==MODE_AVERAGE)
			qSameImage.setMode(Quantifier.MODE_AVERAGE);
		if(mode==MODE_AVERAGE_PER_LINE)
			qSameImage.setMode(Quantifier.MODE_AVERAGE_PER_LINE);
	}
	public int getModeData() {
		return modeData;
	}
	public void setModeData(int modeData) {
		this.modeData = modeData;
	}
	public boolean isUseBothDataStartEnd() {
		return qSameImage.isUseEnd();
	}
	public void setUseBothDataStartEnd(boolean useBothDataStartEnd) {
		qSameImage.setUseStart(true);
		qSameImage.setUseEnd(useBothDataStartEnd);
	}
	public int getStart() {
		return qSameImage.getLowerBound();
	}
	public void setStart(int start) {
		qSameImage.setLowerBound(start);
	}
	public int getEnd() {
		return qSameImage.getUpperBound();
	}
	public void setEnd(int end) {
		qSameImage.setUpperBound(end);
	}
	public boolean isShowInChart() {
		return showInChart;
	}
	public void setShowInChart(boolean showInChart) {
		this.showInChart = showInChart;
	}
	public Quantifier getQSameImage() { 
		return qSameImage;
	}
	public boolean isApplicable() { 
		return (modeData==MODE_DATA_IMG && imgBlank!=null) || (modeData==MODE_DATA_START && qSameImage!=null) ;
	}
	
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
	}
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
	} 
	
}
