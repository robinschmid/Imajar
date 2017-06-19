package net.rs.lamsi.general.settings.image.visualisation.themes;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Vector;

import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.myfreechart.Plot.image2d.annot.ScaleInPlot;
import net.rs.lamsi.general.myfreechart.themes.ChartThemeFactory;
import net.rs.lamsi.general.myfreechart.themes.MyStandardChartTheme;
import net.rs.lamsi.general.myfreechart.themes.ChartThemeFactory.THEME;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.SettingsContainerSettings;
import net.rs.lamsi.general.settings.image.SettingsContainerCollectable2D;
import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLabelLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.ui.RectangleInsets;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SettingsTheme extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	
	protected MyStandardChartTheme theme; 
	
	// chart title
	private String chartTitle = "";

	public SettingsTheme() {
		this(null);
	} 
	/**
	 * example
	 * @param theme example: ChartThemeFactory.THEME_KARST
	 */
	public SettingsTheme(THEME themeid) {
		super("SettingsThemes", "/Settings/Visualization/", "setGeneralPStyle"); 
		resetAll();
		if(themeid!=null)
			this.theme = ChartThemeFactory.createChartTheme(themeid);
	} 
	

	public void setAll(boolean antiAlias, boolean showTitle, boolean noBG, Color cBG, Color cPlotBG,
			boolean showXGrid, boolean showYGrid, boolean showXAxis, boolean showYAxis, 
			Font fMaster, Color cMaster, Font fAxesT, Color cAxesT, Font fAxesL, Color cAxesL, Font fTitle, Color cTitle,
			String chartTitle) {
		theme.setAll(antiAlias, showTitle, noBG, cBG, cPlotBG, showXGrid, showYGrid, showXAxis, showYAxis, 
				fMaster, cMaster, fAxesT, cAxesT, fAxesL, cAxesL, fTitle, cTitle);
		
		this.chartTitle = chartTitle;
		
	}
	@Override
	public void resetAll() { 
		theme = ChartThemeFactory.getStandardTheme();
		chartTitle = "";
	}

	public void setShortTitle(Color c, Color bg, Font font) {
		theme.setShortTitle(c, bg, font);
	}
	
	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		theme.appendThemeSettingsToXML(elParent, doc);
		toXML(elParent, doc, "chartTitle", chartTitle);
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals(MyStandardChartTheme.XML_DESC))
					theme.loadValuesFromXML(nextElement, doc);
				else if(paramName.equals("chartTitle")) chartTitle = nextElement.getTextContent();
			}
		}
	}
	
	@Override
	public void applyToHeatMap(Heatmap heat) {
		super.applyToHeatMap(heat);
		applyToChart(heat.getChart());
		
		// font short title
		heat.getShortTitle().setFont(theme.getFontShortTitle());
		heat.getShortTitle().setPaint(theme.getcShortTitle());
		heat.getShortTitle().setBackgroundPaint(theme.getcBGShortTitle());
	}
	
	/**
	 * applies theme to chart
	 * @param chart
	 */
	public void applyToChart(JFreeChart chart) {
		// apply Chart Theme
		theme.apply(chart);
		
		chart.setTitle(chartTitle);
	}
	
	public MyStandardChartTheme getTheme() {
		return theme;
	} 
	public void setTheme(MyStandardChartTheme theme) {
		this.theme = theme;
	}
	public THEME getID() { 
		return theme.getID();
	}
	public void setID(THEME themeID) {
		theme.setID(themeID);
	}
	public String getChartTitle() {
		return chartTitle;
	}
	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}
}
