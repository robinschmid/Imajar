package net.rs.lamsi.multiimager.FrameModules;


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
import net.rs.lamsi.massimager.Frames.FrameWork.ColorChangedListener;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.ImageSettingsModule;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsZoom;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

import org.jfree.data.Range;

public class ModuleZoom extends ImageSettingsModule<SettingsZoom> { 
	//
	private JTextField txtXLower;
	private JTextField txtXUpper;
	private JTextField txtYLower;
	private JTextField txtYUpper;
	
	// AUTOGEN

	/**
	 * Create the panel.
	 */
	public ModuleZoom() {
		super("Zoom", false, SettingsZoom.class);  
		getLbTitle().setText("Zoom");
		
		JPanel panel = new JPanel();
		getPnContent().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[][grow][grow]", "[][][]"));
		
		JLabel lblLowerBound = new JLabel("lower bound");
		lblLowerBound.setFont(new Font("Tahoma", Font.BOLD, 13));
		panel.add(lblLowerBound, "cell 1 0,alignx center");
		
		JLabel lblUpperBound = new JLabel("upper bound");
		lblUpperBound.setFont(new Font("Tahoma", Font.BOLD, 13));
		panel.add(lblUpperBound, "cell 2 0,alignx center");
		
		JLabel lblX = new JLabel("x");
		lblX.setFont(new Font("Tahoma", Font.BOLD, 13));
		panel.add(lblX, "cell 0 1,alignx trailing");
		
		txtXLower = new JTextField();
		txtXLower.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(txtXLower, "cell 1 1,growx,aligny top");
		txtXLower.setColumns(7);
		
		txtXUpper = new JTextField();
		txtXUpper.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(txtXUpper, "cell 2 1,growx");
		txtXUpper.setColumns(10);
		
		JLabel lblY = new JLabel("y");
		lblY.setFont(new Font("Tahoma", Font.BOLD, 13));
		panel.add(lblY, "cell 0 2,alignx trailing");
		
		txtYLower = new JTextField();
		txtYLower.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(txtYLower, "cell 1 2,growx");
		txtYLower.setColumns(10);
		
		txtYUpper = new JTextField();
		txtYUpper.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(txtYUpper, "cell 2 2,growx,aligny top");
		txtYUpper.setColumns(10);
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
	
	@Override
	public void setSettings(SettingsZoom settings) {
		// TODO Auto-generated method stub
		super.setSettings(settings);
	}
	
	//################################################################################################
	// Autoupdate
	@Override
	public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
	}
	
	@Override
	public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl, ColorChangedListener ccl, ItemListener il) {
		getTxtXLower().getDocument().addDocumentListener(dl);
		getTxtYLower().getDocument().addDocumentListener(dl);
		getTxtXUpper().getDocument().addDocumentListener(dl);
		getTxtYUpper().getDocument().addDocumentListener(dl);
	}

	//################################################################################################
	// LOGIC
	// Paintsclae from Image
	@Override
	public void setAllViaExistingSettings(SettingsZoom si) {  
		ImageLogicRunner.setIS_UPDATING(false);
		// new reseted ps
		if(si == null) {
			si = new SettingsZoom();
			si.resetAll();
		} 
		if(si.getXrange()!=null) {
			getTxtXLower().setText(String.valueOf(si.getXrange().getLowerBound()));
			getTxtXUpper().setText(String.valueOf(si.getXrange().getUpperBound()));
		}
		if(si.getYrange()!=null) {
			getTxtYLower().setText(String.valueOf(si.getYrange().getLowerBound()));
			getTxtYUpper().setText(String.valueOf(si.getYrange().getUpperBound()));
		}
		// finished
		ImageLogicRunner.setIS_UPDATING(true);
		//ImageEditorWindow.getEditor().fireUpdateEvent(true);
	} 

	@Override
	public SettingsZoom writeAllToSettings(SettingsZoom si) {
		if(si!=null) {
			try {
				double xl = Double.valueOf(getTxtXLower().getText());
				double xu = Double.valueOf(getTxtXUpper().getText());
				si.setXrange(new Range(xl, xu));
				
				xl = Double.valueOf(getTxtYLower().getText());
				xu = Double.valueOf(getTxtYUpper().getText());
				si.setYrange(new Range(xl, xu));
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return si;
	}
	
	//################################################################################################
	// GETTERS AND SETTERS 
	public JTextField getTxtXLower() {
		return txtXLower;
	}
	public JTextField getTxtYLower() {
		return txtYLower;
	}
	public JTextField getTxtXUpper() {
		return txtXUpper;
	}
	public JTextField getTxtYUpper() {
		return txtYUpper;
	}
}
