package net.rs.lamsi.multiimager.FrameModules;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.SettingsModuleContainer;
import net.rs.lamsi.massimager.Settings.image.SettingsImage2D;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleBackgroundImg;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleGeneral;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleOperations;
import net.rs.lamsi.multiimager.FrameModules.sub.ModulePaintscale;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleQuantifyStrategy;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleSelectExcludeData;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleThemes;
import net.rs.lamsi.multiimager.FrameModules.sub.ModuleZoom;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;

public class ModuleImage2D extends SettingsModuleContainer<SettingsImage2D, Image2D> {
	private ImageEditorWindow window;
	
	private ModuleGeneral moduleGeneral;
	private ModuleZoom moduleZoom;
	private ModulePaintscale modulePaintscale;
	private ModuleThemes moduleThemes;
	private ModuleBackgroundImg moduleBG;
	private ModuleOperations moduleOperations;
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
		
		moduleOperations = new ModuleOperations(window);
		addModule(moduleOperations);

		// add all modules for Image settings TODO add all mods
		listSettingsModules.addElement(moduleGeneral.getModSplitConImg());
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
