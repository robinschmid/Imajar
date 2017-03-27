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
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;

import com.Ostermiller.util.BadLineEndingException;

public class ImageRenderer extends XYBlockRenderer {

	// 
	protected boolean[] map = null;
	
	
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
	        super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
	    } 
    }

    /**
     * only draw item if true
     * @param item
     * @return
     */
    public boolean isMapTrue(int item) {
    	return map==null || (item<map.length && map[item]==true);
    }

	public boolean[] getMap() {
		return map;
	} 
	public void setMap(boolean[] map) {
		this.map = map;
		this.fireChangeEvent();
	} 
    
}
