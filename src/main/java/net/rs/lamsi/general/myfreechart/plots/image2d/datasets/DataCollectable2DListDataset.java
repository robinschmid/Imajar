package net.rs.lamsi.general.myfreechart.plots.image2d.datasets;

import java.util.ArrayList;
import java.util.List;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.xy.AbstractXYZDataset;
import net.rs.lamsi.general.datamodel.image.ImageMerge;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.settings.merge.SettingsSingleMerge;

/**
 * getX getY and getZ are deprecated
 *
 */
public class DataCollectable2DListDataset extends AbstractXYZDataset
    implements DomainInfo, RangeInfo {
  private static final long serialVersionUID = 1L;

  private List<DataCollectable2DDataset> list;
  private List<SettingsSingleMerge> settings;

  public DataCollectable2DListDataset(ImageMerge image) {
    super();
    List<DataCollectable2D> imgs = image.getSettings().getImageList();
    this.list = new ArrayList<>();
    for (DataCollectable2D img : imgs) {
      DataCollectable2DDataset dataset = new DataCollectable2DDataset(img);
      dataset.applyPostProcessing();
      list.add(dataset);
    }

    this.settings = image.getSettings().getMergeSettings();
  }

  @Override
  public int getItemCount(int series) {
    // always paint image as one item
    return 1;
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
    return list.size();
  }

  @Override
  public Comparable getSeriesKey(int series) {
    if (list == null || list.size() <= series)
      return "";
    return list.get(series).getImage().getTitle();
  }

  @Override
  public double getDomainLowerBound(boolean includeInterval) {
    return 0;
  }

  @Override
  public double getDomainUpperBound(boolean includeInterval) {
    return 10000;
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
    return 10000;
  }

  @Override
  public Range getRangeBounds(boolean includeInterval) {
    return new Range(getRangeLowerBound(includeInterval), getRangeUpperBound(includeInterval));
  }

  public DataCollectable2DDataset getDataset(int series) {
    return list.get(series);
  }

  public SettingsSingleMerge getSettings(int series) {
    return settings.get(series);
  }
}
