package net.rs.lamsi.general.heatmap.dataoperations;

public abstract class PostProcessingOp {

  public double[][] processItensity(double[][] z) {
    double[][] t = new double[z.length][];
    for (int i = 0; i < t.length; i++)
      t[i] = new double[z[i].length];
    return processItensity(z, t);
  }

  public abstract double[][] processItensity(double[][] z, double[][] target);

  public float[][] processXY(float[][] x) {
    float[][] t = new float[x.length][];
    for (int i = 0; i < t.length; i++)
      t[i] = new float[x[i].length];
    return processXY(x, t);
  }

  public abstract float[][] processXY(float[][] x, float[][] target);

  public abstract boolean isProcessingXY();
}
