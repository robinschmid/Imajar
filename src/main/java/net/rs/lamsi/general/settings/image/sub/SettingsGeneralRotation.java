package net.rs.lamsi.general.settings.image.sub;

import java.io.File;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.myfreechart.themes.MyStandardChartTheme;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage.IMAGING_MODE;

import org.jfree.chart.plot.XYPlot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class SettingsGeneralRotation extends Settings {
	// do not change the version!
	private static final long serialVersionUID = 1L;
	//

	

	// Imaging Mode
	protected IMAGING_MODE imagingMode = IMAGING_MODE.MODE_IMAGING_ONEWAY;
	// reflect 
	protected boolean reflectHorizontal = false, reflectVertical = false;
	// rotate 0 90 180 270
	protected int rotationOfData = 0;
	

	public SettingsGeneralRotation(String path, String fileEnding) {
		super("SettingsGeneralImage", path, fileEnding); 
	}
	public SettingsGeneralRotation() {
		super("SettingsGeneralImage", "/Settings/GeneralImage/", "setRotation"); 
	} 

	@Override
	public void resetAll() {  
		imagingMode = IMAGING_MODE.MODE_IMAGING_ONEWAY;
		rotationOfData = 0;
		reflectHorizontal = false; 
		reflectVertical = false; 
	}


	public void setAll(IMAGING_MODE imagingMode, boolean reflectHoriz, boolean reflectVert, int rotationOfData) { 
		this.rotationOfData = rotationOfData;
		this.reflectHorizontal = reflectHoriz; 
		this.reflectVertical = reflectVert;
		this.imagingMode = imagingMode;
	}


	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "imagingMode", imagingMode); 
		toXML(elParent, doc, "rotationOfData", rotationOfData); 
		toXML(elParent, doc, "reflectHorizontal", reflectHorizontal); 
		toXML(elParent, doc, "reflectVertical", reflectVertical); 
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("imagingMode"))imagingMode = IMAGING_MODE.valueOf(nextElement.getTextContent());  
				else if(paramName.equals("rotationOfData"))rotationOfData = intFromXML(nextElement);  
				else if(paramName.equals("reflectHorizontal"))reflectHorizontal = booleanFromXML(nextElement);  
				else if(paramName.equals("reflectVertical"))reflectVertical = booleanFromXML(nextElement);  
			}
		}
	}


	public IMAGING_MODE getImagingMode() {
		return imagingMode;
	}


	public void setImagingMode(IMAGING_MODE imagingMode) {
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

}
