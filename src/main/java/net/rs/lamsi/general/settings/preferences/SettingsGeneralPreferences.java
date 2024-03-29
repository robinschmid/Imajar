package net.rs.lamsi.general.settings.preferences;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JFileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.myfilechooser.FileTypeFilter;

public class SettingsGeneralPreferences extends Settings {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  private final Logger logger = LoggerFactory.getLogger(getClass());

  //
  private final int HISTORY_SIZE = 10;
  // general pref file
  private final File generalPrefFile;
  // Icon settings
  private int iconWidth, iconHeight;
  private boolean generatesIcons = true;

  // last paths
  // private File fcOpen, fcImportPicture, fcImport, fcSave;

  // Filechooser
  private final JFileChooser fcImportImzML = new JFileChooser();
  private final JFileChooser fcOpen = new JFileChooser();
  private final JFileChooser fcImportPicture = new JFileChooser();
  private final JFileChooser fcImport = new JFileChooser();
  private final JFileChooser fcSave = new JFileChooser();
  private final JFileChooser fcExportPicture = new JFileChooser();
  private final FileTypeFilter fileTFImage2D, filePictureExport, fileTFProject, fileTFtxt,
      fileTFtxtcsv, fileTFcsv, fileTFxls, fileTFxlsx, fileTFimzML;
  private final FileTypeFilter filePicture;

  // save a history of image2d imports/exports
  private Vector<File> img2DHistory = new Vector<File>(HISTORY_SIZE);
  private Vector<File> imzmlListHistory = new Vector<File>();



  public SettingsGeneralPreferences() {
    super("GeneralPreferences", "/Settings/General/", "settPrefer");
    generalPrefFile = new File(FileAndPathUtil.getPathOfJar(),
        getPathSettingsFile() + "preferences." + getFileEnding());
    resetAll();

    // init filechooser
    // add Filter
    fileTFImage2D = new FileTypeFilter("image2d", "Image format of this application");
    fileTFProject = new FileTypeFilter("img2dproject", "Project format of this application");
    fcOpen.addChoosableFileFilter(fileTFImage2D);
    fcOpen.addChoosableFileFilter(fileTFProject);
    fcOpen.setFileFilter(fileTFProject);
    fcOpen.setMultiSelectionEnabled(true);

    fcSave.addChoosableFileFilter(fileTFImage2D);
    fcSave.addChoosableFileFilter(fileTFProject);
    fcSave.setFileFilter(fileTFProject);
    fcSave.setMultiSelectionEnabled(false);

    getFcImportImzML()
        .addChoosableFileFilter(fileTFimzML = new FileTypeFilter("imzML", "Import imzML file"));
    getFcImportImzML().setMultiSelectionEnabled(true);
    getFcImportImzML().setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

    String[] txtcsv = {"txt", "csv"};
    fcImport.addChoosableFileFilter(
        fileTFtxtcsv = new FileTypeFilter(txtcsv, "Import text or csv file"));
    fcImport.setFileFilter(fileTFtxtcsv);
    fcImport.addChoosableFileFilter(fileTFtxt = new FileTypeFilter("txt", "Import text file"));
    fcImport.addChoosableFileFilter(fileTFcsv = new FileTypeFilter("csv", "Import csv file"));
    fcImport.addChoosableFileFilter(fileTFxlsx = new FileTypeFilter("xlsx", "Import Excel file"));
    fcImport.addChoosableFileFilter(fileTFxls = new FileTypeFilter("xls", "Import Excel file"));
    fcImport.setMultiSelectionEnabled(true);
    fcImport.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

    fcImportPicture.addChoosableFileFilter(
        filePicture = new FileTypeFilter(new String[] {"png", "jpg", "gif"}, "Import pictures"));
    fcImportPicture.setFileFilter(filePicture);
    fcImportPicture.setMultiSelectionEnabled(false);
    fcImportPicture.setFileSelectionMode(JFileChooser.FILES_ONLY);


    fcExportPicture.addChoosableFileFilter(filePictureExport = new FileTypeFilter(
        new String[] {"png", "jpg", "gif", "pdf", "emf", "eps", "svg"}, "Export images"));
    fcExportPicture.setFileFilter(filePictureExport);
    fcExportPicture.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

    // import general pref
    try {
      loadFromXML(generalPrefFile);
    } catch (IOException e) {
      logger.warn("ERROR: Cannot load preferences! ", e);
    }
  }


  @Override
  public void resetAll() {
    iconWidth = 60;
    iconHeight = 16;
    generatesIcons = true;
    if (img2DHistory != null)
      img2DHistory.removeAllElements();
    if (imzmlListHistory != null)
      imzmlListHistory.removeAllElements();
  }

