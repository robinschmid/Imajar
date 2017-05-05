package net.rs.lamsi.general.settings.image.visualisation;

import net.rs.lamsi.general.settings.Settings;
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
	private int realsize = 0;
	// settings
	private MultiImageTableModel tableModel;

	public SettingsAlphaMap() {
		super("SettingsAlphaMap", "Settings/Visualization/", "setAlphaMap");  
		resetAll();
	}

	public SettingsAlphaMap(Boolean[][] map) {
		this();
		setMap(map);
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
				else if(paramName.equals("map")) setMap(mapFromXML(nextElement));
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

		realsize = 0;

		if(map!=null) {
			for(Boolean[] m : map)
				for(Boolean b : m)
					if(b!=null) 
						realsize++;
		}
	}
	
	/**
	 * converts the map to one dimension as line, line,line,line
	 */
	public boolean[] convertToLinearMap() {
		boolean[] maplinear = new boolean[realsize];
		int c = 0; 
		for(Boolean[] m : map) {
			for(Boolean b : m) {
				if(b!=null) {
					maplinear[c] = b;
					c++;
				}
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
