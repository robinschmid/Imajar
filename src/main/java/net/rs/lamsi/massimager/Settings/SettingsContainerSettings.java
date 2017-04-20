package net.rs.lamsi.massimager.Settings;

import java.util.HashMap;
import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.Settings.image.SettingsImage2D;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * this class can hold multiple sub settings {@link SettingsImage2D}
 * @author r_schm33
 *
 */
public abstract class SettingsContainerSettings extends Settings {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	protected HashMap<Class, Settings> list = new HashMap<Class, Settings>();
	
	public SettingsContainerSettings(String description, String path, String fileEnding) {
		super(description, path, fileEnding);
	}

	
	@Override
	public void applyToHeatMap(Heatmap heat) {
		for(Settings s:list.values())
			if(s!=null)
				s.applyToHeatMap(heat);
	}

	@Override
	public void resetAll() { 
		for(Settings s:list.values())
			if(s!=null)
				s.resetAll();
	}

	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		for(Settings s:list.values())
			if(s!=null)
				s.appendSettingsToXML(elParent, doc);
	}
	
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();

				for(Settings s:this.list.values())
					if(s!=null)
						if(paramName.equals(s.getDescription())) 
							getSettingsByClass(s.getClass()).loadValuesFromXML(nextElement, doc);
			}
		}
	}	
	
	/**
	 * replaces the current sub settings
	 * @param sett
	 * @return true if replaced false if only added
	 */
	public boolean replaceSettings(Settings sett) {
		if(list.replace(sett.getClass(), sett)!=null) {
			return true;
		}
		else {
			list.put(sett.getClass(), sett);
			return false;
		}
	}
	/**
	 * replaces the Settings in the list of settings
	 * @param s
	 */
	public void addSettings(Settings s) {
		replaceSettings(s);
	}
	
	/**
	 * 
	 * @param classsettings
	 * @return
	 */
	public Settings getSettingsByClass(Class classsettings) {
		// TODO -- add other settings here
		if(this.getClass().isAssignableFrom(classsettings))
			return this;
		else {
			return list.get(classsettings);
		}
	}
}
