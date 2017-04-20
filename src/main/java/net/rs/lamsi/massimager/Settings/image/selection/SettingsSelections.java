package net.rs.lamsi.massimager.Settings.image.selection;

import java.util.Vector;

import net.rs.lamsi.massimager.Settings.Settings;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SettingsSelections extends Settings {
	// do not change the version!
	private static final long serialVersionUID = 1L;

	// list of selections, exclusions and info
	protected Vector<SettingsShapeSelection> selections; 

	public SettingsSelections() {
		super("SettingsSelection", "/Settings/Selections/", "setSelList"); 
	} 

	@Override
	public void resetAll() {  
	}


	public void addSelection(SettingsShapeSelection sel) { 
		if(selections==null)
			selections = new Vector<SettingsShapeSelection>();
		selections.addElement(sel); 
	}

	public void removeSelection(SettingsShapeSelection sel) {
		if(selections!=null) {
			selections.remove(sel);
		}
	}


	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		//toXML(elParent, doc, "xrange.lower", xrange.getLowerBound()); 
		if(selections!=null) {
			for(int i=0; i<selections.size(); i++) {
				SettingsShapeSelection s = selections.get(i);
				s.appendSettingsToXML(elParent, doc);
			}
		}
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		double xu=0, yu=0;
		double xlower = Double.NaN, ylower = Double.NaN;
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("SettingsShapeSelection")) {
					if(selections==null)
						selections = new Vector<SettingsShapeSelection>();
					// TODO how to load from xml????
				}
				//				else if(paramName.equals("xrange.upper"))xu = doubleFromXML(nextElement);
			}
		}
	}
}
