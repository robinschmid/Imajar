package net.rs.lamsi.general.settings.importexport;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.importexport.SettingsImageDataImportTxt.ModeData;

public class SettingsImage2DDataSelectionsExport extends SettingsImage2DDataExport { 
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //  settings for xlsx export
    private boolean summary, definitions, arrays, imgEx, imgSel, imgSelNEx, shapes, shapesSelNEx, shapeData, x,y,z;
    //
	public SettingsImage2DDataSelectionsExport() {
		super("SettingsImage2DDataSelectionsExport","/Settings/Export/Image2D", "settExImg2DSel"); 
	} 
	
	@Override
	public void resetAll() {
		super.resetAll();
		summary = true; 
		definitions = true; 
		arrays = true; 
		imgEx = true; 
		imgSel = true; 
		imgSelNEx = true; 
		shapes = true; 
		shapesSelNEx = true;
		x=true;
		y=true;
		z=true;
		shapeData=true;
	}

	public void setAll(boolean summary, boolean definitions,
			boolean arrays, boolean imgEx, boolean imgSel, boolean imgSelNEx,
			boolean shapes, boolean shapesSelNEx, boolean shapeData, boolean x,
			boolean y, boolean z) {
		this.summary = summary;
		this.definitions = definitions;
		this.arrays = arrays;
		this.imgEx = imgEx;
		this.imgSel = imgSel;
		this.imgSelNEx = imgSelNEx;
		this.shapes = shapes;
		this.shapesSelNEx = shapesSelNEx;
		this.shapeData = shapeData;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	
	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		super.appendSettingsValuesToXML(elParent, doc);
		toXML(elParent, doc, "summary", summary);
		toXML(elParent, doc, "definitions", definitions);
		toXML(elParent, doc, "arrays", arrays);
		toXML(elParent, doc, "imgEx", imgEx);
		toXML(elParent, doc, "imgSel", imgSel);
		toXML(elParent, doc, "imgSelNEx", imgSelNEx);
		toXML(elParent, doc, "shapes", shapes);
		toXML(elParent, doc, "shapesSelNEx", shapesSelNEx); 
		toXML(elParent, doc, "shapeData", shapeData); 
		toXML(elParent, doc, "x", x); 
		toXML(elParent, doc, "y", y); 
		toXML(elParent, doc, "z", z); 
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		super.loadValuesFromXML(el, doc);
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("summary")) summary = booleanFromXML(nextElement);   
				else if(paramName.equals("definitions")) definitions = booleanFromXML(nextElement); 
				else if(paramName.equals("arrays")) arrays = booleanFromXML(nextElement); 
				else if(paramName.equals("imgEx")) imgEx = booleanFromXML(nextElement); 
				else if(paramName.equals("imgSel")) imgSel = booleanFromXML(nextElement); 
				else if(paramName.equals("imgSelNEx")) imgSelNEx = booleanFromXML(nextElement); 
				else if(paramName.equals("shapes")) shapes = booleanFromXML(nextElement); 
				else if(paramName.equals("shapesSelNEx")) shapesSelNEx = booleanFromXML(nextElement); 
				else if(paramName.equals("shapeData")) shapeData = booleanFromXML(nextElement); 
				else if(paramName.equals("y")) y = booleanFromXML(nextElement); 
				else if(paramName.equals("x")) x = booleanFromXML(nextElement); 
				else if(paramName.equals("z")) z = booleanFromXML(nextElement); 
			}
		}
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public boolean isSummary() {
		return summary;
	}
	public boolean isDefinitions() {
		return definitions;
	}
	public boolean isArrays() {
		return arrays;
	}
	public boolean isImgEx() {
		return imgEx;
	}
	public boolean isImgSel() {
		return imgSel;
	}
	public boolean isImgSelNEx() {
		return imgSelNEx;
	}
	public boolean isShapes() {
		return shapes;
	}
	public boolean isShapesSelNEx() {
		return shapesSelNEx;
	}

	public void setSummary(boolean summary) {
		this.summary = summary;
	}

	public void setDefinitions(boolean definitions) {
		this.definitions = definitions;
	}

	public void setArrays(boolean arrays) {
		this.arrays = arrays;
	}

	public void setImgEx(boolean imgEx) {
		this.imgEx = imgEx;
	}

	public void setImgSel(boolean imgSel) {
		this.imgSel = imgSel;
	}

	public void setImgSelNEx(boolean imgSelNEx) {
		this.imgSelNEx = imgSelNEx;
	}

	public void setShapes(boolean shapes) {
		this.shapes = shapes;
	}

	public void setShapesSelNEx(boolean shapesSelNEx) {
		this.shapesSelNEx = shapesSelNEx;
	}

	public boolean isShapeData() {
		return shapeData;
	}

	public boolean isX() {
		return x;
	}

	public boolean isY() {
		return y;
	}

	public boolean isZ() {
		return z;
	}

	public void setShapeData(boolean shapeData) {
		this.shapeData = shapeData;
	}

	public void setX(boolean x) {
		this.x = x;
	}

	public void setY(boolean y) {
		this.y = y;
	}

	public void setZ(boolean z) {
		this.z = z;
	}
}
