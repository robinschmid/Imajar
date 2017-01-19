package net.rs.lamsi.massimager.Settings.image;

import java.io.File;

import net.rs.lamsi.massimager.Settings.Settings;

public class SettingsImage2DDataExport extends Settings { 
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	public SettingsImage2DDataExport() {
		super("/Settings/Export/Image2D", "settExImg2DData"); 
		resetAll();
	} 
	/**
	 * export type for combobox
	 * @author vukmir69
	 *
	 */
	public static enum FileType {
		    XLSX("xlsx"),
		    TXT("txt"),
		    CSV("csv"),
		    VTK("vtk");

		    private final String text; 
		    private FileType(final String text) {
		        this.text = text;
		    } 
		    @Override
		    public String toString() {
		        return text;
		    } 
	}
	
	private String path;
	private String filename;
	private String separation;
	
	private FileType fileFormat;
	private boolean exportsAllFiles, savesAllFilesToOneXLS;
	private boolean writeTimeOnlyOnce, isExportRaw, isWriteTitleRow, isWriteXYZData, isWriteNoX, isWritingToClipboard;
	
	// new one
	// cuts the data to minum of line width
	private boolean isCuttingDataToMin = true;
	
	

	@Override
	public void resetAll() {
		path = "";
		filename = "";
		separation = ",";
		fileFormat = FileType.XLSX;
		exportsAllFiles = false;
		savesAllFilesToOneXLS = true;
		writeTimeOnlyOnce = true;
		isExportRaw = false;
		isWriteTitleRow = true;
		isWriteXYZData = false;
		isWriteNoX = false;
		isWritingToClipboard = false;
	}

	/**
	 * standards
	 * @param formatClipboard
	 * @param b
	 */
	public void setUpForDataOnly(boolean clipboard, boolean raw) {
		resetAll();
		separation = "\t";
		setWritingToClipboard(clipboard);
		isWriteNoX = true;
		isWriteTitleRow = false;
		isExportRaw = raw;
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
	public FileType getFileFormat() {
		return fileFormat;
	}
	public void setFileFormat(FileType fileFormat) {
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
	public boolean isWriteTimeOnlyOnce() {
		return writeTimeOnlyOnce;
	}
	public void setWriteTimeOnlyOnce(boolean writeTimeOnlyOnce) {
		this.writeTimeOnlyOnce = writeTimeOnlyOnce;
	} 

	public void setPath(String path) {
		this.path = path;
	}

	public void setIsExportRaw(boolean state) {
		isExportRaw = state;
	}
	public boolean isExportRaw() {
		// TODO Auto-generated method stub
		return isExportRaw;
	}

	public void setIsWriteTitleRow(boolean state) {
		isWriteTitleRow = state;
	}
	public boolean isWriteTitleRow() {
		// TODO Auto-generated method stub
		return isWriteTitleRow;
	}

	public void setIsWriteXYZData(boolean state) {
		isWriteXYZData = state;
	}
	public boolean isWriteXYZData() {
		// TODO Auto-generated method stub
		return isWriteXYZData;
	}

	public String getSeparation() {
		return separation;
	}

	public void setSeparation(String separation) {
		this.separation = separation;
	}
	public void setIsWriteNoX(boolean state) {
		isWriteNoX = state;
	}
	public boolean isWriteNoX() {
		// TODO Auto-generated method stub
		return isWriteNoX;
	}

	public boolean isWritingToClipboard() {
		return isWritingToClipboard;
	}

	public void setWritingToClipboard(boolean isWritingToClipboard) {
		this.isWritingToClipboard = isWritingToClipboard;
	}

	public boolean isCuttingDataToMin() {
		return isCuttingDataToMin;
	}

	public void setCuttingDataToMin(boolean isCuttingDataToMin) {
		this.isCuttingDataToMin = isCuttingDataToMin;
	}
}
