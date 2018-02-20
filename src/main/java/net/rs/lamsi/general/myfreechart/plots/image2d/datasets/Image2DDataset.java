package net.rs.lamsi.general.myfreechart.plots.image2d.datasets;

import org.jfree.data.xy.AbstractXYZDataset;
import net.rs.lamsi.general.datamodel.image.Image2D;

public class Image2DDataset extends AbstractXYZDataset {
  private static final long serialVersionUID = 1L;

  private Image2D img;



  @Override
  public int getItemCount(int series) {
    return img.getData().getMaxDP() * img.getData().getLinesCount();
  }

  @Override
  public Number getX(int series, int item) {
    return img.isRotated();
  }

  @Override
  public Number getY(int series, int item) {
    return null;
  }

  @Override
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


}
