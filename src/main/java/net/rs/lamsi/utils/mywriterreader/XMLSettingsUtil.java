package net.rs.lamsi.utils.mywriterreader;

import org.w3c.dom.Element;

public class XMLSettingsUtil {

	
	
	
	
	
	
	
	public boolean loadBoolean(Element xmlElement) {
		String rangeString = xmlElement.getTextContent();
		if (rangeString.length() == 0)
			return false;
		return Boolean.valueOf(rangeString);
	}
}
