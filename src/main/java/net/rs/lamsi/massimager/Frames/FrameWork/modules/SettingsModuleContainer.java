package net.rs.lamsi.massimager.Frames.FrameWork.modules;

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

import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.massimager.Frames.FrameWork.ColorChangedListener;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.interf.SettingsModuleObject;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.SettingsContainerSettings;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow.LOG;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

/**
 * holds multiple Collectable2DSettingsModules or HeatmapSettingsModules or basic Modules
 * @author r_schm33
 *
 * @param <T> Settings
 * @param <S>
 */
public abstract class SettingsModuleContainer<T extends SettingsContainerSettings, S extends Collectable2D> extends Collectable2DSettingsModule<T, S> implements SettingsModuleObject<S> {

	// panel for settings
	protected JPanel gridsettings;
	
	// list of all Modules
	protected Vector<Module> listSettingsModules = new Vector<Module>();

	public SettingsModuleContainer(String title, boolean westside, Class settc, Class objclass) { 
		super(title, westside, settc, objclass);
		

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.getPnContent().add(scrollPane, BorderLayout.EAST);

		gridsettings = new JPanel();
		gridsettings.setAlignmentY(0.0f);
		gridsettings.setAlignmentX(0.0f);
		scrollPane.setViewportView(gridsettings);
		gridsettings.setLayout(new BoxLayout(gridsettings, BoxLayout.Y_AXIS));
	}
	
	/**
	 * add the module to the current layout of the panel and to the list of modules
	 * @param mod
	 */
	public void addModule(Module mod) {
		gridsettings.add(mod);
		listSettingsModules.addElement(mod);
	}
	
	/**
	 * 
	 * @param modclass
	 * @return this module or a sub module
	 */
	public Module getModuleByClass(Class modclass) {
		// TODO -- add other settings here
		if(this.getClass().isAssignableFrom(modclass))
			return this;
		else {
			for(Module s:this.listSettingsModules)
				if(s!=null)
					if(modclass.isInstance(s))
						return s;
		}
		return null;
	}

	//################################################################################################
		// Autoupdate
		@Override
		public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
			for(Module m : listSettingsModules) {
				if(SettingsModule.class.isInstance(m)) 
					((SettingsModule)m).addAutoupdater(al, cl, dl, ccl, il);
			}
		}
		@Override
		public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
			for(Module m : listSettingsModules) {
				if(SettingsModule.class.isInstance(m)) 
					((SettingsModule)m).addAutoRepainter(al, cl, dl, ccl, il);
			}
		}

		//################################################################################################
		// LOGIC
		@Override
		public void setAllViaExistingSettings(T st) throws Exception {  
			if(st!=null) {
				ImageLogicRunner.setIS_UPDATING(false);
				// new reseted ps 
				for(Module m : listSettingsModules) {
					if(SettingsModule.class.isInstance(m)) {
						SettingsModule sm = ((SettingsModule)m);
						// try to find settings in collection2d
						Settings sett = getCurrentImage().getSettingsByClass(sm.getSettingsClass());
						if(sett==null) {
							// try to find in parent settings
							 sett = st.getSettingsByClass(sm.getSettingsClass());
						}
						
						if(sett!=null) { 
							sm.setAllViaExistingSettings(sett);
							sm.setVisible(true);
						}
						else {
							sm.setVisible(false);
							ImageEditorWindow.log("No Settings for "+sm.getSettingsClass(), LOG.DEBUG);
						}
					}
				}
				// finished
				ImageLogicRunner.setIS_UPDATING(true);
				ImageEditorWindow.getEditor().fireUpdateEvent(true);
			}
		} 

		@Override
		public T writeAllToSettings(T st) {
			if(st!=null) {
				try {
					for(Module m : listSettingsModules) {
						if(SettingsModule.class.isInstance(m)) {
							SettingsModule sm = ((SettingsModule)m);
							Settings sett = getCurrentImage().getSettingsByClass(sm.getSettingsClass());
							if(sett==null) {
								// try to find in parent settings
								 sett = st.getSettingsByClass(sm.getSettingsClass());
							}
							
							sm.writeAllToSettings(sett);
						}
					}	
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			return st;
		}
		
		@Override
		public void setCurrentHeatmap(Heatmap heat) {
			for(Module m : listSettingsModules) {
				if(HeatmapSettingsModule.class.isInstance(m)) {
					HeatmapSettingsModule sm = ((HeatmapSettingsModule)m);
					
					sm.setCurrentHeatmap(heat);
				}
			}
			super.setCurrentHeatmap(heat);
		}
		
		@Override 
		public void setCurrentImage(S img) {
			for(Module m : listSettingsModules) {
				if(Collectable2DSettingsModule.class.isInstance(m)) {
					Collectable2DSettingsModule sm = ((Collectable2DSettingsModule)m);
					
					sm.setCurrentImage(img);
				}
			}
			super.setCurrentImage(img);
		}
}
