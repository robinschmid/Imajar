package net.rs.lamsi.general.datamodel.image.interf;

import java.io.Serializable;

import javax.swing.Icon;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.ImageGroupMD;
import net.rs.lamsi.general.datamodel.image.ImageOverlay;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetContinuousMD;
import net.rs.lamsi.general.settings.Settings;
import net.rs.lamsi.general.settings.SettingsContainerSettings;
import net.rs.lamsi.general.settings.image.SettingsImage2D;
import net.rs.lamsi.general.settings.image.operations.SettingsImage2DOperations;
import net.rs.lamsi.general.settings.image.operations.quantifier.SettingsImage2DQuantifier;
import net.rs.lamsi.general.settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.general.settings.image.sub.SettingsImageContinousSplit;
import net.rs.lamsi.general.settings.image.sub.SettingsZoom;
import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.general.settings.image.visualisation.SettingsThemes;

public abstract class Collectable2D <T extends SettingsContainerSettings>  implements Serializable {	 
	// do not change the version!
	private static final long serialVersionUID = 1L;
	

	// image group: Multi dimensional data sets create an ImageGroupMD
	// this controls image operations
	protected ImageGroupMD imageGroup = null;
	
	// collectable2d settings
	protected T settings;
	

	public Collectable2D() {
		
	}
	
	public Collectable2D(T settings) {
		super();
		this.settings = settings;
	}
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

	public abstract String getShortTitle();
	
	public SettingsThemes getSettTheme() {
		return (SettingsThemes) getSettingsByClass(SettingsThemes.class);
	}
	public SettingsZoom getSettZoom() {
		return (SettingsZoom) getSettingsByClass(SettingsZoom.class);
	}
	
	/**
	 * set settings by class or set whole main settings collection
	 * @param settings
	 */
	public void setSettings(Settings settings){
		if(settings==null)
			return;
		else if(settings.getClass().isInstance(this.settings))
			this.settings = (T)settings;
		else {
			((SettingsContainerSettings)this.settings).replaceSettings(settings);
		}
	}
	
	/**
	 * the main settings collection
	 * @return
	 */
	public T getSettings() {
		return settings;
	}

	/**
	 * get settings by class
	 * @param classsettings
	 * @return
	 */
	public Settings getSettingsByClass(Class classsettings) {
		if(classsettings.isInstance(this.settings))
			return this.settings;
		else {
			Settings s = ((SettingsContainerSettings)this.settings).getSettingsByClass(classsettings);
			if(s==null && imageGroup!=null) {
				s = imageGroup.getSettingsByClass(classsettings);
			}
			return s;
		}
	}
	
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
	


	// a name for lists
	public abstract String toListName();

	@Override
	public String toString() {
		return toListName();
	}
}
