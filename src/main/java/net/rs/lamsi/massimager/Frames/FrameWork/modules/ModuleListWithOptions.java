package net.rs.lamsi.massimager.Frames.FrameWork.modules;
import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JPanel;


public class ModuleListWithOptions extends ModuleList {
	private JPanel pnOptions;

	/**
	 * Create the panel.
	 */
	public ModuleListWithOptions(String stitle, boolean westside, Vector listVector) {
		super(stitle, westside, listVector);
		JPanel panelopholder = new JPanel();
		getPnContent().add(panelopholder, BorderLayout.NORTH);
		panelopholder.setLayout(new BorderLayout(0, 0));
		
		pnOptions = new JPanel();
		panelopholder.add(pnOptions, BorderLayout.WEST);

	}

	public JPanel getPnOptions() {
		return pnOptions;
	}
}
