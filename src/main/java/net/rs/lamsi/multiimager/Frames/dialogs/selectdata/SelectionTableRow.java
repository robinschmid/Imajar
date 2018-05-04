package net.rs.lamsi.multiimager.Frames.dialogs.selectdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import org.jfree.chart.ChartPanel;
import net.rs.lamsi.general.myfreechart.EChartFactory;

public class SelectionTableRow implements Serializable {
  // do not change the version!
  private static final long serialVersionUID = 1L;

  private transient ChartPanel histo;

  // only used for stats calculation
  // use finalise to free this list
  private ArrayList<Double> data = null;

  // statistics
  private double max, min, median, p99, avg, sdev, sum;
  private int n;

  // keep data for rows/columns for blank subtraction
  // private boolean keepDataForRowsCols = false;


  public SelectionTableRow() {
    super();
  }


  // statistics calculation
  public void addValue(double i) {
    // init?
    if (data == null) {
      data = new ArrayList<Double>();
      max = Double.NEGATIVE_INFINITY;
      min = Double.POSITIVE_INFINITY;
      sum = 0;
    }
    data.add(i);
    if (i < min)
      min = i;
    if (i > max)
      max = i;
    // first sum
    sum += i;
  }

  /**
   * final stats calculation after all data points were added via check. run clearData later to
   * clear memory
   */
  public void calculateStatistics() {
    if (data == null || data.isEmpty()) {
      reset();
      return;
    } else {
      // create histo
      // copy to double array
      double[] array = new double[data.size()];
      for (int i = 0; i < data.size(); i++) {
        array[i] = data.get(i);
      }
      histo = new ChartPanel(EChartFactory.createHistogram(array));

      // for percentiles and median
      Collections.sort(data);

      median = data.get(Math.max(0, data.size() / 2 - 1));
      p99 = data.get(Math.max(0, (int) Math.round(data.size() * 0.99 - 1)));

      n = data.size();
      // average and sdev
      avg = sum / (double) n;
      // stdev
      sdev = 0;

      for (double d : data) {
        sdev += Math.pow(d - avg, 2);
      }
      // calc stdev
      sdev = Math.sqrt(sdev / (double) (data.size() - 1));

      // erase data
      // need to keep for histogram
      // erase later TODO
      // data = null;
    }
  }

  public void reset() {
    min = 0;
    max = 0;
    median = 0;
    p99 = 0;
    avg = 0;
    n = 0;
    sdev = 0;
    sum = 0;
    histo = null;
  }


  public ArrayList<Double> getData() {
    return data;
  }

  public void clearData() {
    data = null;
  }

  public ChartPanel getHisto() {
    return histo;
  }

  public void setHisto(ChartPanel histo) {
    this.histo = histo;
  }

  public double getMax() {
    return max;
  }

  public double getMin() {
    return min;
  }

  public double getMedian() {
    return median;
  }

  public double getP99() {
    return p99;
  }

  public double getAvg() {
    return avg;
  }

  public double getSdev() {
    return sdev;
  }

  public int getN() {
    return n;
  }

  public double getSum() {
    return sum;
  }
}
