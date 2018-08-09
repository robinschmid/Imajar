package net.rs.lamsi.general.myfreechart.plots.image2d.merge;

import java.awt.Shape;
import java.io.Serializable;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.data.xy.XYDataset;
import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;
import net.rs.lamsi.general.settings.image.merge.SettingsSingleMerge;

public class ImageMergeItem extends XYItemEntity implements Serializable {

  private static final long serialVersionUID = 1L;
  private SettingsSingleMerge settings;
  private DataCollectable2D img;

  public ImageMergeItem(Shape area, XYDataset dataset, int series, int item, String toolTipText,
      String urlText, SettingsSingleMerge settings, DataCollectable2D img) {
    super(area, dataset, series, item, toolTipText, urlText);
    this.img = img;
    this.settings = settings;
  }

  public SettingsSingleMerge getSettings() {
    return settings;
  }

  public DataCollectable2D getImg() {
    return img;
  }
}
