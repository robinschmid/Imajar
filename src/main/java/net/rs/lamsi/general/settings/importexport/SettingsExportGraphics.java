package net.rs.lamsi.general.settings.importexport;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import org.jfree.ui.FloatDimension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.importexport.SettingsExportGraphics.FORMAT;
import net.rs.lamsi.general.settings.importexport.SettingsImageResolution.DIM_UNIT;
import net.rs.lamsi.utils.FileAndPathUtil;

public class SettingsExportGraphics extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
    public enum FORMAT {
    	PDF, EMF, EPS, SVG, PNG, JPG;
    	public String toString() {
    		return super.toString().toLowerCase();
    	}

		public boolean isPixel() {
			return equals(FORMAT.JPG) || 
					equals(FORMAT.PNG);
		};
    }
    
    public enum FIXED_SIZE {
    	PLOT, CHART
    }
	//
	private String fileName = "";
	private File path;
	private FORMAT format; 
	private FIXED_SIZE fixedSize = FIXED_SIZE.CHART;
	
	// resolution
	private SettingsImageResolution resolution;
	// only use width
	protected boolean useOnlyWidth = false;
	
	protected boolean showAnnotations = true;
	
	// Background Color
	private Color colorBackground = null;
	
	
	public SettingsExportGraphics() {
		super("GraphicsExport", "/Settings/export", "setExport"); 
		resetAll();		
	} 
	@Override
	public void resetAll() {
		path = null;
		format = FORMAT.PDF;  
		fileName = "";
		resolution = new SettingsImageResolution();
		showAnnotations = true;
	}
	

	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "fileName", fileName); 
		toXML(elParent, doc, "path", path); 
		toXML(elParent, doc, "format", format); 
		toXML(elParent, doc, "useOnlyWidth", useOnlyWidth); 
		toXML(elParent, doc, "showAnnotations", showAnnotations); 
		toXML(elParent, doc, "colorBackground", colorBackground); 
		toXML(elParent, doc, "fixedSize", fixedSize);
		resolution.appendSettingsToXML(elParent, doc);
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("fileName")) fileName = nextElement.getTextContent(); 
				else if(paramName.equals("path"))path = fileFromXML(nextElement);  
				else if(paramName.equals("format"))format = FORMAT.valueOf(nextElement.getTextContent());  
				else if(paramName.equals("useOnlyWidth"))useOnlyWidth = booleanFromXML(nextElement); 
				else if(paramName.equals("showAnnotations"))showAnnotations = booleanFromXML(nextElement);  
				else if(paramName.equals("colorBackground"))colorBackground = colorFromXML(nextElement);  
				else if(paramName.equals("fixedSize")) fixedSize = FIXED_SIZE.valueOf(nextElement.getTextContent());
				else if(isSettingsNode(nextElement, resolution.getSuperClass()))
					resolution.loadValuesFromXML(nextElement, doc);
			}
		}
	}
	/**
	 * Parses the full file path with path\filename.format
	 * @return
	 */
	public File getFullFilePath() {
		return FileAndPathUtil.getRealFilePath(path, fileName, getFormatAsString());
	}
	public String getFormatAsString() {
		return format.toString();
	}
	public File getPath() {
		return path;
	}
	public void setPath(File path) {
		this.path = path;
	}
	public FORMAT getFormat() {
		return format;
	}
	public void setFormat(FORMAT format) {
		this.format = format;
	}
	public int getResolution() {
		return resolution.getResolution();
	}
	public DIM_UNIT getUnit() {
		return resolution.getUnit();
	}
	public void setUnit(DIM_UNIT unit) {
		resolution.setUnit(unit);
	}
	public void setResolution(int resolution) {
		this.resolution.setResolution(resolution);;
	}
	public Dimension getSize() {
		return resolution.getSize();
	}
	public void setSize(Dimension size) {
		this.resolution.setSize(size); 
	}

	/**
	 * width and height in DIM_UNIT
	 * @return
	 */
	public FloatDimension getSizeInUnit() {
		return resolution.getSizeInUnit();
	} 
	/**
	 * Sets the size in inches by given width and height
	 * @param width
	 * @param height
	 * @param unit
	 */
	public void setSize(float width, float height, DIM_UNIT unit) { 
		resolution.setSizeAndUnit(width, height, unit);
	}
	/**
	 * Sets the size in inches by given width and height
	 * @param width
	 * @param height
	 * @param unit
	 */
	public void setSize(double width, double height, DIM_UNIT unit) { 
		resolution.setSizeAndUnit((float)width, (float)height, unit);
	}
	public void setHeight(double height) {
		resolution.setSize((int)resolution.getSize().getWidth(), (int) height);
	}  
	public Color getColorBackground() {
		return colorBackground;
	}
	public void setColorBackground(Color colorBackground) {
		this.colorBackground = colorBackground;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public boolean isUseOnlyWidth() {
		return useOnlyWidth;
	}
	public void setUseOnlyWidth(boolean useOnlyWidth) {
		this.useOnlyWidth = useOnlyWidth;
	}
	public FIXED_SIZE getFixedSize() {
		return fixedSize;
	}
	public void setFixedSize(FIXED_SIZE fixedSize) {
		this.fixedSize = fixedSize;
	}
	public boolean isShowAnnotations() {
		return showAnnotations;
	}
	public void setShowAnnotations(boolean showAnnotations) {
		this.showAnnotations = showAnnotations;
	}
	
}
