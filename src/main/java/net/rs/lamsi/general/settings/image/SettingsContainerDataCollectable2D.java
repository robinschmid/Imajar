package net.rs.lamsi.general.settings.image;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import net.rs.lamsi.general.settings.image.filter.SettingsCropAndShift;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralCollecable2D;
import net.rs.lamsi.general.settings.image.sub.SettingsZoom;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsThemesContainer;

public class SettingsContainerDataCollectable2D extends SettingsContainerCollectable2D {
  // do not change the version!
  private static final long serialVersionUID = 1L;


  public SettingsContainerDataCollectable2D(String description, String path, String fileEnding) {
    super(description, path, fileEnding);
  }

  protected void addStandardSettings() {
    addSettings(new SettingsThemesContainer(true));
    addSettings(new SettingsZoom());
    addSettings(new SettingsSelections());
    addSettings(new SettingsCropAndShift());
  }

  public SettingsGeneralCollecable2D getSettImage() {
    return (SettingsGeneralCollecable2D) list.get(SettingsGeneralCollecable2D.class);
  }

  public SettingsSelections getSettSelections() {
    return (SettingsSelections) getSettingsByClass(SettingsSelections.class);
  }

  // ###########################################################
  // XML
  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {}

  @Override
  public void loadValuesFromXML(Element el, Document doc) {}
}
