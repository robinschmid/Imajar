package net.rs.lamsi.general.myfreechart.plots.image2d;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;

import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap;
import net.rs.lamsi.utils.useful.graphics2d.blending.BlendComposite;

public class ImageRenderer extends AbstractXYItemRenderer
implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {


	// 
	protected SettingsAlphaMap sett;
	protected boolean[] map = null;
	
	/**
	 * The block width (defaults to 1.0).
	 */
	private double blockWidth = 1.0;

	/**
	 * The block height (defaults to 1.0).
	 */
	private double blockHeight = 1.0;

	/**
	 * The anchor point used to align each block to its (x, y) location.  The
	 * default value is {@code RectangleAnchor.CENTER}.
	 */
	private RectangleAnchor blockAnchor = RectangleAnchor.CENTER;

	/** Temporary storage for the x-offset used to align the block anchor. */
	private double xOffset;

	/** Temporary storage for the y-offset used to align the block anchor. */
	private double yOffset;

	/** The paint scale. */
	private PaintScale paintScale;

	/**
	 * Creates a new {@code XYBlockRenderer} instance with default
	 * attributes.
	 */
	public ImageRenderer() {
		updateOffsets();
		this.paintScale = new LookupPaintScale();
	}

	/**
	 * Returns the block width, in data/axis units.
	 *
	 * @return The block width.
	 *
	 * @see #setBlockWidth(double)
	 */
	public double getBlockWidth() {
		return this.blockWidth;
	}

	/**
	 * Sets the width of the blocks used to represent each data item and
	 * sends a {@link RendererChangeEvent} to all registered listeners.
	 *
	 * @param width  the new width, in data/axis units (must be &gt; 0.0).
	 *
	 * @see #getBlockWidth()
	 */
	public void setBlockWidth(double width) {
		if (width <= 0.0) {
			throw new IllegalArgumentException(
					"The 'width' argument must be > 0.0");
		}
		this.blockWidth = width;
		updateOffsets();
		fireChangeEvent();
	}

	/**
	 * Returns the block height, in data/axis units.
	 *
	 * @return The block height.
	 *
	 * @see #setBlockHeight(double)
	 */
	public double getBlockHeight() {
		return this.blockHeight;
	}

	/**
	 * Sets the height of the blocks used to represent each data item and
	 * sends a {@link RendererChangeEvent} to all registered listeners.
	 *
	 * @param height  the new height, in data/axis units (must be &gt; 0.0).
	 *
	 * @see #getBlockHeight()
	 */
	public void setBlockHeight(double height) {
		if (height <= 0.0) {
			throw new IllegalArgumentException(
					"The 'height' argument must be > 0.0");
		}
		this.blockHeight = height;
		updateOffsets();
		fireChangeEvent();
	}

	/**
	 * Returns the anchor point used to align a block at its (x, y) location.
	 * The default values is {@link RectangleAnchor#CENTER}.
	 *
	 * @return The anchor point (never {@code null}).
	 *
	 * @see #setBlockAnchor(RectangleAnchor)
	 */
	public RectangleAnchor getBlockAnchor() {
		return this.blockAnchor;
	}

	/**
	 * Sets the anchor point used to align a block at its (x, y) location and
	 * sends a {@link RendererChangeEvent} to all registered listeners.
	 *
	 * @param anchor  the anchor.
	 *
	 * @see #getBlockAnchor()
	 */
	public void setBlockAnchor(RectangleAnchor anchor) {
		Args.nullNotPermitted(anchor, "anchor");
		if (this.blockAnchor.equals(anchor)) {
			return;  // no change
		}
		this.blockAnchor = anchor;
		updateOffsets();
		fireChangeEvent();
	}

	/**
	 * Returns the paint scale used by the renderer.
	 *
	 * @return The paint scale (never {@code null}).
	 *
	 * @see #setPaintScale(PaintScale)
	 * @since 1.0.4
	 */
	public PaintScale getPaintScale() {
		return this.paintScale;
	}

	/**
	 * Sets the paint scale used by the renderer and sends a
	 * {@link RendererChangeEvent} to all registered listeners.
	 *
	 * @param scale  the scale ({@code null} not permitted).
	 *
	 * @see #getPaintScale()
	 * @since 1.0.4
	 */
	public void setPaintScale(PaintScale scale) {
		Args.nullNotPermitted(scale, "scale");
		this.paintScale = scale;
		fireChangeEvent();
	}

