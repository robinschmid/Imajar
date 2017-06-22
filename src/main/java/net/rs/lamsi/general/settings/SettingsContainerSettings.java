package net.rs.lamsi.general.settings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.modules.ModuleTree;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.settings.image.SettingsImage2D;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifierOnePoint;
import net.rs.lamsi.general.settings.interf.Image2DSett;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;

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

	// true if at least one of the subsettings is an object of SettingsContainerSettings
	private boolean hasSubContainerSettings = false;
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
							// get super class name (hashed class name which was inserted to HashMap) 
							Class hashedClass = getHashedClassFromXML(nextElement);
							
							Settings s = getSettingsByClass(hashedClass);
							
							if(settingsClass.equals(SettingsImage2DQuantifierOnePoint.class))
								System.out.println("quanti");
							
							// same class?
							boolean replace = false;
							if(s==null || !s.getClass().equals(settingsClass)) {
								replace = true;
								// create new settings object of different class
								s = Settings.createSettings(settingsClass);
								ImageEditorWindow.log("No settings obj found: Creating a new object of "+settingsClass, LOG.DEBUG);
							}
							// load settings from xml
							if(SettingsContainerSettings.class.isInstance(s))
								((SettingsContainerSettings)s).loadSubSettingsAndValuesFromXML(nextElement, doc);
							else s.loadValuesFromXML(nextElement, doc);
							
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
		// add to this settings container if not present
			return replaceSettings(sett, true);
	}
	
	/**
	 * tryis to replace
	 * @param sett
	 * @param addIfNotReplaced Adds the settings to this container if none was replaced
	 * @return
	 */
	public boolean replaceSettings(Settings sett, boolean addIfNotReplaced) {
		if(list.replace(sett.getSuperClass(), sett)!=null) {
			return true;
		}
		else {
			boolean replaced = false;
			// try in sub settings
			if(hasSubContainerSettings) {
				for (Iterator iterator = list.values().iterator(); iterator.hasNext() && !replaced; ) {
					Settings s = (Settings) iterator.next();
					if(SettingsContainerSettings.class.isInstance(s)) {
						// do not add to sub settings container if not replaced
						replaced = ((SettingsContainerSettings)s).replaceSettings(sett, false);
					}		
				}
			}
			// if not replaced in sub - put
			if(!replaced && addIfNotReplaced) {
				// add super class and sett
				list.put(sett.getSuperClass(), sett);
				hasSubContainerSettings = hasSubContainerSettings || SettingsContainerSettings.class.isInstance(sett);
			}
			return replaced;
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
			Settings s = list.get(classsettings);
			if(s!=null)
				return s;
			else {
				// search in other settings container settings
				if(hasSubContainerSettings) {
					for (Iterator iterator = list.values().iterator(); iterator.hasNext();) {
						Settings sett = (Settings) iterator.next();
						
						if(SettingsContainerSettings.class.isInstance(sett)) {
							s = ((SettingsContainerSettings)sett).getSettingsByClass(classsettings);
							if(s!=null)
								return s;
						}					
					}
				}
				// nothing...
				ImageEditorWindow.log("No settings object found for "+classsettings.getName(), LOG.DEBUG);
				return null;
			}
		}
	}
	
	
	
	/**
	 * replace all collectable2d place holders in settings
	 * @param tree
	 */
	@Override
	public void replacePlaceHoldersInSettings(ModuleTree<Collectable2D> tree) {
		for (Iterator iterator = list.values().iterator(); iterator.hasNext();) {
			Settings sett = (Settings) iterator.next();
			sett.replacePlaceHoldersInSettings(tree);
		}
	}
}
