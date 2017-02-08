package net.rs.lamsi.massimager.Settings.visualization;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.massimager.Settings.Settings; 


public class SettingsPlotSpectraLabelGenerator extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //

	protected double minimumRelativeIntensityOfLabel;
	protected int minimumSpaceBetweenLabels;
	protected boolean showLabels, showCharge;
	

	public SettingsPlotSpectraLabelGenerator() {
		super("SettingsSpectraLabelGenerator", "/Settings/Visualization/", "setVisLabelSpec"); 
		resetAll();
	} 

	@Override
	public void resetAll() { 
		showCharge = true;
		showLabels = true;
		minimumSpaceBetweenLabels = 100;
		minimumRelativeIntensityOfLabel = 0.05;
	}
	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "showCharge", showCharge); 
		toXML(elParent, doc, "showLabels", showLabels); 
		toXML(elParent, doc, "minimumSpaceBetweenLabels", minimumSpaceBetweenLabels); 
		toXML(elParent, doc, "minimumRelativeIntensityOfLabel", minimumRelativeIntensityOfLabel); 
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("showCharge")) showCharge = booleanFromXML(nextElement); 
				else if(paramName.equals("showLabels"))showLabels = booleanFromXML(nextElement);  
				else if(paramName.equals("minimumSpaceBetweenLabels"))minimumSpaceBetweenLabels = intFromXML(nextElement);  
				else if(paramName.equals("minimumRelativeIntensityOfLabel"))minimumRelativeIntensityOfLabel = doubleFromXML(nextElement);  
			}
		}
	}

	public double getMinimumRelativeIntensityOfLabel() {
		return minimumRelativeIntensityOfLabel;
	}

	public void setMinimumRelativeIntensityOfLabel(
			double minimumRelativeIntensityOfLabel) {
		this.minimumRelativeIntensityOfLabel = minimumRelativeIntensityOfLabel;
	}

	public int getMinimumSpaceBetweenLabels() {
		return minimumSpaceBetweenLabels;
	}

	public void setMinimumSpaceBetweenLabels(int minimumSpaceBetweenLabels) {
		this.minimumSpaceBetweenLabels = minimumSpaceBetweenLabels;
	}

	public boolean isShowLabels() {
		return showLabels;
	}

	public void setShowLabels(boolean showLabels) {
		this.showLabels = showLabels;
	}

	public boolean isShowCharge() {
		return showCharge;
	}

	public void setShowCharge(boolean showCharge) {
		this.showCharge = showCharge;
	} 
	
	
}
