package net.rs.lamsi.general.settings.image;

import net.rs.lamsi.general.settings.SettingsContainerSettings;
import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap;
import net.rs.lamsi.general.settings.image.visualisation.SettingsBackgroundImg;
import net.rs.lamsi.general.settings.interf.GroupSettings;
import net.rs.lamsi.utils.FileAndPathUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SettingsImageGroup extends SettingsContainerSettings implements GroupSettings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
    protected String name = "", pathData = "";

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
		name = "";
		pathData = "";
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
				if(paramName.equals("name")) name = nextElement.getTextContent();
				else if(paramName.equals("pathData")) pathData = nextElement.getTextContent();
			}
		}
		// load all sub settings
	}
	

	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "name", name);
		toXML(elParent, doc, "pathData", pathData);
	}
	
	// getters and setters
	public SettingsAlphaMap getSettAlphaMap() {
		return (SettingsAlphaMap) getSettingsByClass(SettingsAlphaMap.class);
	}
    public SettingsBackgroundImg getSettBGImg() {
		return (SettingsBackgroundImg) getSettingsByClass(SettingsBackgroundImg.class);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = FileAndPathUtil.replaceInvalidChar(name);
	}

	public String getPathData() {
		return pathData;
	}
	public void setPathData(String pathData) {
		this.pathData = pathData;
	}

}
