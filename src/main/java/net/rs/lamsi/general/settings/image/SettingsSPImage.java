package net.rs.lamsi.general.settings.image;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.settings.image.filter.SettingsCropAndShift;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.general.settings.image.special.SingleParticleSettings;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralCollecable2D;
import net.rs.lamsi.general.settings.image.sub.SettingsZoom;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsThemesContainer;

public class SettingsSPImage extends SettingsContainerCollectable2D {
  // do not change the version!
  private static final long serialVersionUID = 1L;

  public SettingsSPImage() {
    super("SettingsSPImage", "/Settings/SPImage/", "setSPImg");

    addSettings(new SettingsGeneralCollecable2D());
    addSettings(new SettingsThemesContainer(true));
    addSettings(new SettingsZoom());
    addSettings(new SettingsSelections());
    addSettings(new SettingsCropAndShift());
    addSettings(new SingleParticleSettings());
  }

  @Override
  public void applyToImage(Collectable2D c) throws Exception {
    SettingsGeneralCollecable2D old =
        (SettingsGeneralCollecable2D) c.getSettingsByClass(SettingsGeneralCollecable2D.class);

    if (old != null) {
      // dont copy name
      String name = old.getTitle();
      String shortTitle = old.getShortTitle();

      super.applyToImage(c);

      // new settings object
      SettingsGeneralCollecable2D sett =
          (SettingsGeneralCollecable2D) c.getSettingsByClass(SettingsGeneralCollecable2D.class);
      // reset to old short title only if not the same title
      if (!name.equals(old.getTitle())) {
        sett.setShortTitle(shortTitle);
      }
      // reset to old title
      sett.setTitle(name);
    } else
      super.applyToImage(c);
  }

  public SingleParticleSettings getSettSingleParticle() {
    return (SingleParticleSettings) list.get(SingleParticleSettings.class);
  }

  public SettingsGeneralCollecable2D getSettImage() {
    return (SettingsGeneralCollecable2D) list.get(SettingsGeneralCollecable2D.class);
  }

  public SettingsThemesContainer getSettTheme() {
    return (SettingsThemesContainer) list.get(SettingsThemesContainer.class);
  }

  public SettingsZoom getSettZoom() {
    return (SettingsZoom) getSettingsByClass(SettingsZoom.class);
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
