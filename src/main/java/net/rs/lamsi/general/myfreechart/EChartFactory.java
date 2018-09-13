package net.rs.lamsi.general.myfreechart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.util.function.DoubleFunction;
import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.utils.math.Precision;

public class EChartFactory {
  private static final Logger logger = LoggerFactory.getLogger(EChartFactory.class);



  /**
   * Performs Gaussian fit on XYSeries
   * 
   * @param series the data
   * @param gMin lower bound of Gaussian fit
   * @param gMax upper bound of Gaussian fit
   * @param sigDigits number of significant digits
   * @return double[] {normFactor, mean, sigma} as a result of
   *         GaussianCurveFitter.create().fit(obs.toList())
   */
  public static double[] gaussianFit(XYSeries series, double gMin, double gMax) {
    // gaussian fit
    WeightedObservedPoints obs = new WeightedObservedPoints();

    for (int i = 0; i < series.getItemCount(); i++) {
      double x = series.getX(i).doubleValue();
      if (x >= gMin && x <= gMax)
        obs.add(x, series.getY(i).doubleValue());
    }

    double[] fit = GaussianCurveFitter.create().fit(obs.toList());
    return fit;
  }

  /**
   * Performs Gaussian fit on XYSeries
   * 
   * @param data the data
   * @param series the series index
   * @param gMin lower bound of Gaussian fit
   * @param gMax upper bound of Gaussian fit
   * @param sigDigits number of significant digits
   * @return double[] {normFactor, mean, sigma} as a result of
   *         GaussianCurveFitter.create().fit(obs.toList())
   */
  public static double[] gaussianFit(XYDataset data, int series, double gMin, double gMax) {
    // gaussian fit
    WeightedObservedPoints obs = new WeightedObservedPoints();

    for (int i = 0; i < data.getItemCount(series); i++) {
      double x = data.getXValue(series, i);
      if (x >= gMin && x <= gMax)
        obs.add(x, data.getYValue(series, i));
    }
    double[] fit = GaussianCurveFitter.create().fit(obs.toList());
    return fit;
  }

  /**
   * Adds a Gaussian curve to the plot
   * 
   * @param plot
   * @param series the data
   * @param gMin lower bound of Gaussian fit
   * @param gMax upper bound of Gaussian fit
   * @param sigDigits number of significant digits
   * @return
   */
  public static double[] addGaussianFit(XYPlot plot, XYSeries series, double gMin, double gMax,
      int sigDigits, boolean annotations) {
    double[] fit = gaussianFit(series, gMin, gMax);
    double minval = series.getX(0).doubleValue();
    double maxval = series.getX(series.getItemCount() - 1).doubleValue();
    return addGaussianFit(plot, fit, minval, maxval, gMin, gMax, sigDigits, annotations);
  }

  /**
   * Adds a Gaussian curve to the plot
   * 
   * @param plot
   * @param data the data
   * @param series the series index
   * @param gMin lower bound of Gaussian fit
   * @param gMax upper bound of Gaussian fit
   * @param sigDigits number of significant digits
   * @return
   */
  public static double[] addGaussianFit(XYPlot plot, XYDataset data, int series, double gMin,
      double gMax, int sigDigits, boolean annotations) {
    double[] fit = gaussianFit(data, series, gMin, gMax);
    double minval = data.getX(series, 0).doubleValue();
    double maxval = data.getX(series, data.getItemCount(series) - 1).doubleValue();
    return addGaussianFit(plot, fit, minval, maxval, gMin, gMax, sigDigits, annotations);
  }

  /**
   * Adds a Gaussian curve to the plot
   * 
   * @param plot
   * @param fit double[] {normFactor, mean, sigma}
   * @param drawStart start of curve
   * @param drawEnd end of curve
   * @param sigDigits number of significant digits
   * @return
   */
  public static double[] addGaussianFit(XYPlot plot, double[] fit, double drawStart, double drawEnd,
      int sigDigits, boolean annotations) {
    return addGaussianFit(plot, fit, drawStart, drawEnd, drawStart, drawEnd, sigDigits,
        annotations);
  }

  /**
   * Adds a Gaussian curve to the plot
   * 
   * @param plot
   * @param fit double[] {normFactor, mean, sigma}
   * @param drawStart start of curve
   * @param drawEnd end of curve
   * @param gMin lower bound of Gaussian fit
   * @param gMax upper bound of Gaussian fit
   * @param sigDigits number of significant digits
   * @return
   */
  public static double[] addGaussianFit(XYPlot plot, double[] fit, double drawStart, double drawEnd,
      double gMin, double gMax, int sigDigits, boolean annotations) {
    double gWidth = gMax - gMin;

    Gaussian g = new Gaussian(fit[0], fit[1], fit[2]);

    // create xy series for gaussian
    String mean = Precision.toString(fit[1], sigDigits, 7);
    String sigma = Precision.toString(fit[2], sigDigits, 7);
    String norm = Precision.toString(fit[0], sigDigits, 7);
    XYSeries gs = new XYSeries("Gaussian: " + mean + " \u00B1 " + sigma + " [" + norm
        + "] (mean \u00B1 sigma [normalisation])");
    // add lower dp number out of gaussian fit range
    int steps = 100;
    if (gMin > drawStart) {
      for (int i = 0; i <= steps; i++) {
        double x = drawStart + ((gMin - drawStart) / steps) * i;
        double y = g.value(x);
        gs.add(x, y);
      }
    }
    // add high resolution in gaussian fit area
    steps = 1000;
    for (int i = 0; i <= steps; i++) {
      double x = gMin + (gWidth / steps) * i;
      double y = g.value(x);
      gs.add(x, y);
    }
    // add lower dp number out of gaussian fit range
    steps = 100;
    if (gMax < drawEnd) {
      for (int i = 0; i <= steps; i++) {
        double x = gMax + ((drawEnd - gMax) / steps) * i;
        double y = g.value(x);
        gs.add(x, y);
      }
    }
    // add gaussian
    XYSeriesCollection gsdata = new XYSeriesCollection(gs);
    int index = plot.getDatasetCount();
    plot.setDataset(index, gsdata);
    plot.setRenderer(index, new XYLineAndShapeRenderer(true, false));

    if (annotations)
      addGaussianFitAnnotations(plot, fit);

    return fit;
  }


