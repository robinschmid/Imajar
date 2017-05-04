package net.rs.lamsi.massimager.Settings.image.operations.quantifier;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsGeneralImage.IMAGING_MODE;

public abstract class SettingsImage2DQuantifier extends Settings implements Image2DQuantifyStrategyImpl {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    
    // selected mode
    public static final int MODE_LINEAR = 0, MODE_ONE_POINT = 1, MODE_MULTIPLE_POINTS = 2, MODE_IS=3, MODE_BLANK = 4;
    
    protected final int mode;
    protected boolean isActive = false;
	
	
	public SettingsImage2DQuantifier(int mode) {
		super("SettingsImage2DQuantifier", "/Settings/operations/", "setQuantifier"); 
		this.mode = mode;
		resetAll();		
	} 
	@Override
	public void resetAll() {
		isActive = false;
	}
	
	public int getMode() {
		return mode;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public String getModeAsString() { 
		switch(getMode()) {
		case MODE_IS:
			return "internal standard";
		case MODE_LINEAR:
			return "linear";
		case MODE_ONE_POINT:
			return "one point calibration";
		case MODE_MULTIPLE_POINTS:
			return "regression ("+ ((SettingsImage2DQuantifierMultiPoints)this).getQuantifier().size() +" points)";
		default: 
			return "";
		} 
	}
	
	public abstract boolean isApplicable();

	
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "isActive", isActive);
	}
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("isActive"))isActive = booleanFromXML(nextElement);  
			}
		}
	} 
	
}
