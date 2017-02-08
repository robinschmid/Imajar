package net.rs.lamsi.massimager.MyFreeChart.themes;

import java.awt.Color;
import java.awt.Paint;

import net.rs.lamsi.massimager.Settings.Settings;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.title.PaintScaleLegend;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
 

public class MyStandardChartTheme extends StandardChartTheme {
	
	public static final String XML_DESC = "ChartTheme";
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


	//#########################################################################
	// xml import export
	public void appendThemeSettingsToXML(Element elParent, Document doc) {
		Element el = doc.createElement(XML_DESC);
		elParent.appendChild(el);

		Settings.toXML(el, doc, "axisLinePaint", axisLinePaint); 
		Settings.toXML(el, doc, "themeID", themeID); 
		Settings.toXML(el, doc, "showXGrid", showXGrid); 
		Settings.toXML(el, doc, "showYGrid",showYGrid); 
		Settings.toXML(el, doc, "showXAxis", showXAxis ); 
		Settings.toXML(el, doc, "showYAxis", showYAxis); 
		Settings.toXML(el, doc, "showScale", showScale); 
		Settings.toXML(el, doc, "isPaintScaleInPlot", isPaintScaleInPlot); 
		Settings.toXML(el, doc, "scaleUnit",scaleUnit); 
		Settings.toXML(el, doc, "scaleFactor", scaleFactor); 
		Settings.toXML(el, doc, "scaleValue", scaleValue); 
		Settings.toXML(el, doc, "scaleXPos", scaleXPos); 
		Settings.toXML(el, doc, "scaleYPos", scaleYPos);  
	}

	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("axisLinePaint")) axisLinePaint = Settings.colorFromXML(nextElement); 
				else if(paramName.equals("themeID"))themeID = Settings.intFromXML(nextElement);  
				else if(paramName.equals("showXGrid"))showXGrid = Settings.booleanFromXML(nextElement);  
				else if(paramName.equals("showYGrid"))showYGrid = Settings.booleanFromXML(nextElement);  
				else if(paramName.equals("showXAxis"))showXAxis = Settings.booleanFromXML(nextElement);  
				else if(paramName.equals("showYAxis"))showYAxis = Settings.booleanFromXML(nextElement);  
				else if(paramName.equals("showScale"))showScale = Settings.booleanFromXML(nextElement);  
				else if(paramName.equals("isPaintScaleInPlot"))isPaintScaleInPlot = Settings.booleanFromXML(nextElement);  
				else if(paramName.equals("scaleUnit"))scaleUnit = nextElement.getTextContent();  
				else if(paramName.equals("scaleFactor"))scaleFactor = Settings.floatFromXML(nextElement);  
				else if(paramName.equals("scaleValue"))scaleValue = Settings.floatFromXML(nextElement);  
				else if(paramName.equals("scaleXPos"))scaleXPos = Settings.floatFromXML(nextElement);  
				else if(paramName.equals("scaleYPos"))scaleYPos = Settings.floatFromXML(nextElement);  
			}
		}
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