  /**
   * Adds annotations to the Gaussian fit parameters
   * 
   * @param plot
   * @param fit Gaussian fit {normalisation factor, mean, sigma}
   */
  public static void addGaussianFitAnnotations(XYPlot plot, double[] fit) {
    Paint c = plot.getDomainCrosshairPaint();
    BasicStroke s = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1,
        new float[] {5f, 2.5f}, 0);

    plot.addDomainMarker(new ValueMarker(fit[1], c, s));
    plot.addDomainMarker(new ValueMarker(fit[1] - fit[2], c, s));
    plot.addDomainMarker(new ValueMarker(fit[1] + fit[2], c, s));
  }


  public static JFreeChart createHistogram(double[] data, double binwidth) {
    return createHistogram(data, binwidth, null);
  }

  /**
   * Zero bins are excluded
   * 
   * @param data
   * @param binwidth
   * @param yAxisLabel
   * @return
   */
  public static JFreeChart createHistogram(double[] data, double binwidth, String yAxisLabel) {
    return createHistogram(data, binwidth, yAxisLabel, false);
  }

  public static JFreeChart createHistogram(double[] data, double binwidth, String yAxisLabel,
      boolean includeZeroBins) {
    Range r = getBounds(data);
    return createHistogram(data, binwidth, yAxisLabel, r.getLowerBound(), r.getUpperBound(),
        val -> val, includeZeroBins);
  }

  public static JFreeChart createHistogram(double[] data, double binwidth, String yAxisLabel,
      double min, double max, DoubleFunction<Double> function, boolean includeZeroBins) {
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

      int sum = 0;
      XYSeries series = new XYSeries("histo", true, true);
      for (int i = 0; i < bins.length; i++) {
        if (includeZeroBins || bins[i] > 0) {
          double x = function.apply(min + (binwidth / 2.0) + i * binwidth).doubleValue();
          series.add(x, bins[i]);
          sum += bins[i];
        }
      }

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
      JFreeChart chart = ChartFactory.createXYBarChart("", yAxisLabel, false, "n", dataset,
          PlotOrientation.VERTICAL, true, true, false);

      XYPlot xyplot = chart.getXYPlot();
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
      boolean includeZeroBin) {
    return createHistogram(data, yAxisLabel, min, max, val -> val, includeZeroBin);
  }

  public static JFreeChart createHistogram(double[] data, String yAxisLabel, double min, double max,
      DoubleFunction<Double> function) {
    return createHistogram(data, yAxisLabel, min, max, function, false);
  }

  public static JFreeChart createHistogram(double[] data, String yAxisLabel, double min, double max,
      DoubleFunction<Double> function, boolean includeZeroBin) {
    int bin = (int) Math.sqrt(data.length);
    Range r = getBounds(data);
    return createHistogram(data, r.getLength() / bin, yAxisLabel, min, max, function,
        includeZeroBin);
  }

  /**
   * 
   * @param data
   * @param yAxisLabel
   * @param width automatic width if parameter is <=0
   * @return
   */
  public static JFreeChart createHistogram(double[] data, String yAxisLabel, double width,
      boolean includeZeroHistogram) {
    Range range = getBounds(data);
    return createHistogram(data, yAxisLabel, width, range.getLowerBound(), range.getUpperBound(),
        includeZeroHistogram);
  }

  /**
   * 
   * @param data
   * @param yAxisLabel
   * @param width automatic width if parameter is <=0
   * @return
   */
  public static JFreeChart createHistogram(double[] data, String yAxisLabel, double width,
      double min, double max, boolean includeZeroHistogram) {
    if (width <= 0)
      return createHistogram(data, yAxisLabel, min, max, includeZeroHistogram);
    else {
      return createHistogram(data, width, yAxisLabel, min, max, val -> val, includeZeroHistogram);
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
      double min, double max, DoubleFunction<Double> function, boolean includeZeroHistogram) {
    if (width <= 0)
      return createHistogram(data, yAxisLabel, min, max, function, includeZeroHistogram);
    else {
      return createHistogram(data, width, yAxisLabel, min, max, function, includeZeroHistogram);
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
