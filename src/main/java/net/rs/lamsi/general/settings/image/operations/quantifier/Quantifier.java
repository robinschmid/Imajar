package net.rs.lamsi.general.settings.image.operations.quantifier;

import java.io.Serializable;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.settings.image.operations.listener.IntensityProcessingChangedListener;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;

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
			averageILines = new double[img.getLineCount(lowerBound)];
			int counterLines = 0;
			for(int i=0; i<averageILines.length; i++) {
				averageILines[i]=0;
				int counter = 0;
				// start:
				if(useStart) {
					for(int dp=0; dp<lowerBound; dp++) {
						if((!img.isExcludedDP(i, dp) && img.isSelectedDP(i, dp)) || !useSelectedExcluded) {
							averageILines[i] += img.getI(useRawData,i, dp);
							counter++;
						}
					}
				}
				// middle
				if(useMiddle) {
					for(int dp=lowerBound; dp<upperBound; dp++) {
						if((!img.isExcludedDP(i, dp) && img.isSelectedDP(i, dp)) || !useSelectedExcluded) {
							averageILines[i] += img.getI(useRawData,i, dp);
							counter++;
						}
					}
				}
				// end
				if(useEnd && upperBound>=lowerBound) {
					for(int dp=upperBound; dp<img.getLineLength(line); dp++) {
						if((!img.isExcludedDP(i, dp) && img.isSelectedDP(i, dp)) || !useSelectedExcluded) {
							averageILines[i] += img.getI(useRawData,i, dp);
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
//		int counter = 0;
//		 
//		if(mode==MODE_AVERAGE_BOXES && img.getSelectedData().size()>0) {
//			// loop through all rects
//			for(int r=0; r<img.getSelectedData().size(); r++) {
//				RectSelection rect = img.getSelectedData().get(r); 
//				// loop through all dp in rect
//				for(int l=rect.getMinY(); l<=rect.getMaxY(); l++) {
//					for(int i=rect.getMinX(); i<=rect.getMaxX(); i++) {
//						boolean isDouble = false;
//						// check if DP is in other rect
//						for(int old=0; old<r; old++) {
//							// do not double
//							if(img.getSelectedData().get(old).contains(i, l)) {
//								isDouble = true;
//								break;
//							}
//						}
//						// add
//						if(!isDouble && !img.isExcludedDP(l, i)) {
//							averageI += img.getI(useRawData,i, i);
//							counter ++;
//						}
//					} 
//				} 
//			}
//		}
//		else if(mode==MODE_AVERAGE_BOXES && img.getSelectedData().size()==0) {
//			// all dp except from excluded
//			// TODO this is wron... Lower Bound! is wrong
//			for(int l=0; l<img.getLineCount(lowerBound); l++) {
//				for (int i = 0; i < img.getLineLength(l); i++) { 
//					// add?
//					if(!img.isExcludedDP(l,i)) {
//						averageI += img.getI(useRawData,l, i);
//						counter ++;
//					}
//				}
//			}
//		}
//		if(counter>0)
//			averageI = averageI / counter;
		 
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
			fireIntensityProcessingChanged(img);
		}
	}
	@Override
	public void fireIntensityProcessingChanged(Image2D img) {
		averageILines=null;
		averageI = -1;
	}
	
	public Image2D getImg() {
		return img;
	}
	public void setImg(Image2D img) {
		this.img = img;
		fireIntensityProcessingChanged(img);
	}
	public int getLowerBound() {
		return lowerBound;
	}
	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
		fireIntensityProcessingChanged(img);
	}
	public int getUpperBound() {
		return upperBound;
	}
	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
		fireIntensityProcessingChanged(img);
	}
	public boolean isUseStart() {
		return useStart;
	}
	public void setUseStart(boolean useStart) {
		this.useStart = useStart;
		fireIntensityProcessingChanged(img);
	}
	public boolean isUseMiddle() {
		return useMiddle;
	}
	public void setUseMiddle(boolean useMiddle) {
		this.useMiddle = useMiddle;
		fireIntensityProcessingChanged(img);
	}
	public void setUseAll(boolean state) {
		this.useMiddle = state;
		useEnd = state;
		useStart = state;
		fireIntensityProcessingChanged(img);
	}
	public boolean isUseEnd() {
		return useEnd;
	}
	public void setUseEnd(boolean useEnd) {
		this.useEnd = useEnd;
		fireIntensityProcessingChanged(img);
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
		fireIntensityProcessingChanged(img);
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
		fireIntensityProcessingChanged(img);
	}
	public boolean isUseSelectedExcluded() {
		return useSelectedExcluded;
	}
	public void setUseSelectedExcluded(boolean useSelectedExcluded) {
		this.useSelectedExcluded = useSelectedExcluded;
		fireIntensityProcessingChanged(img);
	}
	public boolean isUseRawData() {
		return useRawData;
	}
	public void setUseRawData(boolean useRawData) {
		this.useRawData = useRawData;
	}
	
}
