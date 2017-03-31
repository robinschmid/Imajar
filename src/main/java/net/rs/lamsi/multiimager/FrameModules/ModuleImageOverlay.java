package net.rs.lamsi.multiimager.FrameModules;


import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import net.rs.lamsi.general.datamodel.image.ImageOverlay;
import net.rs.lamsi.massimager.Frames.FrameWork.ColorChangedListener;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.Collectable2DSettingsModule;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.HeatmapSettingsModule;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.Module;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.SettingsModule;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.SettingsModuleContainer;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.image.SettingsImage2D;
import net.rs.lamsi.multiimager.FrameModules.sub.ModulePaintscaleOverlay;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleThemes;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleZoom;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

public class ModuleImageOverlay extends SettingsModuleContainer<SettingsImage2D, ImageOverlay> {
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
		super("", false, SettingsImage2D.class, ImageOverlay.class);    
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
