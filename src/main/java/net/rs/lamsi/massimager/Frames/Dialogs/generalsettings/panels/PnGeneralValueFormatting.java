package net.rs.lamsi.massimager.Frames.Dialogs.generalsettings.panels;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.massimager.Frames.Dialogs.generalsettings.interfaces.SettingsPanel;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.SettingsConverterRAW;
import net.rs.lamsi.massimager.Settings.SettingsHolder;
import net.rs.lamsi.massimager.Settings.preferences.SettingsGeneralValueFormatting;

import java.awt.BorderLayout;

import javax.swing.JLabel;

import java.awt.Choice;

import javax.swing.JSpinner;
import javax.swing.JCheckBox;
import javax.swing.SpinnerNumberModel;

public class PnGeneralValueFormatting extends JPanel implements SettingsPanel {
	private JSpinner spinMZDecimals;
	private JSpinner spinRTDecimals;
	private JSpinner spinIntensityDecimals;
	private JCheckBox cbIntensityUseExponent;

	/**
	 * Create the panel.
	 */
	public PnGeneralValueFormatting() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[][][][]", "[][][][]"));
		
		JLabel lblMz = new JLabel("m/z value format");
		panel.add(lblMz, "cell 0 0,alignx trailing");
		
		spinMZDecimals = new JSpinner();
		spinMZDecimals.setModel(new SpinnerNumberModel(new Integer(4), new Integer(0), null, new Integer(1)));
		panel.add(spinMZDecimals, "cell 1 0");
		
		JLabel lblDecimals = new JLabel("decimals");
		panel.add(lblDecimals, "cell 2 0");
		
		JLabel lblRetentionTimeValue = new JLabel("Retention time value format");
		panel.add(lblRetentionTimeValue, "cell 0 1");
		
		spinRTDecimals = new JSpinner();
		spinRTDecimals.setModel(new SpinnerNumberModel(new Integer(2), new Integer(0), null, new Integer(1)));
		panel.add(spinRTDecimals, "cell 1 1");
		
		JLabel label = new JLabel("decimals");
		panel.add(label, "cell 2 1");
		
		JLabel lblIntensityValueFormat = new JLabel("Intensity value format");
		panel.add(lblIntensityValueFormat, "cell 0 2,alignx trailing");
		
		spinIntensityDecimals = new JSpinner();
		spinIntensityDecimals.setModel(new SpinnerNumberModel(new Integer(1), new Integer(0), null, new Integer(1)));
		panel.add(spinIntensityDecimals, "cell 1 2");
		
		JLabel label_1 = new JLabel("decimals");
		panel.add(label_1, "cell 2 2");
		
		cbIntensityUseExponent = new JCheckBox("Use exponent");
		panel.add(cbIntensityUseExponent, "cell 3 2");

	}
	
	//
	@Override
	public void setAllSettings(SettingsHolder settings) {
		//
		SettingsGeneralValueFormatting sett = settings.getSetGeneralValueFormatting();
		sett.setDecimalsIntensity((int)spinIntensityDecimals.getValue());
		sett.setDecimalsMZ((int)spinMZDecimals.getValue());
		sett.setDecimalsRT((int)spinRTDecimals.getValue());
		sett.setShowingExponentIntensity(cbIntensityUseExponent.isSelected());
	}
	// alle Settings werden angezeigt
	@Override
	public void setAllSettingsOnPanel(SettingsHolder settings) { 
		SettingsGeneralValueFormatting sett = settings.getSetGeneralValueFormatting();
		//
		spinIntensityDecimals.setValue(sett.getDecimalsIntensity());
		spinMZDecimals.setValue(sett.getDecimalsMZ());
		spinRTDecimals.setValue(sett.getDecimalsRT());		
		//
		cbIntensityUseExponent.setSelected(sett.isShowingExponentIntensity());
	}

	@Override
	public Settings getSettings(SettingsHolder settings) { 
		return settings.getSetGeneralValueFormatting();
	}
	

	public JSpinner getSpinMZDecimals() {
		return spinMZDecimals;
	}
	public JSpinner getSpinRTDecimals() {
		return spinRTDecimals;
	}
	public JSpinner getSpinIntensityDecimals() {
		return spinIntensityDecimals;
	}
	public JCheckBox getCbIntensityUseExponent() {
		return cbIntensityUseExponent;
	}

}
