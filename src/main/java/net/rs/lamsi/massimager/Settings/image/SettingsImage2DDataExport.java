package net.rs.lamsi.massimager.Settings.image;

import java.io.File;

import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImportTxt.ModeData;

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
	private boolean isExportRaw, isWriteTitleRow, isWritingToClipboard;
	
	private ModeData mode = ModeData.X_MATRIX_STANDARD;
	
	// new one
	// cuts the data to minum of line width
	private boolean isCuttingDataToMin = true;
	
	

	@Override
	public void resetAll() {
		path = "";
		filename = "";
		separation = ",";
		fileFormat = FileType.CSV;
		exportsAllFiles = false;
		savesAllFilesToOneXLS = true;
		isExportRaw = false;
		isWriteTitleRow = true;
		isWritingToClipboard = false;
		mode = ModeData.X_MATRIX_STANDARD;
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


	public String getSeparation() {
		return separation;
	}

	public void setSeparation(String separation) {
		this.separation = separation;
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

	public ModeData getMode() {
		return mode;
	}

	public void setMode(ModeData mode) {
		this.mode = mode;
	}
}
