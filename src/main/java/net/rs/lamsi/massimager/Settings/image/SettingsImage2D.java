package net.rs.lamsi.massimager.Settings.image;

import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.image.operations.SettingsImage2DOperations;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifier;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifierIS;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifierLinear;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsZoom;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsThemes;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SettingsImage2D extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
    
    // paint scale
	protected SettingsPaintScale settPaintScale;
	// LaserVelocity and spot size 
	protected SettingsGeneralImage settImage;
	// theme settings
	protected SettingsThemes settTheme; 
	protected SettingsImage2DQuantifier quantifier;
	// blank subtraction and internal standard
	protected SettingsImage2DOperations operations;
	
	protected SettingsZoom settZoom;
	

	public SettingsImage2D() {
		super("SettingsImage2D", "/Settings/Image2d/", "setImg2d"); 
		settPaintScale = SettingsPaintScale.createSettings(SettingsPaintScale.S_KARST_RAINBOW_INVERSE);
		settPaintScale.resetAll();
		settImage = new SettingsGeneralImage();
		settImage.resetAll(); 
		// standard theme
		this.settTheme = new SettingsThemes();  
		//
		this.quantifier = new SettingsImage2DQuantifierLinear();
		this.operations = new SettingsImage2DOperations();
		this.settZoom = new SettingsZoom();
	} 


	// COntstruct 
	public SettingsImage2D(SettingsPaintScale settPaintScale) {  
		super("SettingsImage2D", "/Settings/Image2d/", "setImg2d"); 
		try { 
			this.settPaintScale = (SettingsPaintScale) BinaryWriterReader.deepCopy(settPaintScale); 
			// standard theme
			this.settTheme = new SettingsThemes();
			this.quantifier = new SettingsImage2DQuantifierLinear();
			this.operations = new SettingsImage2DOperations();
			this.settZoom = new SettingsZoom();
		} catch (Exception e) { 
			e.printStackTrace();
		}
	} 
	public SettingsImage2D(SettingsPaintScale settPaintScale, SettingsGeneralImage setImage) {  
		super("SettingsImage2D", "/Settings/Image2d/", "setImg2d"); 
		try {
			settImage = (SettingsGeneralImage) BinaryWriterReader.deepCopy(setImage);
			this.settPaintScale = (SettingsPaintScale) BinaryWriterReader.deepCopy(settPaintScale); 
			// standard theme
			this.settTheme = new SettingsThemes();
			this.quantifier = new SettingsImage2DQuantifierLinear();
			this.operations = new SettingsImage2DOperations();
			this.settZoom = new SettingsZoom();
		} catch (Exception e) { 
			e.printStackTrace();
		}
	} 
	

	public void setCurrentImage(Image2D img) {
		if(operations!=null)
			operations.setCurrentImage(img);
	}
	
	@Override
	public void applyToHeatMap(Heatmap heat) {
		if(settPaintScale!=null)
			settPaintScale.applyToHeatMap(heat);
		if(settImage!=null)
			settImage.applyToHeatMap(heat);
		if(settTheme!=null)
			settTheme.applyToHeatMap(heat);
		if(quantifier!=null)
			quantifier.applyToHeatMap(heat);
		if(operations!=null)
			operations.applyToHeatMap(heat);
		if(settZoom!=null)
			settZoom.applyToHeatMap(heat);
	}

	@Override
	public void resetAll() { 
		if(settPaintScale!=null)
			settPaintScale.resetAll();
		if(settImage!=null)
			settImage.resetAll();
		if(settTheme!=null)
			settTheme.resetAll();
		if(quantifier!=null)
			quantifier.resetAll();
		if(operations!=null)
			operations.resetAll();
		if(settZoom!=null)
			settZoom.resetAll();
	}
	



	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		if(settPaintScale!=null)
			settPaintScale.appendSettingsToXML(elParent, doc);
		if(settImage!=null)
			settImage.appendSettingsToXML(elParent, doc);
		if(settTheme!=null)
			settTheme.appendSettingsToXML(elParent, doc);
		if(quantifier!=null)
			quantifier.appendSettingsToXML(elParent, doc);
		if(operations!=null)
			operations.appendSettingsToXML(elParent, doc);
		if(settZoom!=null)
			settZoom.appendSettingsToXML(elParent, doc);
		
	}
	
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals(settPaintScale.getDescription())) 
					settPaintScale.loadValuesFromXML(nextElement, doc);
				else if(paramName.equals(settImage.getDescription())) 
					settImage.loadValuesFromXML(nextElement, doc);
				else if(paramName.equals(settTheme.getDescription())) 
					settTheme.loadValuesFromXML(nextElement, doc);
				else if(quantifier!=null && paramName.equals(quantifier.getDescription())) 
					quantifier.loadValuesFromXML(nextElement, doc);
				else if(operations!=null && paramName.equals(operations.getDescription())) 
					operations.loadValuesFromXML(nextElement, doc);
				else if(settZoom!=null && paramName.equals(settZoom.getDescription())) 
					settZoom.loadValuesFromXML(nextElement, doc);
			}
		}
	}
	

	public SettingsPaintScale getSettPaintScale() {
		return settPaintScale;
	} 
	public void setSettPaintScale(SettingsPaintScale settPaintScale) {
		this.settPaintScale = settPaintScale;
	} 
	public SettingsGeneralImage getSettImage() {
		return settImage;
	} 
	public void setSettImage(SettingsGeneralImage settImgLaser) {
		this.settImage = settImgLaser;
	} 
	public SettingsThemes getSettTheme() {
		return settTheme;
	}
	public void setSettTheme(SettingsThemes settTheme) {
		this.settTheme = settTheme;
	}

	// if something changes - change the averageI
	public SettingsImage2DQuantifier getQuantifier() {
		return quantifier;
	}
	public void setQuantifier(SettingsImage2DQuantifier quantifier) {
		this.quantifier = quantifier;
	}
	public SettingsImage2DQuantifierIS getInternalQuantifierIS() {
		return getOperations().getInternalQuantifier();
	}
	public void setInternalQuantifierIS(SettingsImage2DQuantifierIS isQ) {
		getOperations().setInternalQuantifier(isQ);
	}
	public SettingsImage2DOperations getOperations() {
		return operations;
	}
	public void setOperations(SettingsImage2DOperations operations, Image2D img) {
		this.operations = operations;
		operations.getBlankQuantifier().getQSameImage().setImg(img);
	}


	public SettingsZoom getSettZoom() {
		return settZoom;
	}


	public void setSettZoom(SettingsZoom settZoom) {
		this.settZoom = settZoom;
	}

}
