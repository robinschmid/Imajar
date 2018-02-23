package net.rs.lamsi.general.heatmap.dataoperations;

public class DPReduction extends PostProcessingOp {

  public enum Mode {
    SUM, AVG, MAX;
  }

  // reduction factor
  private int f;
  private Mode mode;
  private boolean rotated;

  /**
   * Only one dimension is reduced
   * 
   * @param f
   * @param mode
   * @param rotated false for data[][reduction], true for data[reduction][]
   */
  public DPReduction(int f, Mode mode, boolean rotated) {
    super();
    this.f = f;
    this.mode = mode;
    this.rotated = rotated;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof DPReduction))
      return false;
    else {
      DPReduction that = (DPReduction) obj;
      return this.rotated == that.rotated && this.f == that.f && this.mode.equals(that.mode);
    }
  }


  @Override
  public double[][] processItensity(double[][] z) {
    if (rotated) {
      double[][] t = new double[(z.length) / f][];
      for (int i = 0; i < t.length; i += f)
        t[i] = new double[z[i].length];
      return processItensity(z, t);
    } else {
      double[][] t = new double[(z.length)][];
      for (int i = 0; i < t.length; i++)
        t[i] = new double[z[i].length / f + 1];
      return processItensity(z, t);
    }
  }

  @Override
  public double[][] processItensity(double[][] z, double[][] target) {
    if (!rotated) {
      for (int i = 0; i < z.length; i++) {
        double value = mode.equals(Mode.MAX) ? Double.NEGATIVE_INFINITY : 0;
        int counter = 0;
        for (int j = 0; j < z[i].length; j++) {
          // value
          if (!Double.isNaN(z[i][j])) {
            if (mode.equals(Mode.MAX)) {
              if (value < z[i][j])
                value = z[i][j];
            } else
              value += z[i][j];
            counter++;
          }
          // add to target
          if ((j + 1) % f == 0) {
            if (counter > 0) {
              if (mode.equals(Mode.AVG))
                value = value / counter;
              target[i][(j + 1) / f] = value;
            } else
              target[i][(j + 1) / f] = Double.NaN;

            counter = 0;
            value = mode.equals(Mode.MAX) ? Double.NEGATIVE_INFINITY : 0;
          }
        }
      }
    } else {
      int w = 0;
      for (int i = 0; i < z.length; i++)
        if (z[i].length > w)
          w = z[i].length;

      for (int j = 0; j < w; j++) {
        double value = mode.equals(Mode.MAX) ? Double.NEGATIVE_INFINITY : 0;
        int counter = 0;
        for (int i = 0; i < z.length; i++) {
          double v = j < z[i].length ? z[i][j] : Double.NaN;
          // value
          if (!Double.isNaN(v)) {
            if (mode.equals(Mode.MAX)) {
              if (value < v)
                value = v;
            } else
              value += v;
            counter++;
          }
          // add to target
          if ((i + 1) % f == 0) {
            if (counter > 0) {
              if (mode.equals(Mode.AVG))
                value = value / counter;
              target[(i + 1) / f][j] = value;
            } else
              target[(i + 1) / f][j] = Double.NaN;

            counter = 0;
            value = mode.equals(Mode.MAX) ? Double.NEGATIVE_INFINITY : 0;
          }
        }
      }
    }
    return target;
  }


  @Override
  public float[][] processXY(float[][] x) {
    if (rotated) {
      float[][] t = new float[(x.length) / f][];
      for (int i = 0; i < t.length; i += f)
        t[i] = new float[x[i].length];
      return processXY(x, t);
    } else {
      float[][] t = new float[(x.length)][];
      for (int i = 0; i < t.length; i++)
        t[i] = new float[x[i].length / f + 1];
      return processXY(x, t);
    }
  }

  @Override
  public float[][] processXY(float[][] z, float[][] target) {
    if (!rotated) {
      for (int i = 0; i < z.length; i++) {
        for (int j = 0; j < z[i].length; j += f) {
          target[i][j / f] = z[i][j];
        }
      }
    } else {
      int w = 0;
      for (int i = 0; i < z.length; i++)
        if (z[i].length > w)
          w = z[i].length;

      for (int j = 0; j < w; j++) {
        for (int i = 0; i < z.length; i += f) {
          float v = j < z[i].length ? z[i][j] : Float.NaN;
          target[i][j / f] = v;
        }
      }
    }
    return target;
  }

  @Override
  public boolean isProcessingXY() {
    return true;
  }

}
