package net.rs.lamsi.general.datamodel.image;

import java.io.Serializable;
import java.util.List;
import javax.swing.Icon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.settings.image.SettingsImageMerge;
import net.rs.lamsi.general.settings.image.merge.SettingsSingleMerge;
import net.rs.lamsi.general.settings.image.sub.SettingsZoom;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsThemesContainer;

public class ImageMerge extends Collectable2D<SettingsImageMerge> implements Serializable {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public ImageMerge(ImageGroupMD group, SettingsImageMerge settings, String title, boolean init)
      throws Exception {
    super(settings);
    this.imageGroup = group;
    if (init)
      settings.init(group.getProject(), title);
  }

  /**
   * returns an easy icon
   * 
   * @param maxw
   * @param maxh
   * @return
   */
  @Override
  public Icon getIcon(int maxw, int maxh) {
    try { // TODO
      return null;
    } catch (Exception ex) {
      logger.error("", ex);
      return null;
    }
  }

  // ######################################################################################
  // sizing
  /**
   * maximum width of this overlay
   * 
   * @param raw
   * @return
   */
  public float getWidth(boolean raw) {
    float max = 0;
    int i = 0;
    for (DataCollectable2D img : settings.getImageList()) {
      SettingsSingleMerge sett = settings.getMergeSettings(img.getImageGroup().getName());
      float w = img.getWidth() + sett.getDX();
      if (max < w)
        max = w;
    }
    return max;
  }

  /**
   * maximum height of this overlay
   * 
   * @param raw
   * @return
   */
  public float getHeight(boolean raw) {
    float max = 0;
    int i = 0;
    for (DataCollectable2D img : settings.getImageList()) {
      SettingsSingleMerge sett = settings.getMergeSettings(img.getImageGroup().getName());
      float h = img.getHeight() + sett.getDY();
      if (max < h)
        max = h;
    }
    return max;
  }

  /**
   * according to rotation of data
   * 
   * @return
   */
  public int getWidthAsMaxDP() {
    int max = 0;
    for (int i = 0; i < imageGroup.image2dCount(); i++) {
      Image2D img = (Image2D) imageGroup.get(i);
      if (max < img.getWidthAsMaxDP())
        max = img.getWidthAsMaxDP();
    }
    return max;
  }

  /**
   * according to rotation of data
   * 
   * @return
   */
  public int getHeightAsMaxDP() {
    int max = 0;
    for (int i = 0; i < imageGroup.image2dCount(); i++) {
      Image2D img = (Image2D) imageGroup.get(i);
      if (max < img.getHeightAsMaxDP())
        max = img.getHeightAsMaxDP();
    }
    return max;
  }


  public List<DataCollectable2D> getImages() {
    return settings.getImageList();
  }

  public int size() {
    return getImages().size();
  }

  public SettingsThemesContainer getSettTheme() {
    return settings.getSettTheme();
  }

  @Override
  public SettingsZoom getSettZoom() {
    return settings.getSettZoom();
  }

  @Override
  public String getTitle() {
    return settings.getTitle() + " merged";
  }

  public String getShortTitle() {
    return getTitle();
  }

  // a name for lists
  public String toListName() {
    return getTitle();
  }

  @Override
  public void applySettingsToOtherImage(Collectable2D img2) {
    if (img2 instanceof ImageMerge) {
      ImageMerge img = (ImageMerge) img2;

      try {
        // save name and path
        String name = img.getSettings().getTitle();

        // copy all TODO
        img.setSettings(this.settings.copy());
        img.getSettings().setTitle(name);
      } catch (Exception e) {
        logger.error("", e);
      }
    }
  }
}
