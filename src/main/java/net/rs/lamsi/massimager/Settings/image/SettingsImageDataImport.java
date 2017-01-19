package net.rs.lamsi.massimager.Settings.image;

import net.rs.lamsi.massimager.Settings.Settings;

public class SettingsImageDataImport extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //

	  

	protected boolean isSearchingForMetaData = true;
	
 
	public SettingsImageDataImport(String path, String fileEnding, boolean isSearchingForMetaData) {
		super(path, fileEnding); 
		this.isSearchingForMetaData = isSearchingForMetaData;
	}


	@Override
	public void resetAll() { 
		isSearchingForMetaData = true;
	}


	public boolean isSearchingForMetaData() {
		return isSearchingForMetaData;
	}


	public void setSearchingForMetaData(boolean isSearchingForMetaData) {
		this.isSearchingForMetaData = isSearchingForMetaData;
	}


}
