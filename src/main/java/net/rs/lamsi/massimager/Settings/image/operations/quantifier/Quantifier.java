package net.rs.lamsi.massimager.Settings.image.operations.quantifier;

import java.io.Serializable;

import net.rs.lamsi.massimager.Image.Image2D;
import net.rs.lamsi.massimager.Image.data.ScanLine;
import net.rs.lamsi.massimager.Settings.image.operations.listener.IntensityProcessingChangedListener;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.RectSelection;

public class Quantifier implements Serializable, IntensityProcessingChangedListener { 
	private static final long serialVersionUID = 1L;
	
	public static final int MODE_AVERAGE = 0, MODE_AVERAGE_PER_LINE = 1, MODE_AVERAGE_BOXES = 2;
	
	protected Image2D img;
	// intensityProcessingChanged? save lastIProcChangeTime and compare with one from img
	protected long lastIProcChangeTime = 0;
	// mode
	protected int mode = 1;
	// bounds
	protected int lowerBound, upperBound;
	// choose region
	protected boolean useStart, useMiddle, useEnd, isActive = true, useSelectedExcluded = true, useRawData = false;
	
	// get quotient
	protected double averageI=-1;
	protected double[] averageILines;
	
	// concentration 
	protected double concentration = 1;
	
	// draw some manual boxes in img!
	
	
	
	/**
	 * call this always if something changes
	 * @param line
	 * @return
	 */
	private double calcAverageIntensity(int line){
		//
		averageI = 0;
		
			// average for all lines
			averageILines = new double[img.getLines().length];
			int counterLines = 0;
			for(int i=0; i<averageILines.length; i++) {
				averageILines[i]=0;
				int counter = 0;
				// start:
				if(useStart) {
					for(int dp=0; dp<lowerBound; dp++) {
						if((!img.isExcludedDP(i, dp) && img.isSelectedDP(i, dp)) || !useSelectedExcluded) {
							averageILines[i] += useRawData? img.getDP(i, dp).getI() : img.getIProcessed(i, dp);
							counter++;
						}
					}
				}
				// middle
				if(useMiddle) {
					for(int dp=lowerBound; dp<upperBound; dp++) {
						if((!img.isExcludedDP(i, dp) && img.isSelectedDP(i, dp)) || !useSelectedExcluded) {
							averageILines[i] += useRawData? img.getDP(i, dp).getI() : img.getIProcessed(i, dp);
							counter++;
						}
					}
				}
				// end
				if(useEnd && upperBound>=lowerBound) {
					for(int dp=upperBound; dp<img.getLine(line).getDPCount(); dp++) {
						if((!img.isExcludedDP(i, dp) && img.isSelectedDP(i, dp)) || !useSelectedExcluded) {
							averageILines[i] += useRawData? img.getDP(i, dp).getI() : img.getIProcessed(i, dp);
							counter++; 
						}
					}
				}
				// all
				averageI += averageILines[i];
				counterLines += counter;
				// average this line
				if(counter>0)
					averageILines[i]= averageILines[i]/counter;
			}
			// average for all
			if(counterLines>0)
				averageI = averageI/counterLines;
			 
			// return right line
			return averageILines[line];
	}
	/**
	 * for mode average all: uses all dp
	 * or average boxes: uses selection and exclusion model
	 */
	private void calcAverageIRects() {
		//
		averageI = 0;
		int counter = 0;
		 
		if(mode==MODE_AVERAGE_BOXES && img.getSelectedData().size()>0) {
			// loop through all rects
			for(int r=0; r<img.getSelectedData().size(); r++) {
				RectSelection rect = img.getSelectedData().get(r); 
				// loop through all dp in rect
				for(int l=rect.getMinY(); l<=rect.getMaxY(); l++) {
					for(int i=rect.getMinX(); i<=rect.getMaxX(); i++) {
						boolean isDouble = false;
						// check if DP is in other rect
						for(int old=0; old<r; old++) {
							// do not double
							if(img.getSelectedData().get(old).contains(i, l)) {
								isDouble = true;
								break;
							}
						}
						// add
						if(!isDouble && !img.isExcludedDP(l, i)) {
							averageI += useRawData? img.getDP(l, i).getI() : img.getIProcessed(l, i);
							counter ++;
						}
					} 
				} 
			}
		}
		else if(mode==MODE_AVERAGE_BOXES && img.getSelectedData().size()==0) {
			// all dp except from excluded
			for(int l=0; l<img.getLines().length; l++) {
				ScanLine line = img.getLine(l);
				for (int i = 0; i < line.getDPCount(); i++) { 
					// add?
					if(!img.isExcludedDP(l,i)) {
						averageI += useRawData? img.getDP(l, i).getI() : img.getIProcessed(l, i);
						counter ++;
					}
				}
			}
		}
		if(counter>0)
			averageI = averageI / counter;
		 
	}
	/**
	 * call for getting the average intensity of a line
	 * if mode is average it will return the average over all lines!
	 * @param line
	 * @return
	 */
	public double getAverageIntensity(int line) {
		checkForUpdateInIProc();
		// calc
		if(mode==MODE_AVERAGE_BOXES && (averageI==-1 || averageI==Double.NaN)) 
			calcAverageIRects(); 
		else if(mode!=MODE_AVERAGE_BOXES && (averageILines==null || (averageI==-1 || averageI==Double.NaN)))
			calcAverageIntensity(line);
		// return
		if(mode==MODE_AVERAGE || mode==MODE_AVERAGE_BOXES)
			return averageI;
		else return averageILines[line];
	}
	/**
	 * call for getting the average intensity
	 * @return
	 */
	public double getAverageIntensity() {
		checkForUpdateInIProc();
		if(mode==MODE_AVERAGE_BOXES && (averageI==-1 || averageI==Double.NaN))
			calcAverageIRects();  
		else if(mode!=MODE_AVERAGE_BOXES && (averageILines==null || (averageI==-1 || averageI==Double.NaN)))
			calcAverageIntensity(0);
		// return
		return averageI;
	}
	
