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
  protected int linelength = 0;

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

      // op is different to last op
      boolean same = lastOp != null && op.size() == lastOp.size();

      if (same && op.size() > 0) {
        same = op.stream().allMatch(o -> lastOp.stream().anyMatch(last -> o.equals(last)));
      }

      if (op.size() > 0 && !same) {

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

        data.setI(tz);
        if (tx != null) {
          data.setX(tx);
          data.setY(ty);
        }

        linelength = data.getMaximumLineLength();

        lastOp = op;
        return true;
      } else {
        data = null;
        linelength = img.getMaxLineLength();
      }
    }
    return false;
  }
}
