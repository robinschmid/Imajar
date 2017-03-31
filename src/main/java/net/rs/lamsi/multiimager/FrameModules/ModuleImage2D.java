package net.rs.lamsi.multiimager.FrameModules;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Frames.FrameWork.ColorChangedListener;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.Collectable2DSettingsModule;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.HeatmapSettingsModule;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.Module;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.SettingsModule;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.SettingsModuleContainer;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.image.SettingsImage2D;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleGeneral;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleOperations;
import net.rs.lamsi.multiimager.FrameModules.sub.ModulePaintscale;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleThemes;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleZoom;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

public class ModuleImage2D extends SettingsModuleContainer<SettingsImage2D, Image2D> {
	private ImageEditorWindow window;
	
	private ModuleGeneral moduleGeneral;
	private ModuleZoom moduleZoom;
	private ModulePaintscale modulePaintscale;
	private ModuleThemes moduleThemes;
	private ModuleOperations moduleOperations;
	//

	/**
	 * Create the panel.
	 */
	public ModuleImage2D(ImageEditorWindow wnd) {
		super("", false, SettingsImage2D.class, Image2D.class);    
		window = wnd;
		
		moduleGeneral = new ModuleGeneral(window);
		moduleGeneral.setAlignmentY(Component.TOP_ALIGNMENT);
		addModule(moduleGeneral);

		moduleZoom = new ModuleZoom();
		addModule(moduleZoom);
		
		modulePaintscale = new ModulePaintscale();
		addModule(modulePaintscale);

		moduleThemes = new ModuleThemes();
		addModule(moduleThemes);

		moduleOperations = new ModuleOperations(window);
		addModule(moduleOperations);

		// add all modules for Image settings TODO add all mods
		listSettingsModules.addElement(moduleGeneral.getModSplitConImg());
		listSettingsModules.addElement(moduleOperations.getModQuantifier());
		listSettingsModules.addElement(moduleOperations.getModSelectExcludeData());
	}
	

	
	//################################################################################################
	// GETTERS AND SETTERS  
	public ModuleOperations getModuleOperations() {
		return moduleOperations;
	}
	public ModuleGeneral getModuleGeneral() {
		return moduleGeneral;
	}
	public ModuleZoom getModuleZoom() {
		return moduleZoom;
	}
	public ModulePaintscale getModulePaintscale() {
		return modulePaintscale;
	}
	public ModuleThemes getModuleThemes() {
		return moduleThemes;
	}
}
