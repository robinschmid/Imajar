package net.rs.lamsi.massimager.Frames.Dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.massimager.Frames.Window;
import net.rs.lamsi.massimager.Frames.FrameWork.ColorPicker2;
import net.rs.lamsi.massimager.Heatmap.Heatmap;
import net.rs.lamsi.massimager.Settings.image.sub.SettingsGeneralImage;
import net.rs.lamsi.massimager.Settings.image.visualisation.SettingsPaintScale;
import net.rs.lamsi.utils.DialogLoggerUtil;

import org.jfree.chart.ChartPanel;

public class ColorPickerDialog extends JDialog {
	// Mein STUFF
	Window window;
	ColorPickerDialog thisframe;
	ColorPicker2 colorPickerDialog = new ColorPicker2(this);
	// AUTOMATISCH
	private final JPanel contentPanel = new JPanel();
	private JTextField txtLevels;
	private JLabel lblLevels;
	private JCheckBox cbBlackWhite;
	private JCheckBox cbInvert;
	private JCheckBox cbUseLOD;
	private JCheckBox cbLODAsInvisible;
	private JCheckBox cbBlackWhiteAsMinMax;
	private JButton btnMinColor;
	private JButton btnEndColor;
	private JTextField txtMinValue;
	private JTextField txtMaxValue;
	private JButton btnColorMonochrom;
	private JPanel pnPreview;
	private JPanel pnMonochrom;
	private JPanel pnNotMonochrom;
	private JPanel pnMinMax;
	private JButton btnClose;
 

