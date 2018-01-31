package net.rs.lamsi.general.settings.image;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.settings.image.filter.SettingsCropAndShift;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.general.settings.image.special.SingleParticleSettings;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.general.settings.image.sub.SettingsZoom;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsThemesContainer;
import net.rs.lamsi.utils.mywriterreader.BinaryWriterReader;
import net.rs.lamsi.utils.useful.DebugStopWatch;

public class SettingsSPImage extends SettingsContainerCollectable2D {
  // do not change the version!
  private static final long serialVersionUID = 1L;

  public SettingsSPImage() {
    super("SettingsSPImage", "/Settings/SPImage/", "setSPImg");

    addSettings(new SettingsGeneralImage());
    addSettings(new SettingsThemesContainer(true));
    addSettings(new SettingsZoom());
    addSettings(new SettingsSelections());
    addSettings(new SettingsCropAndShift());
    addSettings(new SingleParticleSettings());
  }


  public SettingsSPImage(SettingsGeneralImage setImage) {
    super("SettingsSPImage", "/Settings/SPImage/", "setSPImg");
    try {
      SettingsGeneralImage s = (SettingsGeneralImage) BinaryWriterReader.deepCopy(setImage);
      s.setIntensityFactor(1);
      s.setUseBlur(false);
      s.setBinaryData(false);
      addSettings(s);
      addSettings(new SettingsThemesContainer(true));
      addSettings(new SettingsZoom());
      addSettings(new SettingsSelections());
      addSettings(new SettingsCropAndShift());
      addSettings(new SingleParticleSettings());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void applyToImage(Image2D img) throws Exception {
    SettingsGeneralImage sg = img.getSettings().getSettImage();
    // dont copy name
    String name = img.getTitle();
    String shortTitle = sg.getShortTitle();
    String path = img.getSettings().getSettImage().getRAWFilepath();

    DebugStopWatch t = new DebugStopWatch();
    super.applyToImage(img);
    t.stopAndLOG(" copy image2d settings");

    // reset to old short title only if not the same title
    if (!name.equals(img.getTitle()))
      img.getSettings().getSettImage().setShortTitle(shortTitle);

    // reset to old title
    img.getSettings().getSettImage().setTitle(name);
    img.getSettings().getSettImage().setRAWFilepath(path);
  }

  public SingleParticleSettings getSettSingleParticle() {
    return (SingleParticleSettings) list.get(SingleParticleSettings.class);
  }

  public SettingsGeneralImage getSettImage() {
    return (SettingsGeneralImage) list.get(SettingsGeneralImage.class);
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
