package net.rs.lamsi.massimager.Settings;

import java.io.File;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Heatmap.Heatmap;

import org.jfree.chart.plot.XYPlot;


public class SettingsImage extends Settings {

	public enum XUNIT {
		DP,s
	}
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	
	public static int MODE_IMAGING_ONEWAY = 0, MODE_IMAGING_TWOWAYS = 1;
	public static int MODE_SCANS_PER_LINE = 0, MODE_TIME_PER_LINE = 1;
	
	protected String title = "NODEF", filepath = "";
	protected float velocity, spotsize;
	// crop marks
	protected double x0=0,x1=0,y0=0,y1=0;
	// Imaging Mode
	protected int imagingMode = 0;
	// reflect 
	protected boolean reflectHorizontal = false, reflectVertical = false;
	// rotate 0 90 180 270
	protected int rotationOfData = 0;
	//
	protected double timePerLine = 1;
	protected int modeTimePerLine = MODE_TIME_PER_LINE; 
	protected boolean isTriggert = false;
	
	protected boolean allFiles, isBinaryData = false;
	// Metadata
	protected String metadata = "";
 

	@Override
	public void resetAll() { 
		velocity = 50;
		spotsize = 50;
		allFiles = true;
		title = "";
		isTriggert = false;
		timePerLine = 60;
		deleteCropMarks();
		imagingMode = MODE_IMAGING_ONEWAY;
		rotationOfData = 0;
		reflectHorizontal = false; 
		reflectVertical = false;
		isBinaryData = false;
	}
	
	
	public void setAll(String title, float velocity, float spotsize, int imagingMode, boolean reflectHoriz, boolean reflectVert, int rotationOfData, boolean isBinaryData) { 
		this.velocity = velocity;
		this.spotsize = spotsize; 
		this.title = title;    
		this.rotationOfData = rotationOfData;
		this.reflectHorizontal = reflectHoriz; 
		this.reflectVertical = reflectVert;
		this.isBinaryData = isBinaryData; 
		this.imagingMode = imagingMode;
	}
	
	
	@Override
	public void applyToImage(Image2D img) throws Exception {
		// dont copy name
		String name = img.getTitle();
		String path = img.getSettImage().getRAWFilepath();
		super.applyToImage(img);
		img.getSettImage().setTitle(name);
		img.getSettImage().setRAWFilepath(path);
	}
	

	public SettingsImage(String path, String fileEnding) {
		super(path, fileEnding); 
	}

	public float getVelocity() {
		return velocity;
	}

	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}

	public float getSpotsize() {
		return spotsize;
	}

	public void setSpotsize(float spotsize) {
		this.spotsize = spotsize;
	} 
 

	public boolean isAllFiles() {
		return allFiles;
	}

	public void setAllFiles(boolean allFiles) {
		this.allFiles = allFiles;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public String getRAWFilepath() {
		return filepath;
	}

	public void setRAWFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String toListName() { 
		return getTitle()+"; "+getRAWFileName()+"; "+getRAWFilepath();
	}

	public String getMetadata() {
		return metadata;
	} 
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	} 
	public double getTimePerLine() {
		return timePerLine;
	}

	public void setTimePerLine(double timePerLine) {
		this.timePerLine = timePerLine;
	}

	public boolean isTriggert() {
		return isTriggert;
	}

	public void setTriggert(boolean isTriggert) {
		this.isTriggert = isTriggert;
	}
 

	public int getModeTimePerLine() {
		return modeTimePerLine;
	}

	public void setModeTimePerLine(int modeTimePerLine) {
		this.modeTimePerLine = modeTimePerLine;
	}

	public int getImagingMode() {
		return imagingMode;
	}


	public void setImagingMode(int imagingMode) {
		this.imagingMode = imagingMode;
	}


	public boolean isReflectHorizontal() {
		return reflectHorizontal;
	}


	public void setReflectHorizontal(boolean reflectHorizontal) {
		this.reflectHorizontal = reflectHorizontal;
	}


	public boolean isReflectVertical() {
		return reflectVertical;
	}


	public void setReflectVertical(boolean reflectVertical) {
		this.reflectVertical = reflectVertical;
	}


	public int getRotationOfData() {
		return rotationOfData;
	}


	public void setRotationOfData(int rotationOfData) {
		this.rotationOfData = rotationOfData;
	}


	public void deleteCropMarks() {
		x0=0; x1=0; y0=0; y1=0;
	}

	public void applyCropMarks(double x0, double x1, double y0, double y1) { 
		this.x0 = x0;
		this.x1 = x1;
		this.y0 = y0;
		this.y1 = y1;
	} 

	public void applyCropMarksOnImage(Heatmap heat) { 
		XYPlot plot = heat.getPlot();
		plot.getDomainAxis().setLowerBound(x0);
		plot.getDomainAxis().setUpperBound(x1);
		plot.getRangeAxis().setLowerBound(y0);
		plot.getRangeAxis().setUpperBound(y1); 
	}


	/**
	 * the raw file name of the raw path 
	 * file.extension
	 * @return
	 */
	public String getRAWFileName() {
		return new File(getRAWFilepath()).getName();
	}


	public boolean isBinaryData() {
		return isBinaryData;
	}


	public void setBinaryData(boolean isBinaryData) {
		this.isBinaryData = isBinaryData;
	}
}
