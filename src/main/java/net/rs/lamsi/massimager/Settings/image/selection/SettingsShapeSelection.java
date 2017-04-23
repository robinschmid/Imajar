package net.rs.lamsi.massimager.Settings.image.selection;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIData2D;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.Image2DSelectDataAreaDialog.SelectionMode;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.SelectionTableRow;

import org.jfree.chart.annotations.XYShapeAnnotation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mysql.jdbc.interceptors.ServerStatusDiffInterceptor;

public abstract class SettingsShapeSelection<T extends Shape> extends Settings {
	// do not change the version!
	private static final long serialVersionUID = 1L;

	public static final BasicStroke stroke = new BasicStroke(1.5f);


	// selectionmode
	private SelectionMode mode;

	protected SelectionTableRow stats;
	protected SelectionTableRow statsRegardingExclusion;

	protected Image2D currentImg;

	// the Shape
	protected T shape;

	public SettingsShapeSelection(Image2D currentImage, SelectionMode mode, T shape) {
		super("SettingsShapeSelection", "/Settings/Selections/Shapes/", "setSelShape"); 
		this.shape = shape;
		this.mode = mode;
		this.currentImg = currentImage;
		stats = new SelectionTableRow(currentImage, mode, shape);
		statsRegardingExclusion = new SelectionTableRow(currentImage, mode, shape);
	} 

	@Override
	public void resetAll() {  
	}

	public void setCurrentImage(Image2D img) {
		if(img!=null && !img.equals(currentImg)) {
			currentImg = img;
			stats.setImg(currentImg);
			statsRegardingExclusion.setImg(currentImg);
		}
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
			}
		}
	}


	//##########################################################
	// shape logic
	public abstract void setBounds(float x, float y, float w, float h);

	/**
	 * sets the second anchor (end point)
	 * calculate size and position new
	 * @param x
	 * @param y
	 */
	public abstract void setSecondAnchor(float x, float y);
	/**
	 * sets the second anchor (end point)
	 * calculate size and position new
	 * @param x
	 * @param y
	 */
	public void setSecondAnchor(double x, double y) {
		setSecondAnchor((float)x, (float)y);
	}


	public void setSize(float w, float h) {
		Rectangle2D r = shape.getBounds2D();
		setBounds((float)r.getX(), (float)r.getY(), w, h);
	} 
	public void setPosition(float x, float y) {
		Rectangle2D r = shape.getBounds2D();
		setBounds(x, y, (float)r.getWidth(), (float)r.getHeight());
	}

	/**
	 * translate / shift rect by distance
	 * @param px
	 * @param py
	 */
	public void translate(float px, float py) {
		Rectangle2D r = shape.getBounds2D();
		setPosition((float)r.getX()+px, (float)r.getY()+py);
	}
	/**
	 * grow or shrink(if negative) 
	 * @param px
	 * @param py
	 */
	public void grow(float px, float py) {
		Rectangle2D r = shape.getBounds2D();
		setSize((float)r.getWidth()+px, (float)r.getHeight()+py);
	}


	//##########################################################
	// statistics
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param i
	 * @param isExcluded
	 * @return
	 */
	public boolean check(double x, double y, double i, boolean isExcluded) {
		return check((float)x, (float)y, i, isExcluded);
	}
	public boolean check(float x, float y, double i, boolean isExcluded) {
		if(contains(x, y)) {
			// add data point
			stats.addValue(i);
			if(!isExcluded)
				statsRegardingExclusion.addValue(i);
			return true;
		}
		else return false;
	}

	/**
	 * final stats calculation after all data points were added via check
	 */
	public void calculateStatistics() {
		stats.calculateStatistics();
		statsRegardingExclusion.calculateStatistics();
	}

	/**
	 * checks if the point is inside the shape
	 * @param p coordinates in the given processed data space
	 * @return
	 */
	public boolean contains(Point2D p) {
		return shape.contains(p);
	}

	/**
	 * checks if the point is inside the shape
	 * @param x coordinate in the given processed data space
	 * @param y coordinate in the given processed data space
	 * @return
	 */
	public boolean contains(float x, float y) {
		return shape.contains(x, y);
	}
	/**
	 * checks if the point is inside the shape
	 * @param x coordinate in the given processed data space
	 * @param y coordinate in the given processed data space
	 * @return
	 */
	public boolean contains(double x, double y) {
		return contains((float)x, (float)y);
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

	/**
	 * shape annotation with basic stroke
	 * @return
	 */
	public XYShapeAnnotation createXYShapeAnnotation() {
		Color c = null;
		switch(getMode()) {
		case EXCLUDE:
			c = Color.RED;
			break;
		case SELECT:
			c = Color.BLACK;
			break;
		case INFO:
			c = Color.gray;
			break;
		} 
		return new XYShapeAnnotation(this.getShape(), stroke, c);
	}

	/**
	 * default table row is in regards to exclusion for selections and the other for exclude and info
	 * @return
	 */
	public SelectionTableRow getDefaultTableRow() {
		return mode.equals(SelectionMode.SELECT)? statsRegardingExclusion : stats;
	}

	/**
	 * set the bounds by two mouse events
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 */
	public void setFirstAndSecondMouseEvent(float x0, float y0, float x1, float y1) {
		setBounds(Math.min(x0, x1), Math.min(y0, y1), Math.abs(x1-x0), Math.abs(y0-y1));
	}
}
