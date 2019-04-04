package net.rs.lamsi.general.processing.dataoperations;

import net.rs.lamsi.general.heatmap.dataoperations.PostProcessingOp;

public class SpikeRemover extends PostProcessingOp {

  protected int[] despikeMatrix;
  protected double factor;
  protected int windowN = 0;
  protected double windowSum = 0;
  private boolean isRotated;


  public SpikeRemover(boolean isRotated, int[] despikeMatrix, double factor) {
    super();
    this.isRotated = isRotated;
    this.despikeMatrix = despikeMatrix;
    this.factor = factor;
  }


  /**
   * distance ==0 ### #?# ###
   * 
   * distance ==1 ...#... #...# one #.?.# #...# one ...#...
   * 
   * @param data
   * @param factor
   * @param distance
   */
  public void despikeHorizontally(double[][] data, double factor, int distance) {
    // for lines
    for (int i = 1; i < data.length - 1; i++) {
      double[] line = data[i];

      // calc first average
      windowSum = 0;
      windowN = 0;

      // square
      for (int dp = 0; dp < 2 + distance; dp++) {
        addDP(data[i - 1][dp]);
        addDP(data[i + 1][dp]);
      }
      // add same row end cap
      addDP(data[i][distance + 1]);

      // despike values
      for (int dp = 0; dp < line.length; dp++) {
        // minimum number of datapoints to despike
        if (windowN > 4) {
          double windowAvg = windowSum / windowN;
          // spike?
          if (line[dp] > windowAvg * factor)
            line[dp] = windowAvg;
        }

        // move window to next
        int next = dp + distance + 2;
        if (next < data[i - 1].length)
          addDP(data[i - 1][next]);
        if (next < data[i + 1].length)
          addDP(data[i + 1][next]);
        if (next < line.length)
          addDP(line[next]);
        // remove middel one before next
        if (next - 1 < line.length)
          removeDP(line[next - 1]);

        // remove last points
        int last = dp - distance - 1;
        if (last >= 0) {
          removeDP(data[i - 1][last]);
          removeDP(data[i + 1][last]);
          removeDP(data[i][last]);
        }
        // add middle one after last
        if (last + 1 >= 0) {
          addDP(data[i][last + 1]);
        }
      }
    }
  }

  // TODO change to vertically
  public void despikeVertically(double[][] data, double factor, int distance) {
    // data[dp][line]
    // for lines
    for (int i = 1; i < data[0].length - 1; i++) {
      // calc first average
      windowSum = 0;
      windowN = 0;

      // square
      for (int dp = 0; dp < 2 + distance; dp++) {
        addDP(data[dp][i - 1]);
        addDP(data[dp][i + 1]);
      }
      // add same row end cap
      addDP(data[distance + 1][i]);

      // despike values
      for (int dp = 0; dp < data.length; dp++) {
        // minimum number of datapoints to despike
        if (windowN > 4) {
          double windowAvg = windowSum / windowN;
          // spike?
          if (data[dp][i] > windowAvg * factor)
            data[dp][i] = windowAvg;
        }

        // move window to next
        int next = dp + distance + 2;
        if (next < data.length) {
          addDP(data[next][i - 1]);
          addDP(data[next][i + 1]);
          addDP(data[next][i]);

          // remove middle one before next
          removeDP(data[next - 1][i]);
        }

        // remove last points
        int last = dp - distance - 1;
        if (last > 0) {
          removeDP(data[last][i - 1]);
          removeDP(data[last][i]);
          removeDP(data[last][i + 1]);
        }
        // add middle one after last
        if (last + 1 >= 0) {
          addDP(data[last + 1][i]);
        }
      }
    }
  }

  protected void addDP(double z) {
    if (!Double.isNaN(z)) {
      windowSum += z;
      windowN++;
    }
  }

  protected void removeDP(double z) {
    if (!Double.isNaN(z)) {
      windowSum -= z;
      windowN--;
    }
  }

  @Override
  public double[][] processItensity(double[][] z, double[][] target) {
    for (int i = 0; i < z.length; i++)
      for (int j = 0; j < z[i].length; j++)
        target[i][j] = z[i][j];

    for (int distance : despikeMatrix)
      if (!isRotated)
        despikeHorizontally(target, factor, distance);
      else
        despikeVertically(target, factor, distance);

    return target;
  }

  @Override
  public float[][] processXY(float[][] x, float[][] target) {
    return x;
  }

  @Override
  public boolean isProcessingXY() {
    return false;
  }

}
