package net.rs.lamsi.massimager.Settings.image.selection;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.multiimager.Frames.dialogs.selectdata.SelectionTableRow;
import net.rs.lamsi.utils.useful.graphics2d.blending.BlendComposite;

import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class SettingsShapeSelection<T extends Shape> extends Settings {
	// do not change the version!
	private static final long serialVersionUID = 1L;
	
	// current shape is given by combobox TODO
	public enum SHAPE {
		RECT, ELIPSE, POLYGON, FREEHAND
	}
	// what to draw or do
	public enum SelectionMode {
		SELECT, EXCLUDE, INFO;
	}

	public static final BasicStroke stroke = new BasicStroke(1.5f);


	// selectionmode
	private SelectionMode mode;

	protected SelectionTableRow stats;
	protected SelectionTableRow statsRegardingExclusion;

	protected transient Image2D currentImg;

	// the Shape
	protected T shape;

	public SettingsShapeSelection(Image2D currentImage, SelectionMode mode, T shape) {
		super("SettingsShapeSelection", "/Settings/Selections/Shapes/", "setSelShape"); 
		this.shape = shape;
		this.mode = mode;
		this.currentImg = currentImage;
		stats = new SelectionTableRow(mode, shape);
		statsRegardingExclusion = new SelectionTableRow(mode, shape);
	} 
	
	public SettingsShapeSelection(SelectionMode mode, T shape) {
		super("SettingsShapeSelection", "/Settings/Selections/Shapes/", "setSelShape"); 
		this.shape = shape;
		this.mode = mode;
		stats = new SelectionTableRow(mode, shape);
		statsRegardingExclusion = new SelectionTableRow(mode, shape);
	}

	@Override
	public void resetAll() {  
	}

	public void setCurrentImage(Image2D img) {
		if(img!=null && !img.equals(currentImg)) {
			currentImg = img;
		}
	}
	

	/**
	 * load settings instance from xml
	 * @param parent element has information about the shape
	 * @return
	 */
	public static SettingsShapeSelection loadSettingsFromXML(Element elParent, Document doc) {
		SHAPE shape = SHAPE.valueOf(elParent.getTextContent());
		SettingsShapeSelection s = null;
		switch(shape) {
		case RECT:
			s = new SettingsRectSelection(SelectionMode.SELECT);
			break;
		case ELIPSE:
			s = new SettingsElipseSelection(SelectionMode.SELECT);
			break;
		case POLYGON:
			s = new SettingsPolygonSelection(SelectionMode.SELECT);
			break;
		}
		if(s!=null) {
			// load shape and mode
			s.loadValuesFromXML(elParent, doc);
		}
		return s;
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

	/**
	 * saves settings and calls abstract appendSettingsValuesToXML
	 * creates a new parent element for this settings class
	 * @param elParent
	 * @param doc
	 */
	@Override
	public void appendSettingsToXML(Element elParent, Document doc) { 
		Element elSett = doc.createElement(description);
		elParent.appendChild(elSett);
		appendSettingsValuesToXML(elSett, doc);
	}
	
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
				else if(paramName.equals("selectionMode")) setMode(SelectionMode.valueOf(nextElement.getTextContent()));
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
		stats.calculateStatistics(currentImg);
		statsRegardingExclusion.calculateStatistics(currentImg);
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
			c = Color.GREEN;
			break;
		case INFO:
			c = Color.gray;
			break;
		} 
		return new XYShapeAnnotation(this.getShape(), stroke, c) {
			@Override
			public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea,
					ValueAxis domainAxis, ValueAxis rangeAxis,
					int rendererIndex, PlotRenderingInfo info) {
				// set blendComposite
				g2.setComposite(BlendComposite.Difference);
				super.draw(g2, plot, dataArea, domainAxis, rangeAxis, rendererIndex, info);
				g2.setComposite(BlendComposite.Normal);
				// 
			}
		};
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
