package net.rs.lamsi.massimager.Settings.image.operations.quantifier;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.rs.lamsi.massimager.Settings.Settings;

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
	}
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
	} 
	
}
