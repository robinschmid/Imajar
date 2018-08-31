package net.rs.lamsi.general.datamodel.image.data.twodimensional;

import net.rs.lamsi.general.datamodel.image.data.interf.ImageDataset;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.ScanLineMD;

public class Simple2DDataset extends ImageDataset {

  private float pixelWidth = 50;
  private float pixelHeight = 50;

  /**
   * double[line y][dp x]
   */
  private double[][] intensity;

  private int totalDPCount = -1;
  private int longestLineDPCount = -1, shortestLineDPCount = -1, avgDP = -1;

  /**
   * 
   * @param pixelWidth
   * @param pixelHeight
   * @param intensity [line y][dp x]
   */
  public Simple2DDataset(float pixelWidth, float pixelHeight, double[][] intensity) {
    super();
    this.pixelWidth = pixelWidth > 0 ? pixelWidth : 1;
    this.pixelHeight = pixelHeight > 0 ? pixelHeight : 1;
    this.intensity = intensity;
  }

  @Override
  public void appendLines(ScanLineMD[] add) throws Exception {

  }

  @Override
  public int getLinesCount() {
    return intensity != null ? intensity.length : 0;
  }

  @Override
  public int getLineLength(int i) {
    return intensity != null && i < intensity.length ? intensity[i].length : 0;
  }

  @Override
  public float getX(int line, int dpi) {
    return intensity != null && line < intensity.length && dpi < intensity[line].length
        ? dpi * pixelWidth
        : 0;
  }

  @Override
  public double getI(int index, int line, int dpi) {
    return intensity != null && line < intensity.length && dpi < intensity[line].length
        ? intensity[line][dpi]
        : 0;
  }

  @Override
  public float getX0() {
    return 0;
  }

  @Override
  public float getY0() {
    return 0;
  }

  @Override
  public boolean hasXData() {
    return false;
  }


  @Override
  public boolean hasOneDPWidth() {
    return true;
  }

  @Override
  public boolean hasOneDPHeight() {
    return true;
  }

  @Override
  public float getMaxDPWidth() {
    return pixelWidth;
  }

  @Override
  public float getAvgDPWidth() {
    return pixelWidth;
  }

  @Override
  public int getTotalDPCount() {
    if (totalDPCount != -1)
      return totalDPCount;
    else {
      if (intensity != null) {
        totalDPCount = 0;
        for (double[] i : intensity)
          totalDPCount += i.length;
        return totalDPCount;
      } else
        return 0;
    }
  }

  @Override
  public int getMaxDP() {
    if (longestLineDPCount != -1)
      return longestLineDPCount;
    else {
      if (intensity != null) {
        longestLineDPCount = 0;
        for (double[] i : intensity)
          if (longestLineDPCount < i.length)
            longestLineDPCount = i.length;
        return longestLineDPCount;
      } else
        return 0;
    }
  }

  @Override
  public int getMinDP() {
    if (shortestLineDPCount != -1)
      return shortestLineDPCount;
    else {
      if (intensity != null) {
        shortestLineDPCount = 0;
        for (double[] i : intensity)
          if (shortestLineDPCount > i.length)
            shortestLineDPCount = i.length;
        return shortestLineDPCount;
      } else
        return 0;
    }
  }

  @Override
  public int getAvgDP() {
    if (avgDP != -1)
      return avgDP;
    else {
      if (intensity != null) {
        avgDP = 0;
        for (double[] i : intensity)
          avgDP += i.length;
        avgDP = avgDP / intensity.length;
        return avgDP;
      } else
        return 0;
    }
  }

  @Override
  public float getRightEdgeX(int line) {
    return intensity != null || intensity.length < line ? pixelWidth * (intensity[line].length + 1)
        : 0;
  }

  @Override
  public float getLastXLine(int line) {
    return intensity != null || intensity.length < line ? pixelWidth * (intensity[line].length) : 0;
  }

  @Override
  public float getLastX() {
    return pixelWidth * getMaxDP();
  }

}
