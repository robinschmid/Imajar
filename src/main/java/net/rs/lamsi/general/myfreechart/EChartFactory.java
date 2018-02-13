package net.rs.lamsi.general.myfreechart;

import java.awt.Color;
import java.util.function.DoubleFunction;
import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class EChartFactory {

  public static JFreeChart createHistogram(double[] data, double binwidth) {
    return createHistogram(data, binwidth, null);
  }

  public static JFreeChart createHistogram(double[] data, double binwidth, String yAxisLabel) {
    Range r = getBounds(data);
    return createHistogram(data, binwidth, yAxisLabel, r.getLowerBound(), r.getUpperBound(),
        val -> val);
  }

  public static JFreeChart createHistogram(double[] data, double binwidth, String yAxisLabel,
      double min, double max, DoubleFunction<Double> function) {
    if (data != null && data.length > 0) {
      double datawidth = (max - min);
      int cbin = (int) Math.ceil(datawidth / binwidth);
      int[] bins = new int[cbin + 1];

      // if value>bin.upper put in next
      for (double v : data) {
        int i = (int) Math.ceil((v - min) / binwidth) - 1;
        if (i < 0) // does only happen if min>than minimum value of data
          i = 0;
        bins[i]++;
      }

      // gaussian fit
      GaussianCurveFitter fitter = GaussianCurveFitter.create();
      WeightedObservedPoints obs = new WeightedObservedPoints();

      double gMin = 35, gMax = 55;
      double gWidth = gMax - gMin;

      int sum = 0;
      XYSeries series = new XYSeries("histo", true, true);
      for (int i = 0; i < bins.length; i++) {
        if (bins[i] > 0) {
          double x = function.apply(min + (binwidth / 2.0) + i * binwidth).doubleValue();
          series.add(x, bins[i]);
          sum += bins[i];
          // add to gaussian
          if (x >= gMin && x <= gMax)
            obs.add(x, bins[i]);
        }
      }


      double[] bestFit = fitter.fit(obs.toList());

      Gaussian g = new Gaussian(bestFit[0], bestFit[1], bestFit[2]);

      XYSeries gs = new XYSeries("Gaussian");
      int steps = 1000;
      for (int i = 0; i <= steps; i++) {
        double x = gMin + (gWidth / steps) * i;
        double y = normal.value(x);
        gs.add(x, y);
      }
      XYSeriesCollection gsdata = new XYSeriesCollection(gs);


      // see when 98% of the data is displayed
      int sum2 = 0;
      double barwidth = 0;
      for (int i = 0; i < bins.length; i++) {
        if (bins[i] > 0) {
          sum2 += bins[i];
          if ((sum2 / (double) sum) >= 0.99) {
            barwidth = function.apply(min + (binwidth / 2.0) + i * binwidth).doubleValue()
                - function.apply(min + (binwidth / 2.0) + (i - 1) * binwidth).doubleValue();
          }
        }
      }

      XYSeriesCollection xydata = new XYSeriesCollection(series);
      XYBarDataset dataset = new XYBarDataset(xydata, barwidth);

      // HistogramDataset dataset = new HistogramDataset();
      // dataset.addSeries("histo", data, cbin, min, max);

      // JFreeChart chart = ChartFactory.createHistogram("", yAxisLabel, "n", dataset,
      // PlotOrientation.VERTICAL, true, false, false);
      JFreeChart chart = ChartFactory.createXYBarChart("", yAxisLabel, false, "n", dataset,
          PlotOrientation.VERTICAL, true, false, false);

      XYPlot xyplot = chart.getXYPlot();
      // add gaussian
      xyplot.setDataset(1, gsdata);
      xyplot.setRenderer(1, new XYLineAndShapeRenderer(true, false));

      // JFreeChart chart = ChartFactory.createXYLineChart("", yAxisLabel, "n", xydata,
      // PlotOrientation.VERTICAL, true, false, false);


      chart.setBackgroundPaint(new Color(230, 230, 230));
      chart.getLegend().setVisible(false);
      xyplot.setForegroundAlpha(0.7F);
      xyplot.setBackgroundPaint(Color.WHITE);
      xyplot.setDomainGridlinePaint(new Color(150, 150, 150));
      xyplot.setRangeGridlinePaint(new Color(150, 150, 150));
      xyplot.getDomainAxis().setVisible(true);
      xyplot.getRangeAxis().setVisible(yAxisLabel != null);
      XYBarRenderer xybarrenderer = (XYBarRenderer) xyplot.getRenderer();
      xybarrenderer.setShadowVisible(false);
      xybarrenderer.setBarPainter(new StandardXYBarPainter());
      xybarrenderer.setDrawBarOutline(false);
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
    double bin = Math.sqrt(data.length);
    Range r = getBounds(data);
    return createHistogram(data, r.getLength() / bin);
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
    Range r = getBounds(data);
    return createHistogram(data, r.getLength() / bin, yAxisLabel, min, max, function);
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
      return createHistogram(data, width, yAxisLabel, min, max, val -> val);
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
      return createHistogram(data, width, yAxisLabel, min, max, function);
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
