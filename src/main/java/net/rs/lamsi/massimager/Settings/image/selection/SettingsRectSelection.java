package net.rs.lamsi.massimager.Settings.image.selection;

import java.awt.geom.Rectangle2D;

import net.rs.lamsi.general.datamodel.image.Image2D;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SettingsRectSelection extends SettingsShapeSelection<Rectangle2D.Float> {
	// do not change the version!
	private static final long serialVersionUID = 1L;
	
	public SettingsRectSelection(Image2D currentImage, SelectionMode mode, float x, float y, float w, float h) {
		super(currentImage, mode, new Rectangle2D.Float(x, y, w, h));
	} 
	public SettingsRectSelection(SelectionMode mode) {
		super(mode, new Rectangle2D.Float(0, 0, 0, 0));
	} 

	@Override
	public void resetAll() {  
	}

	//##########################################################
	// xml input/output 
	/**
	 * load shape from xml
	 * @param nextElement
	 * @return
	 */
	protected Rectangle2D.Float loadShapeFromXML(Element nextElement) {
		float x = Float.valueOf(nextElement.getAttribute("x"));
		float y = Float.valueOf(nextElement.getAttribute("y"));
		float w = Float.valueOf(nextElement.getAttribute("w"));
		float h = Float.valueOf(nextElement.getAttribute("h"));
		setBounds(x, y, w, h);
		return shape;
	}

	/**
	 * save shape to xml
	 */
	@Override
	protected void saveShapeToXML(Element elParent, Document doc, Rectangle2D.Float shape) {
		elParent.setNodeValue(SHAPE.RECT.toString());
		toXML(elParent, doc, "shape", "", new String[]{"x","y","w","h"}, 
				new Object[]{shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight()});
	}
	
	public void setBounds(float x, float y, float w, float h) {
		shape.setRect(x, y, w, h);
	}
	
	public void setSize(float w, float h) {
		setBounds(shape.x, shape.y, w, h);
	} 
	public void setPosition(float x, float y) {
		setBounds(x, y, shape.width, shape.height);
	}

	/**
	 * translate / shift rect by distance
	 * @param px
	 * @param py
	 */
	public void translate(float px, float py) {
		setPosition(shape.x+px, shape.y+py);
	}
	/**
	 * grow or shrink(if negative) 
	 * @param px
	 * @param py
	 */
	public void grow(float px, float py) {
		setSize(shape.width+px, shape.height+py);
	}

	@Override
	public void setSecondAnchor(float x, float y) {
		setBounds(Math.min(x, shape.x), Math.min(y, shape.y), Math.abs(x-shape.x), Math.abs(y-shape.y));
	}
	
	
}
