package net.rs.lamsi.general.myfreechart.themes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import net.rs.lamsi.general.myfreechart.themes.ChartThemeFactory.THEME;
import net.rs.lamsi.general.settings.Settings;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.title.PaintScaleLegend;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
 

public class MyStandardChartTheme extends StandardChartTheme {
	
// master font
	protected Font masterFont;
	protected Color masterFontColor;
	
	// ShortTitle
	protected Font fontShortTitle;
	protected Color cShortTitle, cBGShortTitle;
	
	// scientific intensities
	protected boolean useScientificIntensities = true;
	protected int significantDigits = 2;
	protected NumberFormat intensitiesNumberFormat;
	
	// paintscale title
	protected String paintScaleTitle = "I";
	protected boolean usePaintScaleTitle = true;
	
	// Chart appearance 
	protected boolean isAntiAliased = true;
	// orientation : 0 - 2 (90° CW)
	protected int chartOrientation;
	
	protected boolean isShowTitle = false;
	
	
	
	public static final String XML_DESC = "ChartTheme";
	protected Paint axisLinePaint = Color.black;
	protected THEME themeID;

	protected boolean showXGrid = false, showYGrid = false;
	protected boolean showXAxis= true, showYAxis = true;
	// scale for x / y width
	protected Font fontScaleInPlot = new Font("Arial", Font.PLAIN, 11);
	protected Color scaleFontColor = Color.black;
	protected boolean showScale = false;
	protected String scaleUnit = "";
	protected float scaleFactor = 1;
	protected float scaleValue = 1;
	protected float scaleXPos = 0.9f, scaleYPos = 0.1f;
	// paintscale
	protected boolean isPaintScaleInPlot= false;
	

	public MyStandardChartTheme(THEME themeID, String name) {
		super(name);
		this.themeID = themeID;

		// in theme
		setAntiAliased(false);
		setNoBackground(false);
		// general
		
		// Short title in plot
		fontShortTitle = new Font("Arial", Font.BOLD, 14);
		cShortTitle = Color.WHITE;
		cBGShortTitle = new Color(60,120,155, 120);
		
		isAntiAliased = true;
		chartOrientation = 0;
		
		// significant intensities
		useScientificIntensities = true;
		significantDigits = 2;
		intensitiesNumberFormat = new DecimalFormat("0.0E0");
		intensitiesNumberFormat.setMaximumFractionDigits(significantDigits);
		intensitiesNumberFormat.setMinimumFractionDigits(significantDigits);
		
		// paintscale title
		paintScaleTitle = "I";
		usePaintScaleTitle = true;
		
		masterFont = new Font("Arial", Font.PLAIN, 11);
		masterFontColor = Color.black;
	}
	
	public void setAll(boolean antiAlias, boolean showTitle, boolean noBG, Color cBG, boolean showXGrid, boolean showYGrid, boolean showXAxis, boolean showYAxis, 
			boolean showScale, String scaleUnit, float scaleFactor, float scaleValue, boolean isPaintScaleInPlot, float scaleXPos, float scaleYPos,
			boolean useScientificIntensities, int significantDigits, String paintScaleTitle, boolean usePaintScaleTitle, 
			Font fMaster, Color cMaster, Font fAxesT, Color cAxesT, Font fAxesL, Color cAxesL, Font fTitle, Color cTitle, Font fScale, Color cScale) {
		this.setAntiAliased(antiAlias);
		this.setShowTitle(showTitle);
		this.setNoBackground(noBG);
		this.setShowXGrid(showXGrid);
		this.setShowYGrid(showYGrid);
		this.setShowXAxis(showXAxis);
		this.setShowYAxis(showYAxis);
		// scale
		this.setShowScale(showScale);
		this.setScaleUnit(scaleUnit);
		this.setScaleFactor(scaleFactor);
		this.setScaleValue(scaleValue);
		this.setPaintScaleInPlot(isPaintScaleInPlot);
		//
		this.setScaleXPos(scaleXPos);
		this.setScaleYPos(scaleYPos);
		
		this.setFontScaleInPlot(fScale);
		this.setScaleFontColor(cScale);
		
		this.setExtraLargeFont(fTitle);
		this.setLargeFont(fAxesT);
		this.setRegularFont(fAxesL);
		this.setAxisLabelPaint(cAxesT);
		this.setTickLabelPaint(cAxesL);
		this.setTitlePaint(cTitle);
		
		this.setChartBackgroundPaint(cBG);
		this.setPlotBackgroundPaint(cBG);
		this.setLegendBackgroundPaint(cBG);
		
		masterFont = fMaster;
		masterFontColor = cMaster;
		
		// significant intensities
		this.useScientificIntensities = useScientificIntensities;
		this.significantDigits = significantDigits;
		intensitiesNumberFormat = new DecimalFormat(useScientificIntensities? "0.0E0" : "#.0");
		int digits = useScientificIntensities? significantDigits-1 : significantDigits;
		intensitiesNumberFormat.setMaximumFractionDigits(digits);
		intensitiesNumberFormat.setMinimumFractionDigits(digits);
		
		// paintscale title
		this.paintScaleTitle = paintScaleTitle;
		this.usePaintScaleTitle = usePaintScaleTitle;
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
        chart.getPlot().setBackgroundPaint(this.getChartBackgroundPaint());
        
        for(int i=0; i<chart.getSubtitleCount(); i++) 
        	if(PaintScaleLegend.class.isAssignableFrom(chart.getSubtitle(i).getClass())) 
        		((PaintScaleLegend)chart.getSubtitle(i)).setBackgroundPaint(this.getPlotBackgroundPaint());
        if(chart.getLegend()!=null)
        	chart.getLegend().setBackgroundPaint(this.getChartBackgroundPaint());
        
		//
		chart.setAntiAlias(isAntiAliased());
		chart.getTitle().setVisible(isShowTitle()); 
        chart.getPlot().setBackgroundAlpha(isNoBackground()?0:1);
	}


