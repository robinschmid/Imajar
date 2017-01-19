package net.rs.lamsi.massimager.Frames.Panels;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class PnChartWithSettings extends JPanel {
	private JPanel pnChartView;
	private JRadioButton rbSelectedChartView;
	private JPanel EastSettings;

	/**
	 * Create the panel.
	 */
	public PnChartWithSettings() {
		setLayout(new BorderLayout(0, 0));
		
		EastSettings = new JPanel();
		add(EastSettings, BorderLayout.EAST);
		GridBagLayout gbl_EastSettings = new GridBagLayout();
		gbl_EastSettings.columnWidths = new int[]{21, 0};
		gbl_EastSettings.rowHeights = new int[]{21, 0};
		gbl_EastSettings.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_EastSettings.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		EastSettings.setLayout(gbl_EastSettings);
		
		rbSelectedChartView = new JRadioButton("");
		GridBagConstraints gbc_rbSelectedChartView = new GridBagConstraints();
		gbc_rbSelectedChartView.anchor = GridBagConstraints.NORTHWEST;
		gbc_rbSelectedChartView.gridx = 0;
		gbc_rbSelectedChartView.gridy = 0;
		EastSettings.add(rbSelectedChartView, gbc_rbSelectedChartView);
		
		pnChartView = new JPanel();
		add(pnChartView, BorderLayout.CENTER);
		pnChartView.setLayout(new BorderLayout(0, 0));

	}

	public JPanel getPnChartView() {
		return pnChartView;
	}
	public JRadioButton getRbSelectedChartView() {
		return rbSelectedChartView;
	}
	public boolean isSelected() {
		return getRbSelectedChartView().isSelected();
	}
	public JPanel getEastSettings() {
		return EastSettings;
	}
}
