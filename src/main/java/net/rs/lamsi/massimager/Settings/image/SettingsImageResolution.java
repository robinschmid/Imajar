package net.rs.lamsi.massimager.Settings.image;

import java.awt.Dimension;

import net.rs.lamsi.massimager.Settings.Settings;

import com.itextpdf.text.Utilities;

public class SettingsImageResolution extends Settings { 
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	//
	public static final int UNIT_CM = 0, UNIT_MM = 1, UNIT_PT = 2, UNIT_INCH = 3, UNIT_PX = 4;
	//
	private String fileName = ""; 
	
	// resolution
	private int resolution = 300;
	private Dimension size; 
	
	
	public SettingsImageResolution() {
		super("", "");
		resetAll();		
	} 
	
	@Override
	public void resetAll() { 
		size = null; 
	} 
	public int getResolution() {
		return resolution;
	}
	public void setResolution(int resolution) {
		this.resolution = resolution;
	}
	
	/**
	 * 
	 * @return size as pt
	 */
	public Dimension getSize() {
		return size;
	}  
	
	/**
	 * Sets the size in inches by given width and height
	 * @param width
	 * @param height
	 * @param unit
	 */
	public void setSize(float width, float height, int unit) { 
		// convert to pt
		switch(unit) {
		case UNIT_CM:
			width = Utilities.millimetersToPoints(width*10.f);
			height = Utilities.millimetersToPoints(height*10.f);
			break;
		case UNIT_MM:
			width = Utilities.millimetersToPoints(width);
			height = Utilities.millimetersToPoints(height);
			break;
		case UNIT_INCH:
			width = Utilities.inchesToPoints(width);
			height = Utilities.inchesToPoints(height);
			break; 
		case UNIT_PX:
			width = width;
			height = height;
			break;
		}
		
		this.size = new Dimension((int)width, (int)height);
	} 
	public void setSize(int width, int height) { 
		this.size = new Dimension((int)width, (int)height);
	}
	
	/**
	 * 
	 * @param size as pt
	 */
	public void setSize(Dimension size) {
		this.size = size;
	} 
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}  
	
}
