package net.rs.lamsi.general.datamodel.image.data.interf;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;

/**
 * multidimensional data set
 * 
 * @author r_schm33
 *
 */
public abstract class MDDataset extends ImageDataset implements Serializable {
  // do not change the version!
  private static final long serialVersionUID = 1L;

  /**
   * removes the dimension i from the data set
   * 
   * @param i
   * @return
   */
  public abstract boolean removeDimension(int i);

  /**
   * Adds a dimension to the data set
   * 
   * @param dim
   * @return the index of the added dimension
   */
  public abstract int addDimension(List<double[]> dim);

  /**
   * adds the image img as a new dimension sets this data set as the new data set of img the old is
   * discarded
   * 
   * @param img
   * @return
   */
  public abstract boolean addDimension(Image2D img);



  /**
   * Number of dimensions
   * 
   * @return
   */
  public int getImageCount() {
    return size();
  }


  /**
   * true if x data is present (data point indices are often ignored as x data
   * 
   * @return
   */
  public abstract boolean hasXData();

  /**
   * Number of dimensions
   * 
   * @return
   */
  public abstract int size();


  public ImageGroupMD createImageGroup() {
    if (getLinesCount() <= 0)
      return null;

    ImageGroupMD group = new ImageGroupMD();
    for (int i = 0; i < getImageCount(); i++) {
      Image2D img = new Image2D(this, i);
      group.add(img);
    }
    return group;
  }

  public ImageGroupMD createImageGroup(File dataFile) {
    ImageGroupMD g = createImageGroup();
    // set data path and name
    if (dataFile != null) {
      g.getSettings().setName((dataFile.getName()));
      g.getSettings().setPathData(dataFile.getAbsolutePath());
    }
    return g;
  }

  public ImageGroupMD createImageGroup(File dataFile, List<String> titles) {
    ImageGroupMD g = createImageGroup();
    // set data path and name
    if (dataFile != null) {
      g.getSettings().setName((dataFile.getName()));
      g.getSettings().setPathData(dataFile.getAbsolutePath());
    }

    // set titles
    int i = 0;
    for (Collectable2D c : g.getImages()) {
      ((Image2D) c).getSettings().getSettImage().setTitle(titles.get(i));
      i++;
    }
    return g;
  }

  public ImageGroupMD createImageGroup(File dataFile, String[] titles) {
    ImageGroupMD g = createImageGroup();
    // set data path and name
    if (dataFile != null) {
      g.getSettings().setName((dataFile.getName()));
      g.getSettings().setPathData(dataFile.getAbsolutePath());
    }

    // set titles
    int i = 0;
    for (Collectable2D c : g.getImages()) {
      ((Image2D) c).getSettings().getSettImage().setTitle(titles[i]);
      i++;
    }
    return g;
  }

  public ImageGroupMD createImageGroup(String name) {
    ImageGroupMD g = createImageGroup();
    // set data path and name
    if (name != null)
      g.getSettings().setName(name);
    return g;
  }
}