	/**
	 * Updates the offsets to take into account the block width, height and
	 * anchor.
	 */
	private void updateOffsets() {
		if (this.blockAnchor.equals(RectangleAnchor.BOTTOM_LEFT)) {
			this.xOffset = 0.0;
			this.yOffset = 0.0;
		}
		else if (this.blockAnchor.equals(RectangleAnchor.BOTTOM)) {
			this.xOffset = -this.blockWidth / 2.0;
			this.yOffset = 0.0;
		}
		else if (this.blockAnchor.equals(RectangleAnchor.BOTTOM_RIGHT)) {
			this.xOffset = -this.blockWidth;
			this.yOffset = 0.0;
		}
		else if (this.blockAnchor.equals(RectangleAnchor.LEFT)) {
			this.xOffset = 0.0;
			this.yOffset = -this.blockHeight / 2.0;
		}
		else if (this.blockAnchor.equals(RectangleAnchor.CENTER)) {
			this.xOffset = -this.blockWidth / 2.0;
			this.yOffset = -this.blockHeight / 2.0;
		}
		else if (this.blockAnchor.equals(RectangleAnchor.RIGHT)) {
			this.xOffset = -this.blockWidth;
			this.yOffset = -this.blockHeight / 2.0;
		}
		else if (this.blockAnchor.equals(RectangleAnchor.TOP_LEFT)) {
			this.xOffset = 0.0;
			this.yOffset = -this.blockHeight;
		}
		else if (this.blockAnchor.equals(RectangleAnchor.TOP)) {
			this.xOffset = -this.blockWidth / 2.0;
			this.yOffset = -this.blockHeight;
		}
		else if (this.blockAnchor.equals(RectangleAnchor.TOP_RIGHT)) {
			this.xOffset = -this.blockWidth;
			this.yOffset = -this.blockHeight;
		}
	}

	/**
	 * Returns the lower and upper bounds (range) of the x-values in the
	 * specified dataset.
	 *
	 * @param dataset  the dataset ({@code null} permitted).
	 *
	 * @return The range ({@code null} if the dataset is {@code null}
	 *         or empty).
	 *
	 * @see #findRangeBounds(XYDataset)
	 */
	@Override
	public Range findDomainBounds(XYDataset dataset) {
		if (dataset == null) {
			return null;
		}
		Range r = DatasetUtils.findDomainBounds(dataset, false);
		if (r == null) {
			return null;
		}
		return new Range(r.getLowerBound() + this.xOffset,
				r.getUpperBound() + this.blockWidth + this.xOffset);
	}

	/**
	 * Returns the range of values the renderer requires to display all the
	 * items from the specified dataset.
	 *
	 * @param dataset  the dataset ({@code null} permitted).
	 *
	 * @return The range ({@code null} if the dataset is {@code null}
	 *         or empty).
	 *
	 * @see #findDomainBounds(XYDataset)
	 */
	@Override
	public Range findRangeBounds(XYDataset dataset) {
		if (dataset != null) {
			Range r = DatasetUtils.findRangeBounds(dataset, false);
			if (r == null) {
				return null;
			}
			else {
				return new Range(r.getLowerBound() + this.yOffset,
						r.getUpperBound() + this.blockHeight + this.yOffset);
			}
		}
		else {
			return null;
		}
	}

