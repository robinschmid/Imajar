package net.rs.lamsi.general.processing.dataoperations;

import java.util.Arrays;
import java.util.stream.Stream;
import net.rs.lamsi.general.heatmap.dataoperations.PostProcessingOp;

public class SpikeRemoverNegative extends PostProcessingOp {

  private SpikeRemover remover;

  public SpikeRemoverNegative(SpikeRemover remover) {
    super();
    this.remover = remover;
  }

  @Override
  public double[][] processItensity(double[][] z, double[][] target) {
    double[][] removed = remover.processItensity(z);

    double min = Stream.of(z).flatMapToDouble(Arrays::stream).min().orElse(0);

    for (int i = 0; i < removed.length; i++) {
      for (int j = 0; j < removed[i].length; j++) {
        // only keep spikes that were removed
        if (Double.compare(removed[i][j], z[i][j]) == 0)
          target[i][j] = min;
        else
          target[i][j] = z[i][j];
      }
    }

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
