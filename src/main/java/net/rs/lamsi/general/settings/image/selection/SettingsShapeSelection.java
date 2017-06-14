package net.rs.lamsi.general.settings.image.selection;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifier;
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
	
	/**
	 *  current shape is given by combobox
	 *
	 */
	public enum SHAPE {
		RECT, ELIPSE, POLYGON, FREEHAND
	}
	/**
	 *  what to draw or do
	 *
	 */
	public enum SelectionMode {
		SELECT("Sel"), EXCLUDE("Excl"), INFO("Info");
		
		private final String shortTitle;
		public String getShortTitle() {
			return shortTitle;
		}
		private SelectionMode(String shortTitle) {
			this.shortTitle = shortTitle;
		}
	}
	/**
	 * defines the task for this ROI
	 */
	public enum ROI {
		SAMPLE, QUANTIFIER
	}

	public static final BasicStroke stroke = new BasicStroke(1.5f);
	public static final BasicStroke strokeHighlight = new BasicStroke(4f);


	// selectionmode
	protected SelectionMode mode;
	protected ROI roi = ROI.SAMPLE;
	protected SelectionTableRow stats;
	protected SelectionTableRow statsRegardingExclusion;
	// order for quantifiers
	protected int orderNumber = 0;
	// concentration for quantifier / qualifier
	protected double concentration = 0; 

	protected transient Image2D currentImg;

	// highlight selection
	protected boolean isHighlighted = false;
	// the Shape
	protected T shape;

	public SettingsShapeSelection(Image2D currentImage, ROI roi, SelectionMode mode, T shape) {
		super("SettingsShapeSelection", "/Settings/Selections/Shapes/", "setSelShape"); 
		this.shape = shape;
		this.mode = mode;
		this.roi = roi;
		this.currentImg = currentImage;
		stats = new SelectionTableRow();
		statsRegardingExclusion = new SelectionTableRow();
	} 
	
	public SettingsShapeSelection(SelectionMode mode, T shape) {
		super("SettingsShapeSelection", "/Settings/Selections/Shapes/", "setSelShape"); 
		this.shape = shape;
		this.mode = mode;
		stats = new SelectionTableRow();
		statsRegardingExclusion = new SelectionTableRow();
	}
	
	//#############################################################################
	// TABLE MODEL 
	/**
	 * called for table
	 * @return
	 */
	public Object[] getRowData() {
		float y0 = getY0();
		float x0 =getX0();
		float y1 =getY1();
		float x1 = getX1();
		
		SelectionTableRow r = getDefaultTableRow();

		return new Object[]{orderNumber, mode.toString(), roi.toString(), concentration, x0,y0,x1,y1,r.getN(),r.getSum(),r.getMin(), r.getMax(), 
				r.getAvg(), r.getMedian(), r.getP99(), r.getSdev(), r.getHisto()};
	}

	/**
	 * called for data export 
	 * @return without histogram
	 */
	public Object[] getRowDataExport() {
		float y0 = getY0();
		float x0 =getX0();
		float y1 =getY1();
		float x1 = getX1();

		SelectionTableRow r = getDefaultTableRow();
		
		return new Object[]{orderNumber, mode.toString(),  roi.toString(), concentration, x0,y0,x1,y1,r.getN(),r.getSum(),r.getMin(), r.getMax(), 
				r.getAvg(), r.getMedian(), r.getP99(), r.getSdev()};
	}

	/**
	 * array for title line export
	 * without histo
	 */
	public static Object[] getTitleArrayExport() {
		return new Object[]{"Order", "Mode", "ROI","conc.", "x0", "y0", "x1", "y1", "n", "sum", "I min", "I max", "I avg", "I median", "I 99%","Stdev"};
	}

	public float getX0() {
		return (float)shape.getBounds2D().getMinX();
	} 
	public float getX1() {
		return (float)shape.getBounds2D().getMaxX();
	} 
	public float getY0() {
		return (float)shape.getBounds2D().getMinY();
	} 
	public float getY1() {
		return (float)shape.getBounds2D().getMaxY();
	}
	
	//#############################################################################
	// 

	@Override
	public void resetAll() {  
	}

	@Override
	public Class getSuperClass() {
		return SettingsShapeSelection.class; 
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
		SettingsShapeSelection s = (SettingsShapeSelection) Settings.createSettings(Settings.getRealClassFromXML(elParent));
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

	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		saveShapeToXML(elParent, doc, shape);
		toXML(elParent, doc, "selectionMode", mode);
		toXML(elParent, doc, "roi", roi);
		toXML(elParent, doc, "concentration", concentration);
		toXML(elParent, doc, "orderNumber", orderNumber);
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
				else if(paramName.equals("roi")) setRoi(ROI.valueOf(nextElement.getTextContent()));
				else if(paramName.equals("concentration")) concentration = doubleFromXML(nextElement);
				else if(paramName.equals("orderNumber")) orderNumber = intFromXML(nextElement);
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
	public boolean check(double x, double y, double i, float w, float h, boolean isExcluded) {
		return check((float)x, (float)y, i, w, h, isExcluded);
	}
	public boolean check(float x, float y, double i, float w, float h, boolean isExcluded) {
		if(contains(x+w/2.f, y+h/2.f)) {
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
		return new XYShapeAnnotation(this.getShape(), isHighlighted()? strokeHighlight : stroke, c) {
			@Override
			public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea,
					ValueAxis domainAxis, ValueAxis rangeAxis,
					int rendererIndex, PlotRenderingInfo info) {
				// set blendComposite
//				g2.setComposite(BlendComposite.Difference);
				super.draw(g2, plot, dataArea, domainAxis, rangeAxis, rendererIndex, info);
//				g2.setComposite(BlendComposite.Normal);
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

	public ROI getRoi() {
		return roi;
	}

	public void setRoi(ROI roi) {
		this.roi = roi;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public double getConcentration() {
		return concentration;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public void setConcentration(double concentration) {
		this.concentration = concentration;
	}

	public void setHighlighted(boolean b) {
		isHighlighted = b;
	}
	public boolean isHighlighted() {
		return isHighlighted;
	}
}
