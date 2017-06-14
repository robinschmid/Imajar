package net.rs.lamsi.general.settings.image.operations.quantifier;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.interf.Image2DSett;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class SettingsImage2DQuantifier extends Settings implements Image2DQuantifyStrategyImpl, Image2DSett {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    
    // selected mode
    public enum MODE {
    	LINEAR, ONE_POINT, IS, BLANK;
    }
    
    protected final MODE mode;
    protected boolean isActive = false;
    protected transient Image2D currentImg = null;
	
	
	public SettingsImage2DQuantifier(MODE mode) {
		super("SettingsImage2DQuantifier", "/Settings/operations/", "setQuantifier"); 
		this.mode = mode;
		resetAll();		
	} 
	@Override
	public void resetAll() {
		isActive = false;
	}
	
	public MODE getMode() {
		return mode;
	}
	public boolean isActive() {
		return isActive;
	}
	
	/**
	 * 
	 * @param isActive
	 * @return true if state has changed
	 */
	public boolean setActive(boolean isActive) {
		boolean state = this.isActive!=isActive;
		this.isActive = isActive;
		return state;
	}
	public String getModeAsString() { 
		switch(getMode()) {
		case IS:
			return "internal standard";
		case LINEAR:
			return "linear";
		case ONE_POINT:
			return "one point calibration";
		default: 
			return "";
		} 
	}
	
	public abstract boolean isApplicable();

	@Override
	public void setCurrentImage(Image2D img) {
		currentImg = img;
	}
	
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
