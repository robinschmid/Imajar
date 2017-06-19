package net.rs.lamsi.multiimager.FrameModules;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import net.rs.lamsi.general.datamodel.image.ImageOverlay;
import net.rs.lamsi.general.framework.modules.MainSettingsModuleContainer;
import net.rs.lamsi.general.settings.image.SettingsImageOverlay;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleBackgroundImg;
import net.rs.lamsi.multiimager.FrameModules.sub.ModulePaintscaleOverlay;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleZoom;
import net.rs.lamsi.multiimager.FrameModules.sub.theme.ModuleThemes;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

public class ModuleImageOverlay extends MainSettingsModuleContainer<SettingsImageOverlay, ImageOverlay> {
	private ImageEditorWindow window;

	private ModuleZoom moduleZoom;
	private ModuleThemes moduleThemes;
	private ModuleBackgroundImg moduleBG;
	// 
	private ModulePaintscaleOverlay modulePaintscale;
	
	/**
	 * Create the panel.
	 */
	public ModuleImageOverlay(ImageEditorWindow wnd) {
		super("", false, SettingsImageOverlay.class, ImageOverlay.class, true);    
		window = wnd;

		JButton btnApplySettingsToAll = new JButton("apply to all");
		btnApplySettingsToAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				window.getLogicRunner().applySettingsToAllImagesInList();
			}
		});
		getPnTitleCenter().add(btnApplySettingsToAll);

		JButton btnUpdate = new JButton("update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				window.writeAllSettingsFromModules(false);
			}
		});
		getPnTitleCenter().add(btnUpdate);

		moduleZoom = new ModuleZoom();
		addModule(moduleZoom);
		
		modulePaintscale = new ModulePaintscaleOverlay();
		addModule(modulePaintscale);

		moduleBG = new ModuleBackgroundImg();
		addModule(moduleBG);
		
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
	public ModuleBackgroundImg getModuleBackground() {
		return moduleBG;
	}

}
