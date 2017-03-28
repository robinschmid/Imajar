package net.rs.lamsi.general.datamodel.image.interf;

import javax.swing.Icon;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.ImageOverlay;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetContinuousMD;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.image.SettingsImage2D;
import net.rs.lamsi.massimager.Settings.image.operations.SettingsImage2DOperations;
import net.rs.lamsi.massimager.Settings.image.operations.quantifier.SettingsImage2DQuantifier;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsImageContinousSplit;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsZoom;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale;
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
	

	// ######################################################################################
	// sizing
	/**
	 * according to rotation of data
	 * @return
	 */
	public abstract int getWidthAsMaxDP();
	/**
	 * according to rotation of data
	 * @return
	 */
	public abstract int getHeightAsMaxDP();
	
	public abstract Icon getIcon(int maxw, int maxh);
	
	public abstract String getTitle();
	
	public abstract SettingsThemes getSettTheme();
	
	public abstract SettingsZoom getSettZoom();
	
	public abstract Settings getSettings();
	
	/**
	 * set settings by class
	 * @param settings
	 */
	public abstract void setSettings(Settings settings);

	/**
	 * get settings by class
	 * @param classsettings
	 * @return
	 */
	public abstract Settings getSettingsByClass(Class classsettings);
	
	/**
	 * Given image img will be setup like this image
	 * @param img will get all settings from master image
	 */
	public abstract void applySettingsToOtherImage(Collectable2D img2);
	
	public boolean isImage2D() {
		return Image2D.class.isInstance(this);
	}
	public boolean isImageOverlay() {
		return ImageOverlay.class.isInstance(this);
	}
}
