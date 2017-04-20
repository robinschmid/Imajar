package net.rs.lamsi.massimager.Settings.image;

import java.io.File;

import net.rs.lamsi.massimager.Settings.SettingsContainerSettings;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsAlphaMap;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsBackgroundImg;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SettingsImageGroup extends SettingsContainerSettings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //

	// constructors
	public SettingsImageGroup() {
		super("SettingsImageGroup", "/Settings/Image2d/", "setImgGroup"); 
		
		addSettings(new SettingsAlphaMap());
		addSettings(new SettingsBackgroundImg());
	} 

	@Override
	public void resetAll() { 
		super.resetAll();
		// reset 
	}

	// xml
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				// import settings
				// older settings format
				if(paramName.equals("pathBGImage")) 
					getSettBGImg().setPathBGImage(new File(nextElement.getTextContent()));
				else if(paramName.equals("bgWidth")) 
					getSettBGImg().setBgWidth(floatFromXML(nextElement));
			}
		}
		// load all sub settings
		super.loadValuesFromXML(el, doc);
	}
	
	// getters and setters
	public SettingsAlphaMap getSettAlphaMap() {
		return (SettingsAlphaMap) getSettingsByClass(SettingsAlphaMap.class);
	}
    public SettingsBackgroundImg getSettBGImg() {
		return (SettingsBackgroundImg) getSettingsByClass(SettingsBackgroundImg.class);
	}
	
}
