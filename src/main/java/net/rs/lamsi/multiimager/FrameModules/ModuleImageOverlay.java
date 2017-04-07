package net.rs.lamsi.multiimager.FrameModules;


import net.rs.lamsi.general.datamodel.image.ImageOverlay;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.SettingsModuleContainer;
import net.rs.lamsi.massimager.Settings.image.SettingsImage2D;
import net.rs.lamsi.massimager.Settings.image.SettingsImageOverlay;
import net.rs.lamsi.multiimager.FrameModules.sub.ModulePaintscaleOverlay;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleThemes;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleZoom;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

public class ModuleImageOverlay extends SettingsModuleContainer<SettingsImageOverlay, ImageOverlay> {
	private ImageEditorWindow window;

	private ModuleZoom moduleZoom;
	private ModuleThemes moduleThemes;
	// 
	private ModulePaintscaleOverlay modulePaintscale;
	//

	/**
	 * Create the panel.
	 */
	public ModuleImageOverlay(ImageEditorWindow wnd) {
		super("", false, SettingsImageOverlay.class, ImageOverlay.class);    
		window = wnd;
		
		moduleZoom = new ModuleZoom();
		addModule(moduleZoom);
		
		modulePaintscale = new ModulePaintscaleOverlay();
		addModule(modulePaintscale);

		moduleThemes = new ModuleThemes();
		addModule(moduleThemes);
		

		// add all sub modules for settings TODO add all mods
//		listSettingsModules.addElement(moduleGeneral.getModSplitConImg());
	}
	
	//################################################################################################
	// GETTERS AND SETTERS  

	public ModuleZoom getModuleZoom() {
		return moduleZoom;
	}
	public ModulePaintscaleOverlay getModulePaintscale() {
		return modulePaintscale;
	}
	public ModuleThemes getModuleThemes() {
		return moduleThemes;
	}
}
