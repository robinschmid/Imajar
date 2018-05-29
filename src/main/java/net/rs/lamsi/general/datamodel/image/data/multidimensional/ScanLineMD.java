package net.rs.lamsi.general.datamodel.image.data.multidimensional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.common.primitives.Floats;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.data.interf.MDDataset;

/**
 * A scan line of multi dimensional data: x y1 y2 y3 ... (y=intensity)
 * 
 * @author r_schm33
 *
 */
public class ScanLineMD implements Serializable {
  // do not change the version!
  private static final long serialVersionUID = 1L;
  //
  protected float[] x = null;
  // end x as right edge of line
  protected float endX = -1;

  // all dimensions
  protected List<double[]> intensity = null;

  public ScanLineMD(List<Float> x, List<double[]> intensity) {
    super();
    setX(x);
    this.intensity = intensity;
  }

  public ScanLineMD(float[] x, List<double[]> intensity) {
    super();
    this.x = x;
    this.intensity = intensity;
  }

  public ScanLineMD(float[] x, double[][] i) {
    super();
    this.x = x;
    this.intensity = new ArrayList<double[]>();
    for (double[] d : i)
      intensity.add(d);
  }

  public ScanLineMD(float[] x, double[] i) {
    super();
    this.x = x;
    this.intensity = new ArrayList<double[]>();
    intensity.add(i);
  }



  public ScanLineMD(double[] i) {
    super();
    this.intensity = new ArrayList<double[]>();
    intensity.add(i);
  }

  public ScanLineMD() {
    super();
    this.intensity = new ArrayList<double[]>();
  }

  public ScanLineMD(List<Float> x) {
    this();
    setX(x);
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

  public int addDimension(double[] dim) {
    intensity.add(dim);
    return intensity.size() - 1;
  }

  /**
   * adds the dimension of line i
   * 
   * @param img
   * @param line
   */
  public int addDimension(Image2D img, int line) {
    int dp = img.getData().getLineLength(line);
    if (x == null
        && (!MDDataset.class.isInstance(img.getData()) || ((MDDataset) img.getData()).hasXData())) {
      x = new float[dp];
      for (int i = 0; i < dp; i++) {
        x[dp] = img.getXRaw(true, line, i);
      }
    }
    // add dimension
    double[] z = new double[dp];
    for (int i = 0; i < dp; i++) {
      z[i] = img.getIRaw(line, i);
    }
    return addDimension(z);
  }


  public boolean hasXData() {
    return this.getX() != null;
  }

  /**
   * adds an intensity dimension (image)
   * 
   * @param i
   */
  public void addDimension(List<Double> i) {
    double[] d = i.stream().mapToDouble(val -> val).toArray();
    addDimension(d);
  }

  public float[] getX() {
    return x;
  }

  public void setX(float[] x) {
    this.x = x;
  }

  public void setX(List<Float> lx) {
    x = Floats.toArray(lx);
  }

  /**
   * 
   * @param ix
   * @return
   */
  public float getX(int ix) {
    return x != null ? this.x[ix] : ix;
  }


  /**
   * 
   * @param i
   * @param ix
   * @return intensity of image(dimension) i and data point ix
   */
  public double getI(int i, int ix) {
    return intensity.get(i)[ix];
  }

  /**
   * total data points count
   * 
   * @return
   */
  public int getDPCount() {
    return intensity.get(0).length;
  }

  /**
   * raw width of dp by dividing maxX by elements count
   * 
   * @return
   */
  public float getWidthDP() {
    // width is defined by x of last dp devided by all datapoints in front of the last
    return (getXWidth()) / (getDPCount() - 1);
  }

  @Override
  public String toString() {
    return (x == null ? "" : x.toString()) + intensity.toString();
  }

  /**
   * the width between start and end of X values
   * 
   * @return
   */
  public float getXWidth() {
    return getX(getDPCount() - 1) - getX(0);
  }

  /**
   * 
   * @return the number of dimensions (images)
   */
  public int getImageCount() {
    return intensity.size();
  }


  public List<double[]> getIntensity() {
    return intensity;
  }

  public void setIntensity(List<double[]> intensity) {
    this.intensity = intensity;
  }

  /**
   * right edge of the last data point (x)
   * 
   * @return
   */
  public float getEndX() {
    if (endX == -1)
      endX = getX(getDPCount() - 1) + getWidthDP();
    return endX;
  }

  /**
   * distace percentage of x to the middle of width
   * 
   * @param width
   * @param x
   * @return
   */
  private double distPercent(double width, double x) {
    return (width / 2 - x) / width * 2;
  }

  /**
   * X0 (first value)
   * 
   * @return
   */
  public float getX0() {
    return x != null ? x[0] : 0;
  }

}
