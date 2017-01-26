package net.rs.lamsi.massimager.Frames.Dialogs.generalsettings.panels;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.massimager.Frames.Window;
import net.rs.lamsi.massimager.Frames.Dialogs.generalsettings.interfaces.SettingsPanel;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.SettingsConverterRAW;
import net.rs.lamsi.massimager.Settings.SettingsHolder;
import net.rs.lamsi.massimager.Settings.preferences.SettingsGeneralValueFormatting;
import net.rs.lamsi.massimager.Settings.visualization.SettingsPlotSpectraLabelGenerator;

import java.awt.BorderLayout;

import javax.swing.JLabel;

import java.awt.Choice;

import javax.swing.JSpinner;
import javax.swing.JCheckBox;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTextField;

public class PnVisualSpectraLabelGeneratorSettings extends JPanel implements SettingsPanel {
	private JTextField txtMinIntensityToShowLabels;
	private JTextField txtSpaceBetweenLabels;
	private JCheckBox cbShowLabels;
	private JCheckBox cbShowCharge;

	/**
	 * Create the panel.
	 */
	public PnVisualSpectraLabelGeneratorSettings() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[][][][]", "[][][][]"));
		
		JLabel lblShowLabelsAt = new JLabel("Show labels at");
		panel.add(lblShowLabelsAt, "cell 0 0,alignx trailing");
		
		txtMinIntensityToShowLabels = new JTextField();
		txtMinIntensityToShowLabels.setText("5");
		txtMinIntensityToShowLabels.setToolTipText("Relative intensity in %");
		panel.add(txtMinIntensityToShowLabels, "cell 1 0,growx");
		txtMinIntensityToShowLabels.setColumns(10);
		
		JLabel lblOfIntensity = new JLabel("% relative intensity");
		panel.add(lblOfIntensity, "cell 2 0");
		
		JLabel lblSpaceBetweenLabels = new JLabel("Space between labels");
		panel.add(lblSpaceBetweenLabels, "cell 0 1,alignx trailing");
		
		txtSpaceBetweenLabels = new JTextField();
		txtSpaceBetweenLabels.setToolTipText("Space in pixel");
		txtSpaceBetweenLabels.setText("100");
		panel.add(txtSpaceBetweenLabels, "cell 1 1,growx");
		txtSpaceBetweenLabels.setColumns(10);
		
		cbShowLabels = new JCheckBox("Show labels");
		cbShowLabels.setToolTipText("Hide or show labels");
		panel.add(cbShowLabels, "cell 0 2");
		
		cbShowCharge = new JCheckBox("Show charge");
		cbShowCharge.setToolTipText("Only if there is an isotope distribution that allows charge calculation ");
		panel.add(cbShowCharge, "cell 0 3");

	}
	
	//
	@Override
	public void setAllSettings(SettingsHolder settings) {
		//
		try{
			SettingsPlotSpectraLabelGenerator sett = settings.getSetVisPlotSpectraLabelGenerator();
			sett.setShowCharge(getCbShowCharge().isSelected());
			sett.setShowLabels(getCbShowLabels().isSelected());
			
			sett.setMinimumRelativeIntensityOfLabel(Double.valueOf(getTxtMinIntensityToShowLabels().getText())/100.0);		
			sett.setMinimumSpaceBetweenLabels(Integer.valueOf(getTxtSpaceBetweenLabels().getText()));
		} catch(Exception ex) { 
			Window.showErrorDialog("Wrong input? ", ex);
		}
	}
	// alle Settings werden angezeigt
	@Override
	public void setAllSettingsOnPanel(SettingsHolder settings) { 
		SettingsPlotSpectraLabelGenerator sett = settings.getSetVisPlotSpectraLabelGenerator();
		getCbShowCharge().setSelected(sett.isShowCharge());
		getCbShowLabels().setSelected(sett.isShowLabels());
		getTxtMinIntensityToShowLabels().setText(sett.getMinimumRelativeIntensityOfLabel()*100.0+"");
		getTxtSpaceBetweenLabels().setText(sett.getMinimumSpaceBetweenLabels()+"");
	}

	@Override
	public Settings getSettings(SettingsHolder settings) { 
		return settings.getSetVisPlotSpectraLabelGenerator();
	} 

	public JCheckBox getCbShowLabels() {
		return cbShowLabels;
	}
	public JCheckBox getCbShowCharge() {
		return cbShowCharge;
	}
	public JTextField getTxtSpaceBetweenLabels() {
		return txtSpaceBetweenLabels;
	}
	public JTextField getTxtMinIntensityToShowLabels() {
		return txtMinIntensityToShowLabels;
	}
}
