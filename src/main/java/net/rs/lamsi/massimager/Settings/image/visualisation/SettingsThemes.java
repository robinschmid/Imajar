package net.rs.lamsi.massimager.Settings.image.visualisation;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.text.NumberFormat;
import java.util.Vector;

import org.jfree.chart.JFreeChart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.Heatmap.ScaleInPlot;
import net.rs.lamsi.massimager.MyFreeChart.themes.ChartThemeFactory;
import net.rs.lamsi.massimager.MyFreeChart.themes.MyStandardChartTheme;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale.ValueMode;

public class SettingsThemes extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	 
	protected MyStandardChartTheme theme; 
	
	// lists
	protected Vector<Font> listFont = new Vector<Font>();
	protected Vector<Color> listFontColor = new Vector<Color>();
	// Styling things
	// General settings (overwriting other settings!)
	// General font only defines the familie etc - 
	protected boolean useGeneralFont = true, useGeneralFontSize = true, useGeneralFontColor = true, useGeneralBGColor = true;
	protected Font fontGeneral;
	protected Color cGeneralBG, cGeneralFontColor; 
	
	// Tilte
	protected String title = "";
	protected Color cTitle;
	protected Font fontTitle;
	protected boolean showTitle = false;
	
	// Axes styling
	protected Font fontAxesTitle, fontAxesLabels;
	protected Color cAxes, cAxesFont;
	// Domain Axis - x
	protected String xTitle;
	protected NumberFormat xNumberFormat;
	protected boolean showXTickLabels = true, showXTickMarks = true;
	// Tick increment will be selected automatic?
	protected boolean xAutoSelectTickIncrement = true;
	protected double xTickIncrement = 10;
	
	// Range Axis - y
	protected String yTitle;
	protected NumberFormat yNumberFormat;
	protected boolean showYTickLabels = true, showYTickMarks = true;
	// Tick increment will be selected automatic?
	protected boolean yAutoSelectTickIncrement = true;
	protected double yTickIncrement = 10;
	
	// Labelgenerator (Labels inside plotdata)
	protected Font fontLabelGen;
	protected boolean showLabelGens = true;
	
	// Chart appearance 
	protected boolean isAntiAliased = true;
	// orientation : 0 - 2 (90° CW)
	protected int chartOrientation;
	protected Color cChartBG, cPlotBG, cPlotOutline;
	// Outline stroke?
	 

	// background iamges
	protected File bgImagePath = null;
	protected boolean usesBGImage = false;
	
	
	public SettingsThemes() {
		super("SettingsThemes", "/Settings/Visualization/", "setPStyle"); 
		theme = ChartThemeFactory.getStandardTheme();
		resetAll();
	} 
	/**
	 * example
	 * @param theme example: ChartThemeFactory.THEME_KARST
	 */
	public SettingsThemes(int themeid) {
		super("SettingsThemes", "/Settings/Visualization/", "setPStyle"); 
		resetAll();
		this.theme = ChartThemeFactory.createChartTheme(themeid);
	} 
	

	public void setAll(boolean antiAlias, boolean showTitle, boolean noBG, boolean showXGrid, boolean showYGrid, boolean showXAxis, boolean showYAxis, 
			boolean showScale, String scaleUnit, float scaleFactor, float scaleValue, boolean isPaintScaleInPlot, float scaleXPos, float scaleYPos) {
		this.setAntiAliased(antiAlias);
		this.setShowTitle(showTitle);
		this.setNoBackground(noBG);
		theme.setShowXGrid(showXGrid);
		theme.setShowYGrid(showYGrid);
		theme.setShowXAxis(showXAxis);
		theme.setShowYAxis(showYAxis);
		// scale
		theme.setShowScale(showScale);
		theme.setScaleUnit(scaleUnit);
		theme.setScaleFactor(scaleFactor);
		theme.setScaleValue(scaleValue);
		theme.setPaintScaleInPlot(isPaintScaleInPlot);
		//
		theme.setScaleXPos(scaleXPos);
		theme.setScaleYPos(scaleYPos);
	}

	@Override
	public void resetAll() { 
		theme = ChartThemeFactory.getStandardTheme();
		// in theme
		setAntiAliased(false);
		setNoBackground(false);
		// general
		useGeneralFont = true; 
		useGeneralBGColor = true;
		useGeneralFontColor = true;
		useGeneralFontSize = true;
		fontGeneral = new Font("Arial", Font.PLAIN, 11);
		
		cGeneralBG = new Color(255, 255, 255, 0); 
		cGeneralFontColor = Color.BLACK;
		
		// Title
		showTitle = false;
		title = "";
		cTitle = Color.BLACK; 
		fontTitle = new Font("Arial", Font.PLAIN, 11);
		
		// axes
		fontAxesTitle = new Font("Arial", Font.PLAIN, 11); 
		fontAxesLabels = new Font("Arial", Font.PLAIN, 11);
		cAxes = Color.BLACK;
		cAxesFont = Color.BLACK;
		// Domain Axis - x
		xTitle = "x"; 
		showXTickLabels = true; 
		showXTickMarks = true;
		// Tick increment will be selected automatic?
		xAutoSelectTickIncrement = true;
		xTickIncrement = 10;
		
		// Range Axis - y
		yTitle = "y"; 
		showYTickLabels = true; 
		showYTickMarks = true;
		// Tick increment will be selected automatic?
		yAutoSelectTickIncrement = true;
		yTickIncrement = 10;
		
		// Labelgenerator (Labels inside plotdata) 
		showLabelGens = true;
		isAntiAliased = true;
		fontLabelGen = new Font("Arial", Font.PLAIN, 11);
		chartOrientation = 0;
		cChartBG = Color.BLACK; 
		cPlotBG = Color.BLACK;
		cPlotOutline = Color.BLACK;
		
		// List of all FOnts
		listFont.add(fontGeneral);
		listFont.add(fontTitle);
		listFont.add(fontAxesTitle);
		listFont.add(fontAxesLabels);
		listFont.add(fontLabelGen); 
		// list of all  font Colors
		listFontColor.add(cGeneralFontColor);
		listFontColor.add(cAxesFont);
		listFontColor.add(cAxes);
		listFontColor.add(cTitle);

		//new
		bgImagePath = null;
		usesBGImage = false;
	}
	
	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "isAntiAliased", isAntiAliased); 
		toXML(elParent, doc, "showTitle", showTitle); 
		toXML(elParent, doc, "noBackground", isNoBackground()); 

		if(bgImagePath!=null)
			toXML(elParent, doc, "bgImagePath", bgImagePath.getAbsolutePath()); 
		toXML(elParent, doc, "usesBGImage", usesBGImage); 
		
		theme.appendThemeSettingsToXML(elParent, doc);
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("isAntiAliased")) isAntiAliased = booleanFromXML(nextElement); 
				else if(paramName.equals("showTitle"))showTitle = booleanFromXML(nextElement);  
				else if(paramName.equals("noBackground"))setNoBackground(booleanFromXML(nextElement));  
				else if(paramName.equals(MyStandardChartTheme.XML_DESC))
					theme.loadValuesFromXML(nextElement, doc);
				else if(paramName.equals("bgImagePath"))bgImagePath = new File(nextElement.getTextContent()); 
				else if(paramName.equals("usesBGImage"))usesBGImage = booleanFromXML(nextElement);  
			}
		}
	}
	
	@Override
	public void applyToHeatMap(Heatmap heat) {
		super.applyToHeatMap(heat);
		applyToChart(heat.getChart());
		// 
		ScaleInPlot s = heat.getScaleInPlot();
		s.setFactor(theme.getScaleFactor());
		s.setUnit(theme.getScaleUnit());
		s.setValue(theme.getScaleValue());
		s.setVisible(theme.isShowScale());
		s.setPosition(theme.getScaleXPos(),theme.getScaleYPos());
	}
	
	/**
	 * applies theme to chart
	 * @param chart
	 */
	public void applyToChart(JFreeChart chart) {
		// apply Chart Theme
		theme.apply(chart);
		//
		chart.setAntiAlias(isAntiAliased());
		chart.getTitle().setVisible(isShowTitle()); 
        chart.getPlot().setBackgroundAlpha(isNoBackground()?0:1);
	}
	
	
	public void setUseGeneralFont(boolean state) {
		useGeneralFont = state;
		if(state) {
			// change all font families
			for(Font f : listFont) {
				f = new Font(fontGeneral.getFamily(), Font.PLAIN, 11);
			}
		}
	}

	public boolean isUseGeneralFontSize() {
		return useGeneralFontSize;
	}

	public void setUseGeneralFontSize(boolean useGeneralFontSize) {
		this.useGeneralFontSize = useGeneralFontSize;
	}

	public boolean isUseGeneralFontColor() {
		return useGeneralFontColor;
	}

	public void setUseGeneralFontColor(boolean useGeneralFontColor) {
		this.useGeneralFontColor = useGeneralFontColor;
	}

	public boolean isUseGeneralBGColor() {
		return useGeneralBGColor;
	}

	public void setUseGeneralBGColor(boolean useGeneralBGColor) {
		this.useGeneralBGColor = useGeneralBGColor;
	}

	public Font getFontGeneral() {
		return fontGeneral;
	}

	public void setFontGeneral(Font fontGeneral) {
		this.fontGeneral = fontGeneral;
	}

	public Color getcGeneralBG() {
		return cGeneralBG;
	}

	public void setcGeneralBG(Color cGeneralBG) {
		this.cGeneralBG = cGeneralBG;
	}

	public Color getcGeneralFontColor() {
		return cGeneralFontColor;
	}

	public void setcGeneralFontColor(Color cGeneralFontColor) {
		this.cGeneralFontColor = cGeneralFontColor;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Color getcTitle() {
		return cTitle;
	}

	public void setcTitle(Color cTitle) {
		this.cTitle = cTitle;
	}

	public Font getFontTitle() {
		return fontTitle;
	}

	public void setFontTitle(Font fontTitle) {
		this.fontTitle = fontTitle;
	}

	public boolean isShowTitle() {
		return showTitle;
	}

	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}

	public Font getFontAxesTitle() {
		return fontAxesTitle;
	}

	public void setFontAxesTitle(Font fontAxesTitle) {
		this.fontAxesTitle = fontAxesTitle;
	}

	public Font getFontAxesLabels() {
		return fontAxesLabels;
	}

	public void setFontAxesLabels(Font fontAxesLabels) {
		this.fontAxesLabels = fontAxesLabels;
	}

	public Color getcAxes() {
		return cAxes;
	}

	public void setcAxes(Color cAxes) {
		this.cAxes = cAxes;
	}

	public String getxTitle() {
		return xTitle;
	}

	public void setxTitle(String xTitle) {
		this.xTitle = xTitle;
	}

	public NumberFormat getxNumberFormat() {
		return xNumberFormat;
	}

	public void setxNumberFormat(NumberFormat xNumberFormat) {
		this.xNumberFormat = xNumberFormat;
	}

	public boolean isShowXTickLabels() {
		return showXTickLabels;
	}

	public void setShowXTickLabels(boolean showXTickLabels) {
		this.showXTickLabels = showXTickLabels;
	}

	public boolean isShowXTickMarks() {
		return showXTickMarks;
	}

	public void setShowXTickMarks(boolean showXTickMarks) {
		this.showXTickMarks = showXTickMarks;
	}

	public boolean isxAutoSelectTickIncrement() {
		return xAutoSelectTickIncrement;
	}

	public void setxAutoSelectTickIncrement(boolean xAutoSelectTickIncrement) {
		this.xAutoSelectTickIncrement = xAutoSelectTickIncrement;
	}

	public double getxTickIncrement() {
		return xTickIncrement;
	}

	public void setxTickIncrement(double xTickIncrement) {
		this.xTickIncrement = xTickIncrement;
	}

	public String getyTitle() {
		return yTitle;
	}

	public void setyTitle(String yTitle) {
		this.yTitle = yTitle;
	}

	public NumberFormat getyNumberFormat() {
		return yNumberFormat;
	}

	public void setyNumberFormat(NumberFormat yNumberFormat) {
		this.yNumberFormat = yNumberFormat;
	}

	public boolean isShowYTickLabels() {
		return showYTickLabels;
	}

	public void setShowYTickLabels(boolean showYTickLabels) {
		this.showYTickLabels = showYTickLabels;
	}

	public boolean isShowYTickMarks() {
		return showYTickMarks;
	}

	public void setShowYTickMarks(boolean showYTickMarks) {
		this.showYTickMarks = showYTickMarks;
	}

	public boolean isyAutoSelectTickIncrement() {
		return yAutoSelectTickIncrement;
	}

	public void setyAutoSelectTickIncrement(boolean yAutoSelectTickIncrement) {
		this.yAutoSelectTickIncrement = yAutoSelectTickIncrement;
	}

	public double getyTickIncrement() {
		return yTickIncrement;
	}

	public void setyTickIncrement(double yTickIncrement) {
		this.yTickIncrement = yTickIncrement;
	}

	public Font getFontLabelGen() {
		return fontLabelGen;
	}

	public void setFontLabelGen(Font fontLabelGen) {
		this.fontLabelGen = fontLabelGen;
	}

	public boolean isShowLabelGens() {
		return showLabelGens;
	}

	public void setShowLabelGens(boolean showLabelGens) {
		this.showLabelGens = showLabelGens;
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

	public Color getcChartBG() {
		return cChartBG;
	}

	public void setcChartBG(Color cChartBG) {
		this.cChartBG = cChartBG;
	}

	public Color getcPlotBG() {
		return cPlotBG;
	}

	public void setcPlotBG(Color cPlotBG) {
		this.cPlotBG = cPlotBG;
	}

	public Color getcPlotOutline() {
		return cPlotOutline;
	}

	public void setcPlotOutline(Color cPlotOutline) {
		this.cPlotOutline = cPlotOutline;
	}

	public boolean isUseGeneralFont() {
		return useGeneralFont;
	}

	public Color getcAxesFont() {
		return cAxesFont;
	}

	public void setcAxesFont(Color cAxesFont) {
		this.cAxesFont = cAxesFont;
	}

	public MyStandardChartTheme getTheme() {
		return theme;
	} 
	public void setTheme(MyStandardChartTheme theme) {
		this.theme = theme;
	}
	public void setBGImagePath(File pathBGImage) {
		this.bgImagePath = pathBGImage;
	}
	public File getBGImagePath() {
		return bgImagePath;
	}
	public void setUseBGImage(boolean b) {
		usesBGImage = b;
	}
	public boolean usesBGImage() {
		return usesBGImage;
	}
	
	public boolean isNoBackground() { 
		return ((Color)theme.getPlotBackgroundPaint()).getAlpha() == 0;
	}
	public void setNoBackground(boolean state) { 
		Color c = ((Color)theme.getPlotBackgroundPaint());
		Color cchart = ((Color)theme.getChartBackgroundPaint());
		theme.setPlotBackgroundPaint(new Color(c.getRed(), c.getGreen(), c.getBlue(), state? 0 : 255));
		theme.setChartBackgroundPaint(new Color(cchart.getRed(), cchart.getGreen(), cchart.getBlue(), state? 0 : 255));
	}
	public int getID() { 
		return theme.getID();
	}
	public void setID(int themeID) {
		theme.setID(themeID);
	}
	
}
