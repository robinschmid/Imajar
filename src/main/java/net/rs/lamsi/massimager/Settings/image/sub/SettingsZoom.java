package net.rs.lamsi.massimager.Settings.image.sub;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsGeneralImage.IMAGING_MODE;

import org.jfree.data.Range;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class SettingsZoom extends Settings {
	// do not change the version!
	private static final long serialVersionUID = 1L;
	//
	private Range xrange, yrange;
	

	public SettingsZoom(String path, String fileEnding) {
		super("SettingsZoom", path, fileEnding); 
	}
	public SettingsZoom() {
		super("SettingsZoom", "/Settings/GeneralImage/", "setZoom"); 
	} 

	@Override
	public void resetAll() {  
		xrange = null;
		yrange = null;
	}


	public void setAll(Range xrange, Range yrange) { 
		this.xrange = xrange;
		this.yrange = yrange;
	}


	@Override
	public void applyToHeatMap(Heatmap heat) {
		if(xrange!=null)
			heat.getPlot().getDomainAxis().setRange(xrange);

		if(yrange!=null)
			heat.getPlot().getRangeAxis().setRange(yrange);
	}

	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "xrange.lower", xrange.getLowerBound()); 
		toXML(elParent, doc, "xrange.upper", xrange.getUpperBound()); 
		toXML(elParent, doc, "yrange.lower", yrange.getLowerBound()); 
		toXML(elParent, doc, "yrange.upper", yrange.getUpperBound()); 
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
				if(paramName.equals("xrange.lower")) xlower = doubleFromXML(nextElement);
				else if(paramName.equals("xrange.upper"))xu = doubleFromXML(nextElement);
				else if(paramName.equals("yrange.lower"))ylower= doubleFromXML(nextElement);
				else if(paramName.equals("yrange.upper")) yu= doubleFromXML(nextElement);  
			}
		}

		if(!Double.isNaN(xlower))
			xrange=new Range(xlower, xu);
		if(!Double.isNaN(ylower))
			yrange=new Range(ylower, yu);
	}
	
	
	public Range getXrange() {
		return xrange;
	}
	public void setXrange(Range xrange) {
		this.xrange = xrange;
	}
	public Range getYrange() {
		return yrange;
	}
	public void setYrange(Range yrange) {
		this.yrange = yrange;
	}
}
