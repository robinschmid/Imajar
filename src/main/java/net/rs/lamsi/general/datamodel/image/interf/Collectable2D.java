package net.rs.lamsi.general.datamodel.image.interf;

import javax.swing.Icon;

import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsThemes;

public abstract class Collectable2D {
	

	// image group: Multi dimensional data sets create an ImageGroupMD
	// this controls image operations
	protected ImageGroupMD imageGroup = null;
	
	/**
	 * image is marked as a group member in an image group. 
	 * This group handles  multi dimensional data sets (not only)
	 * @param imageGroupMD
	 */
	public void setImageGroup(ImageGroupMD imageGroup) {
		this.imageGroup = imageGroup;
	}
	public ImageGroupMD getImageGroup() {
		return this.imageGroup;
	} 
	
	public abstract Icon getIcon(int maxw, int maxh);
	
	
	
	
	
	
	public abstract SettingsThemes getSettTheme();
}
