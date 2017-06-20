package net.rs.lamsi.general.settings.image.visualisation.themes;

import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.JFreeChart;
import org.jfree.ui.RectangleInsets;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.rs.lamsi.general.myfreechart.themes.ChartThemeFactory.THEME;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.myfreechart.themes.MyStandardChartTheme;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.SettingsContainerSettings;

public class SettingsThemesContainer extends SettingsContainerSettings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //

	public SettingsThemesContainer(boolean createPaintscaleThemeSettings) {
		this(null, createPaintscaleThemeSettings);
	} 
	/**
	 * example
	 * @param theme example: ChartThemeFactory.THEME_KARST
	 */
	public SettingsThemesContainer(THEME themeid, boolean createPaintscaleThemeSettings) {
		super("SettingsThemesContainer", "/Settings/Visualization/", "setThemeContainer"); 
		resetAll();
		// scale in plot
		SettingsScaleInPlot scaleInPlot = new SettingsScaleInPlot();
		addSettings(scaleInPlot);
		
		SettingsTheme th = new SettingsTheme(themeid);
		addSettings(th);

		if(createPaintscaleThemeSettings)
			addSettings(new SettingsPaintscaleTheme());
	} 
	
	public void setAll(boolean antiAlias, boolean showTitle, boolean noBG, Color cBG, Color cPlotBG, boolean showXGrid, boolean showYGrid, boolean showXAxis, boolean showYAxis, 
			boolean showScale, String scaleUnit, float scaleFactor, float scaleValue, boolean isPaintScaleInPlot, float scaleXPos, float scaleYPos,
			boolean useScientificIntensities, int significantDigits, String paintScaleTitle, boolean usePaintScaleTitle, 
			Font fMaster, Color cMaster, Font fAxesT, Color cAxesT, Font fAxesL, Color cAxesL, Font fTitle, Color cTitle, Font fScale, Color cScale,
			String chartTitle, RectangleInsets psMargin, int psWidth, double psTickUnit, boolean psAutoSelectTickUnit) {
		getSettTheme().setAll(antiAlias, showTitle, noBG, cBG, cPlotBG, showXGrid, showYGrid, showXAxis, showYAxis, 
				fMaster, cMaster, fAxesT, cAxesT, fAxesL, cAxesL, fTitle, cTitle, chartTitle);
		getSettScaleInPlot().setAll(showScale, scaleUnit, scaleFactor, scaleValue, scaleXPos, scaleYPos, fScale, cScale);
		
		// can be null
		SettingsPaintscaleTheme pst = getSettPaintscaleTheme();
		if(pst!=null)
			pst.setAll(isPaintScaleInPlot, useScientificIntensities, significantDigits, paintScaleTitle, usePaintScaleTitle, psMargin, psWidth, psTickUnit, psAutoSelectTickUnit);
	}
	
	
	@Override
	public boolean replaceSettings(Settings sett, boolean addIfNotReplaced) {
		return super.replaceSettings(sett, addIfNotReplaced);
	}
	

	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
	}
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
	}
	
	
	@Override
	public void applyToImage(Image2D img) throws Exception {
		super.applyToImage(img);
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
	public SettingsPaintscaleTheme getSettPaintscaleTheme() {
		return (SettingsPaintscaleTheme) getSettingsByClass(SettingsPaintscaleTheme.class);
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
