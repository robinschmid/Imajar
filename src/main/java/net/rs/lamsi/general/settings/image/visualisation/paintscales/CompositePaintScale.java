package net.rs.lamsi.general.settings.image.visualisation.paintscales;

import java.awt.Paint;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.renderer.PaintScale;

public class CompositePaintScale implements PaintScale, Serializable {

  private static final long serialVersionUID = 1L;

  // scale 0 is used from threshold[0] to <threshold[1]
  private List<PaintScale> scales;
  // threshold 0 is the lower bound
  private List<Double> thresholds;
  private double upperBound = -1;

  @Override
  public double getLowerBound() {
    return thresholds != null ? thresholds.get(0) : -1;
  }

  @Override
  public double getUpperBound() {
    return upperBound;
  }

  /**
   * Set upper bound (max) threshold[0] is lower bound
   * 
   * @param max
   */
  public void setUpperBound(double max) {
    upperBound = max;
  }

  public void addScale(PaintScale scale, double threshold) {
    if (scales == null) {
      scales = new ArrayList<PaintScale>();
      this.thresholds = new ArrayList<Double>();
    }
    // sort
    // threshold is > max?
    int index = scales.size() - 1;
    while (threshold < thresholds.get(index)) {
      index--;
    }
    index++;
    scales.add(index, scale);
    thresholds.add(index, threshold);
  }


  @Override
  public Paint getPaint(double value) {
    for (int i = scales.size() - 1; i >= 0; i--) {
      if (thresholds.get(i) <= value) {
        return scales.get(i).getPaint(value);
      }
    }
    // should never happen
    return null;
  }

}
