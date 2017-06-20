package net.rs.lamsi.general.settings.image.needy;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.general.datamodel.image.Collectable2DPlaceHolderLink;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.modules.ModuleTree;
import net.rs.lamsi.general.settings.Settings;

public class SettingsGroupItemSelections extends Settings {
	// do not change the version!
	private static final long serialVersionUID = 1L;
	//

	protected transient Map<Collectable2D, Boolean> active;

	public SettingsGroupItemSelections() {
		super("SettingsGroupItemSelections", "/Settings/SelectedGroupItems/", "setSelGroupItems"); 
		resetAll();
	} 


	public void setAll(boolean state) {
		if(active!=null) {
			for (Map.Entry<Collectable2D, Boolean> entry : active.entrySet()) {
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
		String[] att = new String[]{"state"};
		
		for (Map.Entry<Collectable2D, Boolean> entry : active.entrySet())
			toXML(elParent, doc, "activeEntry", entry.getKey(), att, new Object[]{entry.getValue()}); 
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
						active = new HashMap<Collectable2D, Boolean>();
					else active.clear();
						
					boolean state = Boolean.valueOf(nextElement.getAttribute("state")); 
					Collectable2DPlaceHolderLink c = c2dFromXML(nextElement);
					// insert placeholder
					active.put(c, state);
				}
			}
		}
	}


	public Map<Collectable2D, Boolean> getActive() {
		return active;
	}
	public void setActive(Map<Collectable2D, Boolean> map) {
		active = map;
	}
	
	@Override
	public void replacePlaceHoldersInSettings(ModuleTree<Collectable2D> tree) {
		super.replacePlaceHoldersInSettings(tree);

		if(active!=null) {
			for (Map.Entry<Collectable2D, Boolean> entry : active.entrySet()) {
				if(Collectable2DPlaceHolderLink.class.isInstance(entry.getKey())) {
					Collectable2DPlaceHolderLink pl = (Collectable2DPlaceHolderLink) entry.getKey();
					Collectable2D c = tree.getCollectable2DFromPlaceHolder(pl);
					if(c!=null) {
						active.remove(entry.getKey());
						active.put(c, entry.getValue());
					}
				}
			}
		}
	}
	
	
	@Override
	public Settings copy() throws Exception {
		SettingsGroupItemSelections sett = (SettingsGroupItemSelections) super.copy();
		
		Map<Collectable2D, Boolean> nm = new HashMap<>();
		for (Map.Entry<Collectable2D, Boolean> entry : active.entrySet()) {
			nm.put(entry.getKey(), entry.getValue());
		}
		sett.setActive(nm);
		return sett;
	}
}
