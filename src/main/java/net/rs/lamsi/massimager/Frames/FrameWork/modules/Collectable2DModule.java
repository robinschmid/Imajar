package net.rs.lamsi.massimager.Frames.FrameWork.modules;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.massimager.Frames.FrameWork.ColorChangedListener;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.interf.SettingsModuleObject;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.menu.ModuleMenu;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.SettingsHolder;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.massimager.Settings.listener.SettingsChangedListener;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

/**
 * 
 * @author r_schm33
 *
 * @param <T> Settings
 */
public abstract class Collectable2DModule<T extends Collectable2D> extends Module implements SettingsModuleObject<T> {

	protected T currentImage = null;

	protected Heatmap currentHeat;

	public Collectable2DModule(String title, boolean westside) { 
		super(title, westside);
		setShowTitleAlways(true);
	}

	//################################################################################################
		// Autoupdate
		/**
		 * init with listeners for changes of settings in the modules 
		 * autoUpdater calls SettingsExtraction from modules for creation of a new heatmap
		 * @param al
		 * @param cl
		 * @param dl
		 * @param ccl
		 * @param il
		 */
		public abstract void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il);
		/**
		 * init with listeners for changes of settings in the modules 
		 * autoUpdater calls SettingsExtraction from modules for REPAINTING the current heatmap
		 * @param al
		 * @param cl
		 * @param dl
		 * @param ccl
		 * @param il
		 */
		public abstract void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il);


	//################################################################################################
	// GETTERS and SETTERS

	/**
	 * gets called by {@link ImageEditorWindow#addHeatmapToPanel(Heatmap)}
	 * @param heat
	 */
	public void setCurrentHeatmap(Heatmap heat) {
		currentHeat = heat;
	}

	public Heatmap getCurrentHeat() {
		return currentHeat;
	} 
	public T getCurrentImage() {
		return currentImage;
	}

	/**
	 * gets called before setCurrentHeat()
	 * gets called by {@link ImageEditorWindow#setImage2D(Image2D)}
	 * @param img
	 */
	public void setCurrentImage(T img) {
		currentImage = img;
	}
	
}