	/**
	 * Tests this {@code XYBlockRenderer} for equality with an arbitrary
	 * object.  This method returns {@code true} if and only if:
	 * <ul>
	 * <li>{@code obj} is an instance of {@code XYBlockRenderer} (not
	 *     {@code null});</li>
	 * <li>{@code obj} has the same field values as this
	 *     {@code XYBlockRenderer};</li>
	 * </ul>
	 *
	 * @param obj  the object ({@code null} permitted).
	 *
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof ImageRenderer)) {
			return false;
		}
		ImageRenderer that = (ImageRenderer) obj;
		if (this.blockHeight != that.blockHeight) {
			return false;
		}
		if (this.blockWidth != that.blockWidth) {
			return false;
		}
		if (!this.blockAnchor.equals(that.blockAnchor)) {
			return false;
		}
		if (!this.paintScale.equals(that.paintScale)) {
			return false;
		}
		return super.equals(obj);
	}

	/**
	 * Returns a clone of this renderer.
	 *
	 * @return A clone of this renderer.
	 *
	 * @throws CloneNotSupportedException if there is a problem creating the
	 *     clone.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		ImageRenderer clone = (ImageRenderer) super.clone();
		if (this.paintScale instanceof PublicCloneable) {
			PublicCloneable pc = (PublicCloneable) this.paintScale;
			clone.paintScale = (PaintScale) pc.clone();
		}
		return clone;
	}

	/**
     * Draws the block representing the specified item.
     *
     * @param g2  the graphics device.
     * @param state  the state.
     * @param dataArea  the data area.
     * @param info  the plot rendering info.
     * @param plot  the plot.
     * @param domainAxis  the x-axis.
     * @param rangeAxis  the y-axis.
     * @param dataset  the dataset.
     * @param series  the series index.
     * @param item  the item index.
     * @param crosshairState  the crosshair state.
     * @param pass  the pass index.
     */
    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
            int series, int item, CrosshairState crosshairState, int pass) {
    	// only if in map or if there is no map
    	if(isMapTrue(item)) {
    		drawBlockItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
	    } 
    	else if(sett!=null && sett.getAlpha()>0) {
    		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sett.getAlpha()));
    		drawBlockItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
    		g2.setComposite(BlendComposite.Normal);
    	}
    }
    
    protected void drawBlockItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
            int series, int item, CrosshairState crosshairState, int pass) {
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double z = 0.0;
        if (dataset instanceof XYZDataset) {
            z = ((XYZDataset) dataset).getZValue(series, item);
        }

        Paint p = this.getPaintScale(series).getPaint(z);
        double xx0 = domainAxis.valueToJava2D(x + this.xOffset, dataArea,
                plot.getDomainAxisEdge());
        double yy0 = rangeAxis.valueToJava2D(y + this.yOffset, dataArea,
                plot.getRangeAxisEdge());
        double xx1 = domainAxis.valueToJava2D(x + this.getBlockWidth(series)
                + this.xOffset, dataArea, plot.getDomainAxisEdge());
        double yy1 = rangeAxis.valueToJava2D(y + this.getBlockHeight(series)
                + this.yOffset, dataArea, plot.getRangeAxisEdge());
        Rectangle2D block;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation.equals(PlotOrientation.HORIZONTAL)) {
            block = new Rectangle2D.Double(Math.min(yy0, yy1),
                    Math.min(xx0, xx1), Math.abs(yy1 - yy0),
                    Math.abs(xx0 - xx1));
        }
        else {
            block = new Rectangle2D.Double(Math.min(xx0, xx1),
                    Math.min(yy0, yy1), Math.abs(xx1 - xx0),
                    Math.abs(yy1 - yy0));
        }
        
        // do only paint if inside rect
        if(dataArea.intersects(block)) {
	        g2.setPaint(p);
	        g2.fillRect((int)Math.round(block.getX()),(int)Math.round(block.getY()),
	        		(int)Math.ceil(block.getWidth())+1,(int)Math.ceil(block.getHeight())+1);
//	        g2.fill(block);
//	        g2.setStroke(new BasicStroke(1.0f));
//	        g2.draw(block);

	        if (isItemLabelVisible(series, item)) {
	            drawItemLabel(g2, orientation, dataset, series, item, 
	                    block.getCenterX(), block.getCenterY(), y < 0.0);
	        }

	        int datasetIndex = plot.indexOf(dataset);
	        double transX = domainAxis.valueToJava2D(x, dataArea,
	                plot.getDomainAxisEdge());
	        double transY = rangeAxis.valueToJava2D(y, dataArea,
	                plot.getRangeAxisEdge());        
	        updateCrosshairValues(crosshairState, x, y, datasetIndex,
	                transX, transY, orientation);

	        EntityCollection entities = state.getEntityCollection();
	        if (entities != null) {
	        	Rectangle2D intersect = block.createIntersection(dataArea);
	            addEntity(entities, intersect, dataset, series, item, 
	            		intersect.getCenterX(), intersect.getCenterY());
	        }
        }
    }

    
    /**
     * only draw item if true
     * @param item
     * @return
     */
    public boolean isMapTrue(int item) {
    	return (sett!=null && !sett.isActive()) || map==null || (item<map.length && map[item]==true);
    }

	public boolean[] getMap() {
		return map;
	} 
	public void setMap(SettingsAlphaMap sett) {
		this.sett = sett;
		this.map = sett.convertToLinearMap();
		if(map!=null)
			this.fireChangeEvent();
	}


	public void setMapLinear(boolean[] maplinear) {
		map = maplinear;
	} 

	public PaintScale getPaintScale(int i) {
		return paintScale;
	}

	public double getBlockWidth(int i) {
		return getBlockWidth();
	}
	public double getBlockHeight(int i) {
		return getBlockHeight();
	}
}
