package net.rs.lamsi.general.datamodel.image.data.multidimensional;

import java.io.Serializable;
import java.util.List;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.data.interf.ImageDataset;
import net.rs.lamsi.general.datamodel.image.data.interf.MDDataset;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage.XUNIT;
import net.rs.lamsi.general.settings.image.sub.SettingsImageContinousSplit;

/**
 * basic dataset of multiple scan lines
 * 
 * @author Robin Schmid
 *
 */
public class DatasetContinuousMD extends MDDataset implements Serializable {
  // do not change the version!
  private static final long serialVersionUID = 1L;

  protected SettingsImageContinousSplit sett;

  protected ScanLineMD line;
  protected int[] lineStart;

  // last x of longest line ( left edge of the datapoint)
  protected float lastX = -1;

  protected boolean hasOneDPWidth = false;
  protected boolean hasOneDPHeight = false;
  protected float maxDPWidth = -1;
  protected float avgDPWidth = -1;
  protected int minDP = -1, maxDP = -1, avgDP = -1;

  public DatasetContinuousMD(ScanLineMD line) {
    this(line, new SettingsImageContinousSplit(aproxLineLength(line)));
  }

  public DatasetContinuousMD(ScanLineMD line, SettingsImageContinousSplit sett) {
    this.line = line;
    setSplitSettings(sett);
  }


  public void setSplitSettings(SettingsImageContinousSplit sett) {
    this.sett = sett;
    reset();

    if (sett.getSplitMode() == XUNIT.DP && sett.getSplitAfterDP() == 0) {

    } else if (sett.getSplitMode() == XUNIT.s && sett.getSplitAfterX() == 0) {

    }
    // split data after time TODO
    if (sett.getSplitMode() == XUNIT.s) {
      lineStart = new int[getLinesCount()];
      int start = 0;
      float startTime = line.getX(0);
      for (int i = 0; i < line.getDPCount(); i++) {
        // first line
        if (start == 0 && line.getX(i) - startTime >= sett.getStartX()) {
          lineStart[start] = i;
          start++;
        }
        // next lines
        else if (line.getX(i) - startTime - sett.getStartX() >= sett.getSplitAfterX() * (start)) {
          lineStart[start] = i;
          start++;
        }
      }
    } else {
      lineStart = null;
    }
  }

  public SettingsImageContinousSplit getSplitSettings() {
    return sett;
  }

  /**
   * approximate line length
   * 
   * @param line
   * @return
   */
  private static int aproxLineLength(ScanLineMD line) {
    int total = line.getDPCount();
    int best = 0;
    // i lines
    // break if line length<line count
    for (int i = 5; i <= 700 && total / i < i; i++) {
      // save as best
      if (total % i == 0) {
        best = i;
      }
    }
    return best != 0 ? total / best : (int) Math.sqrt(total);
  }

  /**
   * reset to start conditions (e.g. after data has changed)
   */
  public void reset() {
    maxDPWidth = -1;
    minDP = -1;
    maxDP = -1;
    avgDP = -1;
    lastX = -1;
  }

  /**
   * get settings by class
   * 
   * @param classsettings
   * @return
   */
  @Override
  public Settings getSettingsByClass(Class classsettings) {
    // split settings
    if (classsettings.isInstance(sett)) {
      return sett;
    } else
      return null;
  }

  /**
   * set settings by class
   * 
   * @param classsettings
   */
  public void setSettingsByClass(Settings sett) {
    if (SettingsImageContinousSplit.class.isInstance(sett)) {
      setSplitSettings((SettingsImageContinousSplit) sett);
    }
  }

  // ##################################################
  // Multi dimensional
  @Override
  public boolean removeDimension(int i) {
    return line.removeDimension(i);
  }

  @Override
  public int addDimension(List<double[]> dim) {
    if (dim.size() == 1) {
      line.addDimension(dim.get(0));
    } else {
      // put all lines together
      int size = 0;
      for (double[] d : dim)
        size += d.length;

      double[] sum = new double[size];

      int c = 0;
      for (double[] d : dim)
        for (double val : d) {
          sum[c] = val;
          c++;
        }

      line.addDimension(sum);
    }
    return line.getImageCount() - 1;
  }

  @Override
  public boolean addDimension(Image2D img) {
    if (img.getData().hasSameDataDimensionsAs(this)) {

      int dp = img.getData().getTotalDPCount();
      // add x data if not present in current data set but in new image
      if (!this.hasXData() && (!MDDataset.class.isInstance(img.getData())
          || ((MDDataset) img.getData()).hasXData())) {
        int c = 0;
        float[] x = new float[dp];
        for (int l = 0; l < img.getData().getLinesCount(); l++) {
          for (int i = 0; i < img.getData().getLineLength(l); i++) {
            x[c] = img.getXRaw(true, l, i);
            c++;
          }
        }
        line.setX(x);
      }
      // add dimension
      double[] z = new double[dp];
      int c = 0;
      for (int l = 0; l < img.getData().getLinesCount(); l++) {
        for (int i = 0; i < img.getData().getLineLength(l); i++) {
          z[c] = img.getIRaw(l, i);
          c++;
        }
      }
      int index = line.addDimension(z);

      // replace image data
      img.setData(this);
      img.setIndex(index);
      return true;
    }
    return false;
  }


  @Override
  public boolean hasSameDataDimensionsAs(ImageDataset data) {
    return data.getTotalDPCount() == this.getTotalDPCount();
  }

