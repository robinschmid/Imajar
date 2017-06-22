package net.rs.lamsi.general.settings.image.needy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
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
		int c = 0;
		for (Map.Entry<SettingsCollectable2DLink, Boolean> entry : active.entrySet()) {
			String[][] att = entry.getKey().toXMLAttributes();
			toXML(elParent, doc, "activeEntry"+c, entry.getValue(), att[0], att[1]);
			c++;
		}
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		if(active == null) 
			active = new LinkedHashMap<SettingsCollectable2DLink, Boolean>();
		else active.clear();
		
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.startsWith("activeEntry")) {
						
					boolean state = booleanFromXML(nextElement); 
					
					String p = nextElement.getAttribute("project");
					String g = nextElement.getAttribute("group");
					String t = nextElement.getAttribute("title");
					
					if(g.isEmpty()) g  =null;
					if(p.isEmpty()) p = null;
					
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
	

	/**
	 * is constructed by setImages
	 * whether to paint image[image] or not
	 * @param image
	 * @return
	 */
	public Boolean isActive(Collectable2D img) {
		Map.Entry<SettingsCollectable2DLink, Boolean> e = getEntry(img);
		return e!=null? e.getValue() : false;
	}

	/**
	 * is constructed by setImages
	 * whether to paint image[image] or not
	 * @param source source could be an image overlay to specify the current group/project path for relative links
	 * @param img target
	 * @return
	 */
	public Boolean isActive(Collectable2D source, Collectable2D img) {
		Map.Entry<SettingsCollectable2DLink, Boolean> e = getEntry(source, img);
		return e!=null? e.getValue() : false;
	}
	
	
	private Map.Entry<SettingsCollectable2DLink, Boolean> getEntry(Collectable2D img) {
		if(active==null)
			return null;
		for(Map.Entry<SettingsCollectable2DLink, Boolean> e : active.entrySet()) {
			if(e.getKey().equals(img)) {
				return e;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param source source could be an image overlay to specify the current group/project path for relative links
	 * @param img target
	 * @return
	 */
	private Map.Entry<SettingsCollectable2DLink, Boolean> getEntry(Collectable2D source, Collectable2D img) {
		if(active==null)
			return null;
		for(Map.Entry<SettingsCollectable2DLink, Boolean> e : active.entrySet()) {
			if(e.getKey().equals(source, img)) {
				return e;
			}
		}
		return null;
	}
}
