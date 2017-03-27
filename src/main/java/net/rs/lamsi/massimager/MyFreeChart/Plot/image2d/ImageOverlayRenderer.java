package net.rs.lamsi.massimager.MyFreeChart.Plot.image2d;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import net.rs.lamsi.utils.useful.graphics2d.blending.BlendComposite;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;

public class ImageOverlayRenderer extends ImageRenderer {
	
	protected double[] blockWidths, blockHeights;
	protected PaintScale scales[];
	
	
	public ImageOverlayRenderer(int size) {
		super();
		blockWidths = new double[size];
		blockHeights = new double[size];
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
    		// background is either an image or black
    		BlendComposite blend = BlendComposite.Add; 
    		
	        drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass, blend);
	    } 
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
    public void drawItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
            int series, int item, CrosshairState crosshairState, int pass, BlendComposite blend) {

        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double z = 0.0;
        if (dataset instanceof XYZDataset) {
            z = ((XYZDataset) dataset).getZValue(series, item);
        }

        Paint p = this.getPaintScale(series).getPaint(z);
        double xx0 = domainAxis.valueToJava2D(x, dataArea,
                plot.getDomainAxisEdge());
        double yy0 = rangeAxis.valueToJava2D(y, dataArea,
                plot.getRangeAxisEdge());
        double xx1 = domainAxis.valueToJava2D(x + this.getBlockWidth(series), dataArea, plot.getDomainAxisEdge());
        double yy1 = rangeAxis.valueToJava2D(y + this.getBlockHeight(series), dataArea, plot.getRangeAxisEdge());
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
        // debug
//        if(series==1)
//        	System.out.println("1");
//        if(series==0)
//        	System.out.println("0");
        g2.setPaint(p);
        g2.setComposite(blend);
        g2.fill(block);

        int datasetIndex = plot.indexOf(dataset);
        double transX = domainAxis.valueToJava2D(x, dataArea,
                plot.getDomainAxisEdge());
        double transY = rangeAxis.valueToJava2D(y, dataArea,
                plot.getRangeAxisEdge());        
        // TODO ERROR DATASET INDEX TWICE
        updateCrosshairValues(crosshairState, x, y, datasetIndex,
                datasetIndex, transX, transY, orientation);

        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addEntity(entities, block, dataset, series, item, transX, transY);
        }

    }

    public PaintScale getPaintScale(int i) {
    	if(scales==null)
    		return null;
    	return scales[i];
    }

	public PaintScale[] getPaintScales() {
		return scales;
	}

	public void setPaintScales(PaintScale[] scales) {
		this.scales = scales;
	}

	public void setBlockWidth(int i, double maxBlockWidth) {
		blockWidths[i] = maxBlockWidth;
	}
	public double getBlockWidth(int i) {
		return i<blockWidths.length && i>=0? blockWidths[i] : 0;
	}
	public void setBlockHeight(int i, double maxBlockH) {
		blockHeights[i] = maxBlockH;
	}
	public double getBlockHeight(int i) {
		return i<blockHeights.length && i>=0? blockHeights[i] : 0;
	}
}