	/**
	 * Create the dialog.
	 */
	public ColorPickerDialog(Window window) {
		this.window = window;
		thisframe = this;
		//
		setTitle("Color Picker");
		setBounds(100, 100, 526, 359);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel pnOptions = new JPanel();
			contentPanel.add(pnOptions, BorderLayout.WEST);
			pnOptions.setLayout(new BorderLayout(0, 0));
			{
				JPanel pnLeft = new JPanel();
				pnOptions.add(pnLeft, BorderLayout.WEST);
				{
					lblLevels = new JLabel("Levels");
					lblLevels.setToolTipText("Color levels for a more or less smooth gradient");
				}
				{
					txtLevels = new JTextField();
					txtLevels.setText("256");
					txtLevels.setColumns(3);
				}
				
				cbBlackWhite = new JCheckBox("monochrom");
				cbBlackWhite.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						JCheckBox cb = (JCheckBox)e.getSource();
		        		// monochrom panel off/on
						getPnMonochrom().setVisible(cb.isSelected());
						getPnNotMonochrom().setVisible(!cb.isSelected());
					}
				});
				cbBlackWhite.setToolTipText("Only one color");
				
				cbInvert = new JCheckBox("invert");
				cbInvert.setToolTipText("Iverts the color scale");
				
				cbUseLOD = new JCheckBox("use Min & Max");
				cbUseLOD.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						JCheckBox cb = (JCheckBox)e.getSource();
		        		// monochrom panel off/on
						getPnMinMax().setVisible(cb.isSelected()); 
					}
				});
				cbUseLOD.setToolTipText("Use a limit of detection and set all values beneath the LOD to the minimum color");
				
				pnMinMax = new JPanel();
				cbLODAsInvisible = new JCheckBox("Min as invisible");
				cbLODAsInvisible.setToolTipText("Set the alpha of all values beneath the minimum value to 100% (invisible)");
				
				txtMinValue = new JTextField();
				txtMinValue.setToolTipText("Set a minimum value. Every lower value will be painted as 'start color'. If the minimum is set to 0 it is not used.");
				txtMinValue.setText("0");
				txtMinValue.setColumns(10);
				
				JLabel lblMin = new JLabel("minimum");
				
				JLabel lblMaximum = new JLabel("maximum");
				
				txtMaxValue = new JTextField();
				txtMaxValue.setToolTipText("Set a maximum value. Every higher value will be painted as 'end color'. If the maximum is set to 0 it is not used.");
				txtMaxValue.setText("0");
				txtMaxValue.setColumns(10);
				pnLeft.setLayout(new MigLayout("", "[40px][4px][85px]", "[20px][23px][23px][23px][136px]"));
				pnLeft.add(lblLevels, "cell 0 0,alignx right,aligny center");
				pnLeft.add(txtLevels, "cell 2 0,alignx left,aligny top");
				pnLeft.add(cbBlackWhite, "cell 0 1 3 1,growx,aligny top");
				pnLeft.add(cbInvert, "cell 0 2 3 1,alignx left,aligny top");
				pnLeft.add(cbUseLOD, "cell 0 3 3 1,alignx left,aligny top");
				pnLeft.add(pnMinMax, "cell 0 4 3 1,alignx left,aligny top");
				pnMinMax.setLayout(new MigLayout("", "[50px][4px][55px]", "[23px][20px][20px]"));
				pnMinMax.add(cbLODAsInvisible, "cell 0 0 3 1,alignx right,aligny top");
				pnMinMax.add(lblMaximum, "cell 0 2,alignx left,aligny center");
				pnMinMax.add(txtMaxValue, "cell 2 2,growx,aligny top");
				pnMinMax.add(lblMin, "cell 0 1,growx,aligny center");
				pnMinMax.add(txtMinValue, "cell 2 1,growx,aligny top");
			}
			{
				JPanel pnRight = new JPanel();
				pnOptions.add(pnRight, BorderLayout.CENTER);
				
				pnNotMonochrom = new JPanel();
				
				cbBlackWhiteAsMinMax = new JCheckBox("B/W as Min/Max");
				cbBlackWhiteAsMinMax.setSelected(true);
				cbBlackWhiteAsMinMax.setToolTipText("Use black as min and white as max value. Can be inverted by checking \"invert\".");
				
				JLabel lblStartColor = new JLabel("Start color");
				
				btnMinColor = new JButton("");
				btnMinColor.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Open Color Dialog
						try{
							JButton btn = (JButton)e.getSource();
							colorPickerDialog.showDialog(btn);
						} catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				});
				btnEndColor = new JButton("");
				btnEndColor.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Open Color Dialog
						try{
							JButton btn = (JButton)e.getSource();
							colorPickerDialog.showDialog(btn);
						} catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				});
				JLabel lblEndColor = new JLabel("End color");
				pnNotMonochrom.setLayout(new MigLayout("", "[51px][12px][40px]", "[23px][26px][26px]"));
				pnNotMonochrom.add(cbBlackWhiteAsMinMax, "cell 0 0 3 1,alignx left,aligny top");
				pnNotMonochrom.add(lblStartColor, "cell 0 1,alignx center,aligny center");
				pnNotMonochrom.add(btnMinColor, "cell 2 1,alignx left,growy");
				pnNotMonochrom.add(lblEndColor, "cell 0 2,alignx center,aligny center");
				pnNotMonochrom.add(btnEndColor, "cell 2 2,alignx left,growy");
				
				JComboBox comboBox = new JComboBox();
				comboBox.setModel(new DefaultComboBoxModel(new String[] {"Blue2Red", "Yellow2Red", "Green2Blue"}));
				comboBox.setSelectedIndex(0);
				
				pnMonochrom = new JPanel();
				pnMonochrom.setVisible(false);
				
				JLabel lblColor = new JLabel("color");
				
				btnColorMonochrom = new JButton("");
				btnColorMonochrom.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Open Color Dialog
						try{
							JButton btn = (JButton)e.getSource();
							colorPickerDialog.showDialog(btn);
						} catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				});
				pnRight.setLayout(new MigLayout("", "[111px]", "[20px][92px][73px]"));
				pnRight.add(pnMonochrom, "cell 0 2,alignx left,aligny top");
				pnMonochrom.setLayout(new MigLayout("", "[23px][33px]", "[25px]"));
				pnMonochrom.add(lblColor, "cell 0 0,alignx left,aligny center");
				pnMonochrom.add(btnColorMonochrom, "cell 1 0,alignx left,growy");
				pnRight.add(pnNotMonochrom, "cell 0 1,growx,aligny top");
				pnRight.add(comboBox, "cell 0 0,growx,aligny top");
			}
		}
		{
			pnPreview = new JPanel();
			contentPanel.add(pnPreview, BorderLayout.CENTER);
			pnPreview.setLayout(new BorderLayout(0, 0));
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnApply = new JButton("Apply");
				btnApply.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						// Save all Settings and renew Preview
						applyAllSettingsAndRenewPreview();
					}
				});
				btnApply.setActionCommand("OK");
				buttonPane.add(btnApply);
			}
			{
				JButton btnLoad = new JButton("Load");
				btnLoad.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						loadSettingsFromFile();
					}
				});
				btnLoad.setActionCommand("OK");
				buttonPane.add(btnLoad);
				getRootPane().setDefaultButton(btnLoad);
			}
			{
				JButton btnSave = new JButton("Save");
				btnSave.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) { 
						saveSettingsToFile();
					}
				});
				btnSave.setActionCommand("Cancel");
				buttonPane.add(btnSave);
			}
			
			btnClose = new JButton("Close");
			btnClose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					thisframe.setVisible(false);
				}
			});
			btnClose.setActionCommand("Cancel");
			buttonPane.add(btnClose);
		}
	}

	// SAVE and LOAD from file  
	protected void saveSettingsToFile() { 
		try {
			window.getSettings().saveSettingsToFile(this, window.getSettings().getSetPaintScale());
		} catch (Exception e) { 
			e.printStackTrace();
			DialogLoggerUtil.showErrorDialog(this, "Error while saving", e);
		}  
	}
	protected void loadSettingsFromFile() { 
		try {
			window.getSettings().loadSettingsFromFile(this, window.getSettings().getSetPaintScale());
			SettingsPaintScale set =  window.getSettings().getSetPaintScale();
			// Set all cb and so
			getCbBlackWhite().setSelected(set.isMonochrom());
			getCbInvert().setSelected(set.isInverted());
			getCbUseLOD().setSelected(set.isUsesMinMax());
			getCbLODAsInvisible().setSelected(set.isUsesMinAsInvisible());
			getCbBlackWhiteAsMinMax().setSelected(set.isUsesBAsMax());
			// Zahlen
			getTxtLevels().setText(""+set.getLevels());
			// Color
			getBtnColorMonochrom().setBackground(set.getMinColor());
			getBtnMinColor().setBackground(set.getMinColor());
			getBtnEndColor().setBackground(set.getMaxColor());
			
			// Show Testimage 
			showTestImage(set);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	// Apply
	// generate a new Settings Object and save it to Settings
	protected void applyAllSettingsAndRenewPreview() {
		// Alle Daten erfassen und speichern
		SettingsPaintScale settings = new SettingsPaintScale();
		// Alle Daten hole
		try {
			settings.setMonochrom(getCbBlackWhite().isSelected());
			settings.setInverted(getCbInvert().isSelected());
			settings.setUsesMinMax(getCbUseLOD().isSelected());
			settings.setUsesMinAsInvisible(getCbLODAsInvisible().isSelected());
			settings.setUsesBAsMax(getCbBlackWhiteAsMinMax().isSelected());
			// Zahlen
			settings.setLevels((Integer.valueOf(getTxtLevels().getText())));
			if(getTxtMinValue().getText().length()>0)
				settings.setMin((Double.valueOf(getTxtMinValue().getText())));
			if(getTxtMaxValue().getText().length()>0)
				settings.setMax((Double.valueOf(getTxtMaxValue().getText())));
			
			// Color
			if(settings.isMonochrom()) {
				settings.setMinColor(getBtnColorMonochrom().getBackground());
				settings.setMaxColor(getBtnColorMonochrom().getBackground());
			}
			else {
				settings.setMinColor(getBtnMinColor().getBackground());
				settings.setMaxColor((getBtnEndColor()).getBackground());
			} 
		
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		// Save settings to Settingsholder
		window.getSettings().setSetPaintScale(settings); 	
		// show test Image
		showTestImage(settings);
	} 

	
	private void showTestImage(SettingsPaintScale settings) {
		try { 
			// Test Dataset:
			double[] x = new double[400];
			double[] y = new double[400];
			double[] z = new double[400];  

			for(int i=0; i<20; i++) {
				for(int k=0; k<20; k++) {
					x[i*20+k] = i;
					y[i*20+k] = k;
					z[i*20+k] =  i+k; 
				}
			}
			
			SettingsGeneralImage sett = new SettingsGeneralImage();
			sett.setSpotsize(1);

			// add chart
			Heatmap heat = window.getHeatFactory().generateHeatmap(settings, sett, "Test", x, y, z);
	        ChartPanel myChart = heat.getChartPanel(); 
	        myChart.setMouseWheelEnabled(true); 
	        
			// remove all
	        JPanel pnChartView = getPnPreview();
			pnChartView.removeAll();
			// Add Panel
	        pnChartView.add(myChart,BorderLayout.CENTER);
	        pnChartView.validate(); 
	        //
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	} 


	// GETTER SETTER
	public JTextField getTxtLevels() {
		return txtLevels;
	}
	public JCheckBox getCbBlackWhite() {
		return cbBlackWhite;
	}
	public JCheckBox getCbInvert() {
		return cbInvert;
	}
	public JCheckBox getCbUseLOD() {
		return cbUseLOD;
	}
	public JCheckBox getCbLODAsInvisible() {
		return cbLODAsInvisible;
	}
	public JCheckBox getCbBlackWhiteAsMinMax() {
		return cbBlackWhiteAsMinMax;
	}
	public JButton getBtnMinColor() {
		return btnMinColor;
	}
	public JButton getBtnEndColor() {
		return btnEndColor;
	}
	public JTextField getTxtMinValue() {
		return txtMinValue;
	}
	public JTextField getTxtMaxValue() {
		return txtMaxValue;
	}
	public JButton getBtnColorMonochrom() {
		return btnColorMonochrom;
	}
	public JPanel getPnPreview() {
		return pnPreview;
	}
	public JPanel getPnMonochrom() {
		return pnMonochrom;
	}
	public JPanel getPnNotMonochrom() {
		return pnNotMonochrom;
	}
	public JPanel getPnMinMax() {
		return pnMinMax;
	}
}
