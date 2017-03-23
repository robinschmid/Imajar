package net.rs.lamsi.multiimager.FrameModules;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.data.multidimensional.DatasetContinuousMD;
import net.rs.lamsi.massimager.Frames.FrameWork.ColorChangedListener;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.ImageSettingsModule;
import net.rs.lamsi.massimager.Settings.image.SettingsImage2D;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsGeneralImage.XUNIT;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsImageContinousSplit;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

public class ModuleImage2D extends ImageSettingsModule<SettingsImage2D> {
	private ImageEditorWindow window;
	
	
	//

	/**
	 * Create the panel.
	 */
	public ModuleImage2D(ImageEditorWindow wnd) {
		super("", false, SettingsImage2D.class);    
		window = wnd;
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
		//TODO
	}
	@Override
	public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
	}

	//################################################################################################
	// LOGIC
	@Override
	public void setAllViaExistingSettings(SettingsImage2D st) {  
		if(st!=null) {
			ImageLogicRunner.setIS_UPDATING(false);
			// new reseted ps 
			
			// finished
			ImageLogicRunner.setIS_UPDATING(true);
			ImageEditorWindow.getEditor().fireUpdateEvent(true);
		}
	} 

	@Override
	public SettingsImage2D writeAllToSettings(SettingsImage2D sett) {
		if(sett!=null) {
			try {
								
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return sett;
	}
	

	//################################################################################################
	// GETTERS AND SETTERS  
}
