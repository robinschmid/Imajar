package net.rs.lamsi.general.settings;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SettingsDataSaver extends Settings { 
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	public SettingsDataSaver(String path, String end) {
		super("SettingsDataSaver", path, end); 
	}
	//
	public static final int FORMAT_XLS = 0, FORMAT_TXT = 1, FORMAT_CLIPBOARD = 2;
	
	private String path;
	private String filename;
	
	private int fileFormat, currentMode;
	private boolean exportsAllFiles, savesAllFilesToOneXLS, exportTIC, exportSpectrum, exportEIC;
	private boolean allMZInSeperateFiles, writeTimeOnlyOnce, selectedMZOnly;
	
	// OES Spezifisch
	private boolean usesElementLineAsSheet;
	

	@Override
	public void resetAll() {
		
	}
	


	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		
	}
	
	// Getter and Setter
	public boolean isSelectedMZOnly() {
		return selectedMZOnly;
	}
	public void setSelectedMZOnly(boolean selectedMZOnly) {
		this.selectedMZOnly = selectedMZOnly;
	}
	public File getPath() {
		return new File(path);
	}
	public void setPath(File path) {
		this.path = path.getPath();
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getFileFormat() {
		return fileFormat;
	}
	public void setFileFormat(int fileFormat) {
		this.fileFormat = fileFormat;
	}
	public boolean isExportsAllFiles() {
		return exportsAllFiles;
	}
	public void setExportsAllFiles(boolean exportsAllFiles) {
		this.exportsAllFiles = exportsAllFiles;
	}
	public boolean isSavesAllFilesToOneXLS() {
		return savesAllFilesToOneXLS;
	}
	public void setSavesAllFilesToOneXLS(boolean savesAllFilesToOneXLS) {
		this.savesAllFilesToOneXLS = savesAllFilesToOneXLS;
	}
	public boolean isExportTIC() {
		return exportTIC;
	}
	public void setExportTIC(boolean exportTIC) {
		this.exportTIC = exportTIC;
	}
	public boolean isExportSpectrum() {
		return exportSpectrum;
	}
	public void setExportSpectrum(boolean exportSpectrum) {
		this.exportSpectrum = exportSpectrum;
	}
	public boolean isExportEIC() {
		return exportEIC;
	}
	public void setExportEIC(boolean exportEIC) {
		this.exportEIC = exportEIC;
	}
	public boolean isAllMZInSeperateFiles() {
		return allMZInSeperateFiles;
	}
	public void setAllMZInSeperateFiles(boolean allMZInSeperateFiles) {
		this.allMZInSeperateFiles = allMZInSeperateFiles;
	}
	public boolean isWriteTimeOnlyOnce() {
		return writeTimeOnlyOnce;
	}
	public void setWriteTimeOnlyOnce(boolean writeTimeOnlyOnce) {
		this.writeTimeOnlyOnce = writeTimeOnlyOnce;
	} 

	public void setPath(String path) {
		this.path = path;
	}

	public int getCurrentMode() {
		return currentMode;
	}

	public void setCurrentMode(int currentMode) {
		this.currentMode = currentMode;
	}

	public boolean isUsesElementLineAsSheet() {
		return usesElementLineAsSheet;
	}

	public void setUsesElementLineAsSheet(boolean usesElementLineAsSheet) {
		this.usesElementLineAsSheet = usesElementLineAsSheet;
	}
	
}
