package net.rs.lamsi.massimager.Settings.preferences;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFileChooser;

import net.rs.lamsi.massimager.MyFileChooser.FileTypeFilter;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.utils.FileAndPathUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SettingsGeneralPreferences extends Settings {
	// do not change the version!
    private static final long serialVersionUID = 1L;
    
    //
    private final int HISTORY_SIZE = 10;
    // general pref file
    private final File generalPrefFile;
    // Icon settings
    private int iconWidth, iconHeight;
    private boolean generatesIcons = true;
    
    // last paths
    //private File fcOpen, fcImportPicture, fcImport, fcSave;

	// Filechooser
	private JFileChooser fcOpen = new JFileChooser();
	private JFileChooser fcImportPicture = new JFileChooser();
	private JFileChooser fcImport = new JFileChooser();
	private JFileChooser fcSave = new JFileChooser();
	private FileTypeFilter fileTFImage2D, fileTFtxt, fileTFtxtcsv, fileTFcsv, fileTFxls, fileTFxlsx;
	private FileTypeFilter filePicture;
    
    // save a history of image2d imports/exports
    private Vector<File> img2DHistory = new Vector<File>(HISTORY_SIZE);
    
	

	public SettingsGeneralPreferences() {
		super("GeneralPreferences", "/Settings/General/", "settPrefer");  
		generalPrefFile = new File(FileAndPathUtil.getPathOfJar(), getPathSettingsFile()+"preferences."+getFileEnding());
		resetAll();

		// init filechooser 
		// add Filter 
		fileTFImage2D = new FileTypeFilter("image2d", "Image format from this application");
		fcOpen.addChoosableFileFilter(fileTFImage2D);  
		fcOpen.setFileFilter(fileTFImage2D);
		fcOpen.setMultiSelectionEnabled(true);
		
		fcSave.addChoosableFileFilter(fileTFImage2D); 
		fcSave.setFileFilter(fileTFImage2D);
		fcSave.setMultiSelectionEnabled(false);

		String[] txtcsv = {"txt","csv"};
		fcImport.addChoosableFileFilter(fileTFtxtcsv = new FileTypeFilter(txtcsv, "Import text or csv file")); 
		fcImport.setFileFilter(fileTFtxtcsv);
		fcImport.addChoosableFileFilter(fileTFtxt = new FileTypeFilter("txt", "Import text file")); 
		fcImport.addChoosableFileFilter(fileTFcsv = new FileTypeFilter("csv", "Import csv file")); 
		fcImport.addChoosableFileFilter(fileTFxlsx = new FileTypeFilter("xlsx", "Import Excel file")); 
		fcImport.addChoosableFileFilter(fileTFxls = new FileTypeFilter("xls", "Import Excel file")); 
		fcImport.setMultiSelectionEnabled(true);
		fcImport.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		fcImportPicture.addChoosableFileFilter(filePicture = new FileTypeFilter(new String[]{"png", "jpg", "gif"}, "Import pictures"));
		fcImportPicture.setFileFilter(filePicture);
		fcImportPicture.setMultiSelectionEnabled(false);
		fcImportPicture.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		// import general pref
		try {
			loadFromXML(generalPrefFile);
		} catch (IOException e) {
			e.printStackTrace();
			ImageEditorWindow.log("ERROR: Cannot load preferences! "+e.getMessage(), LOG.ERROR);
		}
	}


	@Override
	public void resetAll() { 
		iconWidth = 60;
		iconHeight = 16;
		generatesIcons = true;
		img2DHistory.removeAllElements();
	}
	//##########################################################
	// xml input/output
	@Override
	public void appendSettingsValuesToXML(Element elParent, Document doc) {
		toXML(elParent, doc, "iconWidth", iconWidth); 
		toXML(elParent, doc, "iconHeight",iconHeight ); 
		toXML(elParent, doc, "generatesIcons", generatesIcons); 
		
		toXML(elParent, doc, "fcOpen", fcOpen.getCurrentDirectory().getAbsolutePath()); 
		toXML(elParent, doc, "fcImportPicture", fcImportPicture.getCurrentDirectory().getAbsolutePath()); 
		toXML(elParent, doc, "fcImport", fcImport.getCurrentDirectory().getAbsolutePath()); 
		toXML(elParent, doc, "fcSave", fcSave.getCurrentDirectory().getAbsolutePath()); 
		// image 2d import/export history
		for(int i=0; i<img2DHistory.size(); i++)
			toXML(elParent, doc, "img2DHistory_"+i, img2DHistory.get(i).getAbsolutePath()); 
			
	}

	@Override
	public void loadValuesFromXML(Element el, Document doc) {
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element nextElement = (Element) list.item(i);
				String paramName = nextElement.getNodeName();
				if(paramName.equals("iconWidth")) iconWidth = intFromXML(nextElement); 
				else if(paramName.equals("iconHeight"))iconHeight = intFromXML(nextElement);  
				else if(paramName.equals("generatesIcons"))generatesIcons = booleanFromXML(nextElement);  
				else if(paramName.equals("fcOpen"))fcOpen.setCurrentDirectory(new File(nextElement.getTextContent()));  
				else if(paramName.equals("fcImportPicture"))fcImportPicture.setCurrentDirectory(new File(nextElement.getTextContent()));  
				else if(paramName.equals("fcImport"))fcImport.setCurrentDirectory(new File(nextElement.getTextContent()));  
				else if(paramName.equals("fcSave"))fcSave.setCurrentDirectory(new File(nextElement.getTextContent()));  
				else if(paramName.startsWith("img2DHistory")) {
					img2DHistory.addElement(new File(nextElement.getTextContent()));
				}
			}
		}
	}
	
	/**
	 * adds the path of an image2d file to the history
	 * @param pathImg2d
	 */
	public void addImage2DImportExportPath(File pathImg2d) {
		addImage2DImportExportPath(pathImg2d, true);
	}
	/**
	 * adds the path of an image2d file to the history
	 * @param pathImg2d
	 */
	public void addImage2DImportExportPath(File pathImg2d, boolean saveChanges) {
		// already inserted?
		if(!img2DHistory.contains(pathImg2d)) {
			// remove
			if(img2DHistory.size()>=HISTORY_SIZE)
				img2DHistory.remove(img2DHistory.size()-1);
			img2DHistory.add(0, pathImg2d);
			if(saveChanges)
				saveChanges();
			// fire event
			fireChangeEvent();
		}
	}
	
	/**
	 * saves the changes to the general pref file
	 */
	public void saveChanges() {
		try {
			saveToXML(generalPrefFile);
		} catch (IOException e) {
			e.printStackTrace();
			ImageEditorWindow.log("ERROR: Cannot save preferences! "+e.getMessage(), LOG.ERROR);
		}
	}
	
	public int getIconWidth() {
		return iconWidth;
	}
	public void setIconWidth(int iconWidth) {
		this.iconWidth = iconWidth;
	}
	public int getIconHeight() {
		return iconHeight;
	}
	public void setIconHeight(int iconHeight) {
		this.iconHeight = iconHeight;
	}
	public boolean isGeneratesIcons() {
		return generatesIcons;
	}
	public void setGeneratesIcons(boolean generatesIcons) {
		this.generatesIcons = generatesIcons;
	}
	public JFileChooser getFcOpen() {
		return fcOpen;
	}
	public JFileChooser getFcImportPicture() {
		return fcImportPicture;
	}
	public JFileChooser getFcImport() {
		return fcImport;
	}
	public JFileChooser getFcSave() {
		return fcSave;
	}
	public FileTypeFilter getFileTFImage2D() {
		return fileTFImage2D;
	}
	public FileTypeFilter getFileTFtxt() {
		return fileTFtxt;
	}
	public FileTypeFilter getFileTFtxtcsv() {
		return fileTFtxtcsv;
	}
	public FileTypeFilter getFileTFcsv() {
		return fileTFcsv;
	}
	public FileTypeFilter getFileTFxls() {
		return fileTFxls;
	}
	public FileTypeFilter getFileTFxlsx() {
		return fileTFxlsx;
	}
	public FileTypeFilter getFilePicture() {
		return filePicture;
	}


	public Vector<File> getImg2DHistory() {
		return img2DHistory;
	}


	public void setImg2DHistory(Vector<File> img2dHistory) {
		img2DHistory = img2dHistory;
	}
	
}
