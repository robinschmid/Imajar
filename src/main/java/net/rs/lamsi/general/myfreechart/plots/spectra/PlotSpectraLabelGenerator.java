package net.rs.lamsi.general.myfreechart.plots.spectra;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.Vector;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.data.xy.XYDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.settings.SettingsChargeCalculator;
import net.rs.lamsi.general.settings.visualization.SettingsPlotSpectraLabelGenerator;
import net.rs.lamsi.massimager.Frames.Window;
import net.rs.lamsi.massimager.MyMZ.preprocessing.filtering.peaklist.chargecalculation.MZChargeCalculatorDouble;

public class PlotSpectraLabelGenerator implements XYItemLabelGenerator {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  /*
   * Number of screen pixels to reserve for each label, so that the labels do not overlap
   */
  public int POINTS_RESERVE_X = 100;

  private ChartPanel plot;

  private DecimalFormat mzFormat = new DecimalFormat("#,###.####");

  public PlotSpectraLabelGenerator(ChartPanel plot) {
    this.plot = plot;
  }

  /**
   * @see org.jfree.chart.labels.XYItemLabelGenerator#generateLabel(org.jfree.data.xy.XYDataset,
   *      int, int)
   */
  public String generateLabel(XYDataset dataset, int series, int item) {
    SettingsPlotSpectraLabelGenerator settings =
        Window.getWindow().getSettings().getSetVisPlotSpectraLabelGenerator();
    // no labels?
    if (!settings.isShowLabels())
      return null;

    // distance
    POINTS_RESERVE_X = settings.getMinimumSpaceBetweenLabels();

    // X and Y values of current data point
    double originalX = dataset.getX(series, item).doubleValue();
    double originalY = dataset.getY(series, item).doubleValue();

    // Calc relative intensity
    double yUpperBound =
        (double) plot.getChart().getXYPlot().getRangeAxis().getRange().getUpperBound();
    double relativeInt = originalY / yUpperBound;
    // if this is not a relevant peak return null
    if (relativeInt < settings.getMinimumRelativeIntensityOfLabel()) {
      return null;
    }

    // Calculate data size of 1 screen pixel
    double xLength = (double) plot.getChart().getXYPlot().getDomainAxis().getRange().getLength();
    double pixelX = xLength / plot.getWidth();


    // Size of data set
    int itemCount = dataset.getItemCount(series);

    // Search for data points higher than this one in the interval
    // from limitLeft to limitRight
    double limitLeft = originalX - ((POINTS_RESERVE_X / 2) * pixelX);
    double limitRight = originalX + ((POINTS_RESERVE_X / 2) * pixelX);

    // Iterate data points to the left and right
    for (int i = 1; (item - i > 0) || (item + i < itemCount); i++) {

      // If we get out of the limit we can stop searching
      if ((item - i > 0) && (dataset.getXValue(series, item - i) < limitLeft)
          && ((item + i >= itemCount) || (dataset.getXValue(series, item + i) > limitRight)))
        break;

      if ((item + i < itemCount) && (dataset.getXValue(series, item + i) > limitRight)
          && ((item - i <= 0) || (dataset.getXValue(series, item - i) < limitLeft)))
        break;

      // If we find higher data point, bail out
      if ((item - i > 0) && (originalY <= dataset.getYValue(series, item - i)))
        return null;

      if ((item + i < itemCount) && (originalY <= dataset.getYValue(series, item + i)))
        return null;

    }

    // Create label
    String label = null;
    if (label == null) {
      double mzValue = dataset.getXValue(series, item);
      label = mzFormat.format(mzValue);
    }

    // Calc and show charge?
    if (settings.isShowCharge()) {
      // Calc Charge and display if isotope pattern found
      int charge = calcChargeByIsotopePattern(dataset, series, item);
      if (charge != 0) {
        label = "z=" + charge + "\n" + label;
      }
    }
    //
    return label;
  }

  /*
   * Calculates Charge by isotope pattern returns 0 if there is no isotope pattern
   */
  private int calcChargeByIsotopePattern(XYDataset dataset, int series, int item) {
    SettingsChargeCalculator settings = Window.getWindow().getSettings().getSetChargeCalc();
    if (settings == null)
      settings = new SettingsChargeCalculator();
    int charge = 0;

    // find all peaks in range and put them in this vector
    Vector<Point2D> peaks = new Vector<Point2D>();
    // + direction
    getPossiblePeaksInDistance(peaks, dataset, series, item, +1);
    // - direction
    getPossiblePeaksInDistance(peaks, dataset, series, item, -1);

    // get maximum intensity
    double maxInt = 0;
    for (Point2D p : peaks)
      if (p.getY() > maxInt)
        maxInt = p.getY();
    // delete peaks with low intensity
    for (int i = 0; i < peaks.size(); i++) {
      double relInt = peaks.get(i).getY() / maxInt;
      if (relInt < 0.025) {
        peaks.remove(i);
        i--;
      }
    }

    // isotope pattern? use filter
    MZChargeCalculatorDouble chargeFilter = new MZChargeCalculatorDouble(peaks, settings,
        new Point2D.Double(dataset.getXValue(series, item), dataset.getYValue(series, item)));
    charge = chargeFilter.doFiltering();
    // no charge found = 0
    return charge;
  }

  /*
   * Searches for peaks in a possible range maximal distance is 1
   */
  private void getPossiblePeaksInDistance(Vector<Point2D> peaks, XYDataset dataset, int series,
      int item, int direction) {
    // if not in bounds return
    if (!(item >= 0 && item < dataset.getItemCount(series)))
      return;
    // distance
    double distance = 1.5;
    double startMZ = dataset.getXValue(series, item);
    boolean peakStartFound = false;
    double peakMZ = -1;
    double peakMaxIntensity = -1;
    // all datapoints in direction
    int i = item;
    do {
      i += direction;
      // only if i-2direction is in bounds
      try {
        if (i - direction >= 0 && i - direction < dataset.getItemCount(series) && i >= 0
            && i < dataset.getItemCount(series)) {
          double lastiIntensity = dataset.getYValue(series, i - direction);
          double iIntensity = dataset.getYValue(series, i);
          // no peak found
          if (!peakStartFound) {
            if (iIntensity > 0 && lastiIntensity > 0 && iIntensity * 2 / 3 > lastiIntensity) {
              // peak found
              peakStartFound = true;
              peakMZ = dataset.getXValue(series, i);
              peakMaxIntensity = iIntensity;
            }
          }
          // peak already found
          else {
            // intensity higher?
            if (iIntensity > lastiIntensity && iIntensity > peakMaxIntensity) {
              peakMZ = dataset.getXValue(series, i);
              peakMaxIntensity = iIntensity;
            }
            // end of peak? 3 points with decreasing tendence or half of maxIntensity
            if ((iIntensity <= lastiIntensity
                && lastiIntensity <= dataset.getYValue(series, i - direction * 2))
                || iIntensity == 0 || iIntensity <= peakMaxIntensity / 2) {
              // add peak to list TODO
              if (direction == 1)
                peaks.add(new Point2D.Double(peakMZ, peakMaxIntensity));
              else
                peaks.add(0, new Point2D.Double(peakMZ, peakMaxIntensity));
              // Start new search
              getPossiblePeaksInDistance(peaks, dataset, series, i, direction);
              // END METHOD
              return;
            }
          }
        }
      } catch (Exception ex) {
        logger.error("", ex);
      }
      // stop when XYData end reached or out of distance
    } while (i > 0 && i < dataset.getItemCount(series)
        && distance > Math.abs(startMZ - dataset.getXValue(series, i)));

  }

}
