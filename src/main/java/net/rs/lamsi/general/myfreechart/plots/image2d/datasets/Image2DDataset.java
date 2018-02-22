package net.rs.lamsi.general.myfreechart.plots.image2d.datasets;

import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.xy.AbstractXYZDataset;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.data.twodimensional.XYIDataMatrix;
import net.rs.lamsi.general.heatmap.dataoperations.blur.FastGaussianBlur;
import net.rs.lamsi.general.processing.dataoperations.DataInterpolator;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage;

/**
 * getX getY and getZ are deprecated
 *
 */
public class Image2DDataset extends AbstractXYZDataset implements DomainInfo, RangeInfo {
  private static final long serialVersionUID = 1L;

  private Image2D img;

  public Image2DDataset(Image2D img) {
    super();
    this.img = img;
  }

  @Override
  public int getItemCount(int series) {
    return img.getData().getMaxDP() * img.getData().getLinesCount();
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

  public Image2D getImage() {
    return img;
  }

  @Override
  public double getDomainLowerBound(boolean includeInterval) {
    return 0;
  }

  @Override
  public double getDomainUpperBound(boolean includeInterval) {
    return img.getWidth(false);
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
    return img.getHeight(false);
  }

  @Override
  public Range getRangeBounds(boolean includeInterval) {
    return new Range(getRangeLowerBound(includeInterval), getRangeUpperBound(includeInterval));
  }


  // interpolation and gaussian blur
  public void applyPostProcessing() {
    SettingsGeneralImage sett = img.getSettings().getSettImage();
    double[][] dat = null;
    // interpolation
    int f = (int) sett.getInterpolation();
    // reduction
    int red = (int) (1 / sett.getInterpolation());

    // applies cropping filter if needed
    if (sett.isUseInterpolation() && (f > 1 || red > 1)) {
      // get matrices
      XYIDataMatrix data = img.toXYIDataMatrix(false, true);
      // interpolate to array [3][n]
      dat = DataInterpolator.interpolateToArray(data, sett.getInterpolation());
    } else {
      // get rotated and reflected dataset
      dat = img.toXYIArray(false, true);
    }
    // blur?
    if (sett.isUseBlur()) {
      double[] z = dat[2];
      double target[] = new double[z.length];
      int w = img.getMinLineLength();
      if (sett.isUseInterpolation()) {
        if (f > 1)
          w *= f;
        else if (red > 1)
          w /= red;
      }
      int h = z.length / w;
      FastGaussianBlur.applyBlur(z, target, w, h, sett.getBlurRadius());
      // set data
      dat[2] = target;
    }
  }
}
