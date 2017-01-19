package net.rs.lamsi.massimager.Image.listener;

import net.rs.lamsi.massimager.Image.Image2D;

/**
 * changes in raw data for example by direct imaging analysis
 * @author vukmir69
 *
 */
public interface RawDataChangedListener {
	public void rawDataChangedEvent(Image2D img);
}
