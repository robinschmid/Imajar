package net.rs.lamsi.general.myfreechart;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.sf.mzmine.util.Range;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleEdge;

public class ChartLogics {


    private static JPanel p = new JPanel(new BorderLayout());
	
	/**
	 * Translates mouse coordinates to chart coordinates (xy-axis)
	 * @param myChart
	 * @param mouseX
	 * @param mouseY
	 * @return Range as chart coordinates
	 */
    public static Point2D mouseXYToPlotXY(ChartPanel myChart, int mouseX, int mouseY) {  
    	Point2D p = myChart.translateScreenToJava2D( new Point(mouseX, mouseY));
         
        XYPlot plot = (XYPlot) myChart.getChart().getPlot();
        ChartRenderingInfo info = myChart.getChartRenderingInfo();
        Rectangle2D dataArea = info.getPlotInfo().getDataArea(); 

        ValueAxis domainAxis = plot.getDomainAxis();
        ValueAxis rangeAxis = plot.getRangeAxis();
        if(domainAxis!=null && rangeAxis!=null) {
	        RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
	        RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
	        double chartX = domainAxis.java2DToValue(p.getX(), dataArea,
	                domainAxisEdge);
	        double chartY = rangeAxis.java2DToValue(p.getY(), dataArea,
	                rangeAxisEdge); 
		 
			return new Point2D.Double(chartX, chartY);
        }
        else return null;
	}
    
    /**
	 * Translates screen values to plot values
	 * @param myChart
	 * @return width in data space for x and y
	 */
    public static Point2D screenValueToPlotValue(ChartPanel myChart, int val) {  
    	Point2D p = mouseXYToPlotXY(myChart, 0, 0);
    	Point2D p2 = mouseXYToPlotXY(myChart, val, val);
    	// inverted y
		return new Point2D.Double(p2.getX()-p.getX(), p.getY()-p2.getY());
	}
    
    
    /**
     * 
     * @param myChart
     * @param dataWidth width of data
     * @param axis for width calculation 
     * @return
     */
    public static double calcWidthOnScreen(ChartPanel myChart, double dataWidth, ValueAxis axis, RectangleEdge axisEdge) { 
        XYPlot plot = (XYPlot) myChart.getChart().getPlot();
        ChartRenderingInfo info = myChart.getChartRenderingInfo();
        Rectangle2D dataArea = info.getPlotInfo().getDataArea();
         
        double width2D = axis.lengthToJava2D(dataWidth, dataArea, axisEdge);   
        
		return width2D;
	}
    

    /**
     * caculates the size of a chart for a given fixed plot width
     * @param chart
     * @param width
     * @return
     */
	public static Dimension calcSizeForPlotWidth(ChartPanel myChart, double plotWidth) {
    	return calcSizeForPlotWidth(myChart, plotWidth, 4);
	}
    
	 /**
     * caculates the size of a chart for a given fixed plot width
     * @param chart
     * @param width
     * @return
     */
	public static Dimension calcSizeForPlotWidth(ChartPanel myChart, double plotWidth, int iterations) {
        // ranges
    	XYPlot plot = (XYPlot) myChart.getChart().getPlot();
        ValueAxis domainAxis = plot.getDomainAxis();
        org.jfree.data.Range x = domainAxis.getRange();
        ValueAxis rangeAxis = plot.getRangeAxis();
        org.jfree.data.Range y = rangeAxis.getRange();
        
        // plot height is fixed
        double plotHeight = plotWidth / x.getLength()*y.getLength();
	    return calcSizeForPlotSize(myChart, plotWidth, plotHeight, iterations);
	}

