package net.rs.lamsi.massimager.Settings.image;

import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.image.operations.SettingsImage2DOperations;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifier;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifierIS;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifierLinear;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsZoom;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsThemes;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SettingsImageOverlay extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	// theme settings
	protected SettingsThemes settTheme; 
	
	protected SettingsZoom settZoom;
	

	public SettingsImageOverlay() {
		super("SettingsImage2D", "/Settings/Image2d/", "setImg2d"); 
		// standard theme
		this.settTheme = new SettingsThemes();  
		//
		this.settZoom = new SettingsZoom();
	} 


	// COntstruct 
	public SettingsImageOverlay(SettingsPaintScale settPaintScale) {  
		super("SettingsImage2D", "/Settings/Image2d/", "setImg2d"); 
		try { 
			// standard theme
			this.settTheme = new SettingsThemes();
			this.settZoom = new SettingsZoom();
		} catch (Exception e) { 
			e.printStackTrace();
		}
	} 
	public SettingsImageOverlay(SettingsPaintScale settPaintScale, SettingsGeneralImage setImage) {  
		super("SettingsImage2D", "/Settings/Image2d/", "setImg2d"); 
		try {
			// standard theme
			this.settTheme = new SettingsThemes();
			this.settZoom = new SettingsZoom();
		} catch (Exception e) { 
			e.printStackTrace();
		}
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
		if(settTheme!=null)
			settTheme.resetAll();
		if(settZoom!=null)
			settZoom.resetAll();
	}
	



	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		if(settTheme!=null)
			settTheme.appendSettingsToXML(elParent, doc);
		if(settZoom!=null)
			settZoom.appendSettingsToXML(elParent, doc);
	}
	
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals(settTheme.getDescription())) 
					settTheme.loadValuesFromXML(nextElement, doc);
				else if(settZoom!=null && paramName.equals(settZoom.getDescription())) 
					settZoom.loadValuesFromXML(nextElement, doc);
			}
		}
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
}
