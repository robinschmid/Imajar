package net.rs.lamsi.general.settings.image;

import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.datamodel.image.interf.PostProcessingOpProvider;
import net.rs.lamsi.general.heatmap.dataoperations.DPReduction;
import net.rs.lamsi.general.heatmap.dataoperations.DPReduction.Mode;
import net.rs.lamsi.general.heatmap.dataoperations.PostProcessingOp;
import net.rs.lamsi.general.settings.image.filter.SettingsCropAndShift;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.general.settings.image.special.SingleParticleSettings;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralCollecable2D;
import net.rs.lamsi.general.settings.image.sub.SettingsZoom;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsThemesContainer;

public class SettingsSPImage extends SettingsContainerCollectable2D
    implements PostProcessingOpProvider {
  // do not change the version!
  private static final long serialVersionUID = 1L;

  // the original image
  private Image2D img;

  public SettingsSPImage() {
    super("SettingsSPImage", "/Settings/SPImage/", "setSPImg");

    addSettings(new SettingsGeneralCollecable2D());
    addSettings(new SettingsThemesContainer(true));
    addSettings(new SettingsZoom());
    addSettings(new SettingsSelections());
    addSettings(new SettingsCropAndShift());
    addSettings(new SingleParticleSettings());
    addSettings(new SettingsPaintScale());
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

  public SettingsSelections getSettSelections() {
    return (SettingsSelections) getSettingsByClass(SettingsSelections.class);
  }

  /**
   * List of post processing operations, e.g., blur, interpolation, reduction ...
   * 
   * @return
   */
  @Override
  public List<PostProcessingOp> getPostProcessingOp() {
    List<PostProcessingOp> op = new LinkedList<>();
    if (getSettSingleParticle().getNumberOfPixel() > 1)
      op.add(
          new DPReduction(getSettSingleParticle().getNumberOfPixel(), Mode.SUM, img.isRotated()));
    return op;
  }

  public Image2D getImg() {
    return img;
  }

  public void setImg(Image2D img) {
    this.img = img;
  }

  // ###########################################################
  // XML
  @Override
  public void appendSettingsValuesToXML(Element elParent, Document doc) {}

  @Override
  public void loadValuesFromXML(Element el, Document doc) {}
}
