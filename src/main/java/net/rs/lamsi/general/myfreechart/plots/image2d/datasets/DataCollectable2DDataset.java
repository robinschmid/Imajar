package net.rs.lamsi.general.myfreechart.plots.image2d.datasets;

import java.util.List;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.xy.AbstractXYZDataset;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIDataMatrix;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.datamodel.image.interf.PostProcessingOpProvider;
import net.rs.lamsi.general.heatmap.dataoperations.PostProcessingOp;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;

/**
 * getX getY and getZ are deprecated
 *
 */
public class DataCollectable2DDataset extends AbstractXYZDataset implements DomainInfo, RangeInfo {
  private static final long serialVersionUID = 1L;

  private DataCollectable2D img;
  // post processed data. Not null if post processing was applied.
  // otherwise use img as data source
  protected XYIDataMatrix data;
  protected List<PostProcessingOp> lastOp;
  protected int lastProcTime = -1;
  protected int linelength = 0;
  protected double maxZ;
  protected double minZ;

  public DataCollectable2DDataset(DataCollectable2D img) {
    super();
    this.img = img;
  }

  @Override
  public int getItemCount(int series) {
    return data == null ? img.getTotalDataPoints() : linelength * data.lineCount();
  }

  /**
   * The linelength to calculate from index to line and dp
   * 
   * @return
   */
  public int getLineLength() {
    return data == null ? img.getMaxLineLength() : linelength;
  }

  public double getZ(boolean raw, int line, int dp) {
    return data == null ? img.getI(raw, line, dp) : data.getI()[line][dp];
  }

  public float getX(boolean raw, int line, int dp) {
    return data == null ? img.getX(raw, line, dp) : data.getX()[line][dp];
  }

  public float getY(boolean raw, int line, int dp) {
    return data == null ? img.getY(raw, line, dp) : data.getY()[line][dp];
  }

  public boolean isProcessed() {
    return data != null;
  }

  @Override
  @Deprecated
  public Number getX(int series, int item) {
    return null;
  }

  @Override
  @Deprecated
  public Number getY(int series, int item) {
    return null;
  }

  @Override
  @Deprecated
  public Number getZ(int series, int item) {
    return null;
  }

  @Override
  public int getSeriesCount() {
    return 1;
  }

  @Override
  public Comparable getSeriesKey(int series) {
    return img.getTitle();
  }

  public DataCollectable2D getImage() {
    return img;
  }

  @Override
  public double getDomainLowerBound(boolean includeInterval) {
    return 0;
  }

  @Override
  public double getDomainUpperBound(boolean includeInterval) {
    return img.getWidth();
  }

  @Override
  public Range getDomainBounds(boolean includeInterval) {
    return new Range(getDomainLowerBound(includeInterval), getDomainUpperBound(includeInterval));
  }

  @Override
  public double getRangeLowerBound(boolean includeInterval) {
    return 0;
  }

  @Override
  public double getRangeUpperBound(boolean includeInterval) {
    return img.getHeight();
  }

  @Override
  public Range getRangeBounds(boolean includeInterval) {
    return new Range(getRangeLowerBound(includeInterval), getRangeUpperBound(includeInterval));
  }

  // interpolation and gaussian blur
  public boolean applyPostProcessing() {
    // get list of post processing operations from settings
    if (img.getSettings() instanceof PostProcessingOpProvider) {
      List<PostProcessingOp> op =
          ((PostProcessingOpProvider) img.getSettings()).getPostProcessingOp();

      int time = img.getLastIProcChangeTime();

      // op is different to last op
      boolean same = time == lastProcTime && lastOp != null && op.size() == lastOp.size();

      if (same && !op.isEmpty()) {
        same = op.stream().allMatch(o -> lastOp.stream().anyMatch(last -> o.equals(last)));
      }

      if (!op.isEmpty() && !same) {
        data = img.toXYIDataMatrix(false, true);
        double[][] z = data.getI();
        float[][] y = data.getY();
        float[][] x = data.getX();

        double[][] tz = null;
        float[][] tx = null, ty = null;

        for (PostProcessingOp o : op) {
          // intensity
          tz = o.processItensity(tz == null ? z : tz);
          // xy
          if (o.isProcessingXY()) {
            tx = o.processXY(tx == null ? x : tx);
            ty = o.processXY(ty == null ? y : ty);
          }
        }

        // error
        if (tz == null)
          return false;

        // pre calc min and max
        minZ = Double.POSITIVE_INFINITY;
        maxZ = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < tz.length; i++) {
          for (int j = 0; j < tz[i].length; j++) {
            if (!Double.isNaN(tz[i][j])) {
              if (tz[i][j] < minZ)
                minZ = tz[i][j];
              if (tz[i][j] > maxZ)
                maxZ = tz[i][j];
            }
          }
        }

        data.setI(tz);
        if (tx != null) {
          data.setX(tx);
          data.setY(ty);
        }

        linelength = data.getMaximumLineLength();

        ImageEditorWindow.log("Post processing of DataCollectable2DDataset done: max line length:"
            + linelength + "; min line length:" + data.getMinimumLineLength(), LOG.MESSAGE);

        lastOp = op;
        lastProcTime = time;
        return true;
      } else {
        data = null;
        linelength = img.getMaxLineLength();
      }
    }
    return false;
  }

  public double getMinIntensity() {
    return isProcessed() ? minZ : img.getMinIntensity(false);
  }

  public double getMaxIntensity() {
    return isProcessed() ? maxZ : img.getMaxIntensity(false);
  }
}
