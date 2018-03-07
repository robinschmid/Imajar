package net.rs.lamsi.general.settings.image.operations.listener;

import net.rs.lamsi.general.datamodel.image.interf.DataCollectable2D;

public interface IntensityProcessingChangedListener {
  public void fireIntensityProcessingChanged(DataCollectable2D img);
}
