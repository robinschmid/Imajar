package net.rs.lamsi.massimager.Settings.image.sub;

import java.io.File;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.MyFreeChart.themes.MyStandardChartTheme;
import net.rs.lamsi.massimager.Settings.Settings;

import org.jfree.chart.plot.XYPlot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class SettingsGeneralImage extends Settings {

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
	protected boolean isTriggered = false;

	protected boolean allFiles, isBinaryData = false;
	// Metadata
	protected String metadata = "";


	public SettingsGeneralImage(String path, String fileEnding) {
		super("SettingsGeneralImage", path, fileEnding); 
	}
	public SettingsGeneralImage() {
		super("SettingsGeneralImage", "/Settings/OESImage/", "setGIMG"); 
	} 

	@Override
	public void resetAll() { 
		velocity = 50;
		spotsize = 50;
		allFiles = true;
		title = "";
		isTriggered = false;
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
	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "velocity", velocity); 
		toXML(elParent, doc, "spotsize", spotsize); 
		toXML(elParent, doc, "allFiles", allFiles); 
		toXML(elParent, doc, "title", title); 
		toXML(elParent, doc, "isTriggered", isTriggered); 
		toXML(elParent, doc, "timePerLine", timePerLine); 
		toXML(elParent, doc, "imagingMode", imagingMode); 
		toXML(elParent, doc, "rotationOfData", rotationOfData); 
		toXML(elParent, doc, "reflectHorizontal", reflectHorizontal); 
		toXML(elParent, doc, "reflectVertical", reflectVertical); 
		toXML(elParent, doc, "isBinaryData", isBinaryData); 
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("allFiles")) allFiles = booleanFromXML(nextElement); 
				else if(paramName.equals("velocity"))velocity = floatFromXML(nextElement);  
				else if(paramName.equals("spotsize"))spotsize = floatFromXML(nextElement);  
				else if(paramName.equals("title"))title = nextElement.getTextContent();  
				else if(paramName.equals("isTriggered"))isTriggered = booleanFromXML(nextElement);  
				else if(paramName.equals("timePerLine"))timePerLine = doubleFromXML(nextElement);  
				else if(paramName.equals("imagingMode"))imagingMode = intFromXML(nextElement);  
				else if(paramName.equals("rotationOfData"))rotationOfData = intFromXML(nextElement);  
				else if(paramName.equals("reflectHorizontal"))reflectHorizontal = booleanFromXML(nextElement);  
				else if(paramName.equals("reflectVertical"))reflectVertical = booleanFromXML(nextElement);  
				else if(paramName.equals("isBinaryData"))isBinaryData = booleanFromXML(nextElement);  
			}
		}
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

	public String getRAWFolder() {
		File f = new File(filepath);
		return f.isDirectory()? f.getAbsolutePath() : f.getParent();
	}
	public File getRAWFolderPath() {
		File f = new File(filepath);
		return f.isDirectory()? f : f.getParentFile();
	}
	public String getRAWFolderName() {
		return getRAWFolderPath().getName();
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

	public boolean isTriggered() {
		return isTriggered;
	}

	public void setTriggered(boolean isTriggert) {
		this.isTriggered = isTriggert;
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
