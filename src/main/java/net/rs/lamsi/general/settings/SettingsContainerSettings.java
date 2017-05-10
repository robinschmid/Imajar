package net.rs.lamsi.general.settings;

import java.util.HashMap;
import java.util.Vector;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.settings.image.SettingsImage2D;

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
	 * identifier by String is the super class
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

	/**
	 * saves settings and calls abstract appendSettingsValuesToXML
	 * creates a new parent element for this settings class
	 * @param elParent
	 * @param doc
	 */
	public void appendSettingsToXML(Element elParent, Document doc) { 
		Element elSett = doc.createElement(this.getSuperClass().getName());
		// save real class as attribute
		if(!this.getClass().equals(this.getSuperClass()))
			elSett.setAttribute(XMLATT_CLASS, this.getClass().getName());
		elParent.appendChild(elSett);

		// append all sub settings
		for(Settings s:list.values())
			if(s!=null)
				s.appendSettingsToXML(elSett, doc);
		
		// append values to extra element
		Element elSett2 = doc.createElement("this.Settings");
		elSett.appendChild(elSett2);
		appendSettingsValuesToXML(elSett2, doc);
	}
	

	/**
	 * loads settings from xml. calls loadValuesFromXML
	 * @param doc
	 * @param xpath
	 * @param path the String path to the parent (need to add /child)
	 * @throws XPathExpressionException
	 */
	@Override
	public void loadSettingsFromXML(Document doc, XPath xpath, String parentpath) throws XPathExpressionException {
		// root= settings 
		XPathExpression expr = xpath.compile(parentpath+"/"+this.getSuperClass().getName());
		NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		if (nodes.getLength() == 1) {
			Element el = (Element) nodes.item(0); 
			loadSubSettingsAndValuesFromXML(el, doc);
		}
	}
	public void loadSubSettingsAndValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				
				// values of this
				if(paramName.equals("this.Settings")) {
					// load values
					loadValuesFromXML(nextElement, doc);
				}
				else {
					// load settings object
					try {
						Class settingsClass = getRealClassFromXML(nextElement);
						if(settingsClass!=null) {
							// get super class name (hashed class name which was inserted to list) 
							Class hashedClass = getHashedClassFromXML(nextElement);
							
							Settings s = getSettingsByClass(hashedClass);
							
							// same class?
							boolean replace = false;
							if(s==null || !s.getClass().equals(settingsClass)) {
								replace = true;
								// create new settings object of different class
								s = Settings.createSettings(settingsClass);
							}
							// load settings from xml
							s.loadValuesFromXML(nextElement, doc);
							
							// replace?
							if(replace)
								replaceSettings(s);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}	
	
	/**
	 * replaces the current sub settings
	 * @param sett
	 * @return true if replaced false if only added
	 */
	public boolean replaceSettings(Settings sett) {
		if(list.replace(sett.getSuperClass(), sett)!=null) {
			return true;
		}
		else {
			list.put(sett.getSuperClass(), sett);
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
