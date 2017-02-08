package net.rs.lamsi.massimager.Settings.preferences;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.massimager.Settings.Settings;

public class SettingsGeneralPreferences extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
    // Icon settings
    private int iconWidth, iconHeight;
    private boolean generatesIcons = true;
    
	

	public SettingsGeneralPreferences() {
		super("GeneralPreferences", "/Settings/General/", "settPrefer");  
		resetAll();
	}


	@Override
	public void resetAll() { 
		iconWidth = 60;
		iconHeight = 16;
		generatesIcons = true;
	}
	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "iconWidth", iconWidth); 
		toXML(elParent, doc, "iconHeight",iconHeight ); 
		toXML(elParent, doc, "generatesIcons", generatesIcons); 
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("iconWidth")) iconWidth = intFromXML(nextElement); 
				else if(paramName.equals("iconHeight"))iconHeight = intFromXML(nextElement);  
				else if(paramName.equals("generatesIcons"))generatesIcons = booleanFromXML(nextElement);  
			}
		}
	}


	public int getIconWidth() {
		return iconWidth;
	}
	public void setIconWidth(int iconWidth) {
		this.iconWidth = iconWidth;
	}
	public int getIconHeight() {
		return iconHeight;
	}
	public void setIconHeight(int iconHeight) {
		this.iconHeight = iconHeight;
	}
	public boolean isGeneratesIcons() {
		return generatesIcons;
	}
	public void setGeneratesIcons(boolean generatesIcons) {
		this.generatesIcons = generatesIcons;
	}
}
