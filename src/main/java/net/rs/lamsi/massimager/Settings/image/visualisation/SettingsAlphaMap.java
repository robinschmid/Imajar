package net.rs.lamsi.massimager.Settings.image.visualisation;

import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.multiimager.Frames.multiimageframe.MultiImageTableModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SettingsAlphaMap extends Settings {
	// do not change the version!
	private static final long serialVersionUID = 1L;
	// 

	private boolean isActive = false;
	private Boolean[][] map = null;
	// settings
	private MultiImageTableModel tableModel;

	public SettingsAlphaMap() {
		super("SettingsAlphaMap", "Settings/Image/Operations/", "setAlphaMap");  
		resetAll();
	}

	public SettingsAlphaMap(Boolean[][] map) {
		this();
		this.map = map;
	}


	@Override
	public void resetAll() { 
		isActive=false;
	}
	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "isActive", isActive); 
		toXMLArray(elParent, doc, "map", map); 
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("isActive")) isActive = booleanFromXML(nextElement); 
				else if(paramName.equals("map"))map =  mapFromXML(nextElement);  
			}
		}
	}

	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public Boolean[][] getMap() {
		return map;
	}
	public void setMap(Boolean[][] map) {
		this.map = map;
	}
	
	/**
	 * converts the map to one dimension as line, line,line,line
	 */
	public boolean[] convertToLinearMap() {
		int size = 0;
		for(Boolean[] m : map)
			size+=m.length;

		boolean[] maplinear = new boolean[size];
		int c = 0;
		for(Boolean[] m : map) {
			for(Boolean b : m) {
				maplinear[c] = b;
				c++;
			}
		}
		return maplinear;
	}

	public MultiImageTableModel getTableModel() {
		return tableModel;
	}

	public void setTableModel(MultiImageTableModel tableModel) {
		this.tableModel = tableModel;
	}
}
