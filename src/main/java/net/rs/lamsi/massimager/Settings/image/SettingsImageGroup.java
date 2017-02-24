package net.rs.lamsi.massimager.Settings.image;

import java.io.File;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.image.operations.SettingsImage2DOperations;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifier;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifierIS;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifierLinear;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsAlphaMap;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsThemes;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale.ValueMode;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;

public class SettingsImageGroup extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	protected File pathBGImage = null;
    
    // paint scale
	protected SettingsAlphaMap settAlphaMap;

	// constructors
	public SettingsImageGroup() {
		super("SettingsImageGroup", "/Settings/Image2d/", "setImgGroup"); 
		settAlphaMap = new SettingsAlphaMap();
	} 

	@Override
	public void resetAll() { 
		pathBGImage = null;
		
		if(settAlphaMap!=null)
			settAlphaMap.resetAll();
	}

	// xml
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "pathBGImage", pathBGImage); 
		// other settings
		if(settAlphaMap!=null)
			settAlphaMap.appendSettingsToXML(elParent, doc);
	}
	
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				// import settings
				if(paramName.equals("pathBGImage")) 
					pathBGImage = new File(nextElement.getTextContent());
				// other settings
				else if(paramName.equals(settAlphaMap.getDescription())) 
					settAlphaMap.loadValuesFromXML(nextElement, doc);
			}
		}
	}
	
	// getters and setters
	public SettingsAlphaMap getSettAlphaMap() {
		return settAlphaMap;
	}
	public void setSettAlphaMap(SettingsAlphaMap settAlphaMap) {
		this.settAlphaMap = settAlphaMap;
	}
	public File getPathBGImage() {
		return pathBGImage;
	}
	public void setPathBGImage(File pathBGImage) {
		this.pathBGImage = pathBGImage;
	}
}
