package net.rs.lamsi.general.settings.image.operations.listener;

import net.rs.lamsi.general.datamodel.image.Image2D;

public interface IntensityProcessingChangedListener {
	public void fireIntensityProcessingChanged(Image2D img);
}
