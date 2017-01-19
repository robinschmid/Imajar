package net.rs.lamsi.massimager.Frames.Dialogs.generalsettings.panels;

import javax.swing.JPanel;

import net.rs.lamsi.massimager.Frames.Dialogs.generalsettings.interfaces.SettingsPanel;
import net.rs.lamsi.massimager.Settings.Settings;
import net.rs.lamsi.massimager.Settings.SettingsHolder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSeparator;
import java.awt.Color;
import javax.swing.JTextField;

public class PnPlotStyle extends JPanel implements SettingsPanel {
	private JTextField txtTitle;
	public PnPlotStyle() {
		setLayout(new MigLayout("", "[][]", "[][][][][][][]"));
		
		JCheckBox chckbxUseGeneralFont = new JCheckBox("use general font");
		add(chckbxUseGeneralFont, "cell 0 0");
		
		JCheckBox chckbxUseGeneralFont_1 = new JCheckBox("use general font size");
		add(chckbxUseGeneralFont_1, "cell 0 1");
		
		JCheckBox chckbxUseGeneralFont_2 = new JCheckBox("use general font color");
		add(chckbxUseGeneralFont_2, "cell 0 2");
		
		JButton colorGeneralFont = new JButton("");
		add(colorGeneralFont, "cell 1 2");
		
		JCheckBox chckbxUseGeneralBg = new JCheckBox("use general BG color");
		chckbxUseGeneralBg.setToolTipText("Use general background color");
		add(chckbxUseGeneralBg, "cell 0 3");
		
		JButton colorGeneralBG = new JButton("");
		add(colorGeneralBG, "cell 1 3");
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.DARK_GRAY);
		add(separator, "cell 0 4 2 1");
		
		JLabel lblTitel = new JLabel("Titel");
		add(lblTitel, "flowx,cell 0 5");
		
		txtTitle = new JTextField();
		add(txtTitle, "cell 0 5 2 1,growx");
		txtTitle.setColumns(10);
		
		JLabel lblColor = new JLabel("Color");
		add(lblColor, "flowx,cell 0 6");
		
		JButton colorTitle = new JButton("New button");
		add(colorTitle, "cell 0 6,growx");
	}

	@Override
	public void setAllSettings(SettingsHolder settings) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAllSettingsOnPanel(SettingsHolder settings) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Settings getSettings(SettingsHolder settings) {
		return settings.getSetPlotStyle();
	}

}