	/**
	 * returns all av of lines
	 * @return
	 */
	public double[] getAverageIntensityForLines() {
		checkForUpdateInIProc();
		if(averageILines==null) {
			calcAverageIntensity(0); 
		}
		return averageILines;
	}
	

	/**
	 * important: check if 
	 */
	private void checkForUpdateInIProc() {
		if(lastIProcChangeTime!=img.getLastIProcChangeTime()) {
			ImageEditorWindow.log("I PROCESSING CHANGED IN QUANTIFIER", LOG.DEBUG);
			lastIProcChangeTime = img.getLastIProcChangeTime();
			fireIntensityProcessingChanged();
		}
	}
	@Override
	public void fireIntensityProcessingChanged() {
		averageILines=null;
		averageI = -1;
	}
	
	public Image2D getImg() {
		return img;
	}
	public void setImg(Image2D img) {
		this.img = img;
		fireIntensityProcessingChanged();
	}
	public int getLowerBound() {
		return lowerBound;
	}
	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
		fireIntensityProcessingChanged();
	}
	public int getUpperBound() {
		return upperBound;
	}
	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
		fireIntensityProcessingChanged();
	}
	public boolean isUseStart() {
		return useStart;
	}
	public void setUseStart(boolean useStart) {
		this.useStart = useStart;
		fireIntensityProcessingChanged();
	}
	public boolean isUseMiddle() {
		return useMiddle;
	}
	public void setUseMiddle(boolean useMiddle) {
		this.useMiddle = useMiddle;
		fireIntensityProcessingChanged();
	}
	public void setUseAll(boolean state) {
		this.useMiddle = state;
		useEnd = state;
		useStart = state;
		fireIntensityProcessingChanged();
	}
	public boolean isUseEnd() {
		return useEnd;
	}
	public void setUseEnd(boolean useEnd) {
		this.useEnd = useEnd;
		fireIntensityProcessingChanged();
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
		fireIntensityProcessingChanged();
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public double getConcentration() {
		return concentration;
	}
	public void setConcentration(double concentration) {
		this.concentration = concentration;
		fireIntensityProcessingChanged();
	}
	public boolean isUseSelectedExcluded() {
		return useSelectedExcluded;
	}
	public void setUseSelectedExcluded(boolean useSelectedExcluded) {
		this.useSelectedExcluded = useSelectedExcluded;
		fireIntensityProcessingChanged();
	}
	public boolean isUseRawData() {
		return useRawData;
	}
	public void setUseRawData(boolean useRawData) {
		this.useRawData = useRawData;
	}
	
}
