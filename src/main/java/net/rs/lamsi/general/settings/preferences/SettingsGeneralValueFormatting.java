package net.rs.lamsi.general.settings.preferences;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.general.settings.Settings;

public class SettingsGeneralValueFormatting extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	
	private int decimalsMZ, decimalsIntensity, decimalsRT;
	private boolean isShowingExponentIntensity;
	

	public SettingsGeneralValueFormatting() {
		super("SettingsGeneralValueFormatting", "/Settings/General/", "valform");  
		resetAll();
	}


	@Override
	public void resetAll() { 
		decimalsMZ = 4;
		decimalsIntensity = 1;
		decimalsRT = 2;
		isShowingExponentIntensity = true;
	}

	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "decimalsMZ", decimalsMZ); 
		toXML(elParent, doc, "decimalsIntensity", decimalsIntensity); 
		toXML(elParent, doc, "decimalsRT", decimalsRT); 
		toXML(elParent, doc, "isShowingExponentIntensity", isShowingExponentIntensity); 
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("decimalsMZ")) decimalsMZ = intFromXML(nextElement); 
				else if(paramName.equals("decimalsIntensity"))decimalsIntensity = intFromXML(nextElement);  
				else if(paramName.equals("decimalsRT"))decimalsRT = intFromXML(nextElement);  
				else if(paramName.equals("isShowingExponentIntensity"))isShowingExponentIntensity = booleanFromXML(nextElement);  
			}
		}
	}

	public int getDecimalsMZ() {
		return decimalsMZ;
	}


	public void setDecimalsMZ(int decimalsMZ) {
		this.decimalsMZ = decimalsMZ;
	}


	public int getDecimalsIntensity() {
		return decimalsIntensity;
	}


	public void setDecimalsIntensity(int decimalsIntensity) {
		this.decimalsIntensity = decimalsIntensity;
	}


	public int getDecimalsRT() {
		return decimalsRT;
	}


	public void setDecimalsRT(int decimalsRT) {
		this.decimalsRT = decimalsRT;
	}


	public boolean isShowingExponentIntensity() {
		return isShowingExponentIntensity;
	}


	public void setShowingExponentIntensity(boolean isShowingExponentIntensity) {
		this.isShowingExponentIntensity = isShowingExponentIntensity;
	}


	public NumberFormat getMZFormat() {   
		return getNumberFormat(decimalsMZ, false);
	}

	public NumberFormat getIntensityFormat() { 
		return getNumberFormat(decimalsIntensity, isShowingExponentIntensity());
	}

	public NumberFormat getRTFormat() {   
		return getNumberFormat(decimalsRT, false);
	}
	
	public static NumberFormat getNumberFormat(int decimals, boolean exponent) { 
		if(exponent) {
			DecimalFormat exponentFormat = new DecimalFormat("0.##E00;(0.##E00)");
			exponentFormat.setMinimumFractionDigits(decimals);
			exponentFormat.setMaximumFractionDigits(decimals);
			return exponentFormat;
		}
		else {
			DecimalFormat format = new DecimalFormat("#,###.######;(#,###.######)");
			format.setMinimumFractionDigits(decimals);
			format.setMaximumFractionDigits(decimals);
			return format;
		}
	}
	
}