	/**
     * caculates the size of a chart for a given fixed plot width and height
     * @param chart
     * @param width
     * @return
     */
	public static Dimension calcSizeForPlotSize(ChartPanel myChart, double plotWidth, double plotHeight) {
		return calcSizeForPlotSize(myChart, plotWidth, plotHeight, 4);
	}
	/**
     * caculates the size of a chart for a given fixed plot width and height
     * @param chart
     * @param width
     * @return
     */
	public static Dimension calcSizeForPlotSize(ChartPanel myChart, double plotWidth, double plotHeight, int iterations) {
        // ranges
    	XYPlot plot = (XYPlot) myChart.getChart().getPlot();
    	
        // estimate plotwidth / height
        double estimatedChartWidth = plotWidth+200;
        double estimatedChartHeight = plotHeight+200;
    	
        // paint and get closer
    	try {
    	for(int i=0; i<iterations; i++) {
    		// paint on ghost panel with estimated height (if copy panel==true)
        	myChart.setSize((int)estimatedChartWidth, (int)estimatedChartHeight);
        	myChart.paintImmediately(myChart.getBounds());

        	// rendering info
            ChartRenderingInfo info = myChart.getChartRenderingInfo();
            Rectangle2D dataArea = info.getPlotInfo().getDataArea();
            Rectangle2D chartArea = info.getChartArea(); 
            
//            // calc title space: will be added later to the right plot size
//            double titleWidth = chartArea.getWidth()-dataArea.getWidth();
//            double titleHeight = chartArea.getHeight()-dataArea.getHeight(); 
            
            // calc width and height
            estimatedChartWidth = estimatedChartWidth/dataArea.getWidth()*plotWidth;
            estimatedChartHeight = estimatedChartHeight/dataArea.getHeight()*plotHeight;
            ImageEditorWindow.log("Estimated (i="+i+") size: "+(int)estimatedChartWidth+"x"+(int)estimatedChartHeight, LOG.DEBUG);
    	}
    	}catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	
    	return new Dimension((int)estimatedChartWidth, (int)estimatedChartHeight);
	}
	
    /**
     * calls this method twice (2 iterations) with an estimated chartHeight of 3*chartWidth
     * @param myChart
     * @param copyToNewPanel 
     * @param dataWidth width of data
     * @param axis for width calculation 
     * @return
     */
    public static double calcHeightToWidth(ChartPanel myChart, double chartWidth, boolean copyToNewPanel) {  
    	return calcHeightToWidth(myChart, chartWidth, (int)chartWidth*3, 4, copyToNewPanel);
	}
    /**
     * calculates the correct height with multiple iterations
     * @param myChart
     * @param copyToNewPanel 
     * @param dataWidth width of data
     * @param axis for width calculation 
     * @return
     */
    public static double calcHeightToWidth(ChartPanel myChart, double chartWidth, double estimatedHeight, int iterations, boolean copyToNewPanel) {  
    	//if(myChart.getChartRenderingInfo()==null || myChart.getChartRenderingInfo().getChartArea()==null || myChart.getChartRenderingInfo().getChartArea().getWidth()==0)
    	// result
    	double height = estimatedHeight;
    	
    	// paint on a ghost panel
    	JPanel parent = (JPanel)myChart.getParent();
    	JPanel p = copyToNewPanel? new JPanel() : parent;
    	if(copyToNewPanel)
    		p.add(myChart, BorderLayout.CENTER);
    	try {
    	for(int i=0; i<iterations; i++) {
    		// paint on ghost panel with estimated height (if copy panel==true)
        	myChart.setSize((int)chartWidth, (int)estimatedHeight);
        	myChart.paintImmediately(myChart.getBounds());

        	XYPlot plot = (XYPlot) myChart.getChart().getPlot();
            ChartRenderingInfo info = myChart.getChartRenderingInfo();
            Rectangle2D dataArea = info.getPlotInfo().getDataArea();
            Rectangle2D chartArea = info.getChartArea(); 
            
            // calc title space: will be added later to the right plot size
            double titleWidth = chartArea.getWidth()-dataArea.getWidth();
            double titleHeight = chartArea.getHeight()-dataArea.getHeight(); 
            
            // calc right plot size with axis dim.
            // real plot width is given by factor;  
            double realPW = chartWidth-titleWidth;
            
            // ranges
            ValueAxis domainAxis = plot.getDomainAxis();
            org.jfree.data.Range x = domainAxis.getRange();
            ValueAxis rangeAxis = plot.getRangeAxis();
            org.jfree.data.Range y = rangeAxis.getRange();
            
            // real plot height can be calculated by 
            double realPH = realPW / x.getLength()*y.getLength();
            
            // the real height
            height = realPH + titleHeight; 
            
            // for next iteration
            estimatedHeight = height;
    	}
    	}catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	
    	if(copyToNewPanel) {
	    	// reset to frame
	    	p.removeAll();
	    	parent.add(myChart);
    	}
    		
		return height;
	}
    
