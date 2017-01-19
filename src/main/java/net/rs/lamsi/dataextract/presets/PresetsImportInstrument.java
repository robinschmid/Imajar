package net.rs.lamsi.dataextract.presets;

import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImportTxt;
import net.rs.lamsi.utils.useful.FileNameExtFilter;

public class PresetsImportInstrument extends SettingsImageDataImportTxt {  
	private static final long serialVersionUID = 1L;
	
	// name for import settings
	protected String name;
	

	public PresetsImportInstrument(String name, IMPORT modeImport, boolean isSearchingForMetaData, String sSeparation) { 
		super("/Settings/Presets/Import/", "importExtract", modeImport, isSearchingForMetaData, sSeparation, new FileNameExtFilter("", ""));   
		this.name = name;
	}

	public String getName() {
		return name;
	}
 
	public void setName(String name) {
		this.name = name;
	} 
}
