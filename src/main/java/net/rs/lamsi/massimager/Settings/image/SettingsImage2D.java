package net.rs.lamsi.massimager.Settings.image;

import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetContinuousMD;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.SettingsContainerSettings;
import net.rs.lamsi.massimager.Settings.image.operations.SettingsImage2DOperations;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifier;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifierIS;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifierLinear;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsImageContinousSplit;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsZoom;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsThemes;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SettingsImage2D extends SettingsContainerSettings {
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
		//	
		list.addElement(settPaintScale);
		list.addElement(settImage);
		list.addElement(settTheme);
		list.addElement(quantifier);
		list.addElement(operations);
		list.addElement(settZoom);
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
			
			//	
			list.addElement(this.settPaintScale);
			list.addElement(settImage);
			list.addElement(settTheme);
			list.addElement(quantifier);
			list.addElement(operations);
			list.addElement(settZoom);
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
			//

			//	
			list.addElement(this.settPaintScale);
			list.addElement(this.settImage);
			list.addElement(settTheme);
			list.addElement(quantifier);
			list.addElement(operations);
			list.addElement(settZoom);
		} catch (Exception e) { 
			e.printStackTrace();
		}
	} 
	

	public void setCurrentImage(Image2D img) {
		if(operations!=null)
			operations.setCurrentImage(img);
	}
	
	public SettingsPaintScale getSettPaintScale() {
		return settPaintScale;
	} 
	public void setSettPaintScale(SettingsPaintScale settPaintScale) {
		list.remove(this.settPaintScale);
		this.settPaintScale = settPaintScale;
		list.add(settPaintScale);
	} 
	public SettingsGeneralImage getSettImage() {
		return settImage;
	} 
	public void setSettImage(SettingsGeneralImage settImgLaser) {
		list.remove(this.settImage);
		this.settImage = settImgLaser;
		list.add(settImage);
	} 
	public SettingsThemes getSettTheme() {
		return settTheme;
	}
	public void setSettTheme(SettingsThemes settTheme) {
		list.remove(this.settTheme);
		this.settTheme = settTheme;
		list.add(settTheme);
	}

	// if something changes - change the averageI
	public SettingsImage2DQuantifier getQuantifier() {
		return quantifier;
	}
	public void setQuantifier(SettingsImage2DQuantifier quantifier) {
		list.remove(this.quantifier);
		this.quantifier = quantifier;
		list.add(quantifier);
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
		list.remove(this.operations);
		this.operations = operations;
		operations.getBlankQuantifier().getQSameImage().setImg(img);
		list.add(operations);
	}


	public SettingsZoom getSettZoom() {
		return settZoom;
	}

	public void setSettZoom(SettingsZoom settZoom) {
		list.remove(this.settZoom);
		this.settZoom = settZoom;
		list.add(settZoom);
	}

}
