package net.rs.lamsi.general.datamodel.image.data.twodimensional;

import org.jfree.data.Range;

public class XYIDataMatrix {

  protected double[][] i;
  protected float[][] x, y;

  // min max
  protected Range rx, ry, ri;
  private boolean rotated;

  public XYIDataMatrix(float[][] x, float[][] y, double[][] i, boolean rotated) {
    super();
    this.x = x;
    this.y = y;
    this.i = i;
    this.setRotated(rotated);

    updateStats();
  }


  private void updateStats() {
    // ranges for x y i
    rx = rangeOf(x);
    ry = rangeOf(y);
    ri = rangeOf(i);

  }


  private Range rangeOf(float[][] data) {
    float max = Float.NEGATIVE_INFINITY;
    float min = Float.MAX_VALUE;

    for (float[] d : data) {
      for (float v : d) {
        if (!Float.isNaN(v)) {
          if (v > max)
            max = v;
          if (v < min)
            min = v;
        }
      }
    }
    return new Range(min, max);
  }

  private Range rangeOf(double[][] data) {
    double max = Double.NEGATIVE_INFINITY;
    double min = Double.MAX_VALUE;
    for (double[] d : data) {
      for (double v : d) {
        if (!Double.isNaN(v)) {
          if (v > max)
            max = v;
          if (v < min)
            min = v;
        }
      }
    }
    return new Range(min, max);
  }


  @Override
  public String toString() {
    return "[" + String.valueOf(x) + "; " + String.valueOf(y) + "; " + String.valueOf(i) + "]";
  }

  public double[][] getI() {
    return i;
  }

  public float[][] getX() {
    return x;
  }

  public float[][] getY() {
    return y;
  }

  public void setI(double[][] i) {
    this.i = i;
    ri = rangeOf(i);
  }

  public void setX(float[][] x) {
    this.x = x;
    rx = rangeOf(x);
  }

  public void setY(float[][] y) {
    this.y = y;
    ry = rangeOf(y);
  }

  public int getMinimumLineLength() {
    int min = Integer.MAX_VALUE;
    for (double[] d : i) {
      int length = lineLength(d);
      if (length < min)
        min = length;
    }
    return min;
  }

  public int getMaximumLineLength() {
    int max = 0;
    for (double[] d : i) {
      int length = lineLength(d);
      if (length > max)
        max = length;
    }
    return max;
  }

  public int getAverageLineLength() {
    int max = 0;
    for (double[] d : i) {
      int length = lineLength(d);
      max += length;
    }
    max = max / i.length;
    return max;
  }

  public int lineLength(int line) {
    if (line >= i.length)
      return 0;
    return lineLength(i[line]);
  }

  private int lineLength(double[] l) {
    for (int i = l.length - 1; i >= 0; i--) {
      if (!Double.isNaN(l[i]))
        return i + 1;
    }
    return 0;
  }

  public int lineCount() {
    return i == null ? 0 : i.length;
  }


  public double getMinI() {
    return ri.getLowerBound();
  }

  public double getMaxI() {
    return ri.getUpperBound();
  }

  /**
   * Intensity range
   * 
   * @return
   */
  public Range getIRange() {
    return ri;
  }

  /**
   * Y range
   * 
   * @return
   */
  public Range getYRange() {
    return ry;
  }

  /**
   * X range
   * 
   * @return
   */
  public Range getXRange() {
    return rx;
  }

  public boolean isRotated() {
    return rotated;
  }


  public void setRotated(boolean rotated) {
    this.rotated = rotated;
  }


  /**
   * Creates a one dimensional array for [x,y,z][dp]
   * 
   * @return
   */
  public double[][] toLinearArray() {
    int size = 0;
    for (double[] d : i) {
      size += d.length;
    }

    int c = 0;
    double[][] data = new double[3][size];
    for (int l = 0; l < i.length; l++) {
      for (int dp = 0; dp < i[l].length; dp++) {
        data[0][c] = x[l][dp];
        data[1][c] = y[l][dp];
        data[2][c] = i[l][dp];
        c++;
      }
    }
    return data;
  }
}
