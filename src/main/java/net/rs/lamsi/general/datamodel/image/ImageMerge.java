package net.rs.lamsi.general.datamodel.image;

import java.io.Serializable;
import javax.swing.Icon;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.settings.image.SettingsImageMerge;
import net.rs.lamsi.general.settings.image.sub.SettingsZoom;
import net.rs.lamsi.general.settings.image.visualisation.themes.SettingsThemesContainer;

public class ImageMerge extends Collectable2D<SettingsImageMerge> implements Serializable {
  // do not change the version!
  private static final long serialVersionUID = 1L;

  protected ImageGroupMD group;

  public ImageMerge(ImageGroupMD group, SettingsImageMerge settings, String title)
      throws Exception {
    super(settings);
    this.group = group;
    if (!settings.isInitialised())
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
      ex.printStackTrace();
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
    for (int i = 0; i < group.image2dCount(); i++) {
      Image2D img = (Image2D) group.get(i);
      if (max < img.getWidth(raw))
        max = img.getWidth(raw);
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
    for (int i = 0; i < group.image2dCount(); i++) {
      Image2D img = (Image2D) group.get(i);
      if (max < img.getHeight(raw))
        max = img.getHeight(raw);
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
    for (int i = 0; i < group.image2dCount(); i++) {
      Image2D img = (Image2D) group.get(i);
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
    for (int i = 0; i < group.image2dCount(); i++) {
      Image2D img = (Image2D) group.get(i);
      if (max < img.getHeightAsMaxDP())
        max = img.getHeightAsMaxDP();
    }
    return max;
  }


  public Image2D[] getImages() {
    return group.getImagesOnly();
  }

  public int size() {
    return group.image2dCount();
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

  public ImageGroupMD getGroup() {
    return group;
  }

  public void setGroup(ImageGroupMD group) {
    this.group = group;
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
        e.printStackTrace();
      }
    }
  }
}
