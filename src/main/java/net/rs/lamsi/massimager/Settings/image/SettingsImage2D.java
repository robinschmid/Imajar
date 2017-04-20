package net.rs.lamsi.massimager.Settings.image;

import java.util.HashMap;
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

public class SettingsImage2D extends SettingsContainerCollectable2D {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //

	public SettingsImage2D() {
		super("SettingsImage2D", "/Settings/Image2d/", "setImg2d"); 
		
		addSettings(SettingsPaintScale.createSettings(SettingsPaintScale.S_KARST_RAINBOW_INVERSE));
		addSettings(new SettingsGeneralImage());
		addSettings(new SettingsThemes());
		addSettings(new SettingsImage2DQuantifierLinear());
		addSettings(new SettingsImage2DOperations());
		addSettings(new SettingsZoom());
	} 


	// COntstruct 
	public SettingsImage2D(SettingsPaintScale settPaintScale) {  
		super("SettingsImage2D", "/Settings/Image2d/", "setImg2d"); 
		try { 
			addSettings((SettingsPaintScale) BinaryWriterReader.deepCopy(settPaintScale));
			addSettings(new SettingsGeneralImage());
			addSettings(new SettingsThemes());
			addSettings(new SettingsImage2DQuantifierLinear());
			addSettings(new SettingsImage2DOperations());
			addSettings(new SettingsZoom());
		} catch (Exception e) { 
			e.printStackTrace();
		}
	} 
	public SettingsImage2D(SettingsPaintScale settPaintScale, SettingsGeneralImage setImage) {  
		super("SettingsImage2D", "/Settings/Image2d/", "setImg2d"); 
		try {
			addSettings((SettingsPaintScale) BinaryWriterReader.deepCopy(settPaintScale));
			addSettings((SettingsGeneralImage) BinaryWriterReader.deepCopy(setImage));
			addSettings(new SettingsThemes());
			addSettings(new SettingsImage2DQuantifierLinear());
			addSettings(new SettingsImage2DOperations());
			addSettings(new SettingsZoom());
		} catch (Exception e) { 
			e.printStackTrace();
		}
	} 
	

	public void setCurrentImage(Image2D img) {
		if(getOperations()!=null)
			getOperations().setCurrentImage(img);
	}
	
	public SettingsPaintScale getSettPaintScale() {
		return (SettingsPaintScale) list.get(SettingsPaintScale.class);
	} 
	public SettingsGeneralImage getSettImage() {
		return (SettingsGeneralImage) list.get(SettingsGeneralImage.class);
	} 
	public SettingsThemes getSettTheme() {
		return (SettingsThemes) list.get(SettingsThemes.class);
	}

	// if something changes - change the averageI
	public SettingsImage2DQuantifier getQuantifier() {
		return (SettingsImage2DQuantifier) list.get(SettingsImage2DQuantifier.class);
	}
	public SettingsImage2DQuantifierIS getInternalQuantifierIS() {
		return getOperations().getInternalQuantifier();
	}
	public void setInternalQuantifierIS(SettingsImage2DQuantifierIS isQ) {
		getOperations().setInternalQuantifier(isQ);
	}
	public SettingsImage2DOperations getOperations() {
		return (SettingsImage2DOperations) list.get(SettingsImage2DOperations.class);
	} 

	public SettingsZoom getSettZoom() {
		return (SettingsZoom) getSettingsByClass(SettingsZoom.class);
	}
}
