package net.rs.lamsi.massimager.Settings.importexport;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.utils.FileAndPathUtil;

public class SettingsExportGraphics extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	public static final int FORMAT_PDF = 0, FORMAT_PNG = 1, FORMAT_JPG = 2, FORMAT_SVG = 4, FORMAT_EPS = 5;
	//
	private String fileName = "";
	private File path;
	private int format; 
	
	// resolution
	private SettingsImageResolution resolution;
	// only use width
	protected boolean useOnlyWidth = false;
	
	// Background Color
	private Color colorBackground = null;
	
	
	public SettingsExportGraphics() {
		super("GraphicsExport", "/Settings/export", "setExport"); 
		resetAll();		
	} 
	@Override
	public void resetAll() {
		path = null;
		format = 0;  
		fileName = "";
		resolution = new SettingsImageResolution();
	}
	

	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "fileName", fileName); 
		toXML(elParent, doc, "path", path); 
		toXML(elParent, doc, "format", format); 
		toXML(elParent, doc, "useOnlyWidth", useOnlyWidth); 
		toXML(elParent, doc, "colorBackground", colorBackground); 
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
				else if(paramName.equals("format"))format = intFromXML(nextElement);  
				else if(paramName.equals("useOnlyWidth"))useOnlyWidth = booleanFromXML(nextElement);  
				else if(paramName.equals("colorBackground"))colorBackground = colorFromXML(nextElement);  
				else if(paramName.equals(resolution.getDescription()))
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
		switch (format) {
		case FORMAT_PDF:
			return "pdf";  
		case FORMAT_PNG:
			return "png"; 
		case FORMAT_JPG:
			return "jpg"; 
		case FORMAT_EPS:
			return "eps"; 
		case FORMAT_SVG:
			return "svg";  
		}
		return "";
	}
	public File getPath() {
		return path;
	}
	public void setPath(File path) {
		this.path = path;
	}
	public int getFormat() {
		return format;
	}
	public void setFormat(int format) {
		this.format = format;
	}
	public int getResolution() {
		return resolution.getResolution();
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
	 * Sets the size in inches by given width and height
	 * @param width
	 * @param height
	 * @param unit
	 */
	public void setSize(float width, float height, int unit) { 
		resolution.setSize(width, height, unit);
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
	
}