	public void setShortTitle(Color c, Color bg, Font font) {
		cShortTitle = c;
		cBGShortTitle = bg;
		fontShortTitle = font;
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
		Settings.toXML(el, doc, "fontScaleInPlot", fontScaleInPlot); 
		Settings.toXML(el, doc, "scaleFontColor", scaleFontColor);  
		Settings.toXML(el, doc, "isAntiAliased", isAntiAliased); 
		Settings.toXML(el, doc, "showTitle", isShowTitle); 
		Settings.toXML(el, doc, "noBackground", isNoBackground()); 
		
		Settings.toXML(el, doc, "fontShortTitle", fontShortTitle); 
		Settings.toXML(el, doc, "cShortTitle", cShortTitle); 
		Settings.toXML(el, doc, "cBGShortTitle", cBGShortTitle); 
		
		Settings.toXML(el, doc, "significantDigits", significantDigits); 
		Settings.toXML(el, doc, "useScientificIntensities", useScientificIntensities); 
		
		Settings.toXML(el, doc, "paintScaleTitle", paintScaleTitle); 
		Settings.toXML(el, doc, "usePaintScaleTitle", usePaintScaleTitle); 
		Settings.toXML(el, doc, "cBackground", getChartBackgroundPaint());
		Settings.toXML(el, doc, "cPlotBackground", getPlotBackgroundPaint());
	}

