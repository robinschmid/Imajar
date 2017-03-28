package net.rs.lamsi.massimager.Frames.FrameWork.modules;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.interf.SettingsModuleObject;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

/**
 * 
 * @author r_schm33
 *
 * @param <T> Settings
 * @param <S>
 */
public abstract class Collectable2DSettingsModule<T extends Settings, S extends Collectable2D> extends HeatmapSettingsModule<T> implements SettingsModuleObject<S> {

	private final Class objclass;
	protected S currentImage = null;

	public Collectable2DSettingsModule(String title, boolean westside, Class settc, Class objclass) { 
		super(title, westside, settc);
		this.objclass = objclass;
		setShowTitleAlways(true);
	}

	//################################################################################################
	// GETTERS and SETTERS

	public S getCurrentImage() {
		return currentImage;
	}

	/**
	 * gets called before setCurrentHeat()
	 * gets called by {@link ImageEditorWindow#setImage2D(Image2D)}
	 * @param img
	 */
	@Override
	public void setCurrentImage(S img) {
		currentImage = img;
		setSettings((T) img.getSettingsByClass(classsettings));
	}
	
	@Override
	public void setSettings(T settings) {
		super.setSettings(settings);
		if(currentImage!=null) currentImage.setSettings((Settings)settings); 
	} 
	
	/**
	 * test if its a Image2D or ImageOverlay or ...
	 * @param c
	 * @return
	 */
	public boolean objectIsA(Class c) {
		return objclass.isAssignableFrom(c); 
	}
}
