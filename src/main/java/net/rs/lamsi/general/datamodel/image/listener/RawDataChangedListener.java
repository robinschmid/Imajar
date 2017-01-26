package net.rs.lamsi.general.datamodel.image.listener;

import net.rs.lamsi.general.datamodel.image.interf.ImageDataset;

/**
 * changes in raw data for example by direct imaging analysis
 * @author vukmir69
 *
 */
public interface RawDataChangedListener {
	public void rawDataChangedEvent(ImageDataset data);
}
