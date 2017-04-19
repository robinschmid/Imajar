package net.rs.lamsi.multiimager.FrameModules.sub;


import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.massimager.Frames.FrameWork.ColorChangedListener;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.Collectable2DSettingsModule;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsZoom;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

import org.jfree.data.Range;

public class ModuleBackgroundImg extends Collectable2DSettingsModule<SettingsZoom, Collectable2D> { 
	//
	
	// AUTOGEN

	/**
	 * Create the panel.
	 */
	public ModuleBackgroundImg() {
		super("Zoom", false, SettingsZoom.class, Collectable2D.class);  
		getLbTitle().setText("Zoom");
		
		JPanel panel = new JPanel();
		getPnContent().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[][grow][grow]", "[][][]"));
		
	}
	
	@Override
	public void setCurrentHeatmap(Heatmap heat) {
		super.setCurrentHeatmap(heat);
		// extract
		if(heat!=null && getSettings()!=null) {
			getSettings().setXrange(heat.getPlot().getDomainAxis().getRange());
			getSettings().setYrange(heat.getPlot().getRangeAxis().getRange());
			
			setAllViaExistingSettings(getSettings());
		}
	}
	
	
	//################################################################################################
	// Autoupdate
	@Override
	public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
	}
	
	@Override
	public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		//getTxtXLower().getDocument().addDocumentListener(dl);
	}

	//################################################################################################
	// LOGIC
	// Paintsclae from Image
	@Override
	public void setAllViaExistingSettings(SettingsZoom si) {  
		ImageLogicRunner.setIS_UPDATING(false);
		// set all to panels
		
		
		// finished
		ImageLogicRunner.setIS_UPDATING(true);
		//ImageEditorWindow.getEditor().fireUpdateEvent(true);
	} 

	@Override
	public SettingsZoom writeAllToSettings(SettingsZoom si) {
		if(si!=null) {
			try {
				// set all to si
				
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return si;
	}
	
	//################################################################################################
	// GETTERS AND SETTERS 
	
}