	public void loadValuesFromXML(Element el, Document doc) {
		boolean hasNoBG = false;
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("axisLinePaint")) axisLinePaint = Settings.colorFromXML(nextElement); 
				else if(paramName.equals("themeID"))themeID = THEME.valueOf(nextElement.getTextContent());  
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
				else if(paramName.equals("fontScaleInPlot"))fontScaleInPlot = Settings.fontFromXML(nextElement);  
				else if(paramName.equals("scaleFontColor"))scaleFontColor = Settings.colorFromXML(nextElement);  
				else  if(paramName.equals("isAntiAliased")) isAntiAliased = Settings.booleanFromXML(nextElement); 
					else if(paramName.equals("showTitle"))isShowTitle = Settings.booleanFromXML(nextElement);  
					else if(paramName.equals("noBackground")){
						hasNoBG = true;
						setNoBackground(Settings.booleanFromXML(nextElement));  
					}
					else if(paramName.equals("fontShortTitle"))fontShortTitle = Settings.fontFromXML(nextElement);  
					else if(paramName.equals("cShortTitle")) cShortTitle = Settings.colorFromXML(nextElement);  
					else if(paramName.equals("cBGShortTitle")) cBGShortTitle = Settings.colorFromXML(nextElement);  
					else if(paramName.equals("significantDigits")) significantDigits = Settings.intFromXML(nextElement);  
					else if(paramName.equals("useScientificIntensities")) useScientificIntensities = Settings.booleanFromXML(nextElement);
					else if(paramName.equals("paintScaleTitle")) paintScaleTitle = (nextElement.getTextContent());  
					else if(paramName.equals("usePaintScaleTitle")) usePaintScaleTitle = Settings.booleanFromXML(nextElement);  
					else if(paramName.equals("cBackground") && !hasNoBG) {
						Color c = Settings.colorFromXML(nextElement);
						setChartBackgroundPaint(c);  
						setLegendBackgroundPaint(c);
					}
					else if(paramName.equals("cPlotBackground")) {
						Color c = Settings.colorFromXML(nextElement);
						setPlotBackgroundPaint(c);  
					}
			}
		}

		// 
		if(paintScaleTitle.equals("null"))
			paintScaleTitle = null;
		// create numberformats
		intensitiesNumberFormat = new DecimalFormat(useScientificIntensities? "0.0E0" : "#.0");
		int digits = significantDigits - (useScientificIntensities? 1 : 0);
		intensitiesNumberFormat.setMaximumFractionDigits(digits);
		intensitiesNumberFormat.setMinimumFractionDigits(digits);
	}

	public boolean isNoBackground() { 
		return ((Color)this.getPlotBackgroundPaint()).getAlpha() == 0;
	}
	public void setNoBackground(boolean state) { 
		Color c = ((Color)this.getPlotBackgroundPaint());
		Color cchart = ((Color)this.getChartBackgroundPaint());
		this.setPlotBackgroundPaint(new Color(c.getRed(), c.getGreen(), c.getBlue(), state? 0 : 255));
		this.setChartBackgroundPaint(new Color(cchart.getRed(), cchart.getGreen(), cchart.getBlue(), state? 0 : 255));
		this.setLegendBackgroundPaint(new Color(cchart.getRed(), cchart.getGreen(), cchart.getBlue(), state? 0 : 255));
	}
	
	// GETTERS AND SETTERS
	public Paint getAxisLinePaint() {
		return axisLinePaint;
	}
	public boolean isShowTitle() {
		return isShowTitle;
	}

	public boolean isAntiAliased() {
		return isAntiAliased;
	}

	public void setAntiAliased(boolean isAntiAliased) {
		this.isAntiAliased = isAntiAliased;
	}

	public int getChartOrientation() {
		return chartOrientation;
	}

	public void setChartOrientation(int chartOrientation) {
		this.chartOrientation = chartOrientation;
	}
	public Font getFontShortTitle() {
		return fontShortTitle;
	}
	public Color getcShortTitle() {
		return cShortTitle;
	}
	public void setFontShortTitle(Font fontShortTitle) {
		this.fontShortTitle = fontShortTitle;
	}
	public void setcShortTitle(Color cShortTitle) {
		this.cShortTitle = cShortTitle;
	}
	public Color getcBGShortTitle() {
		return cBGShortTitle;
	}
	public void setcBGShortTitle(Color cBGShortTitle) {
		this.cBGShortTitle = cBGShortTitle;
	}
	public boolean isUseScientificIntensities() {
		return useScientificIntensities;
	}
	public int getSignificantDigits() {
		return significantDigits;
	}
	public void setUseScientificIntensities(boolean useScientificIntensities) {
		this.useScientificIntensities = useScientificIntensities;
	}
	public void setSignificantDigits(int significantDigits) {
		this.significantDigits = significantDigits;
	}
	public NumberFormat getIntensitiesNumberFormat() {
		return intensitiesNumberFormat;
	}
	public String getPaintScaleTitle() {
		return paintScaleTitle;
	}
	public boolean isUsePaintScaleTitle() {
		return usePaintScaleTitle;
	}
	public void setPaintScaleTitle(String paintScaleTitle) {
		this.paintScaleTitle = paintScaleTitle;
	}
	public void setUsePaintScaleTitle(boolean usePaintScaleTitle) {
		this.usePaintScaleTitle = usePaintScaleTitle;
	}

	public void setShowTitle(boolean showTitle) {
		isShowTitle = showTitle;
	}

	public void setAxisLinePaint(Paint axisLinePaint) {
		this.axisLinePaint = axisLinePaint;
	}
	public THEME getID() { 
		return themeID;
	}
	public void setID(THEME themeID) {
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

	public Font getMasterFont() {
		return masterFont;
	}

	public Color getMasterFontColor() {
		return masterFontColor;
	}

	public void setMasterFont(Font masterFont) {
		this.masterFont = masterFont;
	}

	public void setMasterFontColor(Color masterFontColor) {
		this.masterFontColor = masterFontColor;
	}
}
