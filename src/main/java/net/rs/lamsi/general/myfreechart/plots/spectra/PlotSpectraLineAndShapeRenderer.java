package net.rs.lamsi.general.myfreechart.plots.spectra;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.text.TextUtilities;

public class PlotSpectraLineAndShapeRenderer extends XYLineAndShapeRenderer {


	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	// data points shape
	private static final Shape dataPointsShape = new Ellipse2D.Double(-2, -2, 5, 5);

	/**
	 * This Costructor adds the rendere to the charts xyplot
	 * There is nothing more to do
	 */
	public PlotSpectraLineAndShapeRenderer(ChartPanel chart, Color color) {
		// add self to chart
		chart.getChart().getXYPlot().setRenderer(this);
		// Label generator
		PlotSpectraLabelGenerator labelGenerator = new PlotSpectraLabelGenerator(chart);
		this.setBaseItemLabelGenerator(labelGenerator); 
		this.setBaseItemLabelsVisible(true);
		
		// Set painting color
		setBasePaint(color);
		setBaseFillPaint(color);
		setUseFillPaint(true);
		
		// Set shape properties
		setBaseShape(dataPointsShape);
		setBaseShapesFilled(true);
		setBaseShapesVisible(false);
		setDrawOutlines(false);
				
		// Set the tooltip generator
		PlotSpectraToolTipGenerator tooltipGenerator = new PlotSpectraToolTipGenerator();
		setBaseToolTipGenerator(tooltipGenerator);
		
	}

	public void drawItem(Graphics2D g2, XYItemRendererState state,
			Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
			ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
			int series, int item, CrosshairState crosshairState, int pass) {
 

		super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis,
				dataset, series, item, crosshairState, pass); 
	}

	/**
	 * This method returns null, because we don't want to change the colors
	 * dynamically.
	 */
	public DrawingSupplier getDrawingSupplier() {
		return null;
	}
	
	/**
     * Draws an item label.
     *
     * @param g2  the graphics device.
     * @param orientation  the orientation.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param x  the x coordinate (in Java2D space).
     * @param y  the y coordinate (in Java2D space).
     * @param negative  indicates a negative value (which affects the item
     *                  label position).
     */
	@Override
	protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation,
            XYDataset dataset, int series, int item, double x, double y,
            boolean negative) {

        XYItemLabelGenerator generator = getItemLabelGenerator(series, item);
        if (generator != null) {
            Font labelFont = getItemLabelFont(series, item);
            Paint paint = getItemLabelPaint(series, item);
            g2.setFont(labelFont);
            g2.setPaint(paint);
            String label = generator.generateLabel(dataset, series, item); 
            
            if(label!=null) {
	
	            // get the label position..
	            ItemLabelPosition position;
	            if (!negative) {
	                position = getPositiveItemLabelPosition(series, item);
	            }
	            else {
	                position = getNegativeItemLabelPosition(series, item);
	            }
	
	            // work out the label anchor point...
	            Point2D anchorPoint = calculateLabelAnchorPoint(
	                    position.getItemLabelAnchor(), x, y, orientation);
	            
	            // split by \n
	            String symbol = "\n";
	            String[] splitted = label.split(symbol);
	            
	            if(splitted.length>1) {
	            	// draw more than one row
	            	for(int i=0; i<splitted.length; i++) {
	            		int offset = -13* (splitted.length-i-1);
	            		TextUtilities.drawRotatedString(splitted[i], g2,
	                            (float) anchorPoint.getX(), (float) anchorPoint.getY()+offset,
	                            position.getTextAnchor(), position.getAngle(),
	                            position.getRotationAnchor());
	            	}
	            }
	            else {
	            	// one row 
	                TextUtilities.drawRotatedString(label, g2,
	                        (float) anchorPoint.getX(), (float) anchorPoint.getY(),
	                        position.getTextAnchor(), position.getAngle(),
	                        position.getRotationAnchor());
	            }
            }
        }

    }
}
