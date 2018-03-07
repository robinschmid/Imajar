package net.rs.lamsi.general.datamodel.image.interf;

import java.util.Arrays;
import org.jfree.data.Range;
import net.rs.lamsi.general.settings.SettingsContainerSettings;

public abstract class DataCollectable2D<T extends SettingsContainerSettings>
    extends Collectable2D<T> {

  /**
   * minimum intensity processed
   * 
   * @return
   */
  public double getMinIntensity(boolean onlySelected) {
    checkForUpdateInParentIProcessing();
    if (onlySelected) {
      if (Double.isNaN(minZSelected)) {
        minZSelected = Double.POSITIVE_INFINITY;
        // array with values only (no NaN)
        double[] inten = toIArray(false, onlySelected);
        for (double i : inten)
          if (i < minZSelected)
            minZSelected = i;
      }

      if (minZSelected == Double.POSITIVE_INFINITY) {
        minZSelected = Double.NaN;
        return Double.NaN;
      }
      return minZSelected;
    } else {
      if (Double.isNaN(minZ)) {
        minZ = Double.POSITIVE_INFINITY;
        // array with values only (no NaN)
        double[] inten = toIArray(false, onlySelected);
        for (double i : inten)
          if (i < minZ)
            minZ = i;
      }

      if (minZ == Double.POSITIVE_INFINITY) {
        minZ = Double.NaN;
        return Double.NaN;
      }
      return minZ;
    }
  }

  /**
   * minimum intensity that is not zero processed
   * 
   * @return
   */
  public double getMinNonZeroIntensity(boolean onlySelected) {
    checkForUpdateInParentIProcessing();
    if (onlySelected) {
      if (Double.isNaN(minNonZeroZSelected)) {
        minNonZeroZSelected = Double.POSITIVE_INFINITY;
        // array with values only (no NaN)
        double[] inten = toIArray(false, onlySelected);
        for (double i : inten)
          if (i < minNonZeroZSelected && i != 0)
            minNonZeroZSelected = i;
      }

      if (minNonZeroZSelected == Double.POSITIVE_INFINITY) {
        minNonZeroZSelected = Double.NaN;
        return Double.NaN;
      }
      return minNonZeroZSelected;
    } else {
      if (Double.isNaN(minNonZeroZ)) {
        // array with values only (no NaN)
        double[] inten = toIArray(false, onlySelected);
        minNonZeroZ = Double.POSITIVE_INFINITY;
        for (double i : inten)
          if (i < minNonZeroZ && i != 0)
            minNonZeroZ = i;
      }

      if (minNonZeroZ == Double.POSITIVE_INFINITY) {
        minNonZeroZ = Double.NaN;
        return Double.NaN;
      }
      return minNonZeroZ;
    }
  }

  /**
   * maximum intensity processed
   * 
   * @return
   */
  public double getMaxIntensity(boolean onlySelected) {
    checkForUpdateInParentIProcessing();
    if (onlySelected) {
      if (Double.isNaN(maxZSelected)) {
        maxZSelected = Double.NEGATIVE_INFINITY;
        // array with values only (no NaN)
        double[] inten = toIArray(false, onlySelected);
        for (double i : inten)
          if (i > maxZSelected)
            maxZSelected = i;
      }

      if (maxZSelected == Double.NEGATIVE_INFINITY) {
        maxZSelected = Double.NaN;
        return Double.NaN;
      }
      return maxZSelected;
    } else {
      if (Double.isNaN(maxZ)) {
        maxZ = Double.NEGATIVE_INFINITY;
        // array with values only (no NaN)
        double[] inten = toIArray(false, onlySelected);
        for (double i : inten)
          if (i > maxZ)
            maxZ = i;
      }

      if (maxZ == Double.NEGATIVE_INFINITY) {
        maxZ = Double.NaN;
        return Double.NaN;
      }
      return maxZ;
    }
  }


  /**
   * Calcs the average I for this img
   * 
   * @return
   */
  public double getAverageIntensity(boolean onlySelected) {
    checkForUpdateInParentIProcessing();
    if (onlySelected) {
      if (Double.isNaN(avgZSelected)) {
        avgZSelected = 0;

        // array with values only (no NaN)
        double[] inten = toIArray(false, onlySelected);
        for (double i : inten)
          avgZSelected += i;

        avgZSelected = avgZSelected / inten.length;
      }
      return avgZSelected;
    } else {
      //
      if (Double.isNaN(averageIProcessed)) {
        averageIProcessed = 0;

        // array with values only (no NaN)
        double[] inten = toIArray(false, onlySelected);
        for (double i : inten)
          averageIProcessed += i;

        averageIProcessed = averageIProcessed / inten.length;
      }
      return averageIProcessed;
    }
  }


  /**
   * [min, max]
   * 
   * @param onlySelected
   * @return
   */
  public Range getIRange(boolean onlySelected) {
    return new Range(this.getMinIntensity(onlySelected), this.getMaxIntensity(onlySelected));
  }

  /**
   * 
   * @return value (set in a paintscale) as a percentage of the maximum value (value==max:
   *         result=100)
   */
  public double getIPercentage(double intensity, boolean onlySelected) {
    Range r = getIRange(onlySelected);
    return ((intensity - r.getLowerBound()) / r.getLength() * 100.0);
  }

  /**
   * 
   * @param value as percentage (0-100%)
   * @param onlySelected
   * @return value /100 * intensityRange
   */
  public double getIAbs(double value, boolean onlySelected) {
    Range r = getIRange(onlySelected);
    return value / 100.0 * r.getLength() + r.getLowerBound();
  }

  /**
   * 
   * @param intensity
   * @return the percentile of all intensities (if value is equal to max the result is 100)
   */
  public double getIPercentile(boolean raw, double intensity, boolean onlySelected) {
    // sort all z values
    double[] z = null;
    if (!onlySelected)
      z = toIArray(raw);
    else
      z = getSelectedDataAsArray(raw, true);
    Arrays.sort(z);

    for (int i = 0; i < z.length; i++) {
      if (z[i] <= intensity) {
        return (i / (z.length - 1));
      }
    }
    return 0;
  }
}
