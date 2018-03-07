package net.rs.lamsi.general.datamodel.image.interf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jfree.data.Range;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIDataMatrix;
import net.rs.lamsi.general.settings.SettingsContainerSettings;
import net.rs.lamsi.general.settings.image.operations.listener.IntensityProcessingChangedListener;
import net.rs.lamsi.general.settings.image.selection.SettingsSelections;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale.ValueMode;

public abstract class DataCollectable2D<T extends SettingsContainerSettings>
    extends Collectable2D<T> implements PaintScaleTag, SelectionsTag {
  private static final long serialVersionUID = 1L;
  // image has nothing to do with quantifier class! so dont use a listener for data processing
  // changed events TODO
  protected List<IntensityProcessingChangedListener> listenerProcessingChanged = new ArrayList<>();
  // intensityProcessingChanged? save lastIProcChangeTime and compare with one from quantifier class
  protected int lastIProcChangeTime = 1;
  // are getting calculated only once or after processing changed
  // max and min z (intensity)
  protected double avgZ = -1;
  protected double minZ = Double.NaN, maxZ = Double.NaN;
  protected double minNonZeroZSelected = Double.NaN, minNonZeroZ = Double.NaN;
  protected double minZSelected = Double.NaN, maxZSelected = Double.NaN, avgZSelected = Double.NaN;
  protected double minZFiltered = Double.NaN;
  protected double maxZFiltered = Double.NaN;
  protected double lastAppliedMinFilter = -1, lastAppliedMaxFilter = -1;


  public DataCollectable2D(T settings) {
    super();
    this.settings = settings;
  }


  /**
   * total number of data points
   * 
   * @return
   */
  public abstract int getTotalDataPoints();

  /**
   * width of the image
   * 
   * @param raw
   * @return
   */
  public abstract float getWidth();

  /**
   * height of the image
   * 
   * @param raw
   * @return
   */
  public abstract float getHeight();

  public abstract float getX(boolean raw, int l, int dp);

  public abstract float getY(boolean raw, int l, int dp);

  public abstract double getI(boolean raw, int l, int dp);

  /**
   * generate XYI matrices [line][dp]
   * 
   * @param raw
   * @param useSettings rotation and imaging mode
   * @return
   */
  public abstract XYIDataMatrix toXYIDataMatrix(boolean raw, boolean useSettings);

  /**
   * minimum line length in regards to rotation columns of the image
   * 
   * @return
   */
  public abstract int getMinLineLength();

  /**
   * maximum line length in regards to rotation columns (dp) of the image
   * 
   * @return
   */
  public abstract int getMaxLineLength();

  /**
   * minimum lines count in regards to rotation rows of the image
   * 
   * @return
   */
  public abstract int getMinLinesCount();

  /**
   * maximum lines count in regards to rotation rows of the image
   * 
   * @return
   */
  public abstract int getMaxLinesCount();


  public abstract int getLineLength(int l);

  /**
   * 
   * @return
   */
  public abstract int getLineCount(int dp);

  /**
   * are l and dp in bounds (after rotation, reflection, ...)
   * 
   * @param l
   * @param dp
   * @return
   */
  public boolean isInBounds(int l, int dp) {
    return !(l < 0 || l >= getLineCount(dp) || dp < 0 || dp >= getLineLength(l));
  }

  /**
   * checks if this is a dp with data (because of rotation and different line length)
   * 
   * @param l
   * @param dp
   * @return
   */
  public boolean isDP(int l, int dp) {
    return !Double.isNaN(getI(true, l, dp));
  }

  /**
   * all intensities as one array limited to selections/exclusions or not
   * 
   * @return float intensity Array
   */
  public double[] toIArray(boolean raw, boolean onlySelected) {
    return toIArray(raw, onlySelected, true);
  }

  /**
   * all intensities as one array limited to selections/exclusions or not
   * 
   * @return float intensity Array
   */
  public abstract double[] toIArray(boolean raw, boolean onlySelected, boolean excluded);

  /**
   * checks if there is a parent that has changed I processing then it changes the processing here
   */
  protected void checkForUpdateInParentIProcessing() {}



  /**
   * maximum block width for renderer = distance between one and next block
   * 
   * @return
   */
  public abstract double getMaxBlockWidth();

  /**
   * maximum block height for renderer = distance between one and next block in lines
   * 
   * @return
   */
  public abstract double getMaxBlockHeight();

  //
  public int getLastIProcChangeTime() {
    return lastIProcChangeTime;
  }

  /**
   * save lastIProcChangeTime for comparison in all quantifiers. update all quantifiers if it has
   * changed
   */
  public void fireIntensityProcessingChanged() {
    // gives a indirect signal to Quantifier and children to change iProc
    lastIProcChangeTime++;
    if (lastIProcChangeTime >= Integer.MAX_VALUE - 1)
      lastIProcChangeTime = -1;

    minZ = Double.NaN;
    maxZ = Double.NaN;
    avgZ = Double.NaN;

    minZSelected = Double.NaN;
    maxZSelected = Double.NaN;
    avgZSelected = Double.NaN;

    minNonZeroZ = Double.NaN;
    minNonZeroZSelected = Double.NaN;

    minZFiltered = Double.NaN;
    maxZFiltered = Double.NaN;

    // applyCutFilter?
    lastAppliedMaxFilter = -1;
    lastAppliedMinFilter = -1;

    // register changes
    // e.g. for regression SettingsSelection
    for (IntensityProcessingChangedListener l : listenerProcessingChanged)
      l.fireIntensityProcessingChanged(this);
  }

  public void updateDataParameters() {
    // for selected
    // array with values only (no NaN)
    double[] selected = toIArray(false, true);
    minZSelected = selected.length == 0 ? 0 : Double.POSITIVE_INFINITY;
    maxZSelected = selected.length == 0 ? 0 : Double.NEGATIVE_INFINITY;
    avgZSelected = 0;
    for (double i : selected) {
      avgZSelected += i;
      if (i < minZSelected)
        minZSelected = i;
      if (i > maxZSelected)
        maxZSelected = i;
      if (i < minNonZeroZSelected && i != 0)
        minNonZeroZSelected = i;
    }
    avgZSelected /= selected.length;

    // all dp
    // array with values only (no NaN)
    double[] inten = toIArray(false, false);
    minZ = inten.length == 0 ? 0 : Double.POSITIVE_INFINITY;
    maxZ = inten.length == 0 ? 0 : Double.NEGATIVE_INFINITY;
    avgZ = 0;

    for (double i : inten) {
      avgZ += i;
      if (i < minZ)
        minZ = i;
      if (i > maxZ)
        maxZ = i;
      if (i < minNonZeroZ && i != 0)
        minNonZeroZ = i;
    }
    avgZ /= inten.length;

    // apply filters
    // paintscale?
    SettingsPaintScale ps = getPaintScaleSettings();
    if (ps != null) {
      if (ps.getModeMin().equals(ValueMode.PERCENTILE))
        applyCutFilterMin(ps.getMinFilter());
      if (ps.getModeMax().equals(ValueMode.PERCENTILE))
        applyCutFilterMax(ps.getMaxFilter());
    }
  }



  @Override
  public SettingsPaintScale getPaintScaleSettings() {
    return (SettingsPaintScale) settings.getSettingsByClass(SettingsPaintScale.class);
  }

  @Override
  public SettingsSelections getSelections() {
    return (SettingsSelections) settings.getSettingsByClass(SettingsSelections.class);
  }

  /**
   * minimum intensity processed
   * 
   * @return
   */
  public double getMinIntensity(boolean onlySelected) {
    checkForUpdateInParentIProcessing();
    if (onlySelected) {
      if (Double.isNaN(minZSelected))
        updateDataParameters();
      return minZSelected;
    } else {
      if (Double.isNaN(minZ))
        updateDataParameters();
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
      if (Double.isNaN(minNonZeroZSelected))
        updateDataParameters();

      return minNonZeroZSelected;
    } else {
      if (Double.isNaN(minNonZeroZ))
        updateDataParameters();

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
      if (Double.isNaN(maxZSelected))
        updateDataParameters();

      return maxZSelected;
    } else {
      if (Double.isNaN(maxZ))
        updateDataParameters();

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
      if (Double.isNaN(avgZSelected))
        updateDataParameters();
      return avgZSelected;
    } else {
      //
      if (Double.isNaN(avgZ))
        updateDataParameters();
      return avgZ;
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
    double[] z = toIArray(raw, onlySelected, true);
    Arrays.sort(z);

    for (int i = 0; i < z.length; i++) {
      if (z[i] <= intensity) {
        return (i / (z.length - 1));
      }
    }
    return 0;
  }

  // #############################################################
  // apply filter to cut off first or last values of intensity
  // only apply if not already done

  /**
   * 
   * @param f in percent ( 5 % as 5 not 0.05)
   * @return
   */
  public double applyCutFilterMin(double f) {
    if (f != lastAppliedMinFilter) {
      // apply filter
      // sort all z values
      SettingsPaintScale ps = getPaintScaleSettings();
      boolean selected = ps != null && ps.isUsesMinMaxFromSelection();
      // cut off percent f/100.f
      // save in var
      minZFiltered = getValueCutFilter(f, selected);
      if (ps != null)
        ps.setMin(minZFiltered);
      lastAppliedMinFilter = f;
    }
    return minZFiltered;
  }

  /**
   * 
   * @param f in percent ( 5 % as 5 not 0.05)
   * @return
   */
  public double applyCutFilterMax(double f) {
    if (f != lastAppliedMaxFilter) {
      // apply filter
      // sort all z values
      SettingsPaintScale ps = getPaintScaleSettings();
      boolean selected = ps != null && ps.isUsesMinMaxFromSelection();
      // cut off percent f/100.f
      // save in var --> cut from max 1-p
      maxZFiltered = getValueCutFilter(100.0 - f, selected);
      if (ps != null)
        ps.setMax(maxZFiltered);
      lastAppliedMaxFilter = f;
    }
    return maxZFiltered;
  }

  /**
   * does not apply the cut filter to this image
   * 
   * @param f in percent ( 5 % as 5 not 0.05)
   * @return
   */
  public double getValueCutFilter(double f, boolean useMinMaxFromSelection) {
    // apply filter
    // sort all z values
    double[] z = toIArray(false, useMinMaxFromSelection);
    Arrays.sort(z);
    // cut off percent f/100.f
    int size = z.length - 1;
    // save in var --> cut from max 1-p
    return z[(int) (size * f / 100.0)];
  }

  public double getMinZFiltered() {
    if (Double.isNaN(minZFiltered))
      updateDataParameters();
    return minZFiltered;
  }

  public double getMaxZFiltered() {
    if (Double.isNaN(maxZFiltered))
      updateDataParameters();
    return maxZFiltered;
  }

  // ######################################################
  // Paintscale
  /**
   * returns all data points in intensity range (max/min) (processed) uses the PaintScaleSettings of
   * this image
   * 
   * @return
   */
  public double[] getIInIRange(boolean useSelections) {
    return getIInIRange(getPaintScaleSettings(), useSelections);
  }

  /**
   * returns all data points in intensity range (max/min) (processed)
   * 
   * @return
   */
  public double[] getIInIRange(SettingsPaintScale ps, boolean useSelections) {
    if (ps == null)
      return new double[0];

    double[] inten = toIArray(false, useSelections);
    // count
    int counter = 0;
    for (double d : inten) {
      if (ps.isInIRange(this, d)) {
        counter++;
      }
    }

    // add to list
    double[] list = new double[counter];
    counter = 0;
    for (double d : inten) {
      if (ps.isInIRange(this, d)) {
        list[counter] = d;
        counter++;
      }
    }
    return list;
  }

  /**
   * returns number of data points in intensity range (max/min) uses the PaintScaleSettings of this
   * image
   * 
   * @return
   */
  public int countIInIRange(boolean useSelections) {
    return countIInIRange(getPaintScaleSettings(), useSelections);
  }

  /**
   * returns number of data points in intensity range (max/min)
   * 
   * @return
   */
  public int countIInIRange(SettingsPaintScale ps, boolean useSelections) {
    if (ps == null)
      return 0;

    int counter = 0;
    double[] inten = toIArray(false, useSelections);
    for (double d : inten) {
      if (ps.isInIRange(this, d)) {
        counter++;
      }
    }
    return counter;
  }



  public void addIntensityProcessingChangedListener(IntensityProcessingChangedListener li) {
    listenerProcessingChanged.add(li);
  }

  public void removeIntensityProcessingChangedListener(IntensityProcessingChangedListener li) {
    listenerProcessingChanged.remove(li);
  }

}
