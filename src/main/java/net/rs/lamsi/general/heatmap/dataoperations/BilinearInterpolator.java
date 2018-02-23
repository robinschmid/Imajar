package net.rs.lamsi.general.heatmap.dataoperations;

public class BilinearInterpolator extends PostProcessingOp {

  // interpolation factor
  private int f;

  /**
   * 
   * @param f interpolation factor
   */
  public BilinearInterpolator(int f) {
    super();
    this.f = f;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof BilinearInterpolator))
      return false;
    else {
      BilinearInterpolator that = (BilinearInterpolator) obj;
      return this.f == that.f;
    }
  }

  @Override
  public double[][] processItensity(double[][] z) {
    double[][] t = new double[(z.length - 1) * f][];
    for (int i = 0; i < t.length; i++)
      t[i] = new double[(z[i].length - 1) * f];
    return processItensity(z, t);
  }

  @Override
  public double[][] processItensity(double[][] z, double[][] target) {
    int zi = -1;
    for (int i = 0; i < target.length; i++) {
      int modi = i % f;
      if (modi == 0)
        zi++;
      int zj = -1;
      for (int j = 0; j < target[i].length; j++) {
        int modj = j % f;
        if (modj == 0)
          zj++;
        // value
        target[i][j] = calcI(z[zi][zj], z[zi + 1][zj], z[zi][zj + 1], z[zi + 1][zj + 1],
            modi / (double) f, modj / (double) f);
      }
    }
    return target;
  }

  /**
   * Bilinear interpolation
   * 
   * @param x00 ij
   * @param x10 i+1 j
   * @param x01 i j+1
   * @param x11 i+1 j+1
   * @param fi modi/f
   * @param fj modj/f
   * @return
   */
  private double calcI(double x00, double x10, double x01, double x11, double fi, double fj) {
    if (!Double.isNaN(x00) && !Double.isNaN(x10) && !Double.isNaN(x01) && !Double.isNaN(x11)) {
      return (x00 * (1.0 - fi) + x10 * fi) * (1.0 - fj) + (x01 * (1.0 - fi) + x11 * fi) * fj;
    } else {
      // some values are NaN
      double p1, p2;
      if (!Double.isNaN(x00) && !Double.isNaN(x10))
        p1 = (x00 * (1.0 - fi) + x10 * fi);
      else if (!Double.isNaN(x00))
        p1 = x00;
      else if (!Double.isNaN(x10))
        p1 = x10;
      else
        p1 = Double.NaN;

      if (!Double.isNaN(x01) && !Double.isNaN(x11))
        p2 = (x01 * (1.0 - fi) + x11 * fi);
      else if (!Double.isNaN(x01))
        p2 = x01;
      else if (!Double.isNaN(x11))
        p2 = x11;
      else
        p2 = Double.NaN;

      double res = 0;
      if (!Double.isNaN(p1) && !Double.isNaN(p2))
        return p1 * (1.0 - fj) + p2 * fj;
      else if (!Double.isNaN(p1))
        return p1;
      else if (!Double.isNaN(p2))
        return p2;
      else
        return Double.NaN;
    }
  }

  /**
   * Bilinear interpolation
   * 
   * @param x00 ij
   * @param x10 i+1 j
   * @param x01 i j+1
   * @param x11 i+1 j+1
   * @param fi modi/f
   * @param fj modj/f
   * @return
   */
  private float calcI(float x00, float x10, float x01, float x11, float fi, float fj) {
    if (!Float.isNaN(x00) && !Float.isNaN(x10) && !Float.isNaN(x01) && !Float.isNaN(x11)) {
      return (x00 * (1.f - fi) + x10 * fi) * (1.f - fj) + (x01 * (1.f - fi) + x11 * fi) * fj;
    } else {
      // some values are NaN
      float p1, p2;
      if (!Float.isNaN(x00) && !Float.isNaN(x10))
        p1 = (x00 * (1.f - fi) + x10 * fi);
      else if (!Float.isNaN(x00))
        p1 = x00;
      else if (!Float.isNaN(x10))
        p1 = x10;
      else
        p1 = Float.NaN;

      if (!Float.isNaN(x01) && !Float.isNaN(x11))
        p2 = (x01 * (1.f - fi) + x11 * fi);
      else if (!Float.isNaN(x01))
        p2 = x01;
      else if (!Float.isNaN(x11))
        p2 = x11;
      else
        p2 = Float.NaN;

      float res = 0;
      if (!Float.isNaN(p1) && !Float.isNaN(p2))
        return p1 * (1.f - fj) + p2 * fj;
      else if (!Float.isNaN(p1))
        return p1;
      else if (!Float.isNaN(p2))
        return p2;
      else
        return Float.NaN;
    }
  }


  @Override
  public float[][] processXY(float[][] x) {
    float[][] t = new float[(x.length - 1) * f][];
    for (int i = 0; i < t.length; i++)
      t[i] = new float[(x[i].length - 1) * f];
    return processXY(x, t);
  }

  @Override
  public float[][] processXY(float[][] x, float[][] target) {
    int zi = -1;
    for (int i = 0; i < target.length; i++) {
      int modi = i % f;
      if (modi == 0)
        zi++;
      int zj = -1;
      for (int j = 0; j < target[i].length; j++) {
        int modj = j % f;
        if (modj == 0)
          zj++;
        // value
        target[i][j] = calcI(x[zi][zj], x[zi + 1][zj], x[zi][zj + 1], x[zi + 1][zj + 1],
            modi / (float) f, modj / (float) f);
      }
    }
    return target;
  }

  @Override
  public boolean isProcessingXY() {
    return true;
  }



}
