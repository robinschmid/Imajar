package net.rs.lamsi.general.datamodel.image.data.multidimensional;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.stream.Collectors;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;

/**
 * A scan line of multi dimensional data: x y1 y2 y3 ... (y=intensity)
 * 
 * @author r_schm33
 *
 */
public class XYZDataPointMD implements Serializable {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  //
  protected float x, y;

  // all dimensions
  protected DoubleArrayList intensity = null;

  public XYZDataPointMD(float x, float y, DoubleArrayList intensity) {
    super();
    setX(x);
    setY(y);
    this.intensity = intensity;
  }

  public XYZDataPointMD(float x, float y, double intensity) {
    super();
    setX(x);
    setY(y);
    this.intensity = new DoubleArrayList();
    this.intensity.add(intensity);
  }

  public XYZDataPointMD(float x, float y, double[] i) {
    super();
    setX(x);
    setY(y);
    this.intensity = new DoubleArrayList();
    for (double v : i)
      intensity.add(v);
  }

  public XYZDataPointMD() {
    super();
    this.intensity = new DoubleArrayList();
  }

  // ##################################################
  // Multi dimensional
  public boolean removeDimension(int i) {
    if (i >= 0 && i < intensity.size()) {
      intensity.remove(i);
      return true;
    }
    return false;
  }

  public int addDimension(double dim) {
    intensity.add(dim);
    return intensity.size() - 1;
  }

  public float getX() {
    return x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public void setY(float y) {
    this.y = y;
  }

  @Override
  public String toString() {
    return MessageFormat.format("x, y, (z): {0}, {1}, ({2})", x, y,
        intensity.stream().map(String::valueOf).collect(Collectors.joining(", ")));
  }

  /**
   * 
   * @return the number of dimensions (images)
   */
  public int getImageCount() {
    return intensity.size();
  }

  public DoubleArrayList getIntensity() {
    return intensity;
  }

  public void setIntensity(DoubleArrayList intensity) {
    this.intensity = intensity;
  }

}
