package net.rs.lamsi.multiimager.Frames.multiimageframe;

import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import net.rs.lamsi.general.datamodel.image.Image2D;

import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;


public class MultiImgTableRow {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	
	private Image2D img;
	private boolean isShowing = true, useRange = false;
	private double lower = 0, upper = 0, min = 0, max = 0;
	private int index = 0;

	
	public MultiImgTableRow(int index, Image2D i) {
		this(index, i, true, false, i.getMinIntensity(false), i.getMaxIntensity(false));
	}
	public MultiImgTableRow(int index, Image2D img, boolean isShowing, boolean useRange, double lower, double upper) {
		super();
		this.img = img;
		this.isShowing = isShowing;
		this.useRange = useRange;
		this.lower = lower;
		this.min = lower;
		this.upper = upper;
		this.max = upper;
		this.index= index;
	}

	/**
	 * range is multipied by 1000
	 * @return
	 */
	public Object[] getRowData() {
		return new Object[] {Integer.valueOf(index), img.getTitle(), isShowing, useRange, lower, upper, new int[]{(int)lower*1000, (int)upper*1000, (int)min*1000, (int)max*1000}};
	}

	/**
	 * devided by 1000.0
	 * @param value
	 */
	public boolean setRange(int[] value) {
		return setLower(value[0]/1000.0) || setUpper(value[1]/1000.0);
	}
	
	
	/**
	 * apply settings to boolean map
	 * map init as all true
	 * only change to false
	 * @param map 
	 */
	public void applyToMap(boolean[][] map) {
		if(isUseRange() && (max!=upper || min != lower)) {
			// lines
			for(int l = 0; l<map.length && l<img.getLineCount(); l++) {
				for(int d = 0; d<map[l].length && d<img.getLineLength(l); d++) {
					// check if img.intensity out of range
					if(!inRange(img.getIProcessed(l, d)))
						map[l][d] = false;
				}
			}
		}
	}
	
	/**applyToBinaryMap
	 * apply settings to binary map.
	 * i as index to change to 1
	 * @param map 
	 */
	public void applyToBinaryMap(Integer[][] map, int i) {
		if(isUseRange() && (max!=upper || min != lower)) {
			// lines
			for(int l = 0; l<map.length && l<img.getLineCount(); l++) {
				for(int d = 0; d<map[l].length && d<img.getLineLength(l); d++) {
					// check if img.intensity out of range
					if(inRange(img.getIProcessed(l, d)))
						map[l][d] += (int)Math.pow(2, i);
				}
			}
		}
	}
	
	

	public Image2D getImg() {
		return img;
	}

	public void setImg(Image2D img) {
		this.img = img;
	}

	public boolean isShowing() {
		return isShowing;
	}

	public void setShowing(boolean isShowing) {
		this.isShowing = isShowing;
	}

	public boolean isUseRange() {
		return useRange;
	}

	public void setUseRange(boolean useRange) {
		this.useRange = useRange;
	}

	public double getLower() {
		return lower;
	}

	public boolean setLower(double lower) {
		boolean res = this.lower!=lower;
		this.lower = lower;
		return res;
	}

	public double getUpper() {
		return upper;
	}

	public boolean setUpper(double upper) {
		boolean res = this.upper!=upper;
		this.upper = upper;
		return res;
	} 
	public boolean inRange(double value) {
		return value>=lower && value<=upper;
	}
	
}
