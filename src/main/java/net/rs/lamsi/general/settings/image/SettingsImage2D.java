package net.rs.lamsi.general.settings.image;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.image.filter.SettingsCropAndShift;
import net.rs.lamsi.general.settings.image.operations.SettingsImage2DOperations;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifier;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifierIS;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifierLinear;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.general.settings.image.sub.SettingsZoom;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsThemesContainer;
import net.rs.lamsi.general.settings.interf.Image2DSett;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;
import net.rs.lamsi.utils.useful.DebugStopWatch;

public class SettingsImage2D extends SettingsContainerCollectable2D implements Image2DSett {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    
    protected transient Image2D currentImg = null;
    //

	public SettingsImage2D() {
		super("SettingsImage2D", "/Settings/Image2d/", "setImg2d"); 
		
		addSettings(SettingsPaintScale.createSettings(SettingsPaintScale.S_KARST_RAINBOW_INVERSE));
		addSettings(new SettingsGeneralImage());
		addSettings(new SettingsThemesContainer(true));
		addSettings(new SettingsImage2DQuantifierLinear());
		addSettings(new SettingsImage2DOperations());
		addSettings(new SettingsZoom());
		addSettings(new SettingsSelections());
		addSettings(new SettingsCropAndShift());
	} 


	// COntstruct 
	public SettingsImage2D(SettingsPaintScale settPaintScale) {  
		super("SettingsImage2D", "/Settings/Image2d/", "setImg2d"); 
		try { 
			addSettings((SettingsPaintScale) BinaryWriterReader.deepCopy(settPaintScale));
			addSettings(new SettingsGeneralImage());
			addSettings(new SettingsThemesContainer(true));
			addSettings(new SettingsImage2DQuantifierLinear());
			addSettings(new SettingsImage2DOperations());
			addSettings(new SettingsZoom());
			addSettings(new SettingsSelections());
			addSettings(new SettingsCropAndShift());
		} catch (Exception e) { 
			e.printStackTrace();
		}
	} 
	public SettingsImage2D(SettingsPaintScale settPaintScale, SettingsGeneralImage setImage) {  
		super("SettingsImage2D", "/Settings/Image2d/", "setImg2d"); 
		try {
			addSettings((SettingsPaintScale) BinaryWriterReader.deepCopy(settPaintScale));
			addSettings((SettingsGeneralImage) BinaryWriterReader.deepCopy(setImage));
			addSettings(new SettingsThemesContainer(true));
			addSettings(new SettingsImage2DQuantifierLinear());
			addSettings(new SettingsImage2DOperations());
			addSettings(new SettingsZoom());
			addSettings(new SettingsSelections());
			addSettings(new SettingsCropAndShift());
		} catch (Exception e) { 
			e.printStackTrace();
		}
	} 
	
	@Override
	public void applyToImage(Image2D img) throws Exception {
		SettingsGeneralImage sg = img.getSettings().getSettImage();
		// dont copy name
		String name = img.getTitle();
		String shortTitle = sg.getShortTitle();
		String path = img.getSettings().getSettImage().getRAWFilepath();

		DebugStopWatch t = new DebugStopWatch();
		super.applyToImage(img);
		t.stopAndLOG(" copy image2d settings");
		
		// reset to old short title only if not the same title
		if(!name.equals(img.getTitle()))
			img.getSettings().getSettImage().setShortTitle(shortTitle);

		// reset to old title
		img.getSettings().getSettImage().setTitle(name);
		img.getSettings().getSettImage().setRAWFilepath(path);
	}

	@Override
	public void setCurrentImage(Image2D img) {
		this.currentImg = img;
		for(Settings s:list.values()) {
			if(Image2DSett.class.isInstance(s)) 
				((Image2DSett)s).setCurrentImage(img);
		}
	}
	
	@Override
	public boolean replaceSettings(Settings sett) {
		// set currentimg
		if(Image2DSett.class.isInstance(sett)) 
			((Image2DSett)sett).setCurrentImage(currentImg);
		// replace
		return super.replaceSettings(sett);
	}
	
	
	// get settings directly
	public SettingsPaintScale getSettPaintScale() {
		return (SettingsPaintScale) list.get(SettingsPaintScale.class);
	} 
	public SettingsGeneralImage getSettImage() {
		return (SettingsGeneralImage) list.get(SettingsGeneralImage.class);
	} 
	public SettingsThemesContainer getSettTheme() {
		return (SettingsThemesContainer) list.get(SettingsThemesContainer.class);
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
	public SettingsSelections getSettSelections() {
		return (SettingsSelections) getSettingsByClass(SettingsSelections.class);
	}


	//###########################################################
	// XML
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
	}
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
	}
	}
