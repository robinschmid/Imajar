package net.rs.lamsi.general.myfreechart.plots.image2d.contourplot;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.util.Args;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;

public class ContourImageRenderer extends AbstractXYItemRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PaintScale paintScale;

	private double blockWidth = 1, blockHeight = 1;
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


        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double z = 0.0;
        if (dataset instanceof XYZDataset) {
            z = ((XYZDataset) dataset).getZValue(series, item);
        }

        Paint p = this.paintScale.getPaint(z);
        double xx0 = domainAxis.valueToJava2D(x, dataArea,
                plot.getDomainAxisEdge());
        double yy0 = rangeAxis.valueToJava2D(y, dataArea,
                plot.getRangeAxisEdge());
        double xx1 = domainAxis.valueToJava2D(x + this.blockWidth, dataArea, plot.getDomainAxisEdge());
        double yy1 = rangeAxis.valueToJava2D(y + this.blockHeight, dataArea, plot.getRangeAxisEdge());
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
        
        g2.setPaint(p);
        g2.fill(block);
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(block);

        int datasetIndex = plot.indexOf(dataset);
        double transX = domainAxis.valueToJava2D(x, dataArea,
                plot.getDomainAxisEdge());
        double transY = rangeAxis.valueToJava2D(y, dataArea,
                plot.getRangeAxisEdge());        

        
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addEntity(entities, block, dataset, series, item, transX, transY);
        }
    }
    
//    protected void updateCrosshairValues(CrosshairState crosshairState,
//            double x, double y, int datasetIndex,
//            double transX, double transY, PlotOrientation orientation) {
//
//        ParamChecks.nullNotPermitted(orientation, "orientation");
//        if (crosshairState != null) {
//            // do we need to update the crosshair values?
//            if (this.plot.isDomainCrosshairLockedOnData()) {
//                if (this.plot.isRangeCrosshairLockedOnData()) {
//                    // both axes
//                    crosshairState.updateCrosshairPoint(x, y, datasetIndex,
//                            transX, transY, orientation);
//                }
//                else {
//                    // just the domain axis...
//                    crosshairState.updateCrosshairX(x, transX, datasetIndex);
//                }
//            }
//            else {
//                if (this.plot.isRangeCrosshairLockedOnData()) {
//                    // just the range axis...
//                    crosshairState.updateCrosshairY(y, transY, datasetIndex);
//                }
//            }
//        }
//
//    }
    
    

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
}
