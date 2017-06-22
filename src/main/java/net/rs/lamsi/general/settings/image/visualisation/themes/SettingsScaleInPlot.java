package net.rs.lamsi.general.settings.image.visualisation.themes;

import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.axis.AxisLabelLocation;
import org.jfree.chart.axis.NumberAxis;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.myfreechart.Plot.image2d.annot.ScaleInPlot;
import net.rs.lamsi.general.myfreechart.themes.ChartThemeFactory;
import net.rs.lamsi.general.myfreechart.themes.MyStandardChartTheme;
import net.rs.lamsi.general.myfreechart.themes.ChartThemeFactory.THEME;
import net.rs.lamsi.general.settings.Settings;

public class SettingsScaleInPlot extends Settings {


	// scale for x / y width
	protected Font fontScaleInPlot = new Font("Arial", Font.PLAIN, 11);
	protected Color scaleFontColor = Color.black;
	protected boolean showScale = false;
	protected String scaleUnit = "";
	protected float scaleFactor = 1;
	protected float scaleValue = 1;
	protected float scaleXPos = 0.9f, scaleYPos = 0.1f;
	


	public SettingsScaleInPlot() {
		super("SettingsScaleInPlot", "/Settings/Visualization/", "setScaleInPlot"); 
		resetAll();
	} 
	
	public void setAll(boolean showScale, String scaleUnit, float scaleFactor, float scaleValue, float scaleXPos,
			float scaleYPos, Font fScale, Color cScale) {
		this.setShowScale(showScale);
		this.setScaleUnit(scaleUnit);
		this.setScaleFactor(scaleFactor);
		this.setScaleValue(scaleValue);
		this.setScaleXPos(scaleXPos);
		this.setScaleYPos(scaleYPos);
		this.setFontScaleInPlot(fScale);
		this.setScaleFontColor(cScale);
	}

	@Override
	public void resetAll() {
		setShowScale(false);
		setScaleUnit("µm");
		setScaleFactor(1);
		setScaleValue(500);
		setScaleXPos(0.9f);
		setScaleYPos(0.15f);
		setFontScaleInPlot(new Font("Arial", Font.PLAIN, 11));
		setScaleFontColor(Color.black);
	}
	

	@Override
	public void applyToHeatMap(Heatmap heat) {
		super.applyToHeatMap(heat);
		// 
		ScaleInPlot s = heat.getScaleInPlot();
		
		s.setFactor(this.getScaleFactor());
		s.setUnit(this.getScaleUnit());
		s.setValue(this.getScaleValue());
		s.setVisible(this.isShowScale());
		s.setPosition(this.getScaleXPos(),this.getScaleYPos());
	}
	

	//#########################################################################
	// xml import export
	@Override
	public void appendSettingsValuesToXML(Element el, Document doc) {
		Settings.toXML(el, doc, "showScale", showScale); 
 		Settings.toXML(el, doc, "scaleUnit",scaleUnit); 
		Settings.toXML(el, doc, "scaleFactor", scaleFactor); 
		Settings.toXML(el, doc, "scaleValue", scaleValue); 
		Settings.toXML(el, doc, "scaleXPos", scaleXPos); 
		Settings.toXML(el, doc, "scaleYPos", scaleYPos); 
		Settings.toXML(el, doc, "fontScaleInPlot", fontScaleInPlot); 
		Settings.toXML(el, doc, "scaleFontColor", scaleFontColor);  
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		boolean hasNoBG = false;
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("showScale"))showScale = Settings.booleanFromXML(nextElement);  
				else if(paramName.equals("scaleFactor"))scaleFactor = Settings.floatFromXML(nextElement);  
				else if(paramName.equals("scaleValue"))scaleValue = Settings.floatFromXML(nextElement);  
				else if(paramName.equals("scaleXPos"))scaleXPos = Settings.floatFromXML(nextElement);  
				else if(paramName.equals("scaleYPos"))scaleYPos = Settings.floatFromXML(nextElement);  
				else if(paramName.equals("fontScaleInPlot"))fontScaleInPlot = Settings.fontFromXML(nextElement);  
				else if(paramName.equals("scaleFontColor"))scaleFontColor = Settings.colorFromXML(nextElement);  
			}
		}
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

	public Font getFontScaleInPlot() {
		return fontScaleInPlot;
	}

	public Color getScaleFontColor() {
		return scaleFontColor;
	}

	public void setFontScaleInPlot(Font fontScaleInPlot) {
		this.fontScaleInPlot = fontScaleInPlot;
	}

	public void setScaleFontColor(Color scaleFontColor) {
		this.scaleFontColor = scaleFontColor;
	}

}
