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

public class SettingsCollectable2DPlaceHolder extends SettingsContainerCollectable2D {
	// do not change the version!
	private static final long serialVersionUID = 1L;
	
	private String title, group, project;

	public SettingsCollectable2DPlaceHolder(String title, String group, String project) {  
		super("SettingsCollectable2DPlaceHolder", "/Settings/Image2dLink/", "setImg2dLink"); 
		this.title = title;
		this.group = group; 
		this.project = project;
	} 



	//###########################################################
	// XML
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
	}
	@Override
	public void loadValuesFromXML(Element el, Document doc) {
	}



	public String getTitle() {
		return title;
	}



	public String getGroup() {
		return group;
	}



	public String getProject() {
		return project;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public void setGroup(String group) {
		this.group = group;
	}



	public void setProject(String project) {
		this.project = project;
	}
}