  // ####################################################
  // standard
  @Override
  public int getLinesCount() {
    if (sett.getSplitMode() == XUNIT.s)
      return (int) Math.ceil((line.getXWidth() - sett.getStartX()) / sett.getSplitAfterX());
    else
      return (int) Math.ceil((line.getDPCount() - sett.getStartX()) / sett.getSplitAfterDP());
  }

  @Override
  public int getLineLength(int i) {
    if (sett.getSplitMode() == XUNIT.s)
      return i == getLinesCount() - 1 ? getTotalDPCount() - lineStart[i]
          : lineStart[i + 1] - lineStart[i];
    else
      return i == getLinesCount() - 1
          ? getTotalDPCount() - (int) sett.getStartX() - (sett.getSplitAfterDP() * i)
          : sett.getSplitAfterDP();
  }

  @Override
  public float getX(int line, int dpi) {
    return this.line.getX(getIndex(line, dpi)) - this.line.getX(getIndex(line, 0));
  }

  @Override
  public double getI(int index, int line, int dpi) {
    return this.line.getI(index, getIndex(line, dpi));
  }


  @Override
  public float getRightEdgeX(int l) {
    return getLastXLine(l) + line.getWidthDP();
  }

  @Override
  public float getLastXLine(int line) {
    return getX(line, getLineLength(line) - 1);
  }

  @Override
  public float getLastX() {
    if (lastX == -1) {
      for (int i = 0; i < getLinesCount(); i++)
        if (getLastXLine(i) > lastX)
          lastX = getLastXLine(i);
    }
    return lastX;
  }


  @Override
  public float getX0() {
    return line.getX0();
  }

  @Override
  public float getY0() {
    return 0;
  }

  /**
   * calculates the data point in the continuous dimension
   * 
   * @param line
   * @param dpi
   * @return
   */
  public int getIndex(int line, int dpi) {
    if (sett.getSplitMode() == XUNIT.s) {
      return dpi + lineStart[line];
    } else {
      return line * sett.getSplitAfterDP() + dpi + (int) sett.getStartX();
    }
  }


  @Override
  public int getTotalDPCount() {
    return line.getDPCount();
  }

  /**
   * The maximum datapoints of the longest line
   * 
   * @return
   */
  public int getMaxDP() {
    if (sett.getSplitMode() == XUNIT.s) {
      if (maxDP == -1) {
        maxDP = Integer.MIN_VALUE;
        for (int i = 0; i < lineStart.length; i++) {
          int l = getLineLength(i);
          if (l > maxDP)
            maxDP = l;
        }
      }
      return maxDP;
    } else
      return sett.getSplitAfterDP();
  }

  /**
   * The maximum datapoints of the longest line does not uses the first and last line! for
   * calculation
   * 
   * @return
   */
  public int getMinDP() {
    if (sett.getSplitMode() == XUNIT.s) {
      if (minDP == -1) {
        minDP = Integer.MAX_VALUE;
        for (int i = 0; i < lineStart.length; i++) {
          int l = getLineLength(i);
          if (l < minDP)
            minDP = l;
        }
      }
      return minDP;
    } else
      return getLineLength(getLinesCount() - 1);
  }

  /**
   * The average datapoints of all lines does not uses the first and last line! for calculation
   * 
   * @return
   */
  public int getAvgDP() {
    if (sett.getSplitMode() == XUNIT.s) {
      if (avgDP == -1) {
        int avg = 0;
        for (int i = 1; i < lineStart.length - 1; i++) {
          int l = getLineLength(i);
          avg += l;
        }
        avgDP = Math.round((avg / (getLinesCount() - 2)));
      }
      return avgDP;
    } else
      return sett.getSplitAfterDP();
  }

  @Override
  public float getMaxDPWidth() {
    if (maxDPWidth == -1) {
      analyzeData();
    }
    return maxDPWidth;
  }


  @Override
  public float getAvgDPWidth() {
    if (!hasXData()) {
      return 1;
    } else {
      if (avgDPWidth == -1) {
        analyzeData();
      }
      return avgDPWidth;
    }
  }

  @Override
  public boolean hasOneDPWidth() {
    if (!hasXData()) {
      return true;
    } else {
      if (avgDPWidth == -1) {
        analyzeData();
      }
      return hasOneDPWidth;
    }
  }

  @Override
  public boolean hasOneDPHeight() {
    if (avgDPWidth == -1) {
      analyzeData();
    }
    return hasOneDPHeight;
  }



  private void analyzeData() {
    float tmpwidth = -1;
    hasOneDPWidth = true;
    hasOneDPHeight = true;
    maxDPWidth = Float.NEGATIVE_INFINITY;
    avgDPWidth = -1;
    float sum = 0;
    int dp = 0;
    for (int i = 0; i < line.getDPCount() - 2; i++) {
      float dp1 = line.getX(i);
      float dp2 = line.getX(i + 1);
      float width = Math.abs(dp2 - dp1);
      // avg
      sum += width;
      dp++;
      // max
      if (width > maxDPWidth)
        maxDPWidth = width;
      // all same width?
      if (tmpwidth == -1)
        tmpwidth = width;
      else if (Float.compare(width, tmpwidth) != 0)
        hasOneDPWidth = false;
    }
    avgDPWidth = sum / dp;
  }


  @Override
  public boolean hasXData() {
    return line != null && line.hasXData();
  }

  @Override
  public int size() {
    return line != null ? line.getImageCount() : 0;
  }

  @Override
  public void appendLines(ScanLineMD[] add) throws Exception {
    throw new Exception("appendLines is not applicatble to a continuous dataset");
  }
}
