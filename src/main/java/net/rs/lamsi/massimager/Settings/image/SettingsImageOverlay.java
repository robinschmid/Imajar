package net.rs.lamsi.massimager.Settings.image;

import java.awt.Color;
import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.massimager.MyFreeChart.themes.ChartThemeFactory;
import net.rs.lamsi.massimager.MyFreeChart.themes.ChartThemeFactory.THEME;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsZoom;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsThemes;
import net.rs.lamsi.utils.useful.graphics2d.blending.BlendComposite;
import net.rs.lamsi.utils.useful.graphics2d.blending.BlendComposite.BlendingMode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SettingsImageOverlay extends SettingsContainerCollectable2D {
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

	// blending mode with alpha
	protected BlendComposite blend = BlendComposite.Add; 


	public SettingsImageOverlay() {
		super("SettingsImageOverlay", "/Settings/ImageOv/", "setImgOv"); 
		// standard theme
		this.settTheme = new SettingsThemes(THEME.DARKNESS);
		//
		this.settZoom = new SettingsZoom();

		// TODO add sub settings
		addSettings(settTheme);
		addSettings(settZoom);
		
		resetAll();
	} 

	/**
	 * copy paint scales
	 * reset the active list 
	 * @param images
	 * @throws Exception 
	 */
	public void init(ImageGroupMD group) throws Exception {
		psSettings.clear();
		int c = 0;
		for(int f=0; f<group.image2dCount(); f++) {
			Image2D i = (Image2D)group.get(f);
			SettingsPaintScale ps =( SettingsPaintScale) i.getSettings().getSettPaintScale().copy();
			psSettings.add(ps);
			// monochrome and color
			ps.setMonochrom(true);
			ps.setMinColor(colors.get(c%colors.size()));
			ps.setMaxColor(colors.get(c%colors.size()));
			//
			c++;
		}
		
		// create active array
		active = new Vector<Boolean>(group.size());
		for(int i=0; i<group.size(); i++)
			active.add(new Boolean(true));
	}
	
	public boolean isInitialised() {
		return psSettings.size()>0;
	}
	
	/**
	 * add image2d 
	 * @throws Exception 
	 */
	public void addImage(Image2D i) throws Exception { 
		SettingsPaintScale ps =( SettingsPaintScale) i.getSettings().getSettPaintScale().copy();
		psSettings.add(ps);
		// monochrome and color
		ps.setMonochrom(true);
		ps.setMinColor(colors.get(active.size()%colors.size()));
		ps.setMaxColor(colors.get(active.size()%colors.size()));
		//
		active.add(new Boolean(false));
	}
	/**
	 * add image2d 
	 * @throws Exception 
	 */
	public void removeImage(int i) throws Exception { 
		psSettings.remove(i);
		active.remove(i);
	}

	@Override
	public void resetAll() { 
		super.resetAll();
		title = "";
		setToStandardColors();
		psSettings.clear();
		active = null;
		// other
		settTheme = new SettingsThemes(THEME.DARKNESS);
	}

	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "title", title); 
		toXML(elParent, doc, "blend", blend.getMode().toString(), new String[]{"alpha"}, new Object[]{blend.getAlpha()}); 
		// colors
		for(int i=0; i<colors.size(); i++)
			toXML(elParent, doc, "colorXX"+i, colors.get(i)); 

		if(active!=null)
			for(int i=0; i<active.size(); i++)
				toXML(elParent, doc, "activeXX"+i, active.get(i)); 

		for(int i=0; i<psSettings.size(); i++)
			psSettings.get(i).appendSettingsToXML(elParent, doc);
		
		super.appendSettingsValuesToXML(elParent, doc);
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
				else if(paramName.startsWith("blend")) {
					blend = BlendComposite.getInstance(BlendingMode.valueOf(nextElement.getTextContent()), Float.valueOf(nextElement.getAttribute("alpha")));
				}
				else if(paramName.equals(ps.getDescription())) {
					SettingsPaintScale ps2 = new SettingsPaintScale();
					ps2.loadValuesFromXML(nextElement, doc);
					psSettings.addElement(ps2);
				}
			}
		}
		if(colors.isEmpty())
			setToStandardColors();
		
		// load sub settings
		super.loadValuesFromXML(el, doc);
	}
	
	

	
	public Settings getSettingsByClass(Class classsettings) {
		// TODO -- add other settings here
		if(SettingsPaintScale.class.isAssignableFrom(classsettings))
			return getCurrentSettPaintScale();
		else {
			return super.getSettingsByClass(classsettings);
		}
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
	public BlendComposite getBlend() {
		return blend;
	}

	public void setBlend(BlendComposite blend) {
		this.blend = blend;
	}
}
