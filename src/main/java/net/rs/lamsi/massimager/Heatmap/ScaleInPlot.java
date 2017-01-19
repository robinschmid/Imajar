package net.rs.lamsi.massimager.Heatmap;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import net.rs.lamsi.massimager.Image.Image2D;
import net.rs.lamsi.massimager.MyFreeChart.ChartLogics;
import net.rs.lamsi.massimager.MyFreeChart.themes.MyStandardChartTheme;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.Title;

public class ScaleInPlot extends Title {

	protected Graphics2D g;
	protected Image2D img;
	protected ChartPanel chartPanel;
	protected XYPlot plot;
	float value = 1;
	float factor = 1;
	String unit = ""; 
	//
	protected double width = 0;
	
	public ScaleInPlot(ChartPanel chartPanel, Image2D img, float value, float factor, String unit){
		this.img = img;
		this.chartPanel = chartPanel;
		this.plot = chartPanel.getChart().getXYPlot();
		this.value = value;
		this.factor = factor;
		this.unit = unit;
	}
	@Override
	public Object draw(Graphics2D g, Rectangle2D area, Object params) {
		this.g = g;
		
		MyStandardChartTheme theme = img.getSettTheme().getTheme();
		width = ChartLogics.calcWidthOnScreen(chartPanel, getDataScaleWidth(), chartPanel.getChart().getXYPlot().getDomainAxis(), chartPanel.getChart().getXYPlot().getDomainAxisEdge());
		//
		int tick = 8;
		double x = area.getX()-width; 
		double y = area.getY();
		int w = 2;
		// draw stuff for test 
		g.setColor((Color) theme.getAxisLinePaint());
		g.fill(new Rectangle2D.Double(x, y, width, w));  
		g.fill(new Rectangle2D.Double(x, y-tick/2, w, tick+w));  
		g.fill(new Rectangle2D.Double(x+width-w, y-tick/2, w, tick+w));  
		
		// draw label
		String label = value+" "+unit;
		g.setFont(theme.getSmallFont()); 
	    FontMetrics fm = g.getFontMetrics();
	    double sx = x+ (width - fm.stringWidth(label)) / 2;
	    double sy = y + tick/2 + (fm.getAscent());
		g.drawString(label, (float) (sx), (float)(sy));
		return null;
	}
	
	@Override
	public void draw(Graphics2D g2, Rectangle2D area) { 
        draw(g2, area, null);
	}
	
	
	public double getDataScaleWidth() {
		return value*factor;
	}
}
