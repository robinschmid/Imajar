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
    protected SettingsBackgroundImg settBGImg;
    
	// paint scale
	protected SettingsAlphaMap settAlphaMap;

	// constructors
	public SettingsImageGroup() {
		super("SettingsImageGroup", "/Settings/Image2d/", "setImgGroup"); 
		settAlphaMap = new SettingsAlphaMap();
		settBGImg = new SettingsBackgroundImg();
		
		list.addElement(settAlphaMap);
		list.addElement(settBGImg);
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
					settBGImg.setPathBGImage(new File(nextElement.getTextContent()));
				else if(paramName.equals("bgWidth")) 
					settBGImg.setBgWidth(floatFromXML(nextElement));
			}
		}
		// load all sub settings
		super.loadValuesFromXML(el, doc);
	}
	
	// getters and setters
	public SettingsAlphaMap getSettAlphaMap() {
		return settAlphaMap;
	}
    public SettingsBackgroundImg getSettBGImg() {
		return settBGImg;
	}
	public void setSettAlphaMap(SettingsAlphaMap settAlphaMap) {
		list.remove(this.settAlphaMap);
		this.settAlphaMap = settAlphaMap;
		list.add(this.settAlphaMap);
	}
	public void setSettBGImg(SettingsBackgroundImg settBGImg) {
		list.remove(this.settBGImg);
		this.settBGImg = settBGImg;
		list.add(this.settBGImg);
	}

	
	
}
