package net.rs.lamsi.general.myfreechart.plots.image2d;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap;
import net.rs.lamsi.utils.useful.graphics2d.blending.BlendComposite;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;

public class ImageRenderer extends XYBlockRenderer {

	// 
	protected SettingsAlphaMap sett;
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
    	else if(sett!=null && sett.getAlpha()>0) {
    		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sett.getAlpha()));
	        super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
    		g2.setComposite(BlendComposite.Normal);
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
    
}
