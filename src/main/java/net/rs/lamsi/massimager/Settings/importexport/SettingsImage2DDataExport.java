package net.rs.lamsi.massimager.Settings.importexport;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.importexport.SettingsImageDataImportTxt.ModeData;

public class SettingsImage2DDataExport extends Settings { 
	// do not change the version!
    private static final long serialVersionUID = 1L;
    //
	public SettingsImage2DDataExport() {
		super("ImageDataExport","/Settings/Export/Image2D", "settExImg2DData"); 
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
	// use reflect, rotate and imaging mode
	private boolean useReflectRotate;
	
	public boolean isUseReflectRotate() {
		return useReflectRotate;
	}
	public void setUseReflectRotate(boolean useReflectRotate) {
		this.useReflectRotate = useReflectRotate;
	}

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
		useReflectRotate = true;
	}
	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "path", path); 
		toXML(elParent, doc, "filename", filename); 
		toXML(elParent, doc, "separation", separation); 
		toXML(elParent, doc, "fileFormat", fileFormat); 
		toXML(elParent, doc, "isCuttingDataToMin", isCuttingDataToMin); 
		toXML(elParent, doc, "exportsAllFiles", exportsAllFiles); 
		toXML(elParent, doc, "savesAllFilesToOneXLS", savesAllFilesToOneXLS); 
		toXML(elParent, doc, "isWriteTitleRow", isWriteTitleRow); 
		toXML(elParent, doc, "isWritingToClipboard", isWritingToClipboard); 
		toXML(elParent, doc, "mode", mode); 
		toXML(elParent, doc, "isExportRaw", isExportRaw); 
		toXML(elParent, doc, "useReflectRotate", useReflectRotate); 
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("path")) path = nextElement.getTextContent();
				else if(paramName.equals("filename")) filename = nextElement.getTextContent();
				else if(paramName.equals("separation")) separation = nextElement.getTextContent(); 
				else if(paramName.equals("fileFormat"))fileFormat = FileType.valueOf(nextElement.getTextContent());  
				else if(paramName.equals("mode"))mode = ModeData.valueOf(nextElement.getTextContent());  
				else if(paramName.equals("exportsAllFiles"))exportsAllFiles = booleanFromXML(nextElement);  
				else if(paramName.equals("savesAllFilesToOneXLS"))savesAllFilesToOneXLS = booleanFromXML(nextElement);  
				else if(paramName.equals("isExportRaw"))isExportRaw = booleanFromXML(nextElement);  
				else if(paramName.equals("isWriteTitleRow"))isWriteTitleRow = booleanFromXML(nextElement);  
				else if(paramName.equals("isWritingToClipboard"))isWritingToClipboard = booleanFromXML(nextElement);  
				else if(paramName.equals("isCuttingDataToMin"))isCuttingDataToMin = booleanFromXML(nextElement);
				else if(paramName.equals("useReflectRotate"))useReflectRotate = booleanFromXML(nextElement);    
			}
		}
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
