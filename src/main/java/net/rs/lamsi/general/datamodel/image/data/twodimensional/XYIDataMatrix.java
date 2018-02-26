package net.rs.lamsi.general.datamodel.image.data.twodimensional;

public class XYIDataMatrix {

  protected double[][] i;
  protected float[][] x, y;

  public XYIDataMatrix(float[][] x, float[][] y, double[][] i) {
    super();
    this.x = x;
    this.y = y;
    this.i = i;
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
  }

  public void setX(float[][] x) {
    this.x = x;
  }

  public void setY(float[][] y) {
    this.y = y;
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
    double min = Double.MAX_VALUE;
    for (double[] d : i) {
      for (int f = 0; f < d.length; f++) {
        if (!Double.isNaN(d[f]) && d[f] < min)
          min = d[f];
      }
    }

    return min;
  }

  public double getMaxI() {
    double max = Double.NEGATIVE_INFINITY;
    for (double[] d : i) {
      for (int f = 0; f < d.length; f++) {
        if (!Double.isNaN(d[f]) && d[f] > max)
          max = d[f];
      }
    }

    return max;
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
