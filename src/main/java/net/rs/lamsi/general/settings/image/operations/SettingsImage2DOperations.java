package net.rs.lamsi.general.settings.image.operations;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.image.operations.quantifier.Image2DQuantifyStrategyImpl;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DBlankSubtraction;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifierIS;
import net.rs.lamsi.general.settings.interf.Image2DSett;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SettingsImage2DOperations  extends Settings  implements Image2DQuantifyStrategyImpl, Image2DSett {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    
    //
    protected boolean isActive = false;
    
    // blank 
    protected SettingsImage2DBlankSubtraction blankQuantifier;
    // IS quantifier now here
    protected SettingsImage2DQuantifierIS internalQuantifier;
	
	
	public SettingsImage2DOperations() {
		super("SettingsImage2DOperations", "/Settings/operations/", "setOp"); 
		internalQuantifier = new SettingsImage2DQuantifierIS();
		blankQuantifier = new SettingsImage2DBlankSubtraction();
		resetAll();		
	} 
	@Override
	public void resetAll() {
		isActive = false;
		if(blankQuantifier!=null) 
			blankQuantifier.resetAll();
		if(internalQuantifier!=null)
			internalQuantifier.resetAll();
	}
	
	

	/**
	 * the magic is done here
	 */
	@Override
	public double calcIntensity(Image2D img,  int line, int dp, double intensity) {  
		// blank 
		if(blankQuantifier!=null)
			 intensity = blankQuantifier.calcIntensity(img, line, dp, intensity);
		// IS:
		SettingsImage2DQuantifierIS internalQ = getInternalQuantifier();
		if(internalQ!=null && internalQ.isActive())
			intensity = internalQ.calcIntensity(img, line, dp, intensity);
		// return intensity
		return intensity;
	}
	/**
	 * force blank or IS
	 * @param img
	 * @param line
	 * @param dp
	 * @param intensity
	 * @param blank
	 * @param IS
	 * @return
	 */
	public double calcIntensity(Image2D img,  int line, int dp, double intensity, boolean blank, boolean IS) {  
		// blank 
		if(blankQuantifier!=null && blank) {
			boolean tmp = blankQuantifier.isActive();
			blankQuantifier.setActive(true); 
			intensity = blankQuantifier.calcIntensity(img, line, dp, intensity);
			blankQuantifier.setActive(tmp);
		}
		// IS:
		SettingsImage2DQuantifierIS internalQ = getInternalQuantifier();
		if(internalQ!=null && IS) {
			boolean tmp = internalQ.isActive();
			internalQ.setActive(true); 
			intensity = internalQ.calcIntensity(img, line, dp, intensity);
			internalQ.setActive(tmp);
		}
		return intensity;
	}
	
	
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public SettingsImage2DQuantifierIS getInternalQuantifier() {
		return internalQuantifier;
	}
	public void setInternalQuantifier(SettingsImage2DQuantifierIS internalQuantifier) {
		this.internalQuantifier = internalQuantifier;
	}
	public SettingsImage2DBlankSubtraction getBlankQuantifier() {
		return blankQuantifier;
	}
	public void setBlankQuantifier(SettingsImage2DBlankSubtraction blankQuantifier) {
		this.blankQuantifier = blankQuantifier;
	}
	
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
	}
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
	}
	@Override
	public void setCurrentImage(Image2D img) {
		if(blankQuantifier!=null)
			blankQuantifier.getQSameImage().setImg(img);
	} 
	
}
