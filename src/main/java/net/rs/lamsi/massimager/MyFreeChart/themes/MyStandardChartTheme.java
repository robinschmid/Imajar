package net.rs.lamsi.massimager.MyFreeChart.themes;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.title.PaintScaleLegend;
 

public class MyStandardChartTheme extends StandardChartTheme {
	
	protected Paint axisLinePaint = Color.black;
	protected int themeID;

	protected boolean showXGrid = false, showYGrid = false;
	protected boolean showXAxis= true, showYAxis = true;
	// scale for x / y width
	protected boolean showScale = false;
	protected String scaleUnit = "";
	protected float scaleFactor = 1;
	protected float scaleValue = 1;
	protected float scaleXPos = 0.9f, scaleYPos = 0.1f;
	// paintscale
	protected boolean isPaintScaleInPlot= false;
	

	public MyStandardChartTheme(int themeID, String name) {
		super(name);
		this.themeID = themeID;
	}

	@Override
	public void apply(JFreeChart chart) {
		// TODO Auto-generated method stub
		super.apply(chart);
		//
		chart.getXYPlot().setDomainGridlinesVisible(showXGrid);
		chart.getXYPlot().setRangeGridlinesVisible(showYGrid);
		// all axes
		for(int i=0; i<chart.getXYPlot().getDomainAxisCount(); i++) {
			NumberAxis a = (NumberAxis) chart.getXYPlot().getDomainAxis(i);
			a.setTickMarkPaint(axisLinePaint);
			a.setAxisLinePaint(axisLinePaint);
			// visible?
			a.setVisible(showXAxis);
		}
		for(int i=0; i<chart.getXYPlot().getRangeAxisCount(); i++) {
			NumberAxis a = (NumberAxis) chart.getXYPlot().getRangeAxis(i);
			a.setTickMarkPaint(axisLinePaint);
			a.setAxisLinePaint(axisLinePaint);
			// visible?
			a.setVisible(showYAxis);
		}
		// apply bg 
        chart.setBackgroundPaint(this.getChartBackgroundPaint());
        chart.getPlot().setBackgroundPaint(this.getPlotBackgroundPaint());
        
        for(int i=0; i<chart.getSubtitleCount(); i++) 
        	if(PaintScaleLegend.class.isAssignableFrom(chart.getSubtitle(i).getClass())) 
        		((PaintScaleLegend)chart.getSubtitle(i)).setBackgroundPaint(this.getPlotBackgroundPaint());
        if(chart.getLegend()!=null)
        	chart.getLegend().setBackgroundPaint(this.getPlotBackgroundPaint());
	}

	
	
	// GETTERS AND SETTERS
	public Paint getAxisLinePaint() {
		return axisLinePaint;
	}

	public void setAxisLinePaint(Paint axisLinePaint) {
		this.axisLinePaint = axisLinePaint;
	}
	public int getID() { 
		return themeID;
	}
	public void setID(int themeID) {
		this.themeID = themeID;
	} 
	public void setShowXGrid(boolean showXGrid) {
		this.showXGrid = showXGrid;
	}
	public void setShowYGrid(boolean showYGrid) {
		this.showYGrid = showYGrid;
	} 
	public boolean isShowXGrid() {
		return showXGrid;
	} 
	public boolean isShowYGrid() {
		return showYGrid;
	}

	public boolean isShowXAxis() {
		return showXAxis;
	}

	public void setShowXAxis(boolean showXAxis) {
		this.showXAxis = showXAxis;
	}

	public boolean isShowYAxis() {
		return showYAxis;
	}

	public void setShowYAxis(boolean showYAxis) {
		this.showYAxis = showYAxis;
	} 
	public boolean isShowScale() {
		return showScale;
	}

	public void setShowScale(boolean showScale) {
		this.showScale = showScale;
	}

	public String getScaleUnit() {
		return scaleUnit;
	}

	public void setScaleUnit(String scaleUnit) {
		this.scaleUnit = scaleUnit;
	}

	public float getScaleFactor() {
		return scaleFactor;
	}

	public void setScaleFactor(float scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	public float getScaleValue() {
		return scaleValue;
	}

	public void setScaleValue(float scaleValue) {
		this.scaleValue = scaleValue;
	}

	public boolean isPaintScaleInPlot() {
		return isPaintScaleInPlot;
	}

	public void setPaintScaleInPlot(boolean isPaintScaleInPlot) {
		this.isPaintScaleInPlot = isPaintScaleInPlot;
	}

	public float getScaleXPos() {
		return scaleXPos;
	}

	public void setScaleXPos(float scaleXPos) {
		this.scaleXPos = scaleXPos;
	}

	public float getScaleYPos() {
		return scaleYPos;
	}

	public void setScaleYPos(float scaleYPos) {
		this.scaleYPos = scaleYPos;
	}
}