    /**
     * 
     * @param myChart 
     * @return
     */
    public static double calcWidthToHeight(ChartPanel myChart, double chartHeight) {  
    	// paint on a ghost panel
    	JPanel parent = (JPanel)myChart.getParent();
    	JPanel p = new JPanel();
    	p.removeAll();
    	p.add(myChart, BorderLayout.CENTER);
    	p.setBounds(myChart.getBounds());
    	myChart.paintImmediately(myChart.getBounds());
    	p.removeAll();
    	parent.add(myChart);
    	
        XYPlot plot = (XYPlot) myChart.getChart().getPlot();
        ChartRenderingInfo info = myChart.getChartRenderingInfo();
        Rectangle2D dataArea = info.getPlotInfo().getDataArea();
        Rectangle2D chartArea = info.getChartArea(); 
        
        
        // calc title space: will be added later to the right plot size
        double titleWidth = chartArea.getWidth()-dataArea.getWidth();
        double titleHeight = chartArea.getHeight()-dataArea.getHeight(); 
        
        // calc right plot size with axis dim.
        // real plot width is given by factor;  
        double realPH = chartHeight-titleHeight;
        
        // ranges
        ValueAxis domainAxis = plot.getDomainAxis();
        org.jfree.data.Range x = domainAxis.getRange();
        ValueAxis rangeAxis = plot.getRangeAxis();
        org.jfree.data.Range y = rangeAxis.getRange();
        
        // real plot height can be calculated by 
        double realPW = realPH/y.getLength() * x.getLength();
        
        double width = realPW + titleWidth; 
        
		return width;
	}
    
    /**
     * returns dimensions for limiting factor width or height
     * @param myChart 
     * @return
     */
    public static Dimension calcMaxSize(ChartPanel myChart, double chartWidth, double chartHeight) {  
    	// paint on a ghost panel
    	JPanel parent = (JPanel)myChart.getParent();
    	JPanel p = new JPanel();
    	p.removeAll();
    	p.add(myChart, BorderLayout.CENTER);
    	p.setBounds(myChart.getBounds());
    	myChart.paintImmediately(myChart.getBounds());
    	p.removeAll();
    	parent.add(myChart);
    	
        XYPlot plot = (XYPlot) myChart.getChart().getPlot();
        ChartRenderingInfo info = myChart.getChartRenderingInfo();
        Rectangle2D dataArea = info.getPlotInfo().getDataArea();
        Rectangle2D chartArea = info.getChartArea(); 
        
        
        // calc title space: will be added later to the right plot size
        double titleWidth = chartArea.getWidth()-dataArea.getWidth();
        double titleHeight = chartArea.getHeight()-dataArea.getHeight(); 
        
        // calculatig width for max height
        
        // calc right plot size with axis dim.
        // real plot width is given by factor;  
        double realPH = chartHeight-titleHeight;
        
        // ranges
        ValueAxis domainAxis = plot.getDomainAxis();
        org.jfree.data.Range x = domainAxis.getRange();
        ValueAxis rangeAxis = plot.getRangeAxis();
        org.jfree.data.Range y = rangeAxis.getRange();
        
        // real plot height can be calculated by 
        double realPW = realPH/y.getLength() * x.getLength();
        
        double width = realPW + titleWidth; 
        // if width is higher than given chartWidth then calc height for chartWidth
        if(width>chartWidth) {
        	// calc right plot size with axis dim.
            // real plot width is given by factor;  
            realPW = chartWidth-titleWidth; 
            
            // real plot height can be calculated by 
            realPH = realPW / x.getLength()*y.getLength();
            
            double height = realPH + titleHeight; 
        	// Return size
        	return new Dimension((int)chartWidth, (int)height);
        }
        else {
        	// Return size
        	return new Dimension((int)width, (int)chartHeight);
        }
	}
    
    
    
