package net.rs.lamsi.massimager.Settings.image.selection;

import java.awt.Shape;
import java.awt.geom.Point2D;

import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.Image2DSelectDataAreaDialog.SelectionMode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class SettingsShapeSelection<T extends Shape> extends Settings {
	// do not change the version!
	private static final long serialVersionUID = 1L;
	
	// selectionmode
	private SelectionMode mode;
	
	// the Shape
	protected T shape;
	

	public SettingsShapeSelection(SelectionMode mode, T shape) {
		super("SettingsShapeSelection", "/Settings/Selections/Shapes/", "setSelShape"); 
		this.shape = shape;
		this.mode = mode;
	} 

	@Override
	public void resetAll() {  
	}
	
	/**
	 * save shape to xml
	 */
	protected abstract void saveShapeToXML(Element elParent, Document doc, T shape);
	
	/**
	 * load shape from xml
	 * @param nextElement
	 * @return
	 */
	protected abstract T loadShapeFromXML(Element nextElement);
	

	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		saveShapeToXML(elParent, doc, shape);
		// toXML(elParent, doc, "shape", xrange.getLowerBound()); 
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("shape")) setShape(loadShapeFromXML(nextElement));
//				else if(paramName.equals("xrange.upper"))xu = doubleFromXML(nextElement);
			}
		}
	}
	
	/**
	 * checks if the point is inside the shape
	 * @param p
	 * @return
	 */
	public boolean contains(Point2D p) {
		return shape.contains(p);
	}

	public void setShape(T shape) {
		this.shape = shape;
	}
	public SelectionMode getMode() {
		return mode;
	}
	public void setMode(SelectionMode mode) {
		this.mode = mode;
	}
	public T getShape() {
		return shape;
	}

}
