package net.rs.lamsi.general.settings;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.general.settings.image.sub.SettingsMSImage;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsThemesContainer;
import net.rs.lamsi.general.settings.importexport.SettingsExportGraphics;
import net.rs.lamsi.general.settings.importexport.SettingsImage2DDataExport;
import net.rs.lamsi.general.settings.importexport.SettingsImage2DDataSelectionsExport;
import net.rs.lamsi.general.settings.preferences.SettingsGeneralPreferences;
import net.rs.lamsi.general.settings.preferences.SettingsGeneralValueFormatting;
import net.rs.lamsi.general.settings.visualization.SettingsPlotSpectraLabelGenerator;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.myfilechooser.FileTypeFilter;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SettingsHolder extends SettingsContainerSettings {

  // do not change the version!
  private static final long serialVersionUID = 1L;
  //
  //
  private static final SettingsHolder SETTINGS = new SettingsHolder();
  // Settings writer
  private BinaryWriterReader settingsWriter = new BinaryWriterReader();
  //


  public SettingsHolder() {
    super("SettingsHolder", "/Settings/", "setall");
    // settings
    addSettings(new SettingsDataSaver("/Settings/Export/", "setds"));
    addSettings(new SettingsGeneralValueFormatting());

    // visualization only for Toolset
    addSettings(new SettingsPlotSpectraLabelGenerator());

    // Export settings
    addSettings(new SettingsExportGraphics());

    // export data of image2d
    addSettings(new SettingsImage2DDataExport());

    addSettings(new SettingsImage2DDataSelectionsExport());

    // general preferences
    addSettings(new SettingsGeneralPreferences());
  }


  // ##################################################################################
  // binary
  public File saveSettingsToFileBinary(Component parentFrame, Class settingsClass)
      throws Exception {
    return saveSettingsToFileBinary(parentFrame, getSettingsByClass(settingsClass));
  }

  public File saveSettingsToFileBinary(Component parentFrame, Settings cs) throws Exception {
    // Open new FC
    // create Path
    File path = new File(FileAndPathUtil.getPathOfJar(), cs.getPathSettingsFile());
    FileAndPathUtil.createDirectory(path);
    JFileChooser fc = new JFileChooser(path);
    FileTypeFilter ffilter = new FileTypeFilter(cs.getFileEnding() + "bin", "Save settings to");
    fc.addChoosableFileFilter(ffilter);
    fc.setFileFilter(ffilter);
    // getting the file
    if (fc.showSaveDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      // extention anbringen
      file = ffilter.addExtensionToFileName(file);
      //
      cs.saveToFile(settingsWriter, file);
      return file;
    } else {
      return null;
    }
  }

  public Settings loadSettingsFromFileBinary(Component parentFrame, Class settingsClass)
      throws Exception {
    return loadSettingsFromFileBinary(parentFrame, getSettingsByClass(settingsClass));
  }

  public Settings loadSettingsFromFileBinary(Component parentFrame, Settings cs) throws Exception {
    // TODO Auto-generated method stub
    // Open new FC
    File path = new File(FileAndPathUtil.getPathOfJar(), cs.getPathSettingsFile());
    FileAndPathUtil.createDirectory(path);
    JFileChooser fc = new JFileChooser(path);
    FileFilter ffilter = new FileTypeFilter(cs.getFileEnding() + "bin", "Load settings from");
    fc.addChoosableFileFilter(ffilter);
    fc.setFileFilter(ffilter);
    // getting the file
    if (fc.showOpenDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      return loadSettingsFromFileBinary(file, cs);
    }
    return null;
  }

  public Settings loadSettingsFromFileBinary(File file, Settings cs) {
    // Welches wurde geladen?
    if (cs instanceof SettingsHolder) {
      // alle laden und setzen
      SettingsHolder sett = (SettingsHolder) (cs.loadFromFile(settingsWriter, file));
      // Alle settings aus geladenen holder kopieren
      return this;
    } else {
      Settings loaded = (cs.loadFromFile(settingsWriter, file));
      replaceSettings(loaded);
      return loaded;
    }
  }

  // GETTERS
  public SettingsDataSaver getSetDataSaver() {
    return (SettingsDataSaver) getSettingsByClass(SettingsDataSaver.class);
  }

  public SettingsGeneralImage getSetGeneralImage() {
    return (SettingsGeneralImage) getSettingsByClass(SettingsGeneralImage.class);
  }

  public SettingsPaintScale getSetPaintScale() {
    return (SettingsPaintScale) getSettingsByClass(SettingsPaintScale.class);
  }


  public SettingsGeneralValueFormatting getSetGeneralValueFormatting() {
    return (SettingsGeneralValueFormatting) getSettingsByClass(
        SettingsGeneralValueFormatting.class);
  }

  public SettingsPlotSpectraLabelGenerator getSetVisPlotSpectraLabelGenerator() {
    return (SettingsPlotSpectraLabelGenerator) getSettingsByClass(
        SettingsPlotSpectraLabelGenerator.class);
  }

  public SettingsExportGraphics getSetGraphicsExport() {
    return (SettingsExportGraphics) getSettingsByClass(SettingsExportGraphics.class);
  }


  public SettingsThemesContainer getSetPlotStyle() {
    return (SettingsThemesContainer) getSettingsByClass(SettingsThemesContainer.class);
  }

  @Override
  public Settings getSettingsByClass(Class classsettings) {
    return super.getSettingsByClass(classsettings);
  }

  // SETTER
  public void setSetPlotStyle(SettingsThemesContainer setPlotStyle) {
    replaceSettings(setPlotStyle);
  }

  public void setSetGraphicsExport(SettingsExportGraphics setGraphicsExport) {
    replaceSettings(setGraphicsExport);
  }

  public void setSetPaintScale(SettingsPaintScale setPaintScale) {
    replaceSettings(setPaintScale);
  }

  public void setSetOESImage(SettingsGeneralImage setOESImage) {
    replaceSettings(setOESImage);
  }

  public void setSetMSIDiscon(SettingsMSImage setMSIDiscon) {
    replaceSettings(setMSIDiscon);
  }

  public void setSetMSICon(SettingsMSImage setMSICon) {
    replaceSettings(setMSICon);
  }

  public void setSetDataSaver(SettingsDataSaver setDataSaver) {
    replaceSettings(setDataSaver);
  }

  // get settings instance
  public static SettingsHolder getSettings() {
    return SETTINGS;
  }

  public BinaryWriterReader getSettingsWriter() {
    return settingsWriter;
  }

  public SettingsImage2DDataExport getSetImage2DDataExport() {
    return (SettingsImage2DDataExport) getSettingsByClass(SettingsImage2DDataExport.class);
  }

  public SettingsImage2DDataSelectionsExport getSetImage2DDataSelectionsExport() {
    return (SettingsImage2DDataSelectionsExport) getSettingsByClass(
        SettingsImage2DDataSelectionsExport.class);
  }

  public SettingsGeneralPreferences getSetGeneralPreferences() {
    return (SettingsGeneralPreferences) getSettingsByClass(SettingsGeneralPreferences.class);
  }

  public void setSetGeneralPreferences(SettingsGeneralPreferences setGeneralPreferences) {
    replaceSettings(setGeneralPreferences);
  }

  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {
  }

  @Override
  public void loadValuesFromXML(Element el, Document doc) {
  }

}
