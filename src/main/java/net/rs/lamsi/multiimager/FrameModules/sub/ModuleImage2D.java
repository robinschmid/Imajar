package net.rs.lamsi.multiimager.FrameModules.sub;


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
import net.rs.lamsi.massimager.Frames.FrameWork.modules.Module;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.SettingsModule;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.image.SettingsImage2D;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

public class ModuleImage2D extends Collectable2DSettingsModule<SettingsImage2D, Image2D> {
	private ImageEditorWindow window;
	

	// list of all Modules
	private Vector<Module> listImageSettingsModules = new Vector<Module>();

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
		

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.getPnContent().add(scrollPane, BorderLayout.EAST);

		JPanel gridsettings = new JPanel();
		gridsettings.setAlignmentY(0.0f);
		gridsettings.setAlignmentX(0.0f);
		scrollPane.setViewportView(gridsettings);
		gridsettings.setLayout(new BoxLayout(gridsettings, BoxLayout.Y_AXIS));

		moduleGeneral = new ModuleGeneral(window);
		moduleGeneral.setAlignmentY(Component.TOP_ALIGNMENT);
		gridsettings.add(moduleGeneral);

		moduleZoom = new ModuleZoom();
		gridsettings.add(moduleZoom);
		
		modulePaintscale = new ModulePaintscale();
		gridsettings.add(modulePaintscale);

		moduleThemes = new ModuleThemes();
		gridsettings.add(moduleThemes);

		moduleOperations = new ModuleOperations(window);
		gridsettings.add(moduleOperations);

		// add all modules for Image settings TODO add all mods
		listImageSettingsModules.addElement(moduleGeneral);
		listImageSettingsModules.addElement(moduleGeneral.getModSplitConImg());
		listImageSettingsModules.addElement(moduleZoom);
		
		listImageSettingsModules.addElement(modulePaintscale);
		listImageSettingsModules.addElement(moduleThemes);
		listImageSettingsModules.addElement(moduleOperations);
		listImageSettingsModules.addElement(moduleOperations.getModQuantifier());
		listImageSettingsModules.addElement(moduleOperations.getModSelectExcludeData());
	}
	
	// apply for print or presentation before changing settings
	@Override
	public void setSettings(SettingsImage2D settings) {
		super.setSettings(settings);
	}
	
	//################################################################################################
	// Autoupdate
	@Override
	public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		for(Module m : listImageSettingsModules) {
			if(SettingsModule.class.isInstance(m)) 
				((SettingsModule)m).addAutoupdater(al, cl, dl, ccl, il);
		}
	}
	@Override
	public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		for(Module m : listImageSettingsModules) {
			if(SettingsModule.class.isInstance(m)) 
				((SettingsModule)m).addAutoRepainter(al, cl, dl, ccl, il);
		}
	}

	//################################################################################################
	// LOGIC
	@Override
	public void setAllViaExistingSettings(SettingsImage2D st) {  
		if(st!=null) {
			ImageLogicRunner.setIS_UPDATING(false);
			// new reseted ps 
			for(Module m : listImageSettingsModules) {
				if(SettingsModule.class.isInstance(m)) {
					SettingsModule sm = ((SettingsModule)m);
					Settings sett = st.getSettingsByClass(sm.getSettingsClass());
					
					sm.setAllViaExistingSettings(sett);
				}
			}
			// finished
			ImageLogicRunner.setIS_UPDATING(true);
			ImageEditorWindow.getEditor().fireUpdateEvent(true);
		}
	} 

	@Override
	public SettingsImage2D writeAllToSettings(SettingsImage2D st) {
		if(st!=null) {
			try {
				for(Module m : listImageSettingsModules) {
					if(SettingsModule.class.isInstance(m)) {
						SettingsModule sm = ((SettingsModule)m);
						Settings sett = st.getSettingsByClass(sm.getSettingsClass());
						
						sm.writeAllToSettings(sett);
					}
				}	
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return st;
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
}
