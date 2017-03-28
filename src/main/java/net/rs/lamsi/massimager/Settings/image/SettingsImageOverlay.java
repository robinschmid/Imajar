package net.rs.lamsi.massimager.Settings.image;

import java.awt.Color;
import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.MyFreeChart.themes.ChartThemeFactory;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsZoom;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsThemes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.xml.bind.api.impl.NameConverter.Standard;

public class SettingsImageOverlay extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    private static Vector<Color> STANDARD_COLORS;
    //
	// theme settings
	protected SettingsThemes settTheme; 
	
	protected SettingsZoom settZoom;
	
	// the currently edited paintscale
	protected int currentPaintScale = 0;
	// overlay settings
	protected String title = "";
	// colors for paintscales
	protected Vector<Color> colors;
	protected Vector<SettingsPaintScale> psSettings = new Vector<SettingsPaintScale>();
	// holds which image2d / paintscale is active
	protected Vector<Boolean> active;
	

	public SettingsImageOverlay() {
		super("SettingsImage2D", "/Settings/Image2d/", "setImg2d"); 
		// standard theme
		this.settTheme = new SettingsThemes(ChartThemeFactory.THEME_DARKNESS);
		//
		this.settZoom = new SettingsZoom();
		
		resetAll();
	} 

	/**
	 * copy paint scales
	 * reset the active list 
	 * @param images
	 * @throws Exception 
	 */
	public void setImagesCopyPS(Vector<Image2D> images) throws Exception {
		psSettings.clear();
		int c = 0;
		for(Image2D i : images) {
			SettingsPaintScale ps =( SettingsPaintScale) i.getSettPaintScale().copy();
			psSettings.add(ps);
			// monochrome and color
			ps.setMonochrom(true);
			ps.setMinColor(colors.get(c%colors.size()));
			ps.setMaxColor(colors.get(c%colors.size()));
			//
			c++;
		}
		
		// create active array
		active = new Vector<Boolean>(images.size());
		for(int i=0; i<images.size(); i++)
			active.add(new Boolean(true));
	}


	@Override
	public void applyToHeatMap(Heatmap heat) {
		if(settTheme!=null)
			settTheme.applyToHeatMap(heat);
		if(settZoom!=null)
			settZoom.applyToHeatMap(heat);
	}

	@Override
	public void resetAll() { 
		title = "";
		setToStandardColors();
		psSettings.clear();
		active = null;
		// other
			settTheme = new SettingsThemes(ChartThemeFactory.THEME_DARKNESS);
		if(settZoom!=null)
			settZoom.resetAll();
	}

	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "title", title); 
		// colors
		for(int i=0; i<colors.size(); i++)
			toXML(elParent, doc, "colorXX"+i, colors.get(i)); 

		if(active!=null)
			for(int i=0; i<active.size(); i++)
				toXML(elParent, doc, "activeXX"+i, active.get(i)); 

		
		if(settTheme!=null)
			settTheme.appendSettingsToXML(elParent, doc);
		if(settZoom!=null)
			settZoom.appendSettingsToXML(elParent, doc);
		for(int i=0; i<psSettings.size(); i++)
			psSettings.get(i).appendSettingsToXML(elParent, doc);
	}
	
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		SettingsPaintScale ps = new SettingsPaintScale();
		colors.clear();
		if(active==null)
			active = new Vector<Boolean>();
		else active.clear();
		
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("title")) title = nextElement.getTextContent();
				else if(paramName.startsWith("colorXX")) colors.add(colorFromXML(nextElement));
				else if(paramName.startsWith("activeXX")) active.add(booleanFromXML(nextElement));
				else if(paramName.equals(settTheme.getDescription())) 
					settTheme.loadValuesFromXML(nextElement, doc);
				else if(settZoom!=null && paramName.equals(settZoom.getDescription())) 
					settZoom.loadValuesFromXML(nextElement, doc);
				else if(paramName.equals(ps.getDescription())) {
					SettingsPaintScale ps2 = new SettingsPaintScale();
					ps2.loadValuesFromXML(nextElement, doc);
					psSettings.addElement(ps2);
				}
			}
		}
		if(colors.isEmpty())
			setToStandardColors();
	}
	
	/**
	 * red, blue, green, orange, lila
	 * @return
	 */
	public Vector<Color> setToStandardColors() {
		if(STANDARD_COLORS==null) {
			STANDARD_COLORS = new Vector<Color>();
			STANDARD_COLORS.add(Color.RED);
			STANDARD_COLORS.add(Color.BLUE);
			STANDARD_COLORS.add(Color.GREEN);
			STANDARD_COLORS.add(Color.ORANGE);
			STANDARD_COLORS.add(new Color(200,0,200));
		}
		colors = STANDARD_COLORS;
		return STANDARD_COLORS;
	}

	public SettingsThemes getSettTheme() {
		return settTheme;
	}
	public void setSettTheme(SettingsThemes settTheme) {
		this.settTheme = settTheme;
	}
	public SettingsZoom getSettZoom() {
		return settZoom;
	}
	public void setSettZoom(SettingsZoom settZoom) {
		this.settZoom = settZoom;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}


	public Vector<Color> getColors() {
		return colors;
	}


	public void setColors(Vector<Color> colors) {
		this.colors = colors;
	}
	public void setColors(Color[] color) {
		colors.clear();
		for(Color c : color)
			colors.add(c);
	}

	public Vector<Boolean> getActive() {
		return active;
	}
	public void setActive(Vector<Boolean> active) {
		this.active = active;
	}
	/**
	 * is constructed by setImages
	 * whether to paint image[image] or not
	 * @param image
	 * @return
	 */
	public boolean isActive(int image) {
		return active!=null && active.get(image);
	}

	public SettingsPaintScale getSettPaintScale(int i) {
		return psSettings==null? null : psSettings.get(i);
	}
	public Vector<SettingsPaintScale> getSettPaintScale() {
		return psSettings;
	}

	public void setSettPaintScale(Vector<SettingsPaintScale> psSettings) {
		this.psSettings = psSettings;
	}

	public void setCurrentSettPaintScale(SettingsPaintScale sett) {
		if(psSettings==null || currentPaintScale<0 || currentPaintScale>=psSettings.size())
			return;
		else {
			psSettings.remove(currentPaintScale);
			psSettings.add(currentPaintScale, sett);
		}
	}
	
	public SettingsPaintScale getCurrentSettPaintScale() {
		return psSettings==null || currentPaintScale<0 || currentPaintScale>=psSettings.size()? null : psSettings.get(currentPaintScale);
	}

	public int getCurrentPaintScale() {
		return currentPaintScale;
	}

	public void setCurrentPaintScale(int currentPaintScale) {
		this.currentPaintScale = currentPaintScale;
	}
}
