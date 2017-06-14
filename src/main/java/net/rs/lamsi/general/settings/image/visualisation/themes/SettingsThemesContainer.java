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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SettingsThemesContainer extends SettingsContainerSettings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //

	public SettingsThemesContainer() {
		super("SettingsThemes", "/Settings/Visualization/", "setPStyle"); 
		
		
		
		// scale in plot
		SettingsScaleInPlot scaleInPlot = new SettingsScaleInPlot();
		addSettings(scaleInPlot);

		SettingsTheme th = new SettingsTheme();
		addSettings(th);
		
		resetAll();
	} 
	/**
	 * example
	 * @param theme example: ChartThemeFactory.THEME_KARST
	 */
	public SettingsThemesContainer(THEME themeid) {
		super("SettingsThemes", "/Settings/Visualization/", "setThemeContainer"); 
		// scale in plot
		SettingsScaleInPlot scaleInPlot = new SettingsScaleInPlot();
		addSettings(scaleInPlot);
		
		SettingsTheme th = new SettingsTheme(themeid);
		addSettings(th);
		
		resetAll();
	} 
	
	public void setAll(boolean antiAlias, boolean showTitle, boolean noBG, Color cBG, boolean showXGrid, boolean showYGrid, boolean showXAxis, boolean showYAxis, 
			boolean showScale, String scaleUnit, float scaleFactor, float scaleValue, boolean isPaintScaleInPlot, float scaleXPos, float scaleYPos,
			boolean useScientificIntensities, int significantDigits, String paintScaleTitle, boolean usePaintScaleTitle, 
			Font fMaster, Color cMaster, Font fAxesT, Color cAxesT, Font fAxesL, Color cAxesL, Font fTitle, Color cTitle, Font fScale, Color cScale) {
		getSettTheme().setAll(antiAlias, showTitle, noBG, cBG, showXGrid, showYGrid, showXAxis, showYAxis, 
				isPaintScaleInPlot, useScientificIntensities, significantDigits, paintScaleTitle, usePaintScaleTitle, fMaster, cMaster, fAxesT, cAxesT, fAxesL, cAxesL, fTitle, cTitle);
		getSettScaleInPlot().setAll(showScale, scaleUnit, scaleFactor, scaleValue, scaleXPos, scaleYPos, fScale, cScale);
	}

	

	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
	}
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
	}
	
	/**
	 * applies theme to chart
	 * @param chart
	 */
	public void applyToChart(JFreeChart chart) {
		// apply Chart Theme
		getSettTheme().applyToChart(chart);
	}
	
	// getters and setters getSettTheme
	public SettingsScaleInPlot getSettScaleInPlot() {
		return (SettingsScaleInPlot) getSettingsByClass(SettingsScaleInPlot.class);
	}
	public SettingsTheme getSettTheme() {
		return (SettingsTheme) getSettingsByClass(SettingsTheme.class);
	}
	
	public MyStandardChartTheme getTheme() {
		return getSettTheme().getTheme();
	} 
	public void setTheme(MyStandardChartTheme theme) {
		getSettTheme().setTheme(theme);
	}
	public THEME getID() { 
		return getTheme().getID();
	}
	public void setID(THEME themeID) {
		getTheme().setID(themeID);
	}
	
	public Font getFontScaleInPlot() {
		return getSettScaleInPlot().getFontScaleInPlot();
	}

	public Color getScaleFontColor() {
		return getSettScaleInPlot().getScaleFontColor();
	}

	public void setFontScaleInPlot(Font fontScaleInPlot) {
		getSettScaleInPlot().setFontScaleInPlot(fontScaleInPlot);
	}

	public void setScaleFontColor(Color scaleFontColor) {
		getSettScaleInPlot().setScaleFontColor(scaleFontColor);
	}
	
}
