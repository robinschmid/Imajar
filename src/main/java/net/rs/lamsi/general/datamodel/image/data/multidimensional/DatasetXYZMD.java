package net.rs.lamsi.general.datamodel.image.data.multidimensional;

import java.io.Serializable;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.data.interf.MDDataset;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralRotation;

/**
 * basic dataset of multiple scan lines
 * 
 * @author Robin Schmid
 *
 */
public class DatasetXYZMD extends MDDataset implements Serializable {
  // do not change the version!
  private static final long serialVersionUID = 1L;

  protected XYZDataPointMD[] data;

  // per line
  protected int minDP = -1, maxDP = -1, avgDP = -1;

  // last x of longest line ( left edge of the datapoint)
  protected float lastX = Float.NaN;
  protected float x0 = Float.NaN;

  // settings of rotation, reflection, imaging mode
  protected SettingsGeneralRotation settRot;


  public DatasetXYZMD() {
    settRot = new SettingsGeneralRotation();
  }

  public DatasetXYZMD(XYZDataPointMD[] data) {
    settRot = new SettingsGeneralRotation();
    this.data = data;
  }

  /**
   * reset to start conditions (e.g. after data has changed)
   */
  public void reset() {
    minDP = -1;
    maxDP = -1;
    avgDP = -1;
    x0 = Float.NaN;
    lastX = Float.NaN;
  }


  public void appendLines(XYZDataPointMD[] data) {
    if (this.data == null)
      this.data = data;
    else {
      this.data = ArrayUtils.addAll(this.data, data);
    }

    reset();
    fireRawDataChangedEvent();
  }

  // ##################################################
  // Multi dimensional
  @Override
  public boolean removeDimension(int i) {
    boolean removed = true;
    for (ScanLineMD l : lines) {
      if (!l.removeDimension(i))
        removed = false;
    }
    return removed;
  }

  @Override
  public int addDimension(List<double[]> dim) {
    for (int i = 0; i < lines.length; i++)
      lines[i].addDimension(dim.get(i));
    return lines[0].getImageCount() - 1;
  }

  @Override
  public boolean addDimension(Image2D img) {
    if (this.hasSameDataDimensionsAs(img.getData())) {
      // this is empty?
      if (this.getLinesCount() == 0) {
        lines = new ScanLineMD[img.getData().getLinesCount()];
        for (int y = 0; y < lines.length; y++) {
          int dps = img.getData().getLineLength(y);
          float[] xx = new float[dps];
          double[] ii = new double[dps];
          for (int x = 0; x < dps; x++) {
            xx[x] = img.getData().getX(y, x);
            ii[x] = img.getData().getI(0, y, x);
          }
          lines[y] = new ScanLineMD(xx, ii);
        }
        setLines(lines);
      } else {
        // add dimension to all lines
        for (int i = 0; i < lines.length; i++)
          lines[i].addDimension(img, i);
      }
      // replace image data
      img.setData(this);
      img.setIndex(lines[0].getImageCount() - 1);
      return true;
    }
    return false;
  }


  // ##################################################
  // general ImageDataset
  @Override
  public int getLinesCount() {
    // TODO Auto-generated method stub
    return lines == null ? 0 : lines.length;
  }

  @Override
  public int getLineLength(int i) {
    if (i < 0 || i >= getLinesCount())
      return -1;
    return lines[i].getDPCount();
  }

  public ScanLineMD getLine(int line) {
    return lines[line];
  }

  public ScanLineMD[] getLines() {
    return lines;
  }

  /**
   * fires raw data changed event fires intensity processing changed event
   * 
   * @param lines
   */
  public void setLines(ScanLineMD[] lines) {
    this.lines = lines;
    reset();
    // fireIntensityProcessingChanged();
    // fireRawDataChangedEvent();
  }


  @Override
  public double getI(int index, int line, int ix) {
    // TODO Auto-generated method stub
    return lines[line].getI(index, ix);
  }

