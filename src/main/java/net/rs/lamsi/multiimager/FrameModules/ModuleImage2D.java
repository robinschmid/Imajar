package net.rs.lamsi.multiimager.FrameModules;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.framework.modules.MainSettingsModuleContainer;
import net.rs.lamsi.general.settings.image.SettingsImage2D;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleBackgroundImg;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleGeneral;
import net.rs.lamsi.multiimager.FrameModules.sub.ModulePaintscale;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleQuantifyStrategy;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleSelectExcludeData;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleZoom;
import net.rs.lamsi.multiimager.FrameModules.sub.theme.ModuleThemes;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

public class ModuleImage2D extends MainSettingsModuleContainer<SettingsImage2D, Image2D> {
	private ImageEditorWindow window;
	
	private ModuleGeneral moduleGeneral;
	private ModuleZoom moduleZoom;
	private ModulePaintscale modulePaintscale;
	private ModuleThemes moduleThemes;
	private ModuleBackgroundImg moduleBG;
	private ModuleSelectExcludeData moduleSelect;
	private ModuleQuantifyStrategy modQuantifier;
	//

	/**
	 * Create the panel.
	 */
	public ModuleImage2D(ImageEditorWindow wnd) {
		super("", false, SettingsImage2D.class, Image2D.class, true);    
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
		
		
		moduleGeneral = new ModuleGeneral(window);
		moduleGeneral.setAlignmentY(Component.TOP_ALIGNMENT);
		addModule(moduleGeneral);

		moduleZoom = new ModuleZoom();
		addModule(moduleZoom);
		
		modulePaintscale = new ModulePaintscale();
		addModule(modulePaintscale);

		moduleBG = new ModuleBackgroundImg();
		addModule(moduleBG);
		
		moduleThemes = new ModuleThemes();
		addModule(moduleThemes);

		moduleSelect = new ModuleSelectExcludeData(window);
		addModule(moduleSelect);

		modQuantifier = new ModuleQuantifyStrategy(window);
		addModule(modQuantifier);
		
		// add all modules for Image settings TODO add all mods
		listSettingsModules.add(moduleGeneral.getModSplitConImg());
	}
	

	
	//################################################################################################
	// GETTERS AND SETTERS  
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
	public ModuleBackgroundImg getModuleBackground() {
		return moduleBG;
	}
	public ModuleSelectExcludeData getModuleSelect() {
		return moduleSelect;
	}
	public ModuleQuantifyStrategy getModuleQuantify() {
		return modQuantifier;
	}
}
