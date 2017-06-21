package net.rs.lamsi.general.settings.image.needy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.general.settings.Settings;

public class SettingsGroupItemSelections extends Settings {
	// do not change the version!
	private static final long serialVersionUID = 1L;
	//

	protected transient LinkedHashMap<SettingsCollectable2DLink, Boolean> active;

	public SettingsGroupItemSelections() {
		super("SettingsGroupItemSelections", "/Settings/SelectedGroupItems/", "setSelGroupItems"); 
		resetAll();
	} 


	public void setAll(boolean state) {
		if(active!=null) {
			for (Map.Entry<SettingsCollectable2DLink, Boolean> entry : active.entrySet()) {
				active.put(entry.getKey(), state);
			}
		}
	}
	@Override
	public void resetAll() { 
		setAll(false);
	}

	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		// state is saved as content 
		// link settings are saved as attributes
		for (Map.Entry<SettingsCollectable2DLink, Boolean> entry : active.entrySet()) {
			String[][] att = entry.getKey().toXMLAttributes();
			toXML(elParent, doc, "activeEntry", entry.getValue(), att[0], att[1]);
		}
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("activeEntry")) {
					if(active == null) 
						active = new LinkedHashMap<SettingsCollectable2DLink, Boolean>();
					else active.clear();
						
					boolean state = booleanFromXML(nextElement); 
					
					String p = nextElement.getAttribute("project");
					String g = nextElement.getAttribute("group");
					String t = nextElement.getAttribute("title");
					
					SettingsCollectable2DLink c = new SettingsCollectable2DLink(t,g,p);
					// insert link
					active.put(c, state);
				}
			}
		}
	}


	public LinkedHashMap<SettingsCollectable2DLink, Boolean> getActive() {
		return active;
	}
	public void setActive(LinkedHashMap<SettingsCollectable2DLink, Boolean> map) {
		active = map;
	}

}
