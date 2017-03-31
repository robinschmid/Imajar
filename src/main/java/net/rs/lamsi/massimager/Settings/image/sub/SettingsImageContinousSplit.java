package net.rs.lamsi.massimager.Settings.image.sub;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.massimager.MyFreeChart.themes.MyStandardChartTheme;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsGeneralImage.XUNIT;

public class SettingsImageContinousSplit extends Settings {
	// do not change the version!
	private static final long serialVersionUID = 1L;
	// 
	protected int splitAfterDP = 100;
	protected float splitAfterX = 10, startX=0;
	protected XUNIT splitMode = XUNIT.DP;



	public SettingsImageContinousSplit() {
		super("SettingsImageContinousSplit", "Settings/Image/Continous", "setImgCon");  
		resetAll();
	}

	public SettingsImageContinousSplit(int splitAfterDP) {
		this();
		resetAll();
		this.splitAfterDP = splitAfterDP;
	}


	public SettingsImageContinousSplit(float splitAfter, float splitStart, XUNIT splitUnit) {
		this();
		resetAll();
		this.splitAfterX = splitAfter;
		this.splitAfterDP = Math.round(splitAfter);
		this.startX = splitStart;
		splitMode = splitUnit;
	}

	@Override
	public void resetAll() { 
		splitAfterDP = 100;
		splitAfterX = 10;
		splitMode = XUNIT.DP;
		startX = 0;
	}
	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "splitAfterDP", splitAfterDP); 
		toXML(elParent, doc, "startX", startX); 
		toXML(elParent, doc, "splitAfterX", splitAfterX); 
		toXML(elParent, doc, "splitMode", splitMode); 
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("splitAfterDP")) splitAfterDP = intFromXML(nextElement); 
				else if(paramName.equals("splitAfterX"))splitAfterX = floatFromXML(nextElement);  
				else if(paramName.equals("startX"))startX = floatFromXML(nextElement);  
				else if(paramName.equals("splitMode"))splitMode = XUNIT.valueOf(nextElement.getTextContent());  
			}
		}
	}

	public int getSplitAfterDP() {
		return splitAfterDP;
	}


	public void setSplitAfterDP(int splitAfterDP) {
		this.splitAfterDP = splitAfterDP;
	}


	public float getSplitAfterX() {
		return splitAfterX;
	}


	public void setSplitAfterX(float splitAfterX) {
		this.splitAfterX = splitAfterX;
	}


	public XUNIT getSplitMode() {
		return splitMode;
	}


	public void setSplitMode(XUNIT splitMode) {
		this.splitMode = splitMode;
	} 

	public float getStartX() {
		return startX;
	} 
	public void setStartX(float startX) {
		this.startX = startX;
	}
}