  @Override
  public float getX(int line, int idp) {
    if (hasOnlyOneXColumn())
      return lines[0].getX(idp);
    else
      return lines[line].getX(idp);
  }

  @Override
  public float getRightEdgeX(int l) {
    if (hasOnlyOneXColumn())
      return lines[0].getEndX();
    else
      return lines[l].getEndX();
  }

  @Override
  public float getLastXLine(int line) {
    return getX(line, getLineLength(line) - 1);
  }

  @Override
  public float getLastX() {
    if (lastX == -1) {
      if (hasOnlyOneXColumn())
        lastX = getLastXLine(0);
      else {
        for (int i = 0; i < lines.length; i++)
          if (getLastXLine(i) > lastX)
            lastX = getLastXLine(i);
      }
    }
    return lastX;
  }

  @Override
  public float getX0() {
    if (Float.isNaN(x0)) {
      if (hasOnlyOneXColumn())
        x0 = getX0(0);
      else {
        for (int i = 0; i < lines.length; i++)
          if (Float.isNaN(x0) || getX0(i) < x0)
            x0 = getX0(i);
      }
    }
    return x0;
  }

  /**
   * x0 of line
   * 
   * @param i
   * @return
   */
  private float getX0(int i) {
    if (hasOnlyOneXColumn())
      return lines[0].getX0();
    else
      return lines[i].getX0();
  }

  @Override
  public float getY0() {
    return 0;
  }

  @Override
  public int getTotalDPCount() {
    if (lines == null)
      return 0;
    if (totalDPCount == -1) {
      // calc
      totalDPCount = 0;
      for (ScanLineMD l : lines) {
        for (double d : l.getIntensity().get(0))
          if (!Double.isNaN(d))
            totalDPCount++;
      }
    }
    return totalDPCount;
  }

  /**
   * The maximum datapoints of the longest line
   * 
   * @return
   */
  @Override
  public int getMaxDP() {
    if (maxDP == -1) {
      analyzeData();
    }
    return maxDP;
  }

  /**
   * The maximum datapoints of the longest line does not uses the first and last line! for
   * calculation
   * 
   * @return
   */
  @Override
  public int getMinDP() {
    if (minDP == -1) {
      analyzeData();
    }
    return minDP;
  }

  /**
   * The average datapoints of all lines does not uses the first and last line! for calculation
   * 
   * @return
   */
  @Override
  public int getAvgDP() {
    if (avgDP == -1) {
      analyzeData();
    }
    return avgDP;
  }

  @Override
  public float getMaxDPWidth() {
    if (!hasXData()) {
      return 1;
    } else {
      if (maxDPWidth == -1) {
        analyzeData();
      }
      return maxDPWidth;
    }
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
    float sum = 0;
    int dp = 0;
    for (XYZDataPointMD dp : data) {
      for (int i = 0; i < l.getDPCount() - 2; i++) {
        float dp1 = l.getX(i);
        float dp2 = l.getX(i + 1);
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
      // stop if only one x col
      if (hasOnlyOneXColumn())
        break;
    }
    avgDPWidth = sum / dp;
    //
    minDP = Integer.MAX_VALUE;
    maxDP = Integer.MIN_VALUE;
    avgDP = 0;
    for (int i = 1; i < lines.length - 1; i++) {
      ScanLineMD l = lines[i];
      avgDP += l.getDPCount();
      if (l.getDPCount() < minDP)
        minDP = l.getDPCount();
      if (l.getDPCount() > maxDP)
        maxDP = l.getDPCount();
    }
    avgDP = Math.round((avgDP / (lines.length - 2.f)));
  }

  @Override
  public boolean hasXData() {
    return lines != null && lines[0] != null && lines[0].getX() != null;
  }

  @Override
  public int size() {
    return lines != null && lines.length > 0 ? lines[0].getImageCount() : 0;
  }

}
