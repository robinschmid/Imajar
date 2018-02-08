package net.rs.lamsi.general.myfreechart;

import java.awt.Color;
import java.util.function.DoubleFunction;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.Range;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class EChartFactory {

  public static JFreeChart createHistogram(double[] data, int bin) {
    return createHistogram(data, bin, null);
  }

  public static JFreeChart createHistogram(double[] data, int bin, String yAxisLabel) {
    Range r = getBounds(data);
    return createHistogram(data, bin, yAxisLabel, r.getLowerBound(), r.getUpperBound(), val -> val);
  }

  public static JFreeChart createHistogram(double[] data, int cbin, String yAxisLabel, double min,
      double max, DoubleFunction<Double> function) {
    if (data != null && data.length > 0) {
      double datawidth = (max - min);
      double binwidth = datawidth / cbin;
      int[] bins = new int[cbin + 1];

      // if value>bin.upper put in next
      for (double v : data) {
        int i = (int) Math.ceil((v - min) / binwidth);
        bins[i]++;
      }

      XYSeries series = new XYSeries("histo", true, true);
      for (int i = 0; i < bins.length; i++) {
        if (bins[i] > 0) {
          series.add(function.apply(min + i * binwidth).doubleValue(), bins[i]);
          series.add(function.apply(min + (i + 1) * binwidth).doubleValue(), bins[i]);
        }dada
      }

      XYSeriesCollection xydata = new XYSeriesCollection(series);
      XYBarDataset dataset = new XYBarDataset(xydata, binwidth);

      // HistogramDataset dataset = new HistogramDataset();
      // dataset.addSeries("histo", data, bin, min, max);

      // JFreeChart chart = ChartFactory.createHistogram("", yAxisLabel, "n", dataset,
      // PlotOrientation.VERTICAL, true, false, false);
      // JFreeChart chart = ChartFactory.createXYBarChart("", yAxisLabel, false, "n", dataset,
      // PlotOrientation.VERTICAL, true, false, false);

      JFreeChart chart = ChartFactory.createXYLineChart("", yAxisLabel, "n", xydata,
          PlotOrientation.VERTICAL, true, false, false);


      chart.setBackgroundPaint(new Color(230, 230, 230));
      chart.getLegend().setVisible(false);
      XYPlot xyplot = chart.getXYPlot();
      xyplot.setForegroundAlpha(0.7F);
      xyplot.setBackgroundPaint(Color.WHITE);
      xyplot.setDomainGridlinePaint(new Color(150, 150, 150));
      xyplot.setRangeGridlinePaint(new Color(150, 150, 150));
      xyplot.getDomainAxis().setVisible(true);
      xyplot.getRangeAxis().setVisible(yAxisLabel != null);
      // XYBarRenderer xybarrenderer = (XYBarRenderer) xyplot.getRenderer();
      // xybarrenderer.setShadowVisible(false);
      // xybarrenderer.setBarPainter(new StandardXYBarPainter());
      // xybarrenderer.setDrawBarOutline(false);
      return chart;
    } else
      return null;
  }

  public static JFreeChart createHistogramOld(double[] data, int bin, String yAxisLabel, double min,
      double max) {
    if (data != null && data.length > 0) {
      HistogramDataset dataset = new HistogramDataset();
      dataset.addSeries("histo", data, bin, min, max);

      JFreeChart chart = ChartFactory.createHistogram("", yAxisLabel, "n", dataset,
          PlotOrientation.VERTICAL, true, false, false);

      chart.setBackgroundPaint(new Color(230, 230, 230));
      chart.getLegend().setVisible(false);
      XYPlot xyplot = chart.getXYPlot();
      xyplot.setForegroundAlpha(0.7F);
      xyplot.setBackgroundPaint(Color.WHITE);
      xyplot.setDomainGridlinePaint(new Color(150, 150, 150));
      xyplot.setRangeGridlinePaint(new Color(150, 150, 150));
      xyplot.getDomainAxis().setVisible(true);
      xyplot.getRangeAxis().setVisible(yAxisLabel != null);
      XYBarRenderer xybarrenderer = (XYBarRenderer) xyplot.getRenderer();
      xybarrenderer.setShadowVisible(false);
      xybarrenderer.setBarPainter(new StandardXYBarPainter());
      // xybarrenderer.setDrawBarOutline(false);
      return chart;
    } else
      return null;
  }

  public static JFreeChart createHistogram(double[] data) {
    int bin = (int) Math.sqrt(data.length);
    return createHistogram(data, bin);
  }

  public static JFreeChart createHistogram(double[] data, String yAxisLabel) {
    Range range = getBounds(data);
    return createHistogram(data, yAxisLabel, range.getLowerBound(), range.getUpperBound());
  }

  public static JFreeChart createHistogram(double[] data, String yAxisLabel, double min,
      double max) {
    return createHistogram(data, yAxisLabel, min, max, val -> val);
  }

  public static JFreeChart createHistogram(double[] data, String yAxisLabel, double min, double max,
      DoubleFunction<Double> function) {
    int bin = (int) Math.sqrt(data.length);
    return createHistogram(data, bin, yAxisLabel, min, max, function);
  }

  /**
   * 
   * @param data
   * @param yAxisLabel
   * @param width automatic width if parameter is <=0
   * @return
   */
  public static JFreeChart createHistogram(double[] data, String yAxisLabel, double width) {
    Range range = getBounds(data);
    return createHistogram(data, yAxisLabel, width, range.getLowerBound(), range.getUpperBound());
  }

  /**
   * 
   * @param data
   * @param yAxisLabel
   * @param width automatic width if parameter is <=0
   * @return
   */
  public static JFreeChart createHistogram(double[] data, String yAxisLabel, double width,
      double min, double max) {
    if (width <= 0)
      return createHistogram(data, yAxisLabel, min, max);
    else {
      int bin = (int) ((max - min) / width);
      return createHistogram(data, bin, yAxisLabel, min, max, val -> val);
    }
  }

  /**
   * 
   * @param data
   * @param yAxisLabel
   * @param width automatic width if parameter is <=0
   * @param function transform the data axis after binning
   * @return
   */
  public static JFreeChart createHistogram(double[] data, String yAxisLabel, double width,
      double min, double max, DoubleFunction<Double> function) {
    if (width <= 0)
      return createHistogram(data, yAxisLabel, min, max, function);
    else {
      int bin = (int) ((max - min) / width);
      return createHistogram(data, bin, yAxisLabel, min, max, function);
    }
  }

  public static double getMin(double[] data) {
    double min = Double.MAX_VALUE;
    for (double d : data)
      if (d < min)
        min = d;
    return min;
  }

  public static double getMax(double[] data) {
    double max = Double.NEGATIVE_INFINITY;
    for (double d : data)
      if (d > max)
        max = d;
    return max;
  }

  public static Range getBounds(double[] data) {
    double min = Double.MAX_VALUE;
    double max = Double.NEGATIVE_INFINITY;
    for (double d : data) {
      if (d < min)
        min = d;
      if (d > max)
        max = d;
    }
    return new Range(min, max);
  }
}
