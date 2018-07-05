package net.rs.lamsi.general.datamodel.image;

import java.io.Serializable;
import javax.swing.Icon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.settings.image.SettingsCollectable2DPlaceHolder;

/**
 * this is a placeholder that is inserted when settings which contain links to other collectable2Ds
 * are imported Placeholder is replaced later
 * 
 * @author r_schm33
 *
 */
public class Collectable2DPlaceHolderLink extends Collectable2D<SettingsCollectable2DPlaceHolder>
    implements Serializable {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  private final Logger logger = LoggerFactory.getLogger(getClass());


  public Collectable2DPlaceHolderLink(SettingsCollectable2DPlaceHolder settings) {
    super(settings);
  }

  public Collectable2DPlaceHolderLink(Collectable2D img) {
    this(new SettingsCollectable2DPlaceHolder(img));
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
    try {
      return null;
    } catch (Exception ex) {
      logger.error("", ex);
      return null;
    }
  }

  /**
   * Given image img will be setup like this image
   * 
   * @param img will get all settings from master image
   */
  @Override
  public void applySettingsToOtherImage(Collectable2D img2) {}

  @Override
  public int getWidthAsMaxDP() {
    return 0;
  }

  @Override
  public int getHeightAsMaxDP() {
    return 0;
  }

  @Override
  public String getTitle() {
    return settings.getTitle();
  }

  @Override
  public String getShortTitle() {
    return "";
  }

  @Override
  public String toListName() {
    return "";
  }

}
