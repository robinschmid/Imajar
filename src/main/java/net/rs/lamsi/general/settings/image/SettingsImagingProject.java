package net.rs.lamsi.general.settings.image;

import java.io.File;

import net.rs.lamsi.general.settings.SettingsContainerSettings;
import net.rs.lamsi.general.settings.image.visualisation.SettingsAlphaMap;
import net.rs.lamsi.general.settings.image.visualisation.SettingsBackgroundImg;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SettingsImagingProject extends SettingsContainerSettings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
    protected String name = "";

	// constructors
	public SettingsImagingProject() {
		super("SettingsImagingProject", "/Settings/Projects/", "setImgProject"); 
		
		// addSettings(new SettingsBackgroundImg());
	} 

	@Override
	public void resetAll() { 
		super.resetAll();
		// reset 
		name = "";
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
			}
		}
	}
	

	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "name", name);
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
