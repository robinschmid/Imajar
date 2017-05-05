package net.rs.lamsi.general.settings.importexport;

import java.awt.Dimension;

import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.utils.useful.FloatDim;

import org.jfree.ui.FloatDimension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.itextpdf.text.Utilities;

public class SettingsImageResolution extends Settings { 
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	//
	public static enum DIM_UNIT {
		CM, MM, PT, INCH, PX;
	}
	//
	private String fileName = ""; 
	
	// resolution
	private DIM_UNIT unit = DIM_UNIT.CM; 
	private int resolution = 300;
	private Dimension size; 
	
	
	public SettingsImageResolution() {
		super("SettingsImageResolution", "", "");
		resetAll();		
	} 
	
	@Override
	public void resetAll() { 
		size = new Dimension(0,0); 
		unit = DIM_UNIT.CM; 
		 resolution = 300;
	} 
	public int getResolution() {
		return resolution;
	}
	public void setResolution(int resolution) {
		this.resolution = resolution;
	}

	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "resolution", resolution); 
		toXML(elParent, doc, "unit", unit); 
		toXML(elParent, doc, "width", size.getWidth()); 
		toXML(elParent, doc, "height", size.getHeight()); 
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("resolution")) resolution = intFromXML(nextElement); 
				else if(paramName.equals("unit")) unit = DIM_UNIT.valueOf(nextElement.getTextContent()); 
				else if(paramName.equals("width"))size.setSize(doubleFromXML(nextElement), size.getHeight());  
				else if(paramName.equals("height"))size.setSize(size.getWidth(), doubleFromXML(nextElement));
			}
		}
	}
	
	/**
	 * 
	 * @return size as pt
	 */
	public Dimension getSize() {
		return size;
	}  

	/**
	 * 
	 * @return size in given unit
	 */
	public FloatDimension getSizeInUnit() {
		return getSizeInUnit(size, unit);
	}  
	/**
	 * 
	 * @return size in given unit
	 */
	public static FloatDimension getSizeInUnit(Dimension size, DIM_UNIT unit) {
		float w=0, h=0;
		switch(unit) {
		case CM:
			w = Utilities.pointsToMillimeters((float)size.getWidth())/10.f;
			h = Utilities.pointsToMillimeters((float)size.getHeight())/10.f;
			break;
		case MM:
			w = Utilities.pointsToMillimeters((float)size.getWidth());
			h = Utilities.pointsToMillimeters((float)size.getHeight());
			break;
		case INCH:
			w = Utilities.pointsToInches((float)size.getWidth());
			h = Utilities.pointsToInches((float)size.getHeight());
			break; 
		case PX:
		case PT:
			w = (float) size.getWidth();
			h = (float) size.getHeight();
		}
		return new FloatDimension(w, h);
	} 
	/**
	 * Sets the size in inches by given width and height
	 * @param width
	 * @param height
	 * @param unit
	 */
	public void setSizeAndUnit(float width, float height, DIM_UNIT unit) { 
		setUnit(unit);
		// convert to pt
		switch(unit) {
		case CM:
			width = Utilities.millimetersToPoints(width*10.f);
			height = Utilities.millimetersToPoints(height*10.f);
			break;
		case MM:
			width = Utilities.millimetersToPoints(width);
			height = Utilities.millimetersToPoints(height);
			break;
		case INCH:
			width = Utilities.inchesToPoints(width);
			height = Utilities.inchesToPoints(height);
			break; 
		case PX:
		case PT:
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

	public DIM_UNIT getUnit() {
		return unit;
	}  
	public void setUnit(DIM_UNIT unit) {
		this.unit = unit;
	}
	
}
