package net.rs.lamsi.massimager.Settings.image;

import net.rs.lamsi.utils.useful.FileNameExtFilter;

public class SettingsImageDataImportTxt extends SettingsImageDataImport {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //

 
	public static enum IMPORT {
		MULTIPLE_FILES_LINES_TXT_CSV, ONE_FILE_2D_INTENSITY, CONTINOUS_DATA_TXT_CSV, PRESETS_THERMO_MP17, PRESETS_THERMO_NEPTUNE;
	} 
	
	protected String sSeparation = ",";
	protected IMPORT modeImport = IMPORT.MULTIPLE_FILES_LINES_TXT_CSV;
	protected FileNameExtFilter filter;
	protected boolean isFilesInSeparateFolders = false;
	
 
	public IMPORT getModeImport() {
		return modeImport;
	}


	public void setModeImport(IMPORT modeImport) {
		this.modeImport = modeImport;
	}

	public SettingsImageDataImportTxt(String path, String fileEnding, IMPORT modeImport, boolean isSearchingForMetaData, String sSeparation, FileNameExtFilter filter) {
		super(path, fileEnding, isSearchingForMetaData);  
		this.sSeparation = sSeparation;
		this.modeImport = modeImport;
		this.filter = filter;
	}
	public SettingsImageDataImportTxt(IMPORT modeImport, boolean isSearchingForMetaData, String sSeparation, FileNameExtFilter filter, boolean isFilesInSeparateFolders) {
		super("/Settings/Import/", "txt2img", isSearchingForMetaData);  
		this.sSeparation = sSeparation;
		this.modeImport = modeImport;
		this.filter = filter;
		this.isFilesInSeparateFolders = isFilesInSeparateFolders;
	}
	public SettingsImageDataImportTxt(IMPORT modeImport, boolean isSearchingForMetaData, String sSeparation, boolean isFilesInSeparateFolders) {
		this(modeImport, isSearchingForMetaData, sSeparation, new FileNameExtFilter("", ""), isFilesInSeparateFolders);
	}


	@Override
	public void resetAll() { 
		isSearchingForMetaData = true;
		sSeparation = ",";
	}


	public String getSeparation() {
		return sSeparation;
	}


	public void setSeperation(String sSeperation) {
		this.sSeparation = sSeperation;
	}


	public FileNameExtFilter getFilter() {
		return filter;
	}


	public void setFilter(FileNameExtFilter filter) {
		this.filter = filter;
	}


	public boolean isFilesInSeparateFolders() {
		return isFilesInSeparateFolders;
	}


	public void setFilesInSeparateFolders(boolean isFilesInSeparateFolders) {
		this.isFilesInSeparateFolders = isFilesInSeparateFolders;
	}


}
