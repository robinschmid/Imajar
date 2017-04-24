package net.rs.lamsi.massimager.Settings.image.selection;

import java.awt.geom.Ellipse2D;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Settings.image.selection.SettingsShapeSelection.SHAPE;
import net.rs.lamsi.massimager.Settings.image.selection.SettingsShapeSelection.SelectionMode;

import org.apache.batik.ext.awt.geom.Polygon2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SettingsPolygonSelection extends SettingsShapeSelection<Polygon2D> {
	// do not change the version!
	private static final long serialVersionUID = 1L;

	public SettingsPolygonSelection(Image2D currentImage, SelectionMode mode) {
		super(currentImage, mode, new Polygon2D());
	} 
	public SettingsPolygonSelection(Image2D currentImage, SelectionMode mode, float x, float y) {
		super(currentImage, mode, new Polygon2D());
		shape.addPoint(x, y);
	} 
	public SettingsPolygonSelection(SelectionMode mode) {
		super(mode, new Polygon2D());
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
	protected Polygon2D loadShapeFromXML(Element nextElement) {
		shape.reset();
		int i=0;
		while(true) {
			String sx = nextElement.getAttribute("x"+i);
			if(sx.length()==0)
				break;
			else {
				// add point
				float x = Float.valueOf(sx);
				float y = Float.valueOf(nextElement.getAttribute("y"+i));
				shape.addPoint(x, y);
				i++;
			}
		}
		return shape;
	}

	/**
	 * save shape to xml
	 */
	@Override
	protected void saveShapeToXML(Element elParent, Document doc, Polygon2D shape) {
		elParent.setNodeValue(SHAPE.POLYGON.toString());
		
		String[] att = new String[shape.npoints*2];
		Object[] val = new Object[shape.npoints*2];
		
		for(int i=0; i<shape.npoints*2; i+=2) {
			att[i] = "x"+i/2;
			att[i+1] = "y"+i/2;

			val[i] = shape.xpoints[i/2];
			val[i+1] = shape.ypoints[i/2];
		}
		
		toXML(elParent, doc, "shape", "", att, val);
	}
	
	public void addPoint(float x, float y) {
		shape.addPoint(x, y);
	}
	public void clear() {
		shape.reset();
	}
	
	public void setBounds(float x, float y, float w, float h) {
	}
	
	public void setSize(float w, float h) {
	} 
	public void setPosition(float x, float y) {
	}

	/**
	 * translate / shift rect by distance
	 * @param px
	 * @param py
	 */
	public void translate(float px, float py) {
		Polygon2D poly = new Polygon2D();
		for(int i=0; i<shape.npoints; i++) {
			poly.addPoint(shape.xpoints[i]+px, shape.ypoints[i]+py);
		}
		shape = poly;
	}
	/**
	 * grow or shrink(if negative) 
	 * @param px
	 * @param py
	 */
	public void grow(float px, float py) {
		setSize((float)shape.getBounds2D().getWidth()+px, (float)shape.getBounds2D().getHeight()+py);
	}

	@Override
	public void setSecondAnchor(float x, float y) {
	}

	public void setFirstAndSecondMouseEvent(float x0, float y0, float x1, float y1) {
		addPoint(x1, y1);
	}
}
