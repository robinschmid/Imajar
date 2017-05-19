package net.rs.lamsi.general.heatmap;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.myfreechart.ChartLogics;
import net.rs.lamsi.general.myfreechart.themes.MyStandardChartTheme;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DBlankSubtraction;
import net.rs.lamsi.general.settings.image.visualisation.SettingsThemes;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.Title;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;

public class ScaleInPlot extends Title {

	protected SettingsThemes theme;
	protected XYTitleAnnotation annotation;
	protected ChartPanel chartPanel;
	protected XYPlot plot;
	protected float value = 1;
	protected float factor = 1;
	protected float xp,yp;
	protected String unit = ""; 
	//
	protected double width = 0;
	
	public ScaleInPlot(ChartPanel chartPanel, SettingsThemes theme){
		this.chartPanel = chartPanel;
		this.theme = theme;
		this.plot = chartPanel.getChart().getXYPlot();
		this.value = theme.getTheme().getScaleValue();
		this.factor = theme.getTheme().getScaleFactor();
		this.unit = theme.getTheme().getScaleUnit();
		this.xp = theme.getTheme().getScaleXPos();
		this.yp = theme.getTheme().getScaleYPos();
		annotation = new XYTitleAnnotation(xp,yp, this,RectangleAnchor.BOTTOM_RIGHT); 
	}

	

	@Override
	public Object draw(Graphics2D g, Rectangle2D area, Object params) {
		if(isVisible()) {
			MyStandardChartTheme theme = this.theme.getTheme();
			ValueAxis xaxis =  chartPanel.getChart().getXYPlot().getDomainAxis();
			RectangleEdge edge = chartPanel.getChart().getXYPlot().getDomainAxisEdge();
			ChartRenderingInfo info = chartPanel.getChartRenderingInfo();
	        Rectangle2D dataArea = info.getPlotInfo().getDataArea();
	        
			width = ChartLogics.calcWidthOnScreen(chartPanel, getDataScaleWidth(), xaxis, edge);
			//
			double x = dataArea.getX()+(dataArea.getWidth())*xp -width/2;
			if(x+width>dataArea.getMaxX())
				x = dataArea.getMaxX()-width;
			if(x<dataArea.getMinX())
				x = dataArea.getMinX();
			
			int tick = 8;
			//double x = area.getX()-width; 
			double y = area.getY();
			int w = 2;
			// draw stuff for test 
		    g.setColor(theme.getScaleFontColor());
			g.fill(new Rectangle2D.Double(x, y, width, w));  
			g.fill(new Rectangle2D.Double(x, y-tick/2, w, tick+w));  
			g.fill(new Rectangle2D.Double(x+width-w, y-tick/2, w, tick+w));  
			
			// draw label
			String label = getValue()+" "+getUnit();
			g.setFont(theme.getFontScaleInPlot()); 
		    FontMetrics fm = g.getFontMetrics();
		    double sx = x+ (width - fm.stringWidth(label)) / 2;
		    double sy = y + tick/2 + (fm.getAscent());
		    g.setColor(theme.getScaleFontColor());
			g.drawString(label, (float) (sx), (float)(sy));
		}
		return null;
	}
	
	@Override
	public void draw(Graphics2D g2, Rectangle2D area) { 
        draw(g2, area, null);
	}
	
	public double getDataScaleWidth() {
		return value*factor;
	}

	public XYPlot getPlot() {
		return plot;
	}

	public void setPlot(XYPlot plot) {
		this.plot = plot;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public float getFactor() {
		return factor;
	}

	public void setFactor(float factor) {
		this.factor = factor;
	}

	public float getX() {
		return xp;
	}

	public float getY() {
		return yp;
	}

	public void setPosition(float x, float y) {
		this.xp = x;
		this.yp = y;
		if(annotation!=null)
			plot.removeAnnotation(annotation);
		annotation = new XYTitleAnnotation(x,y, this,RectangleAnchor.BOTTOM_RIGHT); 
		plot.addAnnotation(annotation);
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public XYTitleAnnotation getAnnotation() {
		return annotation;
	}

	public void setAnnotation(XYTitleAnnotation annotation) {
		this.annotation = annotation;
	}

	public ChartPanel getChartPanel() {
		return chartPanel;
	}
	public void setChartPanel(ChartPanel chartPanel) {
		this.chartPanel = chartPanel;
	}
}