  // ##########################################################
  // xml input/output
  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
    toXML(elParent, doc, "iconWidth", iconWidth);
    toXML(elParent, doc, "iconHeight", iconHeight);
    toXML(elParent, doc, "generatesIcons", generatesIcons);

    toXML(elParent, doc, "fcOpen", fcOpen.getCurrentDirectory().getAbsolutePath());
    toXML(elParent, doc, "fcImportPicture",
        fcImportPicture.getCurrentDirectory().getAbsolutePath());
    toXML(elParent, doc, "fcExportPicture",
        fcImportPicture.getCurrentDirectory().getAbsolutePath());
    toXML(elParent, doc, "fcImport", fcImport.getCurrentDirectory().getAbsolutePath());
    toXML(elParent, doc, "fcImportImzML", fcImportImzML.getCurrentDirectory().getAbsolutePath());
    toXML(elParent, doc, "fcSave", fcSave.getCurrentDirectory().getAbsolutePath());
    // image 2d import/export history
    for (int i = 0; i < img2DHistory.size(); i++)
      toXML(elParent, doc, "img2DHistory_" + i, img2DHistory.get(i).getAbsolutePath());
    for (int i = 0; i < imzmlListHistory.size(); i++)
      toXML(elParent, doc, "imzmlListHistory_" + i, imzmlListHistory.get(i).getAbsolutePath());

  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
    NodeList list = el.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element nextElement = (Element) list.item(i);
        String paramName = nextElement.getNodeName();
        if (paramName.equals("iconWidth"))
          iconWidth = intFromXML(nextElement);
        else if (paramName.equals("iconHeight"))
          iconHeight = intFromXML(nextElement);
        else if (paramName.equals("generatesIcons"))
          generatesIcons = booleanFromXML(nextElement);
        else if (paramName.equals("fcOpen"))
          fcOpen.setCurrentDirectory(new File(nextElement.getTextContent()));
        else if (paramName.equals("fcImportPicture"))
          fcImportPicture.setCurrentDirectory(new File(nextElement.getTextContent()));
        else if (paramName.equals("fcExportPicture"))
          fcExportPicture.setCurrentDirectory(new File(nextElement.getTextContent()));
        else if (paramName.equals("fcImportImzML"))
          fcImportImzML.setCurrentDirectory(new File(nextElement.getTextContent()));
        else if (paramName.equals("fcImport"))
          fcImport.setCurrentDirectory(new File(nextElement.getTextContent()));
        else if (paramName.equals("fcSave"))
          fcSave.setCurrentDirectory(new File(nextElement.getTextContent()));
        else if (paramName.startsWith("img2DHistory")) {
          img2DHistory.addElement(new File(nextElement.getTextContent()));
        } else if (paramName.startsWith("imzmlListHistory")) {
          File file = new File(nextElement.getTextContent());
          if (file.exists())
            imzmlListHistory.addElement(file);
        }
      }
    }
  }

  /**
   * adds the path of an image2d file to the history
   * 
   * @param pathImg2d
   */
  public void addImage2DImportExportPath(File pathImg2d) {
    addImage2DImportExportPath(pathImg2d, true);
  }

  /**
   * adds the path of an image2d file to the history
   * 
   * @param pathImg2d
   */
  public void addImage2DImportExportPath(File pathImg2d, boolean saveChanges) {
    // already inserted?
    if (!img2DHistory.contains(pathImg2d)) {
      // remove
      if (img2DHistory.size() >= HISTORY_SIZE)
        img2DHistory.remove(img2DHistory.size() - 1);
      img2DHistory.add(0, pathImg2d);
      if (saveChanges)
        saveChanges();
      // fire event
      fireChangeEvent();
    }
  }

  /**
   * adds the path of an imzml mz list
   * 
   * @param path
   */
  public boolean addMZListForImzMLPath(File path, boolean saveChanges) {
    boolean added = false;
    // already inserted?
    if (!imzmlListHistory.contains(path)) {
      added = true;
      imzmlListHistory.add(0, path);
      if (saveChanges)
        saveChanges();
      // fire event
      fireChangeEvent();
    }

    return added;
  }

  /**
   * Last loaded lists of mz values to extract from imzML
   * 
   * @return
   */
  public Vector<File> getImzmlListHistory() {
    return imzmlListHistory;
  }


  /**
   * saves the changes to the general pref file
   */
  public void saveChanges() {
    try {
      saveToXML(generalPrefFile);
    } catch (Exception e) {
      logger.error("ERROR: Cannot save preferences! ", e);
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

  public JFileChooser getFcExportPicture() {
    return fcExportPicture;
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

  public FileTypeFilter getFileTFimzML() {
    return fileTFimzML;
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


  public FileTypeFilter getFileTFProject() {
    return fileTFProject;
  }


  public JFileChooser getFcImportImzML() {
    return fcImportImzML;
  }

}
