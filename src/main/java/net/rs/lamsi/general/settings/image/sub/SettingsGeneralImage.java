package net.rs.lamsi.general.settings.image.sub;

import java.io.File;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.myfreechart.themes.MyStandardChartTheme;
import net.rs.lamsi.general.settings.Settings;

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

	public static enum IMAGING_MODE{
		MODE_IMAGING_ONEWAY, MODE_IMAGING_TWOWAYS;
		
		public static IMAGING_MODE getMode(String value) {
			if(value.equals("0"))
				return MODE_IMAGING_ONEWAY;
			else if(value.equals("1"))
				return MODE_IMAGING_TWOWAYS;
			else return IMAGING_MODE.valueOf(value);
	    }
	}
	public static int MODE_SCANS_PER_LINE = 0, MODE_TIME_PER_LINE = 1;

	protected String title = "NODEF", shortTitle = "", filepath = "";
	protected boolean showShortTitle = true;
	protected float xPosTitle = 0.9f, yPosTitle = 0.9f;
	
	protected float velocity, spotsize;
	// crop marks
	protected double x0=0,x1=0,y0=0,y1=0;
	// Imaging Mode
	protected SettingsGeneralRotation rotation;
	// rotate 0 90 180 270
	//
	protected double timePerLine = 1;
	protected int modeTimePerLine = MODE_TIME_PER_LINE; 
	protected boolean isTriggered = false;

	protected boolean allFiles, isBinaryData = false;
	// Metadata
	protected String metadata = "";

	public SettingsGeneralImage(String path, String fileEnding) {
		super("SettingsGeneralImage", path, fileEnding); 
		rotation = new SettingsGeneralRotation();
		resetAll();
	}

	public SettingsGeneralImage() {
		this("/Settings/OESImage/", "setGIMG"); 
	} 

	@Override
	public void resetAll() { 
		velocity = 50;
		spotsize = 50;
		allFiles = true;
		title = "";
		shortTitle = "";
		showShortTitle = true;
		isTriggered = false;
		timePerLine = 60;
		deleteCropMarks();
		
		rotation.resetAll();
		isBinaryData = false;
		xPosTitle = 0.9f; 
		yPosTitle = 0.9f;
	}


	public void setAll(String title, String shortTitle, boolean useShortTitle, float xPos, float yPos, float velocity, float spotsize, IMAGING_MODE imagingMode, boolean reflectHoriz, boolean reflectVert, int rotationOfData, boolean isBinaryData) { 
		this.velocity = velocity;
		this.spotsize = spotsize; 
		this.isBinaryData = isBinaryData; 
		this.shortTitle = shortTitle;
		this.title = title;
		this.showShortTitle = useShortTitle;
		this.xPosTitle = xPos;
		this.yPosTitle = yPos;
		rotation.setAll(imagingMode, reflectHoriz, reflectVert, rotationOfData);
		// imaging path? TODO
	}


	@Override
	public void applyToImage(Image2D img) throws Exception {
		// dont copy name
		String name = img.getTitle();
		String path = img.getSettings().getSettImage().getRAWFilepath();
		super.applyToImage(img);
		img.getSettings().getSettImage().setTitle(name);
		img.getSettings().getSettImage().setRAWFilepath(path);
	}
	
	@Override
	public void applyToHeatMap(Heatmap heat) {
		super.applyToHeatMap(heat);
		// TODO apply to title in heat
		heat.setShortTitle(xPosTitle, yPosTitle, showShortTitle);
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
		toXML(elParent, doc, "isBinaryData", isBinaryData); 
		toXML(elParent, doc, "filepath", filepath); 
		toXML(elParent, doc, "shortTitle", shortTitle); 
		toXML(elParent, doc, "showShortTitle", showShortTitle); 
		toXML(elParent, doc, "xPosTitle", xPosTitle); 
		toXML(elParent, doc, "yPosTitle", yPosTitle); 
		
		rotation.appendSettingsToXML(elParent, doc);
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
				else if(paramName.equals("xPosTitle"))xPosTitle = floatFromXML(nextElement); 
				else if(paramName.equals("yPosTitle"))yPosTitle = floatFromXML(nextElement);  
				else if(paramName.equals("title"))title = nextElement.getTextContent();  
				else if(paramName.equals("shortTitle"))shortTitle = nextElement.getTextContent();   
				else if(paramName.equals("isTriggered"))isTriggered = booleanFromXML(nextElement);  
				else if(paramName.equals("timePerLine"))timePerLine = doubleFromXML(nextElement);  
				else if(paramName.equals("isBinaryData"))isBinaryData = booleanFromXML(nextElement);  
				else if(paramName.equals("showShortTitle"))showShortTitle = booleanFromXML(nextElement);  
				else if(paramName.equals("filepath"))filepath = nextElement.getTextContent(); 
				else if(isSettingsNode(nextElement, rotation.getSuperClass()))
					rotation.loadValuesFromXML(nextElement, doc);
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

	public IMAGING_MODE getImagingMode() {
		return rotation.getImagingMode();
	}


	public void setImagingMode(IMAGING_MODE imagingMode) {
		rotation.setImagingMode(imagingMode);
	}


	public boolean isReflectHorizontal() {
		return rotation.isReflectHorizontal();
	}


	public void setReflectHorizontal(boolean reflectHorizontal) {
		rotation.setReflectHorizontal(reflectHorizontal);
	}


	public boolean isReflectVertical() {
		return rotation.isReflectVertical();
	}


	public void setReflectVertical(boolean reflectVertical) {
		rotation.setReflectVertical(reflectVertical);
	}


	public int getRotationOfData() {
		return rotation.getRotationOfData();
	}


	public void setRotationOfData(int rotationOfData) {
		rotation.setRotationOfData(rotationOfData);
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

	
	
	public String getShortTitle() {
		return shortTitle;
	}

	public boolean isShowShortTitle() {
		return showShortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public void setShowShortTitle(boolean showShortTitle) {
		this.showShortTitle = showShortTitle;
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

	public float getXPosTitle() {
		return xPosTitle;
	}

	public float getYPosTitle() {
		return yPosTitle;
	}

	public void setXPosTitle(float xPosTitle) {
		this.xPosTitle = xPosTitle;
	}

	public void setYPosTitle(float yPosTitle) {
		this.yPosTitle = yPosTitle;
	}
	
}