    /**
     * 
     * @param myChart
     * @return Range the domainAxis zoom (X-axis)
     */
    public static Range getZoomDomainAxis(ChartPanel myChart) {  
        XYPlot plot = (XYPlot) myChart.getChart().getPlot(); 
        ValueAxis domainAxis = plot.getDomainAxis(); 
        
		return new Range(domainAxis.getLowerBound(), domainAxis.getUpperBound());
    }
    /**
     * Zoom into a chart panel
     * @param myChart
     * @param zoom
     * @param autoRangeY if true the range (Y) axis auto bounds will be restored
     */
    public static void setZoomDomainAxis(ChartPanel myChart, Range zoom, boolean autoRangeY) {  
        XYPlot plot = (XYPlot) myChart.getChart().getPlot(); 
        ValueAxis domainAxis = plot.getDomainAxis(); 
        
        domainAxis.setLowerBound(zoom.getMin());
        domainAxis.setUpperBound(zoom.getMax());
        
        if(autoRangeY) { 
        	NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
            rangeAxis.setAutoRangeIncludesZero(false); 
            myChart.restoreAutoRangeBounds();
        } 
    }
    
    /**
     * Move a chart by a percentage x-offset
     * if xoffset is <0 the shift will be negativ (xoffset>0 results in a positive shift)
     * 
     * @param myChart
     * @param xoffset in percent
     * @param autoRangeY if true the range (Y) axis auto bounds will be restored
     */
    public static void offsetDomainAxis(ChartPanel myChart, double xoffset, boolean autoRangeY) { 
        XYPlot plot = (XYPlot) myChart.getChart().getPlot(); 
        ValueAxis domainAxis = plot.getDomainAxis();  
        // apply offset on x 
        double distance = (domainAxis.getUpperBound()-domainAxis.getLowerBound())*xoffset;
        
    	Range range = new Range(domainAxis.getLowerBound()+distance, domainAxis.getUpperBound()+distance);
    	
    	if(range.getMin()<0){
    		double negative = range.getMin(); 
    		range = new Range(0, range.getMax()-negative);
    	}
    	
        setZoomDomainAxis(myChart, range, autoRangeY);
    }
    
    /**
     * Apply an absolute offset to domain (x) axis and move it
     * @param myChart
     * @param xoffset
     * @param autoRangeY
     */
    public static void offsetDomainAxisAbsolute(ChartPanel myChart, double xoffset, boolean autoRangeY) { 
        XYPlot plot = (XYPlot) myChart.getChart().getPlot(); 
        ValueAxis domainAxis = plot.getDomainAxis();  
        // apply offset on x 
        
    	Range range = new Range(domainAxis.getLowerBound()+xoffset, domainAxis.getUpperBound()+xoffset);
    	
    	if(range.getMin()<0){
    		double negative = range.getMin(); 
    		range = new Range(0, range.getMax()-negative);
    	}
    	
        setZoomDomainAxis(myChart, range, autoRangeY);
    }

    /**
     * Zoom in (negative yzoom) or zoom out of range axis.
     * @param myChart
     * @param yzoom percentage zoom factor
     * @param holdLowerBound if true only the upper bound will be zoomed
     */
	public static void zoomRangeAxis(ChartPanel myChart, double yzoom, boolean holdLowerBound) {
		XYPlot plot = (XYPlot) myChart.getChart().getPlot(); 
        ValueAxis rangeAxis = plot.getRangeAxis(); 
        
        double lower = rangeAxis.getLowerBound();
        double upper = rangeAxis.getUpperBound();
        double dist = upper - lower;
        
        if(holdLowerBound) {
        	upper += dist*yzoom;
        }
        else {
        	lower -= dist*yzoom/2;
        	upper += dist*yzoom/2;
        }
        
        rangeAxis.setLowerBound(lower);
        rangeAxis.setUpperBound(upper); 
	}

}
